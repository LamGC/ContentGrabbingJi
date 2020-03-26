package net.lamgc.cgj.pixiv;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PixivSession {

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36";

    /**
     * 全登陆过程的关键,
     * 保存会话用的cookieStore!
     */
    private CookieStore cookieStore = new BasicCookieStore();

    /**
     * 可以直接使用的HttpClient对象
     */
    private HttpClient httpClient;

    /**
     * 最后一次登录的错误信息
     */
    private String errMsg;

    public PixivSession(){
        this(null);
    }

    public PixivSession(CookieStore cookieStore){
        this(null, cookieStore);
    }

    /**
     * 创建一个Pixiv登录会话
     */
    public PixivSession(HttpHost proxy, CookieStore cookieStore) {
        if(cookieStore != null){
            this.cookieStore = cookieStore;
        }
        List<Header> defaultHeader = new ArrayList<>();
        defaultHeader.add(new BasicHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"));
        defaultHeader.add(new BasicHeader("user-agent", PixivSession.USER_AGENT));
        defaultHeader.add(new BasicHeader("accept-encoding", "gzip, deflate, br"));
        defaultHeader.add(new BasicHeader("accept-language", "zh-CN,zh;q=0.9"));

        /*defaultHeader.add(new BasicHeader("sec-fetch-mode", "navigate"));
        defaultHeader.add(new BasicHeader("sec-fetch-site", "same-origin"));
        defaultHeader.add(new BasicHeader("upgrade-insecure-requests", "1"));*/
        //创建一个Http访问器
        httpClient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .setDefaultHeaders(defaultHeader)
                .setProxy(proxy)
                .build();
    }

    /**
     * 程序自行通过帐号密码登录Pixiv.
     * @param PixivID Pixiv帐号
     * @param Password Pixiv密码
     * @return 登录成功返回true
     * @throws IOException 当登录抛出异常时返回
     * @deprecated Pixiv已经新增Google人机验证, 程序已无法自行登录Pixiv
     */
    public boolean Login(String PixivID, String Password) throws IOException {
        // 获取登录接口所需的PostKey
        String post_key = getPostKey();
        HttpPost postRequest = new HttpPost(PixivURL.PIXIV_LOGIN_URL); //https://accounts.pixiv.net/api/login?lang=zh
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("pixiv_id", PixivID));
        params.add(new BasicNameValuePair("password", Password));
        params.add(new BasicNameValuePair("post_key", post_key));
        //Form编码表单,作为Post的数据
        postRequest.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
        //启动访问
        HttpResponse response = httpClient.execute(postRequest);
        //获取接口返回数据
        String httpXML = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        System.out.println(httpXML);
        JsonObject responseJson = (JsonObject) new JsonParser().parse(httpXML);
        if(!responseJson.get("error").getAsBoolean() && !responseJson.get("body").getAsJsonObject().has("validation_errors")){
            errMsg = null;
            return true;
        }else{
            errMsg = responseJson.get("body").getAsJsonObject().get("validation_errors").toString();
            //System.err.println("登录失败！MSG: " + errMsg);
            return false;
        }
    }

    /**
     * 登录前准备, 获取PostKey
     * @return Post_Key
     */
    private String getPostKey() throws IOException {
        //创建请求,获取PostKey
        HttpGet getRequest = new HttpGet(PixivURL.PIXIV_LOGIN_PAGE_URL);
        //设置请求
        //getRequest.setConfig(config);
        getRequest.setHeader("User-Agent", USER_AGENT);
        //启动访问
        HttpResponse response = httpClient.execute(getRequest);
        //获取网页内容
        String pageAsXML = EntityUtils.toString(response.getEntity(),"utf-8");
        //创建Http解析器
        Document document = Jsoup.parse(pageAsXML);
        //获取init-config内容
        String init_config = document.getElementById("init-config").val();
        //System.out.println(init_config);
        //创建Json解析器解析init-config
        JsonObject initConfigObj = (JsonObject) new JsonParser().parse(init_config);
        //检查是否有postKey
        if(!initConfigObj.has("pixivAccount.postKey")){
            throw new RuntimeException("postKey获取失败!可能是Pixiv修改了登录过程!");
        }
        //获取postKey
        return initConfigObj.get("pixivAccount.postKey").getAsString();
    }

    /**
     * 获取CookieStore
     * @return CookieStore
     */
    public CookieStore getCookieStore(){
        return cookieStore;
    }

    /**
     * 获取可直接使用的HttpClient对象
     * @return 已配置好的HttpClient对象
     */
    public HttpClient getHttpClient(){
        return this.httpClient;
    }

    public boolean hasError(){
        return errMsg == null;
    }

    /**
     * 获取错误信息
     * @return 返回登录错误信息
     * @deprecated {@link #Login(String, String)}已经废除, 故本接口废除
     */
    public String getErrMsg(){
        return errMsg;
    }

}
