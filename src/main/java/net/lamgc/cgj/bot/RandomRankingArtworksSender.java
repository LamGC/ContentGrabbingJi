package net.lamgc.cgj.bot;

import com.google.gson.JsonObject;
import net.lamgc.cgj.bot.cache.CacheStoreCentral;
import net.lamgc.cgj.bot.message.MessageSender;
import net.lamgc.cgj.pixiv.PixivDownload;
import net.lamgc.cgj.pixiv.PixivURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 推荐作品发送器
 */
public class RandomRankingArtworksSender extends AutoSender {

    private final Logger log;
    private final long groupId;
    private final int rankingStart;
    private final int rankingStop;
    private final PixivURL.RankingMode mode;
    private final PixivURL.RankingContentType contentType;
    private final PixivDownload.PageQuality quality;

    /**
     * 构造一个推荐作品发送器
     * @param messageSender 消息发送器
     * @param rankingStart 排行榜开始范围(从1开始, 名次)，如传入0或负数则为默认值，默认为1
     * @param rankingStop 排名榜结束范围(包括该名次)，如传入0或负数则为默认值，默认为150
     * @param mode 排行榜模式
     * @param contentType 排行榜内容类型
     * @param quality 图片质量, 详见{@link PixivDownload.PageQuality}
     * @throws IndexOutOfBoundsException 当 rankingStart > rankingStop时抛出
     */
    public RandomRankingArtworksSender(
            MessageSender messageSender,
            int rankingStart,
            int rankingStop,
            PixivURL.RankingMode mode,
            PixivURL.RankingContentType contentType,
            PixivDownload.PageQuality quality) {
        this(messageSender, 0, rankingStart, rankingStop, mode, contentType, quality);
    }

    /**
     * 构造一个推荐作品发送器
     * @param messageSender 消息发送器
     * @param groupId 群组Id, 如果发送目标为群组, 则可设置群组Id, 以使用群组配置.
     * @param rankingStart 排行榜开始范围(从1开始, 名次)，如传入0或负数则为默认值，默认为1
     * @param rankingStop 排名榜结束范围(包括该名次)，如传入0或负数则为默认值，默认为150
     * @param mode 排行榜模式
     * @param contentType 排行榜内容类型
     * @param quality 图片质量, 详见{@link PixivDownload.PageQuality}
     * @throws IndexOutOfBoundsException 当 rankingStart > rankingStop时抛出
     */
    public RandomRankingArtworksSender(
            MessageSender messageSender,
            long groupId,
            int rankingStart,
            int rankingStop,
            PixivURL.RankingMode mode,
            PixivURL.RankingContentType contentType,
            PixivDownload.PageQuality quality) {
        super(messageSender);
        this.groupId = groupId;
        this.mode = mode;
        this.contentType = contentType;
        log = LoggerFactory.getLogger(this.toString());
        this.rankingStart = rankingStart > 0 ? rankingStart : 1;
        this.rankingStop = rankingStop > 0 ? rankingStop : 150;
        if(this.rankingStart > this.rankingStop) {
            throw new IndexOutOfBoundsException("rankingStart=" + this.rankingStart + ", rankingStop=" + this.rankingStop);
        }
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

        int selectRanking = rankingStart + new Random().nextInt(rankingStop - rankingStart + 1);
        try {
            List<JsonObject> rankingList = CacheStoreCentral.getCentral().getRankingInfoByCache(
                    contentType,
                    mode,
                    queryDate,
                    selectRanking,
                    1, false);

            log.debug("RankingResult.size: {}", rankingList.size());
            if(rankingList.size() != 1) {
                log.error("排行榜选取失败!(获取到了多个结果)");
                return;
            }

            JsonObject rankingInfo = rankingList.get(0);
            int illustId = rankingInfo.get("illust_id").getAsInt();
            if(BotCommandProcess.isNoSafe(illustId,
                    SettingProperties.getProperties(groupId), false)) {
                log.warn("作品为r18作品, 取消本次发送.");
                return;
            } else if(BotCommandProcess.isReported(illustId)) {
                log.warn("作品Id {} 被报告, 正在等待审核, 跳过该作品.", illustId);
                return;
            }

            String message = "#美图推送 - 今日排行榜 第 " + rankingInfo.get("rank").getAsInt() + " 名\n" +
                    "标题：" + rankingInfo.get("title").getAsString() + "(" + illustId + ")\n" +
                    "作者：" + rankingInfo.get("user_name").getAsString() + "\n" +
                    CacheStoreCentral.getCentral().getImageById(0, illustId, quality, 1) +
                    "\n如有不当作品，可使用\".cgj report -id " + illustId + "\"向色图姬反馈。";
            getMessageSender().sendMessage(message);
        } catch (Exception e) {
            log.error("发送随机作品时发生异常", e);
        }
    }
}
