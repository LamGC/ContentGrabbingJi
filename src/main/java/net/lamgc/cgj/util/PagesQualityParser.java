package net.lamgc.cgj.util;

import net.lamgc.cgj.pixiv.PixivDownload;
import net.lamgc.cgj.pixiv.PixivURL;
import net.lamgc.utils.base.runner.StringParameterParser;

public class PagesQualityParser implements StringParameterParser<PixivDownload.PageQuality> {

    @Override
    public PixivDownload.PageQuality parse(String strValue) throws Exception {
        return PixivDownload.PageQuality.valueOf(strValue.toUpperCase());
    }

    @Override
    public PixivDownload.PageQuality defaultValue() {
        return PixivDownload.PageQuality.ORIGINAL;
    }
}
