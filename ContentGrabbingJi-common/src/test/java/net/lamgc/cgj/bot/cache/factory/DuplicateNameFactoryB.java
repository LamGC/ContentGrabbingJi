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

package net.lamgc.cgj.bot.cache.factory;

import net.lamgc.cgj.bot.cache.*;
import net.lamgc.cgj.bot.cache.convert.StringConverter;
import net.lamgc.cgj.bot.cache.exception.GetCacheStoreException;

import java.io.File;

@Factory(name = "duplicate", priority = FactoryPriority.PRIORITY_HIGHEST)
public class DuplicateNameFactoryB implements CacheStoreFactory {
    @Override
    public void initial(File dataDirectory) {

    }

    @Override
    public <V> SingleCacheStore<V> newSingleCacheStore(String identify, StringConverter<V> converter) throws GetCacheStoreException {
        return null;
    }

    @Override
    public <E> ListCacheStore<E> newListCacheStore(String identify, StringConverter<E> converter) throws GetCacheStoreException {
        return null;
    }

    @Override
    public <E> SetCacheStore<E> newSetCacheStore(String identify, StringConverter<E> converter) throws GetCacheStoreException {
        return null;
    }

    @Override
    public <V> MapCacheStore<V> newMapCacheStore(String identify, StringConverter<V> converter) throws GetCacheStoreException {
        return null;
    }

    @Override
    public boolean canGetCacheStore() {
        return false;
    }
}
