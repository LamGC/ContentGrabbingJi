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

package net.lamgc.cgj.bot.event.handler;

import net.lamgc.cgj.bot.event.EventHandler;
import net.lamgc.cgj.bot.event.object.TestEvent;

public abstract class IllegalHandler {

    @EventHandler
    public void nonArgumentHandleMethod() {

    }

    @EventHandler
    public abstract void abstractHandleMethod(TestEvent event);

    @EventHandler
    public static void staticHandleMethod(TestEvent event) {

    }

    @EventHandler
    private void privateHandleMethod(TestEvent event) {

    }

    public void nonAnnotationHandle() {

    }

    @EventHandler
    public void multiArgumentsHandleMethod(TestEvent event, Object object) {

    }

    @EventHandler
    public void invalidArgumentMethod(Object object) {

    }

    @EventHandler
    public Object invalidReturnTypeMethod(TestEvent event) {
        return null;
    }

}
