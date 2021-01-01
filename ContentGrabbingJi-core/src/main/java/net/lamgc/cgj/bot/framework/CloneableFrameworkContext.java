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

package net.lamgc.cgj.bot.framework;

/**
 * 可克隆的 Context.
 * @author LamGC
 */
public abstract class CloneableFrameworkContext implements FrameworkContext {

    /**
     * 克隆一个成员对象相同的全新 Context.
     * @return 返回新的 FrameworkContext, 该 Context 不能与当前 Context 相同对象.
     */
    protected abstract FrameworkContext cloneContext();

}
