package net.lamgc.cgj.pixiv;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Pixiv {

    /**
     * illust Link
     */
    public final static String ATTR_LINK = "link";

    /**
     * illust Id
     */
    public final static String ATTR_ILLUST_ID = "illustId";

    /**
     * illust Title
     */
    public final static String ATTR_TITLE = "title";

    /**
     * illust Author Name
     */
    public final static String ATTR_AUTHOR_NAME = "authorName";

    /**
     * illust Author UserID
     */
    public final static String ATTR_AUTHOR_ID = "authorId";

    private final HttpClient httpClient;

    public Pixiv(HttpClient client){
        this.httpClient = client;
    }

    /**
     * 使用帐号密码登录Pixiv
     * @param PixivID Pixiv账户登录名
     * @param Password Pixiv帐号密码
     * @throws IOException 当登录连接出现异常时抛出
     * @deprecated {@link PixivSession#Login(String, String)} 已经废除, 故本方法不可用
     */
    public Pixiv(String PixivID, String Password) throws IOException {
        this(PixivID, Password, null);
    }

    /**
     * 使用帐号密码登录Pixiv
     * @param PixivID Pixiv账户登录名
     * @param Password Pixiv帐号密码
     * @param proxy 代理设置
     * @throws IOException 当登录连接出现异常时抛出
     * @deprecated {@link PixivSession#Login(String, String)} 已经废除, 故本方法不可用
     */
    public Pixiv(String PixivID, String Password, HttpHost proxy) throws IOException {
        PixivSession pixivSession = new PixivSession(proxy, null);
        if(pixivSession.Login(PixivID, Password)){
            System.out.println("P站登录成功!");
        }else{
            System.out.println("P站登录失败!错误信息: " + pixivSession.getErrMsg());
            throw new RuntimeException(pixivSession.getErrMsg());
        }
        //httpClient = pixivSession.getHttpClient();
        httpClient = HttpClientBuilder.create()
                .setDefaultCookieStore(pixivSession.getCookieStore())
                .build();
    }

    /**
     * 获取首页推荐列表
     * @return 首页推荐列表, 一个Map对应一个推荐项, 使用<code>ATTR_</code>开头常量访问即可
     * @throws IOException
     */
    public List<Map<String, String>> getRecommend() throws IOException {
        HttpGet getRequest = new HttpGet(PixivURL.PIXIV_INDEX_URL);
        HttpResponse response = httpClient.execute(getRequest);
        String pageAsXML = EntityUtils.toString(response.getEntity(),"utf-8");

        //获取推荐图列表(li)
        //System.out.println(pageAsXML);

        Document document = Jsoup.parse(pageAsXML);

        List<String> links  = document.select("._image-items.gtm-illust-recommend-zone>li>.gtm-illust-recommend-thumbnail-link").eachAttr("href");
        List<String> illustId  = document.select("._image-items.gtm-illust-recommend-zone>li>.gtm-illust-recommend-thumbnail-link").eachAttr("data-gtm-recommend-illust-id");
        List<String> title = document.select("._image-items.gtm-illust-recommend-zone>li>.gtm-illust-recommend-title>h1").eachAttr("title");
        List<String> authorName = document.select("._image-items.gtm-illust-recommend-zone>li>.gtm-illust-recommend-user-name").eachText();
        List<String> authorId = document.select("._image-items.gtm-illust-recommend-zone>li>.gtm-illust-recommend-user-name").eachAttr("data-user_id");

        List<Map<String, String>> recommendList = new ArrayList<>();
        for(int i = 0; i < links.size(); i++){
            //System.out.println(links.get(i));
            Map<String, String> info = new HashMap<>();
            info.put(ATTR_LINK, PixivURL.PIXIV_INDEX_URL + links.get(i));
            info.put(ATTR_ILLUST_ID, illustId.get(i));
            info.put(ATTR_TITLE, title.get(i));
            info.put(ATTR_AUTHOR_NAME, authorName.get(i));
            info.put(ATTR_AUTHOR_ID, authorId.get(i));
            recommendList.add(info);
        }
        return recommendList;
    }

    public String[] getAllDownloadLink(int illustID) throws IOException {
        HttpGet illustPage = new HttpGet(PixivURL.PIXIV_ILLUST_API_URL.replaceAll("\\{illustId}", String.valueOf(illustID)));
        HttpResponse response = httpClient.execute(illustPage);
        String pageAsXML = EntityUtils.toString(response.getEntity(),"utf-8");
        //System.out.println(pageAsXML);
        JsonObject resultObj = (JsonObject) new JsonParser().parse(pageAsXML);
        if(!resultObj.get("error").getAsBoolean()){
            JsonArray bodyArray = resultObj.get("body").getAsJsonArray();
            int length = bodyArray.size();
            String[] result = new String[length];
            for(int i = 0; i < length; i++){
                JsonObject childObj = bodyArray.get(i).getAsJsonObject();
                result[i] = childObj.get("urls").getAsJsonObject().get("original").getAsString();
            }
            return result;
        }else{
            return null;
        }
    }



    /**
     * 下载P站图片
     * @param illustID 插图ID
     * @return 成功返回图片输入流,失败或为多图则返回null
     */
    public InputStream[] downloadIllustImage(int illustID) throws IOException {
        String[] links = getAllDownloadLink(illustID);
        List<InputStream> inputStreamList = new ArrayList<>();
        int count = 1;
        boolean retry = false;
        for(int i = 0; i < links.length; i++){
            try {
                long sleepTime = (new Random().nextInt(4) + 2) * 1000;
                System.out.println("nextTime: " + (float)(sleepTime / 1000));
                Thread.sleep(sleepTime);
            } catch (InterruptedException ignored) {}
            String link = links[i];
            System.out.print("page:" + count++ + "/" + links.length + " ...");
            HttpGet imgGet = new HttpGet(link);
            //关键!如果不加上Referer的话,会返回403
            imgGet.setHeader("Referer", PixivURL.PIXIV_ILLUST_MEDIUM_URL.replaceAll("\\{illustId}", String.valueOf(illustID)));
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(20 * 1000)
                    .setConnectionRequestTimeout(20 * 1000)
                    .setSocketTimeout(30 * 1000)
                    .build();
            imgGet.setConfig(config);
            HttpResponse response;
            try {
                response = httpClient.execute(imgGet);
            }catch(ConnectionPoolTimeoutException e){
                if(retry){
                    retry = false;
                    System.out.println("获取失败,跳过...");
                    continue;
                }
                System.out.println("连接超时,重新获取...");
                retry = true;
                i--;
                continue;
            }
            retry = false;

            ByteArrayOutputStream cacheOutputStream = new ByteArrayOutputStream((int)response.getEntity().getContentLength());
            InputStream content = response.getEntity().getContent();
            int readLength;
            byte[] cache = new byte[4096];
            while((readLength = content.read(cache)) != -1){
                cacheOutputStream.write(cache, 0, readLength);
            }
            byte[] data = cacheOutputStream.toByteArray();
            //System.out.println("读到数据: " + data.length);
            inputStreamList.add(new ByteArrayInputStream(data));
            System.out.println("done!（length: " + response.getEntity().getContentLength() + ")");

        }
        return inputStreamList.toArray(new InputStream[0]);
    }

    /**
     * 下载P站图片
     * @param illustID 插图ID
     * @return 成功返回图片输入流,失败或为多图则返回null
     */
    public InputStream downloadIllustImages(int illustID){
        throw new UnsupportedOperationException();
    }

    /**
     * 通过解析插图详情页获取
     * - 插图标题
     * - 插图作者(及其UserId)
     * - 插图上传时间
     * - 插图标签(原始标签)
     * ...
     * @return 成功返回IllustInfo对象,失败返回null
     */
    public IllustInfo[] getIllustInfo(int[] illustIDs) throws IOException {
        //获取Api
        HttpGet apiRequest = new HttpGet(PixivURL.getPixivIllustInfoAPI(illustIDs));
        HttpResponse response = httpClient.execute(apiRequest);
        String resultText = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        System.out.println(resultText);
        JsonObject resultObj = ((JsonObject) new JsonParser().parse(resultText));
        if(resultObj.get("error").getAsBoolean()){
            System.err.println("获取失败!");
            return null;
        }
        List<IllustInfo> illustInfoList = new ArrayList<>();
        JsonArray illustArray = resultObj.get("body").getAsJsonObject().get("illusts").getAsJsonArray();
        illustArray.forEach(jsonElement -> {
            JsonObject illustInfoObj = jsonElement.getAsJsonObject();
            JsonArray tagsArray = illustInfoObj.get("tags").getAsJsonArray();
            String[] tags = new String[tagsArray.size()];
            for(int i = 0; i < tags.length; i++){
                tags[i] = tagsArray.get(i).getAsString();
            }
            //TODO: 通过不需要作者id就能获取图片信息的api无法获取图片尺寸
            IllustInfo illustInfo = new IllustInfo(
                    illustInfoObj.get("workId").getAsInt(),
                    illustInfoObj.get("title").getAsString(),
                    null,
                    tags,
                    -1,
                    -1,
                    illustInfoObj.get("userName").getAsString(),
                    illustInfoObj.get("userId").getAsInt()
            );
        });
        return null;
    }

    /**
     * 获取指定用户的所有插画
     */
    public void getUserAllIllustTest() {

    }


}
