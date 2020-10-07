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

import net.lamgc.cgj.bot.framework.message.BotCodeDescriptor;
import org.pf4j.PluginDependency;

import java.util.ArrayList;
import java.util.List;

/**
 * 默认框架描述对象.
 * @author LamGC
 */
class DefaultFrameworkDescriptor implements FrameworkDescriptor {

    private String id;
    private String description;
    private String version;
    private String requiresVersion;
    private String provider;
    private String license;
    private String frameworkClass;
    private final List<PluginDependency> dependencies = new ArrayList<>();

    private Platform platform;
    private BotCodeDescriptor botCode;
    private List<Author> authors;

    @Override
    public String getPluginId() {
        return id;
    }

    @Override
    public String getPluginDescription() {
        return description;
    }

    @Override
    public String getPluginClass() {
        return frameworkClass;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getRequires() {
        return requiresVersion;
    }

    @Override
    public String getProvider() {
        return provider;
    }

    @Override
    public String getLicense() {
        return license;
    }

    @Override
    public List<PluginDependency> getDependencies() {
        return dependencies;
    }

    @Override
    public Platform getPlatform() {
        return platform;
    }

    @Override
    public BotCodeDescriptor getBotCodeDescriptor() {
        return botCode;
    }

    @Override
    public List<Author> getAuthors() {
        return authors;
    }

}
