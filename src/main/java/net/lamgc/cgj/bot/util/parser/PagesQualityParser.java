package net.lamgc.cgj.bot.util.parser;

import net.lamgc.cgj.pixiv.PixivDownload;
import net.lamgc.utils.base.runner.StringParameterParser;

public class PagesQualityParser implements StringParameterParser<PixivDownload.PageQuality> {

    @Override
    public PixivDownload.PageQuality parse(String strValue) {
        return PixivDownload.PageQuality.valueOf(strValue.toUpperCase());
    }

    @Override
    public PixivDownload.PageQuality defaultValue() {
        return PixivDownload.PageQuality.ORIGINAL;
    }
}
