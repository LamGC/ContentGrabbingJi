package net.lamgc.cgj.bot;

import net.lamgc.cgj.bot.event.BotEventHandler;
import net.lamgc.cgj.bot.event.VirtualLoadMessageEvent;
import net.lamgc.cgj.pixiv.RankingContentType;
import net.lamgc.cgj.pixiv.RankingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class RankingUpdateTimer {

    private final Timer timer = new Timer("PixivRankingUpdate@" + Integer.toHexString(this.hashCode()), true);
    private final Logger log = LoggerFactory.getLogger(this.toString());

    /**
     * 启动定时任务.
     * 本方法在设置后立即返回
     * @param firstRunDate 首次运行时间, 只需要设置日期, 时间为自动设置.
     */
    public void schedule(Date firstRunDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(firstRunDate == null ? new Date() : firstRunDate);
        LocalDate currentLocalDate = LocalDate.now();
        if(cal.get(Calendar.DAY_OF_YEAR) < currentLocalDate.getDayOfYear() || (
                cal.get(Calendar.DAY_OF_YEAR) == currentLocalDate.getDayOfYear() &&
                        (cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE) >= 690))) {
            cal.set(Calendar.DAY_OF_YEAR, currentLocalDate.getDayOfYear() + 1);
        }
        cal.set(Calendar.HOUR_OF_DAY, 11);
        cal.set(Calendar.MINUTE, 30);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        long delay = cal.getTime().getTime() - System.currentTimeMillis();
        log.warn("已设置排行榜定时更新, 首次运行时间: {} ({}min)", cal.getTime(), delay / 1000 / 60);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                now(null);
            }
        }, delay, 86400000); // 1 Day
    }

    public void now(Date queryDate) {
        log.warn("当前时间 {}, 定时任务开始执行...", new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(queryDate == null ? new Date() : queryDate);

        LocalDate currentLocalDate = LocalDate.now();
        if(calendar.get(Calendar.DAY_OF_YEAR) == currentLocalDate.getDayOfYear() ||
                calendar.get(Calendar.DAY_OF_YEAR) == currentLocalDate.getDayOfYear() - 1) {
            if(calendar.get(Calendar.HOUR_OF_DAY) < 11) {
                calendar.add(Calendar.DAY_OF_YEAR, -2);
            } else {
                calendar.add(Calendar.DAY_OF_YEAR, -1);
            }
        }

        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        log.info("正在获取 {} 期排行榜数据...", calendar.getTime());
        for (RankingMode rankingMode : RankingMode.values()) {
            for (RankingContentType contentType : RankingContentType.values()) {
                if(!contentType.isSupportedMode(rankingMode)) {
                    log.debug("不支持的类型, 填空值跳过...(类型: {}.{})", rankingMode.name(), contentType.name());
                }
                log.info("当前排行榜类型: {}.{}, 正在更新...", rankingMode.name(), contentType.name());
                BotEventHandler.executeMessageEvent(new VirtualLoadMessageEvent(0,0,
                                ".cgj ranking -type=" + contentType.name() +
                                        " -mode=" + rankingMode.name() + " -force -date " + dateStr));
                log.info("排行榜 {}.{} 负载指令已投递.", rankingMode.name(), contentType.name());
            }
        }
        log.warn("定时任务更新完成.");
    }

    /**
     * 取消任务.
     */
    public void stop() {
        timer.cancel();
        log.warn("排行榜更新任务已取消.");
    }

}
