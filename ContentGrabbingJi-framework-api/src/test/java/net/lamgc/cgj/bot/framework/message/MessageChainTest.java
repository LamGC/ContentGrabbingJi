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

package net.lamgc.cgj.bot.framework.message;

import net.lamgc.cgj.bot.framework.Platform;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * @see MessageChain
 */
public class MessageChainTest {

    @Test
    public void createEmptyChainTest() {
        MessageChain chain = new MessageChain();

        Assert.assertEquals(0, chain.size());
        Assert.assertTrue(chain.isEmpty());
    }

    @Test
    public void createNoEmptyChainTest() {
        String[] expectContents = new String[] {"This", " is ", "a simple", " message"};
        final String[] contents = Arrays.copyOf(expectContents, expectContents.length + 3);
        contents[expectContents.length] = "";
        contents[expectContents.length + 1] = null;
        contents[expectContents.length + 2] = ".";

        MessageChain chain = new MessageChain(contents);

        expectContents = Arrays.copyOf(expectContents, expectContents.length + 1);
        expectContents[expectContents.length - 1] = ".";

        final StringBuilder expectContentsString = new StringBuilder();
        for (String content : expectContents) {
            expectContentsString.append(content);
        }
        Assert.assertEquals(expectContents.length, chain.size());
        Assert.assertFalse(chain.isEmpty());
        Assert.assertEquals(expectContentsString.toString(), chain.contentToString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void plusSelfTest() {
        MessageChain chain = new MessageChain("test");
        chain.plus(chain);
    }

    @Test
    public void clearTest() {
        MessageChain chain = new MessageChain("Test", "123");
        Assert.assertFalse(chain.isEmpty());
        chain.clear();
        Assert.assertTrue(chain.isEmpty());
    }

    @Test
    public void plusBotCodeTest() {
        MessageChain chain = new MessageChain("BotCode: ");
        chain.plus(new AbstractBotCode(StandardBotCodeFunction.AT) {
            @Override
            public String contentToString() {
                return "[at:arg=value]";
            }
        });
        chain.plus(".");
        Assert.assertEquals("BotCode: [at:arg=value].", chain.contentToString());
    }

    @Test
    public void plusMessageTest() {
        MessageChain chain = new MessageChain("This ", "is ", "a ");
        chain.plus(new CharSequenceMessage("simple message."));
        Assert.assertEquals("This is a simple message.", chain.contentToString());
    }

    @Test
    public void deleteTest() {
        final String[] contents = new String[] {"This", " is ", "a ", "simple", " message."};
        MessageChain chain = new MessageChain(contents);

        Assert.assertEquals("a ", chain.delete(2).contentToString());
        Assert.assertEquals(contents.length - 1, chain.size());
        Assert.assertEquals("This is simple message.", chain.contentToString());
    }

    @Test
    public void insertTest() {
        final String[] contents = new String[] {"This", " is ", "simple", " message"};
        MessageChain chain = new MessageChain(contents);

        chain.insert(2,  "a ");
        chain.insert(5, new CharSequenceMessage("."));
        Assert.assertEquals(contents.length + 2, chain.size());
        Assert.assertEquals("This is a simple message.", chain.contentToString());
    }

}