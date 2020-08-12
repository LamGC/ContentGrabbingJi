package net.lamgc.cgj.bot.util.parser;

import net.lamgc.cgj.pixiv.RankingMode;
import net.lamgc.utils.base.runner.StringParameterParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RankingModeParser implements StringParameterParser<RankingMode> {

    private final static Logger log = LoggerFactory.getLogger(RankingModeParser.class);

    @Override
    public RankingMode parse(String strValue) {
        try {
            if(strValue.toUpperCase().startsWith("MODE_")) {
                return RankingMode.valueOf(strValue.toUpperCase());
            }
            return RankingMode.valueOf("MODE_" + strValue.toUpperCase());
        } catch(IllegalArgumentException e) {
            log.warn("无效的RankingMode值: {}", strValue);
            throw e;
        }
    }

    @Override
    public RankingMode defaultValue() {
        return RankingMode.MODE_DAILY;
    }
}
