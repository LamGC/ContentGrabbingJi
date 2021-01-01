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

package net.lamgc.cgj.bot.cache;

import net.lamgc.cgj.bot.cache.factory.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * @see FactoryInfo
 */
public class FactoryInfoTest {

    @Test
    public void analyticTest() {
        FactoryInfo infoA = new FactoryInfo(FactoryInfoTestFactory.class);
        Assert.assertEquals("test-factoryInfo", infoA.getFactoryName());
        Assert.assertEquals(6, infoA.getFactoryPriority());
        Assert.assertEquals(CacheStoreSource.MEMORY, infoA.getStoreSource());

        FactoryInfo infoB = new FactoryInfo(FactoryInfoTestFactory.class);

        Assert.assertEquals(infoA, infoB);
        Assert.assertEquals(infoA.hashCode(), infoB.hashCode());
    }

    @Test
    @SuppressWarnings({"SimplifiableAssertion", "EqualsWithItself", "ConstantConditions"})
    public void equalsTest() {
        FactoryInfo infoA = new FactoryInfo(FactoryInfoTestFactory.class);
        FactoryInfo infoB = new FactoryInfo(FactoryInfoTestFactory.class);

        Assert.assertTrue(infoA.equals(infoA));
        Assert.assertTrue(infoA.equals(infoB));

        Assert.assertFalse(infoA.equals(null));
        Assert.assertFalse(infoA.equals(new Object()));

        Assert.assertFalse(
                new FactoryInfo(NameNoEqualFactoryA.class)
                        .equals(new FactoryInfo(NameNoEqualFactoryB.class)));

        Assert.assertFalse(
                new FactoryInfo(NameNoEqualFactoryA.class)
                        .equals(new FactoryInfo(CacheStoreSourceNoEqualFactory.class)));

    }

    @Test(expected = IllegalArgumentException.class)
    public void noAnnotationTest() {
        new FactoryInfo(NoAnnotationFactory.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void unnamedTest() {
        new FactoryInfo(UnnamedFactory.class);
    }

    @Test
    public void priorityCheckTest() {
        FactoryInfo toHighPriorityFactoryInfo = new FactoryInfo(TooHighPriorityFactory.class);
        Assert.assertEquals(FactoryPriority.PRIORITY_HIGHEST,
                toHighPriorityFactoryInfo.getFactoryPriority());

        FactoryInfo toLowPriorityFactoryInfo = new FactoryInfo(TooLowPriorityFactory.class);
        Assert.assertEquals(FactoryPriority.PRIORITY_LOWEST,
                toLowPriorityFactoryInfo.getFactoryPriority());

        Assert.assertNotEquals(toHighPriorityFactoryInfo, toLowPriorityFactoryInfo);

    }

}
