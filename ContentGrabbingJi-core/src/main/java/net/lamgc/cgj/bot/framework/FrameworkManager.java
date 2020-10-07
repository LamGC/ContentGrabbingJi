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

import org.pf4j.*;

import java.io.File;

/**
 * 框架管理器.
 * @author LamGC
 */
public class FrameworkManager extends JarPluginManager {

    public FrameworkManager(String systemVersion, File frameworksDirectory) {
        super(frameworksDirectory.toPath());
        setSystemVersion(systemVersion);
    }

    @Override
    protected PluginDescriptorFinder createPluginDescriptorFinder() {
        return new JsonFrameworkDescriptorFinder();
    }

    @Override
    protected PluginRepository createPluginRepository() {
        return new CompoundPluginRepository()
                .add(new DevelopmentPluginRepository(getPluginsRoot()), this::isDevelopment)
                .add(new JarPluginRepository(getPluginsRoot()), this::isNotDevelopment)
                .add(new DefaultPluginRepository(getPluginsRoot()), this::isNotDevelopment);
    }

    @Override
    protected PluginFactory createPluginFactory() {
        return new FrameworkFactory(getPluginsRoot().getParent().resolve("frameworkData").toFile());
    }
}
