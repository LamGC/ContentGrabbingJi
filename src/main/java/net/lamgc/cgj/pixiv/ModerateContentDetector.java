package net.lamgc.cgj.pixiv;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * 使用ModerateContent服务开发的检测器.<br/>
 * ModerateContent: www.moderatecontent.com
 */
public class ModerateContentDetector implements AdultContentDetector {

    private final static HttpClient httpClient = HttpClientBuilder.create().build();
    private final static Gson gson = new Gson();
    private final String requestUrl;

    private final static String API_URL = "https://www.moderatecontent.com/api/v2?key={key}&url=https://pixiv.cat/";

    /**
     * 创建一个使用ModerateContent鉴黄服务的检测器
     * @param apiKey API密钥
     */
    public ModerateContentDetector(String apiKey) {
        requestUrl = API_URL.replace("{key}", apiKey);
    }

    private JsonObject accessInterface(int illustId, boolean isUgoira, int pageIndex) throws IOException {
        HttpResponse response;
        if(pageIndex <= 0) {
            response = httpClient.execute(new HttpGet(requestUrl + illustId + (isUgoira ? ".gif" : ".jpg")));
        } else {
            response = httpClient.execute(new HttpGet(requestUrl + illustId + "-" + pageIndex + (isUgoira ? ".gif" : ".jpg")));
        }
        if(response.getStatusLine().getStatusCode() != 200) {
            throw new IOException("Http response error: " + response.getStatusLine());
        }

        JsonObject result = gson.fromJson(EntityUtils.toString(response.getEntity()), JsonObject.class);
        if (result.get("error_code").getAsInt() != 0) {
            throw new IOException("Interface result error: " + (result.has("error") ? result.get("error").getAsString() : "(error message is empty)"));
        }
        return result;
    }

    @Override
    public double detect(int illustId, boolean isUgoira, int pageIndex) throws IOException {
        return accessInterface(illustId, isUgoira,  pageIndex).getAsJsonObject("predictions").get("adult").getAsDouble();
    }

    @Override
    public boolean isAdultContent(int illustId, boolean isUgoira, int pageIndex) throws IOException {
        return accessInterface(illustId, isUgoira, pageIndex).get("rating_index").getAsInt() == 3;
    }

    @Override
    public boolean isAdultContent(int illustId, boolean isUgoira, int pageIndex, double threshold) throws IOException {
        return detect(illustId, isUgoira,  pageIndex) >= threshold;
    }
}
