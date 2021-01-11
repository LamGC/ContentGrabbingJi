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

package net.lamgc.cgj.bot.cache.redis;

/**
 * @author LamGC
 */

public enum LuaScript {
    /**
     * [List] 检查元素是否存在.
     */
    LIST_CHECK_ELEMENT_CONTAINS("CheckElementContains"),
    /**
     * [List] 删除指定索引的元素.
     */
    LIST_REMOVE_ELEMENT_BY_INDEX("RemoveElementByIndex"),
    /**
     * [All] 删除所有前缀为指定字符串的键.
     */
    STORE_REMOVE_KEYS_BY_PREFIX("RemoveKeysByPrefix")
    ;

    public final static String PACKAGE_PATH = "lua/";

    private final String scriptName;

    LuaScript(String scriptName) {
        this.scriptName = scriptName;
    }

    public String getScriptName() {
        return scriptName;
    }
}
