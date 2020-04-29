package net.lamgc.cgj.pixiv;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.gifencoder.GifEncoder;
import com.squareup.gifencoder.Image;
import com.squareup.gifencoder.ImageOptions;
import io.netty.handler.codec.http.HttpHeaderNames;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Pixiv动图构建器
 */
public final class PixivUgoiraBuilder {

    private final Logger log = LoggerFactory.getLogger(PixivUgoiraBuilder.class.getSimpleName() + "@" + Integer.toHexString(this.hashCode()));

    private final HttpClient httpClient;
    private final JsonObject ugoiraMeta;
    private final int illustId;

    private int height;
    private int width;

    public PixivUgoiraBuilder(HttpClient httpClient, int illustId) throws IOException {
        this.httpClient = Objects.requireNonNull(httpClient);
        log.trace("正在获取动图元数据...");
        HttpGet request = new HttpGet(PixivURL.PIXIV_GET_UGOIRA_META_URL.replaceAll("\\{illustId}", String.valueOf(illustId)));
        log.debug("Request Url: {}", request.getURI());
        HttpResponse response = httpClient.execute(request);
        String bodyStr = EntityUtils.toString(response.getEntity());
        log.debug("JsonBodyStr: {}", bodyStr);
        JsonObject resultObject = new Gson().fromJson(bodyStr, JsonObject.class);
        if(resultObject.get("error").getAsBoolean()) {
            String message = resultObject.get("message").getAsString();
            log.error("获取动图元数据失败!(接口报错: {})", message);
            throw new IOException(message);
        } else if(!resultObject.has("body")) {
            String message = "接口返回数据不存在body属性, 可能接口发生改变!";
            log.error(message);
            throw new IOException(message);
        }
        log.trace("动图元数据获取完成.");

        this.ugoiraMeta = resultObject.getAsJsonObject("body");
        this.illustId = illustId;
    }

    /**
     * 构造一个动图构建器
     * @param httpClient Http客户端对象
     * @param ugoiraMeta 动图元数据
     */
    public PixivUgoiraBuilder(HttpClient httpClient, JsonObject ugoiraMeta) {
        this.httpClient = Objects.requireNonNull(httpClient);
        Objects.requireNonNull(ugoiraMeta);
        if(!ugoiraMeta.get("error").getAsBoolean() && ugoiraMeta.has("body")) {
            this.ugoiraMeta = ugoiraMeta.getAsJsonObject("body");
        } else {
            this.ugoiraMeta = ugoiraMeta;
        }
        String src = this.ugoiraMeta.get("src").getAsString();
        int startIndex = src.lastIndexOf("/");
        illustId = Integer.parseInt(src.substring(startIndex + 1, src.indexOf("_", startIndex)));
        log.debug("IllustId: {}, UgoiraMeta: {}", this.illustId, this.ugoiraMeta);
    }

    public InputStream buildUgoira(boolean original) throws IOException {
        getUgoiraImageSize();
        log.debug("动图尺寸信息: Height: {}, Width: {}", height, width);

        JsonArray frames = ugoiraMeta.getAsJsonArray("frames");

        log.trace("正在获取帧压缩包...");
        HttpGet request = new HttpGet(ugoiraMeta.get(original ? "originalSrc" : "src").getAsString());
        request.addHeader(HttpHeaderNames.REFERER.toString(), PixivURL.getPixivRefererLink(illustId));
        log.trace("发送请求...");
        HttpResponse response = httpClient.execute(request);
        log.trace("请求已发送, 正在处理响应...");
        ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(response.getEntity().getContent(), 64 * 1024));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipEntry entry;
        ByteArrayOutputStream cacheOutputStream = new ByteArrayOutputStream(512);
        HashMap<String, InputStream> frameMap = new HashMap<>(frames.size());
        while((entry = zipInputStream.getNextEntry()) != null) {
            log.trace("ZipEntry {} 正在接收...", entry);
            IOUtils.copy(zipInputStream, cacheOutputStream);
            frameMap.put(entry.getName(), new ByteArrayInputStream(cacheOutputStream.toByteArray()));
            log.trace("ZipEntry {} 已接收完成.", entry);
            cacheOutputStream.reset();
        }


        InputStream firstFrameInput = frameMap.get("000000.jpg");
        BufferedImage firstFrame = ImageIO.read(firstFrameInput);
        firstFrameInput.reset();
        if(width != firstFrame.getWidth() || height != firstFrame.getHeight()) {
            log.warn("动图第一帧实际尺寸与预设尺寸不符, 将调整尺寸为实际尺寸." + "(差距: Width[{}(预设) -> {}(实际)], Height[{}(预设) -> {}(实际)])",
                    width, firstFrame.getWidth(),
                    height, firstFrame.getHeight()
            );
            width = firstFrame.getWidth();
            height = firstFrame.getHeight();
        }

        GifEncoder encoder = new GifEncoder(outputStream, width, height, 0);

        frames.forEach(frame -> {
            JsonObject frameInfo = frame.getAsJsonObject();
            BufferedImage image;
            String frameFileName = frameInfo.get("file").getAsString();
            log.trace("正在插入帧 {}", frameFileName);
            try {
                image = Objects.requireNonNull(ImageIO.read(frameMap.get(frameFileName)));
                int[] rgb = new int[image.getHeight() * image.getWidth()];
                log.debug("FrameName: {}, Height: {}, Width: {}, cacheSize: {}", frameFileName, image.getHeight(), image.getWidth(), rgb.length);
                image.getRGB(0, 0, image.getWidth(), image.getHeight(), rgb, 0, image.getWidth());
                log.trace("帧解析完成, 正在插入...");
                encoder.addImage(Image.fromRgb(rgb, image.getWidth()), new ImageOptions().setDelay(frameInfo.get("delay").getAsLong(), TimeUnit.MILLISECONDS));
                log.trace("帧 {} 插入完成.", frameFileName);
            } catch (IOException e) {
                log.error("解析帧图片数据时发生异常", e);
            }
        });
        encoder.finishEncoding();
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    /**
     * 获取动图图片大小, 并刷新到构建器内的属性
     */
    private void getUgoiraImageSize() throws IOException {
        log.debug("正在从Pixiv获取动图尺寸...");
        HttpGet request = new HttpGet(PixivURL.getPixivIllustInfoAPI(illustId));
        log.debug("Request Url: {}", request.getURI());
        HttpResponse response = httpClient.execute(request);
        String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        log.debug("ResponseBody: {}", responseBody);
        JsonObject resultObject = new Gson().fromJson(responseBody, JsonObject.class);
        if(resultObject.get("error").getAsBoolean()) {
            String message = resultObject.get("message").getAsString();
            log.error("接口返回错误: {}", message);
            throw new IOException(message);
        }

        JsonArray illustsArray = resultObject.getAsJsonObject("body").getAsJsonArray("illusts");
        if(illustsArray.size() != 1) {
            log.error("接口返回空, 查询失败.");
            throw new IOException("指定的illustId查询失败");
        }

        JsonObject illustObject = illustsArray.get(0).getAsJsonObject();
        int resultIllustId = illustObject.get("illustId").getAsInt();
        if(resultIllustId != illustId) {
            log.error("illustId不符合, 查询失败(原illustId: {}, 返回illustId: {})", illustId, resultIllustId);
            throw new IOException("接口返回数据不符合");
        }

        this.height = illustObject.get("height").getAsInt();
        this.width = illustObject.get("width").getAsInt();
        log.debug("动图尺寸获取完成(width: {}, height: {})", width, height);
    }

}
