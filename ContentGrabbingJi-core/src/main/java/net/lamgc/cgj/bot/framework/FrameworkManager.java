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
import java.nio.file.Path;
import java.util.Objects;

/**
 * 框架管理器.
 * @author LamGC
 */
public class FrameworkManager extends JarPluginManager {

    private final boolean initialed;
    private final CloneableFrameworkContext parentContext;

    public FrameworkManager(String systemVersion, File frameworksDirectory, CloneableFrameworkContext context) {
        super(frameworksDirectory.toPath());
        setSystemVersion(systemVersion);
        this.parentContext = Objects.requireNonNull(context);

        // 在 super() 中会调用一次 initialize(), 但此时 FrameworkManager 内的成员变量尚未初始化,
        // 此时 super.initialize() 调用到 FrameworkManager 中的 createPluginFactory 时, context 传递为 null
        // 导致 FrameworkFactory 抛出 NPE, 故覆写 initialize() 阻止 super() 过早调用 initialize().
        initialed = true;
        initialize();
    }

    @Override
    protected void initialize() {
        if (!initialed) {
            return;
        }
        super.initialize();
    }

    @Override
    protected PluginLoader createPluginLoader() {
        return new CompoundPluginLoader()
                .add(new DevelopmentPluginLoader(this) {
                    @Override
                    protected PluginClassLoader createPluginClassLoader(Path pluginPath, PluginDescriptor pluginDescriptor) {
                        return new PluginClassLoader(FrameworkManager.this, pluginDescriptor,
                                getClass().getClassLoader(), ClassLoadingStrategy.ADP);
                    }
                }, this::isDevelopment)
                .add(new JarFrameworkLoader(this), this::isNotDevelopment);
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
        return new FrameworkFactory(getPluginsRoot().getParent().resolve("frameworkData").toFile(), parentContext);
    }
}
