/*
 * Copyright (C) 2021  LamGC
 *
 * ContentGrabbingJi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * ContentGrabbingJi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.lamgc.cgj.bot.cache.converter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import net.lamgc.cgj.bot.cache.convert.StringConverter;

import java.util.Objects;

/**
 * Gson 的 JsonArray 转换器.
 * @author LamGC
 */
public class GsonJsonArrayConverter implements StringConverter<JsonArray> {

    private final Gson gson;

    /**
     * 使用默认配置的 Gson 对象创建转换器.
     */
    public GsonJsonArrayConverter() {
        this(new Gson());
    }

    /**
     * 使用自定义的 Gson 对象创建转换器.
     * @param gson Gson 对象
     * @throws NullPointerException 如果 gson 参数传入 null, 则抛出该异常.
     */
    public GsonJsonArrayConverter(Gson gson) {
        this.gson = Objects.requireNonNull(gson);
    }

    @Override
    public String to(JsonArray source) {
        return gson.toJson(source);
    }

    @Override
    public JsonArray from(String target) {
        return gson.fromJson(target, JsonArray.class);
    }
}
