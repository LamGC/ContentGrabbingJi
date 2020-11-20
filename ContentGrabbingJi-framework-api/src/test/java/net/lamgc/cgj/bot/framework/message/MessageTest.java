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

import java.util.Random;

import static org.junit.Assert.*;

/**
 * @see Message
 */
public class MessageTest {

    private final static String TEST_CONTENT = "test";

    @Test
    public void lengthTest() {
        Assert.assertEquals(TEST_CONTENT.length(), new TestMessage().length());
    }

    @Test
    public void charAtTest() {
        final int targetCharIndex = new Random().nextInt(TEST_CONTENT.length());

        Assert.assertEquals(TEST_CONTENT.charAt(targetCharIndex), new TestMessage().charAt(targetCharIndex));
    }

    @Test
    public void subSequenceTest() {
        final int startsIndex = 1;
        final int endsIndex = 3;

        Assert.assertEquals(TEST_CONTENT.subSequence(startsIndex, endsIndex),
                new TestMessage().subSequence(startsIndex, endsIndex));
    }

    private final static class TestMessage implements Message {
        @Override
        public String contentToString() {
            return TEST_CONTENT;
        }
    }

}