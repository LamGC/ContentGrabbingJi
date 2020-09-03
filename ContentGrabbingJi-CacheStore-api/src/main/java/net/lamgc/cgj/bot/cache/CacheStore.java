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

import java.util.Set;

/**
 * 缓存存储容器.
 * 缓存库之间不应该出现干扰的情况, 即使允许在实现上共用一个存储.
 * @author LamGC
 */
public interface CacheStore<V> {

    /**
     * 设置指定缓存的生存时间.
     * 当该缓存项的 TTL 为 0 时, 该缓存项将会失效, 并被删除.
     * 关于删除失效缓存项的时机在此并不特别规定, 依照各实现自行处理.
     * @param key 欲设置过期时间的缓存项键名.
     * @param ttl 有效期时间, 如果设为 -1 则代表清除缓存项的 TTL, 缓存项不会因为 TTL 为 0 而失效. 单位"毫秒(ms)".
     * @return 如果设置成功, 返回 true, 如果设置失败, 或缓存项不存在, 返回 false.
     * @throws NullPointerException 当 key 为 null 时抛出.
     */
    boolean setTimeToLive(String key, long ttl);

    /**
     * 查询指定缓存项的 TTL.
     * @param key 欲查询 TTL 的缓存项键名.
     * @return 如果缓存项存在且已设置 TTL, 则返回当前剩余 TTL, 如果缓存项不存在或未设置 TTL, 返回 -1.
     * @throws NullPointerException 当 key 为 null 时抛出.
     */
    long getTimeToLive(String key);

    /**
     * 获取当前缓存项数量.
     * @return 返回缓存项数量.
     */
    long size();

    /**
     * 清空缓存存储容器.
     * @return 操作成功返回 true.
     */
    boolean clear();

    /**
     * 检查指定缓存项是否存在.
     * 如果缓存项不存在或失效(比如因 TTL 为 0 而失效), 则会判定为不存在.
     * 缓存项失效不代表不存在(因为根据实现的不同, 可能还没来得及清理失效缓存项), 但缓存项不存在一定是失效的(即便如此, 缓存项一旦失效, 便不可获取).
     * 故后续除特殊情况使用"缓存项 失效"描述, 文档将以"缓存项不存在"描述缓存项失效或不存在的情况.
     * @param key 缓存项键名
     * @return 如果存在, 返回 true, 如果不存在或失效, 返回 false.
     * @throws NullPointerException 当 key 为 null 时抛出.
     */
    boolean exists(String key);

    /**
     * 删除指定缓存.
     * @param key 欲删除的缓存项键名.
     * @return 如果存在并删除成功, 返回 true.
     * @throws NullPointerException 当 key 为 null 时抛出.
     */
    boolean remove(String key);

    // 文档没有硬性要求"Set 中不能存在失效缓存项"的原因是因为: 即便确保了当时获取到的 Set 没有失效缓存项,
    //    但是如果在获取后立刻出现失效缓存项了呢? 这个情况下依然不能保证没有失效的缓存项, 故不在文档中做出该硬性要求.
    // 虽然是没说吧, 但是在返回时依然需要确保不会有过多失效缓存项的情况, 且获取时务必检查缓存项是否存在, 不要认为 Set 中的缓存项必然存在!
    /**
     * 获取缓存存储容器中的所有缓存项键名.
     * @return 返回存储了所有缓存项键名的 Set 对象.
     */
    Set<String> keySet();

}
