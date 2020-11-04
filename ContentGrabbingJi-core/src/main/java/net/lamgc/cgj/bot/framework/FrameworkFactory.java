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

import net.lamgc.cgj.bot.cache.CacheStoreBuilder;
import net.lamgc.cgj.bot.event.EventExecutor;
import org.pf4j.Plugin;
import org.pf4j.PluginFactory;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

/**
 * 经过调整的, 针对 Framework 的实例工厂类.
 * @author LamGC
 */
final class FrameworkFactory implements PluginFactory {

    private final static Logger log = LoggerFactory.getLogger(FrameworkFactory.class);

    private final File dataRootFolder;
    private final CacheStoreBuilder cacheStoreBuilder;
    private final EventExecutor eventExecutor;

    public FrameworkFactory(File dataRootFolder, CacheStoreBuilder cacheStoreBuilder, EventExecutor eventExecutor) {
        this.dataRootFolder = dataRootFolder;
        this.cacheStoreBuilder = cacheStoreBuilder;
        this.eventExecutor = eventExecutor;
        if (!this.dataRootFolder.exists() && !this.dataRootFolder.mkdirs()) {
            log.warn("框架数据目录创建异常, 可能会导致后续框架存取数据失败!");
        }
    }

    @Override
    public Plugin create(PluginWrapper pluginWrapper) {
        String pluginClassName = pluginWrapper.getDescriptor().getPluginClass();
        log.debug("Create instance for framework '{}'", pluginClassName);

        Class<?> pluginClass;
        try {
            pluginClass = pluginWrapper.getPluginClassLoader().loadClass(pluginClassName);
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            return null;
        }

        // 如果成功获取类, 就需要对其检查, 以确保类符合框架主类的要求.
        int modifiers = pluginClass.getModifiers();
        if (Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers)
                || (!Framework.class.isAssignableFrom(pluginClass))) {
            log.error("The framework class '{}' is not valid", pluginClassName);
            return null;
        }

        try {
            // <init>(PluginWrapper, DataFolder)
            Constructor<?> constructor = pluginClass
                    .getConstructor(PluginWrapper.class, File.class, FrameworkContext.class);
            return (Framework) constructor.newInstance(pluginWrapper,
                    new File(dataRootFolder, pluginWrapper.getPluginId()),
                    new DefaultFrameworkContext(eventExecutor, cacheStoreBuilder));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }
}
