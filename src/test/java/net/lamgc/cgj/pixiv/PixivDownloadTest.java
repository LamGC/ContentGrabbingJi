package net.lamgc.cgj.pixiv;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Ignore
public class PixivDownloadTest {

    private static CookieStore cookieStore;

    private final static Logger log = LoggerFactory.getLogger("PixivDownloadTest");

    private static HttpHost proxy = new HttpHost("127.0.0.1", 1001);
    
    @BeforeClass
    public static void before() throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("cookies.store")));
        cookieStore = (CookieStore) ois.readObject();
        ois.close();
        log.info("已载入CookieStore");
    }

    @Test
    public void logOutTest() throws IOException {
        PixivDownload pixivDownload = new PixivDownload(Objects.requireNonNull(cookieStore), proxy);
        Assert.assertTrue(pixivDownload.logOut());
    }

    @Test
    public void collectionDownloadTest() throws IOException {
        PixivDownload pixivDownload = new PixivDownload(Objects.requireNonNull(cookieStore), proxy);
        File outputFile = new File("collection.zip");
        if(!outputFile.exists() && !outputFile.createNewFile()) {
            Assert.fail("文件创建失败: " + outputFile.getAbsolutePath());
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

    @Test
    public void getRecommendsTest() throws IOException {
        PixivDownload pixivDownload = new PixivDownload(Objects.requireNonNull(cookieStore), proxy);
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        int id = 1;
        File outputFile = new File("recommends-" + date + "-" + id + ".zip");
        while(outputFile.exists()) {
            id++;
            outputFile = new File("recommends-" + date + "-" + id + ".zip");
        }

        if(!outputFile.createNewFile()) {
            Assert.fail("文件创建失败: " + outputFile.getAbsolutePath());
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

    @Test
    public void getRankingIllustByRangeTest() throws IOException {
        PixivDownload pixivDownload = new PixivDownload(cookieStore, proxy);

        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(new Date());
        gregorianCalendar.add(Calendar.DATE, -2);
        Date queryDate = gregorianCalendar.getTime();
        String date = new SimpleDateFormat("yyyyMMdd").format(queryDate);
        log.info("查询时间: {}", date);


        int id = 1;
        File outputFile = new File("ranking-" + date + "-" + id + ".zip");
        while(outputFile.exists()) {
            id++;
            outputFile = new File("ranking-" + date + "-" + id + ".zip");
        }

        if(!outputFile.createNewFile()) {
            Assert.fail("文件创建失败: " + outputFile.getAbsolutePath());
        }
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputFile));
        zos.setLevel(9);
        log.info("正在调用方法...");
        try {
            pixivDownload.getRankingAsInputStream(null, PixivURL.RankingMode.MODE_DAILY_R18, queryDate, 500, PixivDownload.PageQuality.ORIGINAL, (rank, link, rankInfo, inputStream) -> {
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

    @Test
    public void getRankingIllustTest() throws IOException {
        PixivDownload pixivDownload = new PixivDownload(cookieStore, proxy);

        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(new Date());
        gregorianCalendar.add(Calendar.DATE, -2);
        Date queryDate = gregorianCalendar.getTime();
        String date = new SimpleDateFormat("yyyyMMdd").format(queryDate);
        log.info("查询时间: {}", date);

        int id = 1;
        File outputFile = new File("ranking-" + date + "-" + id + ".zip");
        while(outputFile.exists()) {
            id++;
            outputFile = new File("ranking-" + date + "-" + id + ".zip");
        }

        if(!outputFile.createNewFile()) {
            Assert.fail("文件创建失败: " + outputFile.getAbsolutePath());
        }
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputFile));
        zos.setLevel(9);

        log.info("正在调用方法...");
        try {
            pixivDownload.getRankingAsInputStream(null, null, queryDate, 5, 50, PixivDownload.PageQuality.ORIGINAL, (rank, link, rankInfo, inputStream) -> {
                /*try {

                    ZipEntry entry = new ZipEntry("Rank" + rank + "-" + link.substring(link.lastIndexOf("/") + 1));
                    entry.setComment(rankInfo.toString());
                    log.info("正在写入: " + entry.getName());
                    zos.putNextEntry(entry);
                    IOUtils.copy(inputStream, zos);
                    zos.flush();
                    log.info("已成功写入 {}", entry.getName());
                    inputStream.close();
                } catch (IOException e) {
                    log.error("写入文件项时发生异常", e);
                }*/
                log.info("空操作");
            });
        } finally {
            zos.finish();
            zos.flush();
            zos.close();
        }
        log.info("调用完成.");
    }

    @Test
    public void getIllustPreLoadDataByIdTest() throws IOException {
        log.info(new PixivDownload(cookieStore, proxy).getIllustPreLoadDataById(64076261).toString());
    }

}
