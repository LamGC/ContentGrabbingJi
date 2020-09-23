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

import com.google.common.base.Strings;

/**
 * CacheStoreFactory 的标识信息.
 * @author LamGC
 */
public class FactoryInfo {

    private final String factoryName;
    private final int factoryPriority;

    public FactoryInfo(Class<? extends CacheStoreFactory> factoryClass) {
        Factory factoryAnnotation = factoryClass.getAnnotation(Factory.class);
        if (factoryAnnotation == null) {
            throw new IllegalArgumentException("Annotation not found");
        } else if (Strings.isNullOrEmpty(factoryAnnotation.name())) {
            throw new IllegalArgumentException("Factory name is empty");
        }

        this.factoryName = factoryAnnotation.name();
        int factoryPriority = factoryAnnotation.priority();
        if (factoryPriority > FactoryPriority.PRIORITY_HIGHEST) {
            this.factoryPriority = FactoryPriority.PRIORITY_HIGHEST;
        } else if (factoryPriority < FactoryPriority.PRIORITY_LOWEST) {
            this.factoryPriority = FactoryPriority.PRIORITY_LOWEST;
        } else {
            this.factoryPriority = factoryPriority;
        }
    }

    /**
     * 获取 Factory 声明的名称.
     * @return 返回 Factory 名称.
     */
    public String getFactoryName() {
        return factoryName;
    }

    /**
     * 获取 Factory 优先级.
     * @return 返回 Factory 的优先级.
     */
    public int getFactoryPriority() {
        return factoryPriority;
    }
}
