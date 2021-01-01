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

package net.lamgc.cgj.bot.framework.message;

/**
 * 自定义 BotCode 方法.
 * @author LamGC
 */
public class CustomBotCodeFunction implements BotCodeFunction {

    private final String name;
    private final boolean headerFunction;


    public CustomBotCodeFunction(String name, boolean isHeaderFunction) {
        this.name = name;
        headerFunction = isHeaderFunction;
    }

    @Override
    public String getFunctionName() {
        return name;
    }

    @Override
    public boolean headerFunction() {
        return headerFunction;
    }
}
