package net.lamgc.cgj;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.lamgc.cgj.bot.boot.ApplicationBoot;
import net.lamgc.cgj.bot.boot.BotGlobal;
import net.lamgc.cgj.bot.framework.cli.ConsoleMain;
import net.lamgc.cgj.bot.framework.coolq.SpringCQApplication;
import net.lamgc.cgj.bot.framework.mirai.MiraiMain;
import net.lamgc.cgj.pixiv.PixivDownload;
import net.lamgc.cgj.pixiv.PixivSearchLinkBuilder;
import net.lamgc.cgj.pixiv.PixivURL;
import net.lamgc.utils.base.runner.Argument;
import net.lamgc.utils.base.runner.ArgumentsRunner;
import net.lamgc.utils.base.runner.Command;
import net.lamgc.utils.encrypt.MessageDigestUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.http.fileupload.util.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Main {

    private final static Logger log = LoggerFactory.getLogger(Main.class);

    private final static File storeDir = new File("store/");

    private static CookieStore cookieStore;

    private static HttpHost proxy;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        if(args.length != 0 && args[0].equalsIgnoreCase("buildpassword")) {
            ArgumentsRunner.run(Main.class, args);
        } else {
            standardStart(args);
        }
    }

    private static void standardStart(String[] args) throws IOException, ClassNotFoundException {
        log.info("ContentGrabbingJi 正在启动...");
        log.debug("Args: {}, LogsPath: {}", Arrays.toString(args), System.getProperty("cgj.logsPath"));
        log.debug("运行目录: {}", System.getProperty("user.dir"));

        ApplicationBoot.initialApplication(args);
        log.debug("botDataDir: {}", System.getProperty("cgj.botDataDir"));

        proxy = BotGlobal.getGlobal().getProxy();
        File cookieStoreFile = new File(BotGlobal.getGlobal().getDataStoreDir(), "cookies.store");
        if(!cookieStoreFile.exists()) {
            log.warn("未找到cookies.store文件, 请检查数据存放目录下是否存在'cookies.store'文件！");
            System.exit(1);
            return;
        }
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cookieStoreFile));
        cookieStore = (CookieStore) ois.readObject();
        BotGlobal.getGlobal().setCookieStore(cookieStore);
        ois.close();
        log.info("已载入CookieStore");

        log.debug("传入参数: {}", Arrays.toString(args));

        ArgumentsRunner.run(Main.class, args);
        System.exit(0);
    }

    @Command
    public static void buildPassword(@Argument(name = "password") String password) {
        System.out.println("Password: " +
                Base64.getEncoder().encodeToString(MessageDigestUtils.encrypt(password.getBytes(),
                MessageDigestUtils.Algorithm.MD5)));
    }

    @Command
    public static void botMode(@Argument(name = "args", force = false) String argsStr) {
        MiraiMain main = new MiraiMain();
        main.init();
        main.close();
    }

    @Command
    public static void consoleMode() throws IOException {
        ConsoleMain.start();
    }

    @Command
    public static void pluginMode(@Argument(name = "args", force = false) String argsStr) {
        new SpringCQApplication().start(argsStr);
    }

    @Command
    public static void collectionDownload() throws IOException {
        PixivDownload pixivDownload = new PixivDownload(Objects.requireNonNull(cookieStore), proxy);
        File outputFile = new File(getStoreDir(), "collection.zip");
        if(!outputFile.exists() && !outputFile.createNewFile()) {
            log.error("文件创建失败: " + outputFile.getAbsolutePath());
        }
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputFile));
        zos.setLevel(9);
        log.info("正在调用方法...");
        pixivDownload.getCollectionAsInputStream(PixivDownload.PageQuality.ORIGINAL, (link, inputStream) -> {
            try {
                ZipEntry entry = new ZipEntry(link.substring(link.lastIndexOf("/") + 1));
                log.info("正在写入: " + entry.getName());
                zos.putNextEntry(entry);
                Streams.copy(inputStream, zos, false);
                zos.flush();
            } catch (IOException e) {
                log.error("写入文件项时发生异常", e);
            }
        });
        log.info("调用完成.");
        zos.close();
    }

    @Command
    public static void getRecommends() throws IOException {
        PixivDownload pixivDownload = new PixivDownload(Objects.requireNonNull(cookieStore), proxy);
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        int id = 1;
        File outputFile = new File(getStoreDir(), "recommends-" + date + "-" + id + ".zip");
        while(outputFile.exists()) {
            id++;
            outputFile = new File(getStoreDir(), "recommends-" + date + "-" + id + ".zip");
        }

        if(!outputFile.createNewFile()) {
            log.error("文件创建失败: " + outputFile.getAbsolutePath());
        }
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputFile));
        zos.setLevel(9);
        log.info("正在调用方法...");
        pixivDownload.getRecommendAsInputStream(PixivDownload.PageQuality.ORIGINAL, (link, inputStream) -> {
            try {
                ZipEntry entry = new ZipEntry(link.substring(link.lastIndexOf("/") + 1));
                log.info("正在写入: " + entry.getName());
                zos.putNextEntry(entry);
                Streams.copy(inputStream, zos, false);
                zos.flush();
                log.info("已成功写入 {}", entry.getName());
            } catch (IOException e) {
                log.error("写入文件项时发生异常", e);
            }
        });
        log.info("调用完成.");
        zos.close();
    }

    @Command
    public static void getRankingIllust(@Argument(name = "range", force = false, defaultValue = "100") int range,
                                        @Argument(name = "mode", force = false) String mode,
                                        @Argument(name = "content", force = false) String content,
                                        @Argument(name = "queryTime", force = false) String queryTime) throws IOException, ParseException {
        PixivDownload pixivDownload = new PixivDownload(cookieStore, proxy);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date queryDate;
        String date;
        if (queryTime == null) {
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(new Date());
            gregorianCalendar.add(Calendar.DATE, -1);
            queryDate = gregorianCalendar.getTime();
        } else {
            queryDate = format.parse(queryTime);
        }

        date = format.format(queryDate);

        log.info("查询时间: {}", date);
        PixivURL.RankingMode rankingMode = PixivURL.RankingMode.MODE_DAILY;
        PixivURL.RankingContentType contentType = null;
        if(mode != null) {
            try {
                rankingMode = PixivURL.RankingMode.valueOf(mode);
            } catch (IllegalArgumentException e) {
                log.warn("不支持的RankingMode: {}", mode);
            }
        }
        if(content != null) {
            try {
                contentType = PixivURL.RankingContentType.valueOf(content);
            } catch (IllegalArgumentException e) {
                log.warn("不支持的RankingContentType: {}", content);
            }
        }

        int id = 1;
        File outputFile = new File(getStoreDir(), "ranking" + rankingMode.modeParam + "-" + date + "-" + id + ".zip");
        while(outputFile.exists()) {
            id++;
            outputFile = new File(getStoreDir(), "ranking" + rankingMode.modeParam + "-" + date + "-" + id + ".zip");
        }

        if(!outputFile.createNewFile()) {
            log.error("文件创建失败: " + outputFile.getAbsolutePath());
            return;
        }
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputFile));
        zos.setLevel(9);

        log.info("正在调用方法...");
        try {
            pixivDownload.getRankingAsInputStream(contentType, rankingMode, queryDate, range, PixivDownload.PageQuality.ORIGINAL, (rank, link, rankInfo, inputStream) -> {
                try {
                    ZipEntry entry = new ZipEntry("Rank" + rank + "-" + link.substring(link.lastIndexOf("/") + 1));
                    entry.setComment(rankInfo.toString());
                    log.info("正在写入: " + entry.getName());
                    zos.putNextEntry(entry);
                    Streams.copy(inputStream, zos, false);
                    zos.flush();
                    log.info("已成功写入 {}", entry.getName());
                } catch (IOException e) {
                    log.error("写入文件项时发生异常", e);
                }
            });
        } finally {
            zos.finish();
            zos.flush();
            zos.close();
        }
        log.info("调用完成.");
    }

    @Command
    public static void search(
            @Argument(name = "content") String content,
            @Argument(name = "type", force = false) String type,
            @Argument(name = "area", force = false) String area,
            @Argument(name = "includeKeywords", force = false) String includeKeywords,
            @Argument(name = "excludeKeywords", force = false) String excludeKeywords,
            @Argument(name = "contentOption", force = false) String contentOption
    ) throws IOException {
        PixivSearchLinkBuilder searchBuilder = new PixivSearchLinkBuilder(Strings.isNullOrEmpty(content) ? "" : content);
        if (type != null) {
            try {
                searchBuilder.setSearchType(PixivSearchLinkBuilder.SearchType.valueOf(type.toUpperCase()));
            } catch (IllegalArgumentException e) {
                log.warn("不支持的SearchType: {}", type);
            }
        }
        if(area != null) {
            try {
                searchBuilder.setSearchArea(PixivSearchLinkBuilder.SearchArea.valueOf(area));
            } catch (IllegalArgumentException e) {
                log.warn("不支持的SearchArea: {}", area);
            }
        }
        if(contentOption != null) {
            try {
                searchBuilder.setSearchContentOption(PixivSearchLinkBuilder.SearchContentOption.valueOf(contentOption));
            } catch (IllegalArgumentException e) {
                log.warn("不支持的SearchContentOption: {}", contentOption);
            }
        }

        if(!Strings.isNullOrEmpty(includeKeywords)) {
            for (String keyword : includeKeywords.split(";")) {
                searchBuilder.removeExcludeKeyword(keyword);
                searchBuilder.addIncludeKeyword(keyword);
                log.info("已添加关键字: {}", keyword);
            }
        }
        if(!Strings.isNullOrEmpty(excludeKeywords)) {
            for (String keyword : excludeKeywords.split(";")) {
                searchBuilder.removeIncludeKeyword(keyword);
                searchBuilder.addExcludeKeyword(keyword);
                log.info("已添加排除关键字: {}", keyword);
            }
        }

        log.info("正在搜索作品, 条件: {}", searchBuilder.getSearchCondition());

        String requestUrl = searchBuilder.buildURL();
        log.info("RequestUrl: {}", requestUrl);
        PixivDownload pixivDownload = new PixivDownload(cookieStore, proxy);
        HttpGet httpGetRequest = pixivDownload.createHttpGetRequest(requestUrl);
        HttpResponse response = pixivDownload.getHttpClient().execute(httpGetRequest);

        String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        log.info("ResponseBody: {}", responseBody);
        JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
        if(jsonObject.get("error").getAsBoolean()) {
            log.error("接口请求错误, 错误信息: {}", jsonObject.get("message").getAsString());
            return;
        }

        JsonObject resultBody = jsonObject.getAsJsonObject("body");

        for(PixivSearchLinkBuilder.SearchArea searchArea : PixivSearchLinkBuilder.SearchArea.values()) {
            if(!resultBody.has(searchArea.jsonKey) || resultBody.getAsJsonObject(searchArea.jsonKey).getAsJsonArray("data").size() == 0) {
                //log.info("返回数据不包含 {}", searchArea.jsonKey);
                continue;
            }
            JsonArray illustsArray = resultBody
                    .getAsJsonObject(searchArea.jsonKey).getAsJsonArray("data");
            log.info("已找到与 {} 相关插图信息({})：", content, searchArea.name().toLowerCase());
            int count = 1;
            for (JsonElement jsonElement : illustsArray) {
                JsonObject illustObj = jsonElement.getAsJsonObject();
                if(!illustObj.has("illustId")) {
                    continue;
                }
                int illustId = illustObj.get("illustId").getAsInt();
                StringBuilder builder = new StringBuilder("[");
                illustObj.get("tags").getAsJsonArray().forEach(el -> builder.append(el.getAsString()).append(", "));
                builder.replace(builder.length() - 2, builder.length(), "]");
                log.info("{} ({} / {})\n\t作品id: {}, \n\t作者名(作者id): {} ({}), \n\t作品标题: {}, \n\t作品Tags: {}, \n\t作品链接: {}",
                        searchArea.name(),
                        count++,
                        illustsArray.size(),
                        illustId,
                        illustObj.get("userName").getAsString(),
                        illustObj.get("userId").getAsInt(),
                        illustObj.get("illustTitle").getAsString(),
                        builder,
                        PixivURL.getPixivRefererLink(illustId)
                );
            }
        }
    }


    @Command(defaultCommand = true)
    public static void testRun() {
        log.info("这里啥都没有哟w");
    }

    private static File getStoreDir() {
        if(!storeDir.exists() && !storeDir.mkdirs()) {
            log.error("创建文件夹失败!");
        }
        return storeDir;
    }
    
}
