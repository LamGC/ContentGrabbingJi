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
import io.netty.handler.codec.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpCookie;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 由用户介入, 让用户手动登录Pixiv的方式, 再通过代理服务器捕获Cookie来绕过Google人机验证
 * @author LamGC
 */
public class PixivLoginProxyServer_Old {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private boolean login = false;

    private final HttpProxyServer proxyServer;

    //private final CookieManager cookieManager = new CookieManager();

    private final CookieStore cookieStore = new BasicCookieStore();

    public PixivLoginProxyServer_Old(){
        this(null);
    }

    public PixivLoginProxyServer_Old(ProxyConfig proxyConfig){
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
                                //log.info("Match: " + httpRequest.headers().get(HttpHeaderNames.HOST) + httpRequest.uri());
                                //return HttpUtil.checkUrl(httpRequest, ".*pixiv\\.net");
                                String host = httpRequest.headers().get(HttpHeaderNames.HOST);
                                return host.equalsIgnoreCase("pixiv.net") || host.contains(".pixiv.net");
                            }

                            @Override
                            public void handelResponse(HttpRequest httpRequest, FullHttpResponse fullHttpResponse, HttpProxyInterceptPipeline httpProxyInterceptPipeline) {
                                log.info("拦截到Pixiv请求, 正在导出Response Cookie...");
                                String url = httpRequest.headers().get(HttpHeaderNames.HOST) + httpRequest.uri();
                                log.info("URL: " + url);
                                /*URI requestURI = URI.create(httpRequest.headers().get(HttpHeaderNames.HOST) + httpRequest.uri());
                                log.info("正在导出Request Cookie...(Header Name: " + HttpHeaderNames.COOKIE + ")");
                                List<String> requestCookie = httpRequest.headers().getAll(HttpHeaderNames.COOKIE);
                                //requestCookie.forEach(value -> log.info("Request Cookie: " + value));
                                requestCookie.forEach(value -> {
                                    String[] items = value.split(";");
                                    for(String item : items){
                                        log.info("Raw Request Cookie: " + item);
                                        String[] keyValueSet = item.split("=");
                                        if(keyValueSet.length == 2){
                                            //TODO: 会出现重复的情况
                                            log.info("Request Cookie: " + keyValueSet[0].trim() + "=" + keyValueSet[1].trim());
                                            cookieStore.addCookie(new BasicClientCookie(keyValueSet[0].trim(), keyValueSet[1].trim()));
                                        }
                                    }
                                    //cookieStore.addCookie(parseRawCookie(value));
                                });*/
                                log.info("正在导出Response Cookie...(Header Name: " + HttpHeaderNames.SET_COOKIE + ")");
                                List<String> responseCookies = fullHttpResponse.headers().getAll(HttpHeaderNames.SET_COOKIE);
                                responseCookies.forEach(value -> {
                                    /*if(check(value)){
                                        log.info("黑名单Cookie, 已忽略: " + value);
                                        return;
                                    }*/
                                    log.info("Response Cookie: " + value);
                                    cookieStore.addCookie(parseRawCookie(value));
                                });

                                log.info("Cookie导出完成");

                                if(url.contains("accounts.pixiv.net/api/login")){
                                    log.info("正在检查登录结果...");
                                    FullHttpResponse copyResponse = fullHttpResponse.copy();
                                    //ByteArrayOutputStream contentOS = new ByteArrayOutputStream(copyResponse.content().capacity());
                                    ByteBuffer buffer = ByteBuffer.allocate(copyResponse.content().capacity());
                                    String contentStr;
                                    copyResponse.content().readBytes(buffer);
                                    contentStr = new String(buffer.array(), StandardCharsets.UTF_8);
                                    //log.info("Login Result: " + contentStr);
                                    JsonObject resultObject = new Gson().fromJson(contentStr, JsonObject.class);
                                    login = !resultObject.get("error").getAsBoolean() &&
                                            resultObject.has("body") &&
                                            resultObject.get("body").getAsJsonObject().has("success");
                                    if(login) {
                                        log.info("登录状态确认: 登录成功");
                                    } else {
                                        log.info("登录状态确认: 登录失败");
                                    }
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

                            private boolean check(String cookieValue){
                                for(String blackItem : new String[]{
                                        "a_type",
                                        "b_type",
                                        "c_type",
                                        "is_sensei_service_user",
                                        "module_orders_mypage",
                                }){
                                    if(cookieValue.startsWith(blackItem)){
                                        return true;
                                    }
                                }
                                return false;
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
