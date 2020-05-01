package net.lamgc.cgj;

import com.github.monkeywie.proxyee.proxy.ProxyConfig;
import com.github.monkeywie.proxyee.proxy.ProxyType;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.lamgc.cgj.bot.framework.coolq.CQConfig;
import net.lamgc.cgj.bot.framework.mirai.MiraiMain;
import net.lamgc.cgj.pixiv.PixivDownload;
import net.lamgc.cgj.pixiv.PixivSearchBuilder;
import net.lamgc.cgj.pixiv.PixivURL;
import net.lamgc.plps.PixivLoginProxyServer;
import net.lamgc.utils.base.ArgumentsProperties;
import net.lamgc.utils.base.runner.Argument;
import net.lamgc.utils.base.runner.ArgumentsRunner;
import net.lamgc.utils.base.runner.Command;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@SpringBootApplication
public class Main {

    private final static Logger log = LoggerFactory.getLogger("Main");

    private final static File storeDir = new File("store/");

    public static CookieStore cookieStore;

    public static HttpHost proxy;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        log.trace("ContentGrabbingJi 正在启动...");
        log.debug("Args: {}, LogsPath: {}", Arrays.toString(args), System.getProperty("cgj.logsPath"));
        log.debug("运行目录: {}", System.getProperty("user.dir"));
        ArgumentsProperties argsProp = new ArgumentsProperties(args);
        if(argsProp.containsKey("proxy")) {
            URL proxyUrl = new URL(argsProp.getValue("proxy"));
            proxy = new HttpHost(proxyUrl.getHost(), proxyUrl.getPort());
            log.info("已启用Http协议代理：{}", proxy.toHostString());
        } else {
            proxy = null;
        }

        if(!storeDir.exists() && !storeDir.mkdirs()) {
            log.error("创建文件夹失败!");
        }

        // TODO: 需要修改参数名了, 大概改成类似于 workerDir这样的吧
        if(argsProp.containsKey("botDataDir")) {
            log.info("botDataDir: {}", argsProp.getValue("botDataDir"));
            System.setProperty("cgj.botDataDir", argsProp.getValue("botDataDir"));
        } else {
            log.warn("未设置botDataDir, 当前运行目录将作为酷Q机器人所在目录.");
            System.setProperty("cgj.botDataDir", "./");
        }

        if(argsProp.containsKey("redisAddr")) {
            log.info("redisAddress: {}", argsProp.getValue("redisAddr"));
            System.setProperty("cgj.redisAddress", argsProp.getValue("redisAddr"));
        } else {
            log.info("未设置RedisAddress, 将使用默认值连接Redis服务器(127.0.0.1:6379)");
            System.setProperty("cgj.redisAddress", "127.0.0.1");
        }

