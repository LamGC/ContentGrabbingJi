package net.lamgc.cgj.pixiv;

/**
 * 排名榜模式
 */
public enum RankingMode{
    /**
     * 每天
     */
    MODE_DAILY("daily"),
    /**
     * 每周
     */
    MODE_WEEKLY("weekly"),
    /**
     * 每月
     */
    MODE_MONTHLY("monthly"),
    /**
     * 新人
     */
    MODE_ROOKIE("rookie"),
    /**
     * 原创
     */
    MODE_ORIGINAL("original"),
    /**
     * 受男性喜欢
     */
    MODE_MALE("male"),
    /**
     * 受女性喜欢
     */
    MODE_FEMALE("female"),

    /**
     * 每天 - R18
     */
    MODE_DAILY_R18("daily_r18"),
    /**
     * 每周 - R18
     */
    MODE_WEEKLY_R18("weekly_r18"),
    /**
     * 受男性喜欢 - R18
     */
    MODE_MALE_R18("male_r18"),
    /**
     * 受女性喜欢 - R18
     */
    MODE_FEMALE_R18("female_r18"),
    ;
    public String modeParam;

    RankingMode(String modeParamName){
        this.modeParam = modeParamName;
    }
}