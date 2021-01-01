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

package net.lamgc.cgj.bot.cache.local;

/**
 * 可清理接口, 实现该接口代表该类具有清理动作.
 * @author LamGC
 */
public interface Cleanable {

    /**
     * 该方法需要CacheStore完成对过期Entry的清除.
     * @return 返回已清理数量.
     * @throws Exception 即使该方法抛出异常, 也不会影响后续情况.
     */
    long clean() throws Exception;

}
