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

package net.lamgc.cgj.bot.event;

import net.lamgc.cgj.bot.event.handler.IllegalHandler;
import net.lamgc.cgj.bot.event.handler.StandardHandler;
import net.lamgc.cgj.bot.event.object.TestEvent;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * @see EventUtils
 */
public class EventUtilsTest {

    @Test
    public void supportedCancelCheckTest() {
        class NonSupportedCancelClass implements EventExecutor {
            @Override
            public void execute(EventObject event) {

            }

            @Override
            public boolean isAsync() {
                return false;
            }
        }
        class SupportedCancelClass implements SupportedCancel, EventExecutor {

            @Override
            public boolean cancelEvent(UUID eventId) throws UnsupportedOperationException, NoSuchElementException {
                return false;
            }

            @Override
            public boolean cancelEvent(EventObject event) throws UnsupportedOperationException, NoSuchElementException {
                return false;
            }

            @Override
            public boolean cancelEvent(Cancelable cancelableEvent) throws NoSuchElementException {
                return false;
            }

            @Override
            public void execute(EventObject event) {

            }

            @Override
            public boolean isAsync() {
                return false;
            }
        }

        Assert.assertTrue(EventUtils.isSupportedCancel(new SupportedCancelClass()));
        Assert.assertFalse(EventUtils.isSupportedCancel(new NonSupportedCancelClass()));
    }

    @Test
    public void standardHandlerMethodCheckTest() throws NoSuchMethodException {
        Method targetMethod = StandardHandler.class.getMethod("standardHandle", TestEvent.class);
        Assert.assertTrue(EventUtils.checkEventHandlerMethod(targetMethod));
    }

    @Test
    public void invalidHandlerMethodCheckTest() {
        for (Method method : IllegalHandler.class.getDeclaredMethods()) {
            Assert.assertFalse(EventUtils.checkEventHandlerMethod(method));
        }
    }

}
