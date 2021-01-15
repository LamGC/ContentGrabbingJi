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

package net.lamgc.cgj.bot.cache.redis.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.lamgc.cgj.bot.cache.redis.RedisConnectionProperties;
import net.lamgc.cgj.bot.cache.redis.RedisUtils;
import org.junit.Assert;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Random;

/**
 *
 * @author LamGC
 */
public final class RedisTestUtils {

    private RedisTestUtils() {}

    private final static Gson GSON_INSTANCE = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .create();

    /**
     * 创建测试用配置文件.
     * @param testDirectory 组件文件夹.
     * @param properties 配置对象.
     * @throws IOException 当配置文件写入失败时抛出.
     */
    public static void createConnectionProperties(File testDirectory, RedisConnectionProperties properties)
            throws IOException {
        File propertiesFile = new File(testDirectory, "Redis/" + RedisUtils.PROPERTIES_FILE_NAME);
        Files.write(propertiesFile.toPath(),
                GSON_INSTANCE.toJson(properties).getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE);
    }

    /**
     * 如果存在键, 则删除, 如果失败则判定断言失败.
     * @param jedis Jedis 对象.
     * @param keyString 待删除的键名.
     */
    public static void assertDeleteIfExist(Jedis jedis, String keyString) {
        if (jedis.exists(keyString)) {
            Assert.assertEquals("The key used by the test is occupied and deletion failed.",
                    1, jedis.del(keyString).intValue());
        }
    }

    /**
     * 随机字符串.
     * @param length 长度.
     * @return 返回指定长度的随机字符串.
     */
    public static String randomString(int length) {
        final char[] chars = new char[] {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        };

        StringBuilder buffer = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            buffer.append(chars[random.nextInt(chars.length)]);
        }
        return buffer.toString();
    }

}
