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

package net.lamgc.cgj.bot.framework.message.exception;

import java.io.File;

/**
 * 图片上传异常.
 * 当图片上传发生异常时抛出, 需附带原因.
 * @author LamGC
 * @see net.lamgc.cgj.bot.framework.message.MessageSender
 * @see net.lamgc.cgj.bot.framework.message.MessageSender#uploadImage(File)
 * @see net.lamgc.cgj.bot.framework.message.MessageSender#uploadImage(java.io.InputStream)
 */
public class UploadImageException extends Exception {

    public UploadImageException(String message) {
        super(message);
    }

    public UploadImageException(String message, Throwable cause) {
        super(message, cause);
    }

    public UploadImageException(Throwable cause) {
        super(cause);
    }
}
