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

package net.lamgc.cgj.bot.framework.util;

import com.google.gson.*;
import net.lamgc.cgj.bot.framework.message.BotCodeDescriptor;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * BotCode 描述对象序列化工具.
 * @see BotCodeDescriptor
 * @author LamGC
 */
public class BotCodeDescriptorJsonSerializer
        implements JsonSerializer<BotCodeDescriptor>, JsonDeserializer<BotCodeDescriptor> {

    private final static String FIELD_PATTERNS = "patterns";

    @Override
    public BotCodeDescriptor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (!json.isJsonObject()) {
            throw new JsonParseException("Not a JsonObject");
        }

        JsonObject descriptorObject = json.getAsJsonObject();
        List<String> patternStrings = new ArrayList<>();
        if (descriptorObject.has(FIELD_PATTERNS) && descriptorObject.get(FIELD_PATTERNS).isJsonArray()) {
            for (JsonElement jsonElement : descriptorObject.getAsJsonArray(FIELD_PATTERNS)) {
                if (!jsonElement.isJsonPrimitive()) {
                    continue;
                }

                JsonPrimitive primitive = jsonElement.getAsJsonPrimitive();
                if (!primitive.isString()) {
                    continue;
                }
                patternStrings.add(primitive.getAsString());
            }
        }

        return new BotCodeDescriptor(patternStrings);
    }

    @Override
    public JsonElement serialize(BotCodeDescriptor src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();

        JsonArray patterns = new JsonArray();
        for (Pattern pattern : src.getPatterns()) {
            patterns.add(pattern.pattern());
        }
        result.add(FIELD_PATTERNS, patterns);

        return result;
    }
}
