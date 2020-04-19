package net.lamgc.cgj.bot;

import com.google.gson.JsonObject;
import net.lamgc.cgj.pixiv.PixivDownload;
import net.lamgc.cgj.pixiv.PixivURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 推荐作品发送器
 */
public class RandomRankingArtworksSender extends AutoSender {

    private final Logger log;
    private final int rankingStart;
    private final int rankingStop;
    private final PixivDownload.PageQuality quality;

    /**
     * 构造一个推荐作品发送器
     * @param messageSender 消息发送器
     * @param rankingStart 排行榜开始范围(从1开始, 名次)
     * @param rankingStop 排名榜结束范围(包括该名次)
     * @param quality 图片质量, 详见{@link net.lamgc.cgj.pixiv.PixivDownload.PageQuality}
     */
    public RandomRankingArtworksSender(MessageSender messageSender, int rankingStart, int rankingStop, PixivDownload.PageQuality quality) {
        super(messageSender);
        log = LoggerFactory.getLogger("RecommendArtworksSender@" + Integer.toHexString(this.hashCode()));
        this.rankingStart = rankingStart > 0 ? rankingStart : 1;
        this.rankingStop = rankingStop > 0 ? rankingStop : 150;
        this.quality = quality == null ? PixivDownload.PageQuality.REGULAR : quality;
    }

    @Override
    public void send() {
        Date queryDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(queryDate);
        if(calendar.get(Calendar.HOUR_OF_DAY) < 12) {
            calendar.add(Calendar.DAY_OF_YEAR, -2);
        } else {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }
        queryDate = calendar.getTime();

        int selectRanking = rankingStart + new Random().nextInt(rankingStop + 1);
        try {
            List<JsonObject> rankingList = BotCommandProcess.getRankingInfoByCache(
                    PixivURL.RankingContentType.TYPE_ILLUST,
                    PixivURL.RankingMode.MODE_DAILY,
                    queryDate,
                    selectRanking,
                    1, false);

            log.info("RankingResult.size: {}", rankingList.size());
            if(rankingList.size() != 1) {
                log.error("排行榜选取失败!(获取到了多个结果)");
                return;
            }

            JsonObject rankingInfo = rankingList.get(0);
            int illustId = rankingInfo.get("illust_id").getAsInt();
            if(BotCommandProcess.isNoSafe(illustId, BotCommandProcess.globalProp, false)) {
                log.warn("作品为r18作品, 取消本次发送.");
                return;
            }

            StringBuilder message = new StringBuilder();
            message.append("#美图推送 - 今日排行榜 第 ").append(rankingInfo.get("rank").getAsInt()).append(" 名\n");
            message.append("标题：").append(rankingInfo.get("title").getAsString()).append("(").append(illustId).append(")\n");
            message.append("作者：").append(rankingInfo.get("user_name").getAsString()).append("\n");
            message.append(BotCommandProcess.getImageById(illustId, quality, 1));

            getMessageSender().sendMessage(message.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
