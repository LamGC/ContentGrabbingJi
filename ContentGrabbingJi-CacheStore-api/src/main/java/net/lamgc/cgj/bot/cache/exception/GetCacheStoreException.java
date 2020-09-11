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

package net.lamgc.cgj.bot.cache.exception;

/**
 * 获取 CacheStore 异常.
 * <p>当无法获取 CacheStore 时抛出.
 * @author LamGC
 */
public class GetCacheStoreException extends RuntimeException {

    public GetCacheStoreException(String message) {
        super(message);
    }

    public GetCacheStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
