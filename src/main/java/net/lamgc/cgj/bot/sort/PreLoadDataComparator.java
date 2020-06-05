package net.lamgc.cgj.bot.sort;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.lamgc.cgj.bot.BotCommandProcess;
import net.lamgc.cgj.bot.cache.CacheStoreCentral;

import java.io.IOException;
import java.util.Comparator;

/**
 * 收藏数比较器
 */
public class PreLoadDataComparator implements Comparator<JsonElement> {

    private final Attribute attribute;

    public PreLoadDataComparator(Attribute attribute) {
        this.attribute = attribute;
    }

    @Override
    public int compare(JsonElement o1, JsonElement o2) {
        if(!o1.getAsJsonObject().has("illustId") || !o2.getAsJsonObject().has("illustId")) {
            if(o1.getAsJsonObject().has("illustId")) {
                return 1;
            } else if(o2.getAsJsonObject().has("illustId")) {
                return -1;
            } else {
                return 0;
            }
        }
        try {
            JsonObject illustPreLoadData1 =
                    CacheStoreCentral.getIllustPreLoadData(o1.getAsJsonObject().get("illustId").getAsInt(), false);
            JsonObject illustPreLoadData2 =
                    CacheStoreCentral.getIllustPreLoadData(o2.getAsJsonObject().get("illustId").getAsInt(), false);
            return Integer.compare(
                    illustPreLoadData2.get(attribute.attrName).getAsInt(),
                    illustPreLoadData1.get(attribute.attrName).getAsInt());
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public enum Attribute {
        /**
         * 按点赞数排序
         */
        LIKE("likeCount"),

        /**
         * 按页面数排序
         */
        PAGE("pageCount"),

        /**
         * 按收藏数排序
         */
        BOOKMARK("bookmarkCount"),

        /**
         * 按评论数排序
         */
        COMMENT("commentCount"),

        /**
         * 不明
         */
        RESPONSE("responseCount"),

        /**
         * 按查看次数排序
         */
        VIEW("viewCount"),
        ;

        public final String attrName;

        Attribute(String attrName) {
            this.attrName = attrName;
        }
    }


}
