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
 * Factory 优先级常量.
 * @author LamGC
 */
public final class FactoryPriority {

    /**
     * 最高优先级
     * <p> 优先级数值: 10
     */
    public final static int PRIORITY_HIGHEST = 10;

    /**
     * 较高优先级
     * <p> 优先级数值: 8
     */
    public final static int PRIORITY_HIGHER = 8;

    /**
     * 普通优先级
     * <p> 优先级数值: 5
     */
    public final static int PRIORITY_NORMAL = 5;

    /**
     * 较低优先级
     * <p> 优先级数值: 3
     */
    public final static int PRIORITY_LOWER = 3;

    /**
     * 最高优先级
     * <p> 优先级数值: 0
     */
    public final static int PRIORITY_LOWEST = 0;
    
}
