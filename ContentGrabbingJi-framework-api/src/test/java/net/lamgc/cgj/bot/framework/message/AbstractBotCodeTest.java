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

import net.lamgc.cgj.bot.framework.Platform;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * @see AbstractBotCode
 */
public class AbstractBotCodeTest {

    @Test
    public void createBotCodeWithoutParameterTest() {
        final String functionName = "function";
        BotCode botCode = new TestBotCode(functionName);
        Assert.assertEquals(functionName, botCode.getFunctionName());
        Assert.assertNotNull(botCode.getPropertiesKeys());
        Assert.assertEquals(0, botCode.getPropertiesKeys().size());

        botCode = new TestBotCode(functionName, new HashMap<>());
        Assert.assertEquals(functionName, botCode.getFunctionName());
        Assert.assertNotNull(botCode.getPropertiesKeys());
        Assert.assertEquals(0, botCode.getPropertiesKeys().size());
    }

    @Test
    public void createBotCodeWithParameterTest() {
        final String functionName = "function";
        final Map<String, String> argumentsMap = new HashMap<>();
        argumentsMap.put("arg1", "value1");
        argumentsMap.put("arg2", "value2");

        BotCode botCode = new TestBotCode(functionName, argumentsMap);
        Assert.assertEquals(functionName, botCode.getFunctionName());
        Assert.assertTrue(argumentsMap.keySet().containsAll(botCode.getPropertiesKeys()));

        for (String key : argumentsMap.keySet()) {
            Assert.assertEquals(argumentsMap.get(key), botCode.getProperty(key));
        }

    }

    @Test
    public void emptyOrNullFunctionNameTest() {
        Assert.assertThrows(IllegalArgumentException.class, () -> new TestBotCode((String) null));
        Assert.assertThrows(IllegalArgumentException.class, () -> new TestBotCode(""));
    }

    @Test
    public void createByBotCodeTest() {
        final String functionName = "function";
        final Map<String, String> argumentsMap = new HashMap<>();
        argumentsMap.put("arg1", "value1");
        argumentsMap.put("arg2", "value2");

        BotCode botCode = new TestBotCode(functionName, argumentsMap);

        BotCode newBotCode = new TestBotCode(botCode);
        Assert.assertEquals(botCode.getFunctionName(), newBotCode.getFunctionName());
        Assert.assertEquals(botCode.getPropertiesKeys().size(), newBotCode.getPropertiesKeys().size());
        Assert.assertTrue(botCode.getPropertiesKeys().containsAll(newBotCode.getPropertiesKeys()));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void propertiesKeysUnmodifiedTest() {
        final String functionName = "function";
        final Map<String, String> argumentsMap = new HashMap<>();
        argumentsMap.put("arg1", "value1");
        argumentsMap.put("arg2", "value2");

        BotCode botCode = new TestBotCode(functionName, argumentsMap);

        botCode.getPropertiesKeys().clear();
    }

    @Test
    public void functionNameChangeTest() {
        final String newFunctionName = "functionB";
        BotCode botCode = new TestBotCode("functionA");
        botCode.setFunctionName(newFunctionName);
        Assert.assertEquals(newFunctionName, botCode.getFunctionName());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void setPropertyTest() throws NoSuchFieldException, IllegalAccessException {
        BotCode botCode = new TestBotCode("function");
        final String key = "arg";
        final String value = "value";
        Assert.assertThrows(NullPointerException.class, () -> botCode.setProperty(null, "value"));
        botCode.setProperty(key, value);
        Assert.assertEquals(value, botCode.getProperty(key));

        botCode.setProperty(key, null);
        Assert.assertNull(botCode.getProperty(key));
        Field propertiesField = AbstractBotCode.class.getDeclaredField("functionProperties");
        propertiesField.setAccessible(true);
        Hashtable<String, String> properties = (Hashtable<String, String>) propertiesField.get(botCode);
        Assert.assertFalse(properties.containsKey(key));
    }

    @Test
    public void toStringTest() {
        BotCode botCode = new TestBotCode("function");
        String objectStr = botCode.getClass().getSimpleName() + '@' + Integer.toHexString(botCode.hashCode());
        Assert.assertEquals(objectStr + "{Platform=null, functionName='function', " +
                "functionProperties={Hashtable}}", botCode.toString());

        botCode.setProperty("arg1", "value1");
        botCode.setProperty("arg2", "测试");
        botCode.setProperty("arg3", "Hello World.");
        Assert.assertEquals(objectStr + "{Platform=null, functionName='function', " +
                "functionProperties={Hashtable{\"arg1\"='value1', \"arg2\"='测试', \"arg3\"='Hello World.'}}",
                botCode.toString());
    }

    private final static class TestBotCode extends AbstractBotCode {

        public TestBotCode(String functionName) {
            super(functionName);
        }

        public TestBotCode(BotCode botCode) {
            super(botCode);
        }

        public TestBotCode(String functionName, Map<String, String> functionProperties) {
            super(functionName, functionProperties);
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

}