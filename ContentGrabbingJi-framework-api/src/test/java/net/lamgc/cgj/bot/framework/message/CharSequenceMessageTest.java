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

package net.lamgc.cgj.bot.framework.message;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;

/**
 * @see CharSequenceMessage
 */
public class CharSequenceMessageTest {

    @Test
    public void buildTest() {
        final String contentStr = "test";

        Assert.assertEquals(contentStr, new CharSequenceMessage(contentStr).contentToString());
        Assert.assertEquals(contentStr, new CharSequenceMessage(new StringBuilder(contentStr)).contentToString());
    }

    @Test(expected = NullPointerException.class)
    public void nullContentTest() {
        new CharSequenceMessage(null);
    }

    @Test
    public void buildWithMessageTest() throws NoSuchFieldException, IllegalAccessException {
        final String testContent = "test123";

        class TestMessage implements Message {
            @Override
            public String contentToString() {
                return testContent;
            }
        }

        CharSequenceMessage charSequenceMessage = new CharSequenceMessage(new TestMessage());
        Assert.assertEquals(testContent, charSequenceMessage.contentToString());

        Field contentField = CharSequenceMessage.class.getDeclaredField("content");
        contentField.setAccessible(true);
        Assert.assertFalse(contentField.get(charSequenceMessage) instanceof Message);
    }

    @Test
    public void toStringTest() {
        final String testContent = "test";

        Assert.assertEquals("CharSequenceMessage{content='" + testContent + "'}",
                new CharSequenceMessage(testContent).toString());
    }

}