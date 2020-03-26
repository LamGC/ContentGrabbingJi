package net.lamgc.cgj.proxy;

import com.github.monkeywie.proxyee.intercept.HttpProxyInterceptInitializer;
import com.github.monkeywie.proxyee.intercept.HttpProxyInterceptPipeline;
import com.github.monkeywie.proxyee.intercept.common.CertDownIntercept;
import com.github.monkeywie.proxyee.intercept.common.FullResponseIntercept;
import com.github.monkeywie.proxyee.proxy.ProxyConfig;
import com.github.monkeywie.proxyee.server.HttpProxyServer;
import com.github.monkeywie.proxyee.server.HttpProxyServerConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpCookie;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 由用户介入, 让用户手动登录Pixiv的方式, 再通过代理服务器捕获Cookie来绕过Google人机验证
 * @author LamGC
 */
public class PixivLoginProxyServer {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private boolean login = false;

    private final HttpProxyServer proxyServer;

    private final CookieStore cookieStore = new BasicCookieStore();

    public PixivLoginProxyServer(){
        this(null);
    }

    public PixivLoginProxyServer(ProxyConfig proxyConfig){
        HttpProxyServerConfig config = new HttpProxyServerConfig();
        config.setHandleSsl(true);
        this.proxyServer = new HttpProxyServer();
        this.proxyServer
                .serverConfig(config)
                .proxyConfig(proxyConfig)
                .proxyInterceptInitializer(new HttpProxyInterceptInitializer(){
                    @Override
                    public void init(HttpProxyInterceptPipeline pipeline) {
                        pipeline.addLast(new CertDownIntercept());
                        pipeline.addLast(new FullResponseIntercept() {
                            @Override
                            public boolean match(HttpRequest httpRequest, HttpResponse httpResponse, HttpProxyInterceptPipeline httpProxyInterceptPipeline) {
                                String host = httpRequest.headers().get(HttpHeaderNames.HOST);
                                return host.equalsIgnoreCase("pixiv.net") || host.contains(".pixiv.net");
                            }

                            @Override
                            public void handelResponse(HttpRequest httpRequest, FullHttpResponse fullHttpResponse, HttpProxyInterceptPipeline httpProxyInterceptPipeline) {
                                String url = httpRequest.headers().get(HttpHeaderNames.HOST) + httpRequest.uri();
                                log.info("拦截到Pixiv请求, URL: " + url);

                                log.info("正在导出Response Cookie...(Header Name: " + HttpHeaderNames.SET_COOKIE + ")");
                                List<String> responseCookies = fullHttpResponse.headers().getAll(HttpHeaderNames.SET_COOKIE);
                                AtomicInteger responseCookieCount = new AtomicInteger();
                                responseCookies.forEach(value -> {
                                    log.debug("Response Cookie: " + value);
                                    cookieStore.addCookie(parseRawCookie(value));
                                    responseCookieCount.incrementAndGet();
                                });
                                log.info("Cookie导出完成(已导出 " + responseCookieCount.get() + " 条Cookie)");

                                //登录检查
                                // 如果用户在登录界面登录成功后反复刷新，会出现登录返回不对但已经成功登录的情况，
                                // 故此处在登录完成后不再判断是否成功登录
                                if(!isLogin() && url.contains("accounts.pixiv.net/api/login")){
                                    log.info("正在检查登录结果...");
                                    //拷贝一份以防止对原响应造成影响
                                    FullHttpResponse copyResponse = fullHttpResponse.copy();
                                    ByteBuffer buffer = ByteBuffer.allocate(copyResponse.content().capacity());
                                    String contentStr;
                                    copyResponse.content().readBytes(buffer);
                                    contentStr = new String(buffer.array(), StandardCharsets.UTF_8);
                                    log.debug("Login Result: " + contentStr);

                                    JsonObject resultObject = new Gson().fromJson(contentStr, JsonObject.class);
                                    //只要error:false, body存在(应该是会存在的)且success字段存在, 即为登录成功
                                    login = !resultObject.get("error").getAsBoolean() &&
                                            resultObject.has("body") &&
                                            resultObject.get("body").getAsJsonObject().has("success");
                                    log.info("登录状态确认: " + (login ? "登录成功" : "登录失败"));

                                    fullHttpResponse.content().clear().writeBytes(
                                            ("{\"error\":false,\"message\":\"\",\"body\":{\"validation_errors\":{\"etc\":\"" +
                                                    StringEscapeUtils.escapeJava("Pixiv登录代理器已确认登录") + "\"}}}")
                                                    .getBytes(StandardCharsets.UTF_8));
                                }
                            }

                            protected BasicClientCookie parseRawCookie(String rawCookie) {
                                List<HttpCookie> cookies = HttpCookie.parse(rawCookie);
                                if (cookies.size() < 1)
                                    return null;
                                HttpCookie httpCookie = cookies.get(0);
                                BasicClientCookie cookie = new BasicClientCookie(httpCookie.getName(), httpCookie.getValue());
                                if (httpCookie.getMaxAge() >= 0) {
                                    Date expiryDate = new Date(System.currentTimeMillis() + httpCookie.getMaxAge() * 1000);
                                    cookie.setExpiryDate(expiryDate);
                                }
                                if (httpCookie.getDomain() != null)
                                    cookie.setDomain(httpCookie.getDomain());
                                if (httpCookie.getPath() != null)
                                    cookie.setPath(httpCookie.getPath());
                                if (httpCookie.getComment() != null)
                                    cookie.setComment(httpCookie.getComment());
                                cookie.setSecure(httpCookie.getSecure());
                                return cookie;
                            }
                        });
                    }
                });
    }

    public void start(int port){
        this.proxyServer.start(port);
    }

    public void close(){
        this.proxyServer.close();
    }

    /**
     * 是否已登录Pixiv
     * @return 如已登录返回true
     */
    public boolean isLogin(){
        return login;
    }

    /**
     * 导出CookieStore.
     * 注意!该方法导出的CookieStore不适用于ApacheHttpClient, 如需使用则需要进行转换.
     * @return CookieStore对象
     */
    public CookieStore getCookieStore(){
        return this.cookieStore;
    }

}
