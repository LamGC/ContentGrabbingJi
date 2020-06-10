package net.lamgc.cgj.bot.framework.mirai;

import net.mamoe.mirai.utils.MiraiLogger;
import net.mamoe.mirai.utils.MiraiLoggerPlatformBase;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * MiraiLoggerToSlf4jLogger适配器
 * <p>该Logger通过Slf4j的Marker进行标识, loggerName为{@code mirai.[identity]}</p>
 * <p>由于适配器适配方式的原因, 日志输出的调用信息将不可用(调用指向了适配器内的方法);</p>
 */
public class MiraiToSlf4jLoggerAdapter extends MiraiLoggerPlatformBase {
    
    private final static Marker marker = MarkerFactory.getMarker("mirai");

    private final Logger logger;

    private final String identity;

    public MiraiToSlf4jLoggerAdapter(String identity) {
        this.identity = identity;
        this.logger = LoggerFactory.getLogger("mirai." + identity);
    }

    @Override
    protected void debug0(@Nullable String s) {
        logger.debug(marker, s);
    }

    @Override
    protected void debug0(@Nullable String s, @Nullable Throwable throwable) {
        logger.debug(marker, s, throwable);
    }

    @Override
    protected void error0(@Nullable String s) {
        logger.error(marker, s);
    }

    @Override
    protected void error0(@Nullable String s, @Nullable Throwable throwable) {
        logger.error(marker, s, throwable);
    }

    @Override
    protected void info0(@Nullable String s) {
        logger.info(marker, s);
    }

    @Override
    protected void info0(@Nullable String s, @Nullable Throwable throwable) {
        logger.info(marker, s, throwable);
    }

    @Override
    protected void verbose0(@Nullable String s) {
        logger.trace(marker, s);
    }

    @Override
    protected void verbose0(@Nullable String s, @Nullable Throwable throwable) {
        logger.trace(marker, s, throwable);
    }

    @Override
    protected void warning0(@Nullable String s) {
        logger.warn(marker, s);
    }

    @Override
    protected void warning0(@Nullable String s, @Nullable Throwable throwable) {
        logger.warn(marker, s, throwable);
    }

    @Nullable
    @Override
    public String getIdentity() {
        if(identity == null) {
            MiraiLogger followerLogger = getFollower();
            return followerLogger == null ? null : followerLogger.getIdentity();
        } else {
            return identity;
        }
    }
}
