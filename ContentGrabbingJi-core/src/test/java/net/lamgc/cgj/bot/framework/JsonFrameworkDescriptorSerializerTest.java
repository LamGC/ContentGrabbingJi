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

package net.lamgc.cgj.bot.framework;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.lamgc.cgj.bot.framework.message.BotCodeDescriptor;
import net.lamgc.cgj.bot.framework.util.AuthorJsonSerializer;
import net.lamgc.cgj.bot.framework.util.BotCodeDescriptorJsonSerializer;
import net.lamgc.cgj.bot.framework.util.PlatformJsonSerializer;
import net.lamgc.cgj.bot.framework.util.PluginDependencyJsonSerializer;
import org.junit.Assert;
import org.junit.Test;
import org.pf4j.PluginDependency;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class JsonFrameworkDescriptorSerializerTest {

    private final static Gson gson = getGson();

    @Test
    public void deserializerTest() throws IOException {
        FrameworkDescriptor descriptor;
        JsonObject rawObject;
        try (Reader resourceReader = getResourceAsReader("test-framework.json")) {
            rawObject = gson.fromJson(resourceReader, JsonObject.class);
            descriptor = gson.fromJson(rawObject, DefaultFrameworkDescriptor.class);
        }

        assertDescriptor(descriptor);
        assertDescriptor(gson.fromJson(gson.toJson(descriptor), DefaultFrameworkDescriptor.class));
    }

    private static Reader getResourceAsReader(String resourceName) {
        InputStream resource = JsonFrameworkDescriptorSerializerTest.class
                .getClassLoader().getResourceAsStream(resourceName);
        if (resource == null) {
            Assert.fail("未找到测试用资源: " + resourceName);
        }
        return new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8));
    }

    private void assertDescriptor(FrameworkDescriptor descriptor) {
        Assert.assertEquals("cgj-mirai", descriptor.getPluginId());
        Assert.assertEquals("test", descriptor.getPluginDescription());
        Assert.assertEquals("3.0.0-alpha", descriptor.getVersion());
        Assert.assertEquals("=>3.0.0", descriptor.getRequires());
        Assert.assertEquals("Github@LamGC, Github@mamoe", descriptor.getProvider());
        Assert.assertEquals("AGPL-3.0", descriptor.getLicense());
        Assert.assertEquals("com.example.FrameworkMain", descriptor.getPluginClass());

        List<PluginDependency> expectedDependency = new ArrayList<>();
        expectedDependency.add(new PluginDependency("RequireDepend@1.0.0"));
        expectedDependency.add(new PluginDependency("OptionalDepend?@1.0.0"));
        expectedDependency.add(new PluginDependency("AllVersionSupportedDependA"));
        expectedDependency.add(new PluginDependency("AllVersionSupportedDependB@*"));

        Assert.assertEquals(expectedDependency, descriptor.getDependencies());

        Assert.assertEquals("Tencent QQ", descriptor.getPlatform().getPlatformName());
        Assert.assertEquals("qq", descriptor.getPlatform().getPlatformIdentify());

        List<Author> expectedAuthors = new ArrayList<>();
        expectedAuthors.add(new Author("LamGC", "https://github.com/LamGC", "lam827@lamgc.net"));
        expectedAuthors.add(new Author("UserA"));
        expectedAuthors.add(new Author("UserB"));

        Assert.assertEquals(expectedAuthors, descriptor.getAuthors());

        List<String> expectedBotCodePatterns = new ArrayList<>();
        expectedBotCodePatterns.add("(?:\\[mirai:([^:]+)\\])");
        expectedBotCodePatterns.add("(?:\\[mirai:([^\\]]*)?:(.*?)?\\])");
        expectedBotCodePatterns.add("(?:\\[mirai:([^\\]]*)?(:(.*?))*?\\])");

        for (Pattern pattern : descriptor.getBotCodeDescriptor().getPatterns()) {
            if (!expectedBotCodePatterns.contains(pattern.pattern())) {
                Assert.fail("存在不符的表达式: " + pattern.pattern());
            }
        }
    }

    @Test(expected = JsonParseException.class)
    public void author_missingFieldTest() throws IOException {
        try (Reader resourceReader = getResourceAsReader("badAuthor-MissingField-framework.json")) {
            gson.fromJson(resourceReader, DefaultFrameworkDescriptor.class);
        }
    }

    @Test(expected = JsonParseException.class)
    public void author_invalidFieldTest() throws IOException {
        try (Reader resourceReader = getResourceAsReader("badAuthor-InvalidField-framework.json")) {
            gson.fromJson(resourceReader, DefaultFrameworkDescriptor.class);
        }
    }

    @Test(expected = JsonParseException.class)
    public void author_NonAObjectTest() throws IOException {
        try (Reader resourceReader = getResourceAsReader("badAuthor-NonObject-framework.json")) {
            gson.fromJson(resourceReader, DefaultFrameworkDescriptor.class);
        }
    }

    @Test(expected = JsonParseException.class)
    public void botCode_NonAObjectTest() throws IOException {
        try (Reader resourceReader = getResourceAsReader("badBotCode-NonObject-framework.json")) {
            gson.fromJson(resourceReader, DefaultFrameworkDescriptor.class);
        }
    }

    @Test
    public void botCode_MissingPatternsTest() throws IOException {
        try (Reader resourceReader = getResourceAsReader("badBotCode-MissingPatternsField-framework.json")) {
            List<Pattern> patterns = gson.fromJson(resourceReader, DefaultFrameworkDescriptor.class)
                    .getBotCodeDescriptor().getPatterns();
            Assert.assertTrue(patterns.isEmpty());
        }
    }

    @Test
    public void botCode_PatternsFieldNonArrayTest() throws IOException {
        try (Reader resourceReader = getResourceAsReader("badBotCode-PatternsFieldNonArray-framework.json")) {
            List<Pattern> patterns = gson.fromJson(resourceReader, DefaultFrameworkDescriptor.class)
                    .getBotCodeDescriptor().getPatterns();
            Assert.assertTrue(patterns.isEmpty());
        }
    }

    @Test(expected = JsonParseException.class)
    public void platform_NonAObjectTest() throws IOException {
        try (Reader resourceReader = getResourceAsReader("badPlatform-NonObject-framework.json")) {
            gson.fromJson(resourceReader, DefaultFrameworkDescriptor.class);
        }
    }

    @Test(expected = JsonParseException.class)
    public void platform_MissingNameFieldTest() throws IOException {
        try (Reader resourceReader = getResourceAsReader("badPlatform-MissingField-Name-framework.json")) {
            gson.fromJson(resourceReader, DefaultFrameworkDescriptor.class);
        }
    }

    @Test(expected = JsonParseException.class)
    public void platform_MissingIdentifyFieldTest() throws IOException {
        try (Reader resourceReader = getResourceAsReader("badPlatform-MissingField-Identify-framework.json")) {
            gson.fromJson(resourceReader, DefaultFrameworkDescriptor.class);
        }
    }

    @Test(expected = JsonParseException.class)
    public void pluginDependency_NonPrimitiveTest() throws IOException {
        try (Reader resourceReader = getResourceAsReader("badPluginDependency-NonPrimitive-framework.json")) {
            gson.fromJson(resourceReader, DefaultFrameworkDescriptor.class);
        }
    }

    @Test(expected = JsonParseException.class)
    public void pluginDependency_NonStringTest() throws IOException {
        try (Reader resourceReader = getResourceAsReader("badPluginDependency-NonString-framework.json")) {
            gson.fromJson(resourceReader, DefaultFrameworkDescriptor.class);
        }
    }

    private static Gson getGson() {
        return new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(Author.class, new AuthorJsonSerializer())
                .registerTypeAdapter(BotCodeDescriptor.class, new BotCodeDescriptorJsonSerializer())
                .registerTypeAdapter(Platform.class, new PlatformJsonSerializer())
                .registerTypeAdapter(PluginDependency.class, new PluginDependencyJsonSerializer())
                .create();
    }

}
