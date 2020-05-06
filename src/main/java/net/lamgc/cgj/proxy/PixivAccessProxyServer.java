package net.lamgc.cgj.proxy;

import com.github.monkeywie.proxyee.intercept.HttpProxyIntercept;
import com.github.monkeywie.proxyee.intercept.HttpProxyInterceptInitializer;
import com.github.monkeywie.proxyee.intercept.HttpProxyInterceptPipeline;
import com.github.monkeywie.proxyee.intercept.common.CertDownIntercept;
import com.github.monkeywie.proxyee.proxy.ProxyConfig;
import com.github.monkeywie.proxyee.server.HttpProxyServer;
import com.github.monkeywie.proxyee.server.HttpProxyServerConfig;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpCookie;
import java.util.Date;
import java.util.List;

/**
 * 登录成功后提供CookieStore, 然后由程序自动登录Pixiv
 * @author LamGC
 */
public class PixivAccessProxyServer {

    private final Logger log = LoggerFactory.getLogger(PixivAccessProxyServer.class.getName());

    private final HttpProxyServer proxyServer;

    private final CookieStore cookieStore;

    public PixivAccessProxyServer(CookieStore cookieStore){
        this(cookieStore, null);
    }

    public PixivAccessProxyServer(CookieStore cookieStore, ProxyConfig proxyConfig){
        HttpProxyServerConfig config = new HttpProxyServerConfig();
        this.cookieStore = cookieStore;
        config.setHandleSsl(true);
        this.proxyServer = new HttpProxyServer();
        this.proxyServer
                .serverConfig(config)
                .proxyConfig(proxyConfig)
                .proxyInterceptInitializer(new HttpProxyInterceptInitializer(){
                    @Override
                    public void init(HttpProxyInterceptPipeline pipeline) {
                        pipeline.addLast(new CertDownIntercept());
                        pipeline.addLast(new HttpProxyIntercept(){

                            private boolean match(HttpRequest request){
                                String host = request.headers().get(HttpHeaderNames.HOST);
                                return host.equalsIgnoreCase("pixiv.net") || host.contains(".pixiv.net");
                            }

                            @Override
                            public void beforeRequest(Channel clientChannel, HttpRequest httpRequest, HttpProxyInterceptPipeline pipeline) throws Exception {
                                log.info("URL: " + httpRequest.headers().get(HttpHeaderNames.HOST) + httpRequest.uri());
                                if(!match(httpRequest)){
                                    super.beforeRequest(clientChannel, httpRequest, pipeline);
                                    return;
                                }
                                log.info("正在注入Cookies...");
                                HttpHeaders requestHeaders = httpRequest.headers();
                                if(requestHeaders.contains(HttpHeaderNames.COOKIE)){
                                    log.info("原请求存在自带Cookies, 正在清除Cookies...");
                                    log.debug("原Cookies: {}", requestHeaders.getAsString(HttpHeaderNames.COOKIE));
                                    requestHeaders.remove(HttpHeaderNames.COOKIE);
                                }
                                StringBuilder cookieBuilder = new StringBuilder();
                                cookieStore.getCookies().forEach(cookie -> {
                                    if(cookie.isExpired(new Date())){
                                        return;
                                    }
                                    cookieBuilder.append(cookie.getName()).append("=").append(cookie.getValue()).append("; ");
                                });
                                log.info("Cookies构造完成, 结果: " + cookieBuilder.toString());
                                requestHeaders.add(HttpHeaderNames.COOKIE, cookieBuilder.toString());
                                log.info("Cookies注入完成.");

                                super.beforeRequest(clientChannel, httpRequest, pipeline);
                            }

                            @Override
                            public void afterResponse(Channel clientChannel, Channel proxyChannel, HttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) throws Exception {
                                if(!match(pipeline.getHttpRequest())){
                                    super.afterResponse(clientChannel, proxyChannel, httpResponse, pipeline);
                                    return;
                                }
                                log.info("正在更新Response Cookie...(Header Name: " + HttpHeaderNames.SET_COOKIE + ")");
                                List<String> responseCookies = httpResponse.headers().getAll(HttpHeaderNames.SET_COOKIE);
                                responseCookies.forEach(value -> {
                                    /*if(check(value)){
                                        log.info("黑名单Cookie, 已忽略: " + value);
                                        return;
                                    }*/
                                    log.info("Response Cookie: " + value);
                                    BasicClientCookie cookie = parseRawCookie(value);
                                    cookieStore.addCookie(null);
                                });
                                httpResponse.headers().remove(HttpHeaderNames.SET_COOKIE);
                                super.afterResponse(clientChannel, proxyChannel, httpResponse, pipeline);
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
     * 导出CookieStore.
     * 注意!该方法导出的CookieStore不适用于ApacheHttpClient, 如需使用则需要进行转换.
     * @return CookieStore对象
     */
    public CookieStore getCookieStore(){
        return this.cookieStore;
    }

}
