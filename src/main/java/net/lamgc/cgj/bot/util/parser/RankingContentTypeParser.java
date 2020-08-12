package net.lamgc.cgj.bot.util.parser;

import net.lamgc.cgj.pixiv.RankingContentType;
import net.lamgc.utils.base.runner.StringParameterParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RankingContentTypeParser implements StringParameterParser<RankingContentType> {

    private final static Logger log = LoggerFactory.getLogger(RankingContentTypeParser.class);

    @Override
    public RankingContentType parse(String strValue) {
        try {
            if(strValue.toUpperCase().startsWith("TYPE_")) {
                return RankingContentType.valueOf(strValue.toUpperCase());
            }
            return RankingContentType.valueOf("TYPE_" + strValue.toUpperCase());
        } catch(IllegalArgumentException e) {
            log.warn("无效的RankingContentType值: {}", strValue);
            throw e;
        }
    }

    @Override
    public RankingContentType defaultValue() {
        return RankingContentType.TYPE_ALL;
    }
}
