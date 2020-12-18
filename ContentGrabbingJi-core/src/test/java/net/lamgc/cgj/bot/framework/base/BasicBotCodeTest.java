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

package net.lamgc.cgj.bot.framework.base;

import net.lamgc.cgj.bot.framework.Platform;
import net.lamgc.cgj.bot.framework.message.AbstractBotCode;
import net.lamgc.cgj.bot.framework.message.BotCode;
import net.lamgc.cgj.bot.framework.message.BotCodeFunction;
import net.lamgc.cgj.bot.framework.message.StandardBotCodeFunction;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @see BasicBotCode
 */
public class BasicBotCodeTest {

    @Test
    public void getPlatform() {
        BotCode botCode = new BasicBotCode(StandardBotCodeFunction.AT);

        Assert.assertEquals("ContentGrabbingJi", botCode.getPlatform().getPlatformName());
        Assert.assertEquals("CGJ", botCode.getPlatform().getPlatformIdentify());
    }

    @Test
    public void contentToStringWithoutParameter() {
        BotCode botCode = new BasicBotCode(StandardBotCodeFunction.FILE);

        Assert.assertEquals("[file]", botCode.contentToString());
    }

    @Test
    public void contentToStringWithParameter() {
        final Map<String, String> argumentsMap = new HashMap<>();
        argumentsMap.put("arg1", "value1");
        argumentsMap.put("arg2", "Hello World.");
        argumentsMap.put("arg3", "测试");

        BotCode botCode = new BasicBotCode(StandardBotCodeFunction.EMOJI, argumentsMap);
        Assert.assertEquals("[emoji:arg3=%E6%B5%8B%E8%AF%95&arg2=Hello+World.&arg1=value1]", botCode.contentToString());
    }

    @Test
    public void createInstanceByBotCode() {
        class TestBotCode extends AbstractBotCode {

            public TestBotCode(BotCodeFunction function, Map<String, String> functionProperties) {
                super(function, functionProperties);
            }

            @Override
            public Platform getPlatform() {
                return null;
            }

            @Override
            public String contentToString() {
                return null;
            }

        }
        final Map<String, String> argumentsMap = new HashMap<>();
        argumentsMap.put("arg1", "value1");
        argumentsMap.put("arg2", "Hello World.");
        argumentsMap.put("arg3", "测试");

        BotCode expectBotCode = new TestBotCode(StandardBotCodeFunction.AUDIO, argumentsMap);
        BotCode botCode = new BasicBotCode(expectBotCode);

        Assert.assertEquals(expectBotCode.getFunction(), botCode.getFunction());
        Assert.assertTrue(expectBotCode.getPropertiesKeys().containsAll(botCode.getPropertiesKeys()));

        for (String key : expectBotCode.getPropertiesKeys()) {
            Assert.assertEquals(expectBotCode.getProperty(key), botCode.getProperty(key));
        }

    }

}