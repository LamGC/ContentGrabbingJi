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

package net.lamgc.cgj.bot.cache;

/**
 * 找不到 Factory 异常.
 * <p>当尝试获取 {@link CacheStoreFactory} 失败时抛出.
 * @see CacheStoreFactory
 * @author LamGC
 */
public class NoSuchFactoryException extends Exception {

    public NoSuchFactoryException() {
        super("Unable to get available factory");
    }

    public NoSuchFactoryException(Throwable cause) {
        super("Unable to get available factory", cause);
    }
}
