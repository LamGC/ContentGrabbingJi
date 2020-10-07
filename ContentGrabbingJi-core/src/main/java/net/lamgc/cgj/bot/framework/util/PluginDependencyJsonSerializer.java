/*
 * Copyright (C) 2020  LamGC
 *
 * ContentGrabbingJi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * ContentGrabbingJi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.lamgc.cgj.bot.framework.util;

import com.google.gson.*;
import org.pf4j.PluginDependency;

import java.lang.reflect.Type;

/**
 * PluginDependency Json 序列化/反序列化 工具.
 * @see PluginDependency
 * @author LamGC
 */
public class PluginDependencyJsonSerializer implements JsonSerializer<PluginDependency>, JsonDeserializer<PluginDependency> {
    private final static String PLUGIN_VERSION_SUPPORT_ALL = "*";

    @Override
    public JsonElement serialize(PluginDependency src, Type typeOfSrc, JsonSerializationContext context) {
        StringBuilder builder = new StringBuilder(src.getPluginId());
        String pluginVersionSupport = src.getPluginVersionSupport();
        if (src.isOptional()) {
            builder.append('?');
        }
        if (src.getPluginVersionSupport() != null || !PLUGIN_VERSION_SUPPORT_ALL.equals(pluginVersionSupport)) {
            builder.append('@').append(pluginVersionSupport);
        }
        return new JsonPrimitive(builder.toString());
    }

    @Override
    public PluginDependency deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonPrimitive()) {
            throw new JsonParseException("Only JsonPrimitive types are supported for conversion");
        }

        JsonPrimitive primitive = json.getAsJsonPrimitive();
        if (!primitive.isString()) {
            throw new JsonParseException("Only String is supported");
        }

        return new PluginDependency(primitive.getAsString());
    }
}
