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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.pf4j.ClassLoadingStrategy;
import org.pf4j.PluginClassLoader;

import java.io.IOException;
import java.lang.reflect.Field;

public class JarFrameworkLoaderTest {

    private final static TemporaryFolder tempFolder = TemporaryFolder.builder().build();

    @BeforeClass
    public static void beforeClass() throws IOException {
        tempFolder.create();
    }

    @Test
    public void loaderCheck() throws IOException, NoSuchFieldException, IllegalAccessException {
        ClassLoader classLoader = new JarFrameworkLoader(null)
                .loadPlugin(tempFolder.newFile("invalid files").toPath(), null);
        Assert.assertTrue(classLoader instanceof PluginClassLoader);
        Field classLoadingStrategyField = PluginClassLoader.class.getDeclaredField("classLoadingStrategy");
        classLoadingStrategyField.setAccessible(true);
        ClassLoadingStrategy strategy = (ClassLoadingStrategy) classLoadingStrategyField.get(classLoader);
        if (strategy.getSources().get(0) != ClassLoadingStrategy.Source.APPLICATION) {
            Assert.fail("The class loading policy does not make application the first priority (First: " +
                    strategy.getSources().get(0) + ").");
        }
    }

}