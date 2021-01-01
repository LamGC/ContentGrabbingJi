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

package net.lamgc.cgj.bot.framework;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.lamgc.cgj.bot.framework.message.BotCodeDescriptor;
import net.lamgc.cgj.bot.framework.util.AuthorJsonSerializer;
import net.lamgc.cgj.bot.framework.util.BotCodeDescriptorJsonSerializer;
import net.lamgc.cgj.bot.framework.util.PlatformJsonSerializer;
import net.lamgc.cgj.bot.framework.util.PluginDependencyJsonSerializer;
import org.pf4j.PluginDependency;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginDescriptorFinder;
import org.pf4j.PluginRuntimeException;
import org.pf4j.util.FileUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Json格式的框架描述文件查找器.
 * @author LamGC
 */
class JsonFrameworkDescriptorFinder implements PluginDescriptorFinder {

    private final static String DESCRIPTOR_FILE_NAME = "framework.json";
    private final Gson gson;

    public JsonFrameworkDescriptorFinder() {
        this(new Gson());
    }

    public JsonFrameworkDescriptorFinder(Gson gson) {
        this.gson = gson.newBuilder()
                .serializeNulls()
                .registerTypeAdapter(Author.class, new AuthorJsonSerializer())
                .registerTypeAdapter(BotCodeDescriptor.class, new BotCodeDescriptorJsonSerializer())
                .registerTypeAdapter(Platform.class, new PlatformJsonSerializer())
                .registerTypeAdapter(PluginDependency.class, new PluginDependencyJsonSerializer())
                .create();
    }

    @Override
    public boolean isApplicable(Path frameworkPath) {
        return Files.exists(frameworkPath) && (Files.isDirectory(frameworkPath) || FileUtils.isJarFile(frameworkPath));
    }

    @Override
    public PluginDescriptor find(Path frameworkPath) {
        JsonObject descriptorObject = loadFrameworkDescriptorObject(frameworkPath);
        return createFrameworkDescriptor(descriptorObject);
    }

    private Path getFrameworkDescriptorPath(Path frameworkPath) {
        if (Files.isDirectory(frameworkPath)) {
            return frameworkPath.resolve(Paths.get(DESCRIPTOR_FILE_NAME));
        } else {
            try {
                return FileUtils.getPath(frameworkPath, DESCRIPTOR_FILE_NAME);
            } catch (IOException e) {
                throw new PluginRuntimeException(e);
            }
        }
    }

    private JsonObject loadFrameworkDescriptorObject(Path frameworkPath) {
        Path descriptorPath = getFrameworkDescriptorPath(frameworkPath);
        if (frameworkPath == null) {
            throw new PluginRuntimeException("Cannot find the json path");
        }

        JsonObject descriptorObject;
        try {
            if (Files.notExists(descriptorPath)) {
                throw new PluginRuntimeException("Cannot find '{}' path", descriptorPath);
            }
            try (InputStream input = Files.newInputStream(descriptorPath)) {
                descriptorObject = gson.fromJson(
                        new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8)), JsonObject.class);
            } catch (IOException e) {
                throw new PluginRuntimeException("Exception loading descriptor", e);
            }
        } finally {
            FileUtils.closePath(descriptorPath);
        }
        return descriptorObject;
    }

    private FrameworkDescriptor createFrameworkDescriptor(JsonObject descriptorObject) {
        return gson.fromJson(descriptorObject, DefaultFrameworkDescriptor.class);
    }

}
