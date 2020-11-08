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

import java.util.Objects;

/**
 * 框架作者信息.
 * <p>我觉得改成维护者会更好一些?
 * @author LamGC
 */
public class Author {

    private final String name;
    private final String url;
    private final String email;

    public Author(String name) {
        this(name, null, null);
    }

    public Author(String name, String url, String email) {
        this.name = Objects.requireNonNull(name);
        this.url = url;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "Author{" +
                "name='" + getName() + '\'' +
                ", url='" + getUrl() + '\'' +
                ", email='" + getEmail() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Author author = (Author) o;
        return name.equals(author.name) &&
                Objects.equals(url, author.url) &&
                Objects.equals(email, author.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, url, email);
    }
}
