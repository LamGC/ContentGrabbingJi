package net.lamgc.cgj.pixiv;

import java.util.Arrays;

/**
 * 排名榜类型
 */
public enum RankingContentType{
    /**
     * 所有类型.
     * 支持所有Mode.
     */
    TYPE_ALL("", RankingMode.values()),
    /**
     * 插画
     * 支持的时间类型: 每天, 每周, 每月, 新人
     */
    TYPE_ILLUST("illust",
            new RankingMode[]{
                    RankingMode.MODE_DAILY,
                    RankingMode.MODE_MONTHLY,
                    RankingMode.MODE_WEEKLY,
                    RankingMode.MODE_ROOKIE,
                    RankingMode.MODE_DAILY_R18,
                    RankingMode.MODE_WEEKLY_R18,
            }
    ),
    /**
     * 动图
     * 支持的时间类型:每天, 每周
     */
    TYPE_UGOIRA("ugoira",
            new RankingMode[]{
                    RankingMode.MODE_DAILY,
                    RankingMode.MODE_WEEKLY,
                    RankingMode.MODE_DAILY_R18,
                    RankingMode.MODE_WEEKLY_R18
            }
    ),
    /**
     * 漫画
     * 支持的时间类型: 每天, 每周, 每月, 新人
     */
    TYPE_MANGA("manga",
            new RankingMode[]{
                    RankingMode.MODE_DAILY,
                    RankingMode.MODE_MONTHLY,
                    RankingMode.MODE_WEEKLY,
                    RankingMode.MODE_ROOKIE,
                    RankingMode.MODE_DAILY_R18,
                    RankingMode.MODE_WEEKLY_R18,
            }
    )
    ;

    String typeName;

    private final RankingMode[] supportedMode;

    RankingContentType(String typeName, RankingMode[] supportedMode){
        this.typeName = typeName;
        this.supportedMode = supportedMode;
    }

    /**
     * 检查指定RankingMode是否支持
     * @param mode 要检查的RankingMode项
     * @return 如果支持返回true
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isSupportedMode(RankingMode mode) {
        return Arrays.binarySearch(supportedMode, mode) >= 0;
    }

}