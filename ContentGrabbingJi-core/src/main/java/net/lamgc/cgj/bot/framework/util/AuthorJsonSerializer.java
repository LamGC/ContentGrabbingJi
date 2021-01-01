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
import net.lamgc.cgj.bot.framework.Author;

import java.lang.reflect.Type;

/**
 * {@link Author} Json 序列化工具.
 * @see Author
 * @author LamGC
 */
public class AuthorJsonSerializer implements JsonSerializer<Author>, JsonDeserializer<Author> {

    private final static String FIELD_NAME = "name";
    private final static String FIELD_URL = "url";
    private final static String FIELD_EMAIL = "email";

    @Override
    public Author deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (!json.isJsonObject()) {
            throw new JsonParseException("Not a JsonObject");
        }

        JsonObject authorObject = json.getAsJsonObject();
        if (!authorObject.has(FIELD_NAME) || !authorObject.get(FIELD_NAME).isJsonPrimitive()) {
            throw new JsonParseException("A required field is missing or the type is incorrect: " + FIELD_NAME);
        }

        String name = authorObject.get(FIELD_NAME).getAsString();
        String url = authorObject.has(FIELD_URL) && authorObject.get(FIELD_URL).isJsonPrimitive() ?
                authorObject.get(FIELD_URL).getAsString() : null;
        String email = authorObject.has(FIELD_EMAIL) && authorObject.get(FIELD_EMAIL).isJsonPrimitive() ?
                authorObject.get(FIELD_EMAIL).getAsString() : null;

        return new Author(name, url, email);
    }

    @Override
    public JsonElement serialize(Author src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.addProperty(FIELD_NAME, src.getName());
        result.addProperty(FIELD_URL, src.getUrl());
        result.addProperty(FIELD_EMAIL, src.getEmail());
        return result;
    }
}
