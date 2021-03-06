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

import org.pf4j.*;

import java.nio.file.Path;

/**
 * 对 {@link JarPluginLoader} 重写的 Loader.
 * <p> 除了更改 {@link ClassLoadingStrategy} 外没啥区别.
 * @author LamGC
 */
public class JarFrameworkLoader extends JarPluginLoader {

    public JarFrameworkLoader(PluginManager pluginManager) {
        super(pluginManager);
    }

    @Override
    public ClassLoader loadPlugin(Path pluginPath, PluginDescriptor pluginDescriptor) {
        PluginClassLoader pluginClassLoader =
                new PluginClassLoader(pluginManager, pluginDescriptor,
                        getClass().getClassLoader(), ClassLoadingStrategy.ADP);
        pluginClassLoader.addFile(pluginPath.toFile());

        return pluginClassLoader;
    }
}
