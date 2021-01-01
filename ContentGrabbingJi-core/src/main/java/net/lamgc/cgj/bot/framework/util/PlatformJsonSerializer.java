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
import net.lamgc.cgj.bot.framework.Platform;

import java.lang.reflect.Type;

/**
 * {@link Platform} 序列化工具.
 * @see Platform
 * @author LamGC
 */
public final class PlatformJsonSerializer implements JsonSerializer<Platform>, JsonDeserializer<Platform> {

    private final static String FIELD_NAME = "name";
    private final static String FIELD_IDENTIFY = "identify";


    @Override
    public Platform deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (!json.isJsonObject()) {
            throw new JsonParseException("Not a JsonObject");
        }

        JsonObject descriptorObject = json.getAsJsonObject();
        if (!descriptorObject.has(FIELD_NAME) || !descriptorObject.has(FIELD_IDENTIFY)) {
            throw new JsonParseException("A required field is missing");
        }
        return new Platform(descriptorObject.get(FIELD_NAME).getAsString(),
                descriptorObject.get(FIELD_IDENTIFY).getAsString());
    }

    @Override
    public JsonElement serialize(Platform src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.addProperty(FIELD_NAME, src.getPlatformName());
        result.addProperty(FIELD_IDENTIFY, src.getPlatformIdentify());
        return result;
    }
}
