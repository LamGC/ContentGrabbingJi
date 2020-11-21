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

package net.lamgc.cgj.bot.framework.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class CollectionUtilsTest {

    @Test
    public void toMapTest() {
        Map<String, String> expectMap = new HashMap<>();
        expectMap.put("arg1", "value1");
        expectMap.put("arg2", "value2");
        expectMap.put("arg3", "value3");

        Set<String> modifySet = new CopyOnWriteArraySet<>(expectMap.keySet());
        modifySet.add(null);

        Map<String, String> map = CollectionUtils.toMap(modifySet, expectMap::get);
        Assert.assertEquals(expectMap.size(), map.size());
        Assert.assertTrue(expectMap.keySet().containsAll(map.keySet()));

        for (String key : expectMap.keySet()) {
            Assert.assertEquals(expectMap.get(key), map.get(key));
        }
    }

}