        File cookieStoreFile = new File(System.getProperty("cgj.botDataDir"), "cookies.store");
        if(!cookieStoreFile.exists()) {
            log.warn("未找到cookies.store文件, 是否启动PixivLoginProxyServer? (yes/no)");
            Scanner scanner = new Scanner(System.in);
            if(scanner.nextLine().equalsIgnoreCase("yes")) {
                startPixivLoginProxyServer();
            } else {
                System.exit(1);
                return;
            }
        }
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cookieStoreFile));
        cookieStore = (CookieStore) ois.readObject();
        ois.close();
        log.info("已载入CookieStore");

        log.debug("传入参数: {}", Arrays.toString(args));

        ArgumentsRunner.run(Main.class, args);
    }

    @Command
    public static void botMode(@Argument(name = "args", force = false) String argsStr) {
        new MiraiMain().init();
    }

    @Command
    public static void pluginMode(@Argument(name = "args", force = false) String argsStr) {
        if(!System.getProperty("cgj.botDataDir").endsWith("\\") && !System.getProperty("cgj.botDataDir").endsWith("/")) {
            System.setProperty("cgj.botDataDir", System.getProperty("cgj.botDataDir") + "/");
        }
        log.info("酷Q机器人根目录: {}", System.getProperty("cgj.botDataDir"));
        CQConfig.init();
        Pattern pattern = Pattern.compile("/\\s*(\".+?\"|[^:\\s])+((\\s*:\\s*(\".+?\"|[^\\s])+)|)|(\".+?\"|[^\"\\s])+");
        Matcher matcher = pattern.matcher(Strings.nullToEmpty(argsStr));
        ArrayList<String> argsList = new ArrayList<>();
        while (matcher.find()) {
            argsList.add(matcher.group());
        }
        String[] args = new String[argsList.size()];
        argsList.toArray(args);
        SpringApplication.run(Main.class, args);
    }

    @Command
    public static void collectionDownload() throws IOException {
        PixivDownload pixivDownload = new PixivDownload(Objects.requireNonNull(cookieStore), proxy);
        File outputFile = new File(storeDir, "collection.zip");
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
                IOUtils.copy(inputStream, zos);
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
        File outputFile = new File(storeDir, "recommends-" + date + "-" + id + ".zip");
        while(outputFile.exists()) {
            id++;
            outputFile = new File(storeDir, "recommends-" + date + "-" + id + ".zip");
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
                IOUtils.copy(inputStream, zos);
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
        File outputFile = new File(storeDir, "ranking" + rankingMode.modeParam + "-" + date + "-" + id + ".zip");
        while(outputFile.exists()) {
            id++;
            outputFile = new File(storeDir, "ranking" + rankingMode.modeParam + "-" + date + "-" + id + ".zip");
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
                    IOUtils.copy(inputStream, zos);
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
        PixivSearchBuilder searchBuilder = new PixivSearchBuilder(Strings.isNullOrEmpty(content) ? "" : content);
        if (type != null) {
            try {
                searchBuilder.setSearchType(PixivSearchBuilder.SearchType.valueOf(type.toUpperCase()));
            } catch (IllegalArgumentException e) {
                log.warn("不支持的SearchType: {}", type);
            }
        }
        if(area != null) {
            try {
                searchBuilder.setSearchArea(PixivSearchBuilder.SearchArea.valueOf(area));
            } catch (IllegalArgumentException e) {
                log.warn("不支持的SearchArea: {}", area);
            }
        }
        if(contentOption != null) {
            try {
                searchBuilder.setSearchContentOption(PixivSearchBuilder.SearchContentOption.valueOf(contentOption));
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

        for(PixivSearchBuilder.SearchArea searchArea : PixivSearchBuilder.SearchArea.values()) {
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

    private static void saveCookieStoreToFile() throws IOException {
        log.info("正在保存CookieStore...");
        File outputFile = new File(System.getProperty("cgj.botDataDir"), "cookies.store");
        if(!outputFile.exists() && !outputFile.delete() && !outputFile.createNewFile()){
            log.error("保存CookieStore失败.");
            return;
        }
        ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(outputFile));
        stream.writeObject(cookieStore);
        stream.flush();
        stream.close();
        log.info("CookieStore保存成功.");
    }

    private static void startPixivLoginProxyServer(){
        ProxyConfig proxyConfig = null;
        if(proxy != null) {
            proxyConfig = new ProxyConfig(ProxyType.HTTP, proxy.getHostName(), proxy.getPort());
        }
        PixivLoginProxyServer proxyServer = new PixivLoginProxyServer(proxyConfig);
        Thread proxyServerStartThread = new Thread(() -> {
            log.info("启动代理服务器...");
            proxyServer.start(1006);
            log.info("代理服务器已关闭.");
        });
        proxyServerStartThread.setName("LoginProxyServerThread");
        proxyServerStartThread.start();
        //System.console().readLine();
        Scanner scanner = new Scanner(System.in);
        log.info("登录完成后, 使用\"done\"命令结束登录过程.");
        while(true) {
            if (scanner.nextLine().equalsIgnoreCase("done")) {
                log.info("关闭PLPS服务器...");
                proxyServer.close();
                cookieStore = proxyServer.getCookieStore();
                try {
                    log.info("正在保存CookieStore...");
                    saveCookieStoreToFile();
                    log.info("CookieStore保存完成.");
                } catch (IOException e) {
                    log.error("CookieStore保存时发生异常, 本次CookieStore仅可在本次运行使用.", e);
                }
                break;
            } else {
                log.warn("要结束登录过程, 请使用\"done\"命令.");
            }
        }
    }

}
