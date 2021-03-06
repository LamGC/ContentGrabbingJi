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

package net.lamgc.cgj.bot.cache.convert;

import org.junit.Assert;
import org.junit.Test;

/**
 * @see StringToStringConverter
 */
public class StringToStringConverterTest {

    private final Converter<String, String> stringConverter = new StringToStringConverter();

    private final static String TEST_CONTENT = "test";

    @Test
    public void convertTest() {
        Assert.assertEquals(TEST_CONTENT, stringConverter.to(TEST_CONTENT));
        Assert.assertEquals(TEST_CONTENT, stringConverter.from(TEST_CONTENT));
    }

}