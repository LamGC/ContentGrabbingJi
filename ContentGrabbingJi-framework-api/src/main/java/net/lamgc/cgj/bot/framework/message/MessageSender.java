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

import net.lamgc.cgj.bot.framework.Platform;
import net.lamgc.cgj.bot.framework.message.exception.UploadImageException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * 消息发送者.
 * <p> 可通过该对象回复发送者, 或是获取来自该发送者消息中,
 * 通过消息内 {@link BotCode} 解析而来的消息资源(例如图片, 视频, 语音, 文件等).
 * <p>发送者的识别依靠三个要素:
 *  <ul>
 *      <li> {@link Platform}
 *      <li> {@link MessageSource}
 *      <li> {@link #getId() SenderId}
 *  </ul>
 *  三个要素全部符合即为同一个联系人, 有以下情况:
 *  <ul>
 *      <li> Platform 相同, MessageSource 相同, 则 SenderId 不允许相同;
 *      <li> Platform 相同, SenderId 相同, 则 MessageSender 不允许相同;
 *      <li> MessageSender 相同, SenderId 相同, 则 Platform 不允许相同;
 *  </ul>
 * @author LamGC
 */
public interface MessageSender {

    /**
     * 获取平台信息.
     * @return 返回平台信息.
     */
    Platform getPlatform();

    /**
     * 获取消息源类型.
     * @return 返回消息源类型, 不允许返回 null.
     */
    MessageSource getSource();

    /**
     * 获取消息源 Id.
     * @return 返回消息源 Id, 同一种消息源中不允许有两个相同的 Id.
     */
    long getId();

    /**
     * 发送消息
     * @param message 消息内容, 特殊内容将以功能码形式插入内容中.
     * @return 如果成功返回 0 或消息 Id, 发送失败返回负数代表错误码.
     */
    int sendMessage(Message message);

    /**
     * 获取图片Url
     * @param imageIdentify 图片标识
     * @return 返回图片Url
     */
    URL getImageUrl(String imageIdentify);

    /**
     * 获取图片输入流
     * @param imageIdentify 图片标识
     * @return 返回图片输入流.
     * @throws IOException 当输入流获取发生异常时可抛出.
     */
    default InputStream getImageAsInputStream(String imageIdentify) throws IOException {
        URL imageUrl = getImageUrl(imageIdentify);
        if (imageUrl == null) {
            return null;
        }
        URLConnection connection = imageUrl.openConnection();
        connection.setDoInput(true);
        if(connection instanceof HttpURLConnection) {
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.connect();
            if(httpConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException("Http response error: " +
                        httpConnection.getResponseCode() + " " + httpConnection.getResponseMessage());
            }
            return httpConnection.getInputStream();
        } else {
            connection.connect();
            return connection.getInputStream();
        }
    }

    /**
     * 上传图片.
     * @param imageInput 图片输入流
     * @return 返回图片的标识, 如果平台无提供相关标识, 可能需要框架内部处理;
     *         返回的标识将会在需要发送时, 以<pre>[Platform:image,id=图片标识]</pre>的形式进行指示;
     *         标识会在内部加入平台标识, 除非平台自带, 否则无需自行添加.
     * @throws UploadImageException 如果图片上传时发生异常可抛出.
     */
    String uploadImage(InputStream imageInput) throws UploadImageException;

    /**
     * 上传图片.
     * @param imageFile 图片文件
     * @return 返回图片的标识, 如果平台无提供相关标识, 可能需要框架内部处理.
     * @throws UploadImageException 如果图片上传时发生异常可抛出.
     */
    default String uploadImage(File imageFile) throws UploadImageException {
        if (!imageFile.exists()) {
            throw new UploadImageException(new FileNotFoundException(imageFile.getAbsolutePath()));
        }
        try (InputStream imageInput = new FileInputStream(imageFile)) {
            return uploadImage(imageInput);
        } catch(Exception e) {
            throw new UploadImageException("Image upload exception", e);
        }
    }

}
