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

package net.lamgc.cgj.bot.framework;

import net.lamgc.cgj.bot.cache.CacheStoreBuilder;
import net.lamgc.cgj.bot.event.EventExecutor;

/**
 * 框架上下文的默认实现.
 * @author LamGC
 */
class DefaultFrameworkContext implements FrameworkContext {

    private final EventExecutor eventExecutor;
    private final CacheStoreBuilder cacheStoreBuilder;

    public DefaultFrameworkContext(EventExecutor eventExecutor, CacheStoreBuilder cacheStoreBuilder) {
        this.eventExecutor = eventExecutor;

        this.cacheStoreBuilder = cacheStoreBuilder;
    }

    @Override
    public EventExecutor getEventExecutor() {
        return eventExecutor;
    }

    @Override
    public CacheStoreBuilder getCacheStoreBuilder() {
        return cacheStoreBuilder;
    }
}
