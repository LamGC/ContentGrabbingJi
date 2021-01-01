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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * BotCode 描述对象.
 * @author LamGC
 */
public final class BotCodeDescriptor {

    private final List<Pattern> patterns;

    public BotCodeDescriptor(List<String> patternStrings) {
        List<Pattern> patterns = new ArrayList<>();
        for (String patternString : patternStrings) {
            patterns.add(Pattern.compile(patternString));
        }
        this.patterns = patterns;
    }

    public List<Pattern> getPatterns() {
        return patterns;
    }

}
