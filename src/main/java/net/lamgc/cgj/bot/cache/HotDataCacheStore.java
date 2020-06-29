package net.lamgc.cgj.bot.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 具有继承性的热点数据缓存库
 * @param <T> 存储类型
 * @author LamGC
 */
public class HotDataCacheStore<T> implements CacheStore<T>, Cleanable {

    private final CacheStore<T> parent;
    private final CacheStore<T> current;
    private final long expireTime;
    private final int expireFloatRange;
    private final Random random = new Random();
    private final Logger log = LoggerFactory.getLogger(this.toString());

    /**
     * 构造热点缓存存储对象
     * @param parent 上级缓存存储库
     * @param current 热点缓存存储库, 最好使用本地缓存(例如 {@linkplain LocalHashCacheStore LocalHashCacheStore})
     * @param expireTime 本地缓存库的缓存项过期时间, 单位毫秒;
 *                   该时间并不是所有缓存项的最终过期时间, 还需要根据expireFloatRange的设定随机设置, 公式:
 *                   {@code expireTime + new Random().nextInt(expireFloatRange)}
     * @param expireFloatRange 过期时间的浮动范围(单位毫秒), 用于防止短时间内大量缓存项失效导致的缓存雪崩,
     *                         如设置为0或负数, 则不启用浮动范围.
     * @param autoClean 是否交由{@link AutoCleanTimer}自动执行清理, 启用后, AutoCleanTimer会自动检查过期Key并进行删除.
     */
    public HotDataCacheStore(CacheStore<T> parent, CacheStore<T> current, long expireTime, int expireFloatRange, boolean autoClean) {
        this.parent = parent;
        this.current = current;
        this.expireTime = expireTime;
        this.expireFloatRange = expireFloatRange;
        if(autoClean) {
            AutoCleanTimer.add(this);
        }

        log.trace("HotDataCacheStore初始化完成. " +
                        "(Parent: {}, Current: {}, expireTime: {}, expireFloatRange: {}, autoClean: {})",
                parent, current, expireTime, expireFloatRange, autoClean);
    }

    @Override
    public void update(String key, T value, long expire) {
        update(key, value, expire <= 0 ? null : new Date(System.currentTimeMillis() + expire));
    }

    @Override
    public void update(String key, T value, Date expire) {
        parent.update(key, value, expire);
        current.update(key, value, expire);
    }

    @Override
    public T getCache(String key) {
        if(!exists(key)) {
            log.trace("查询缓存键名不存在, 直接返回null.");
            return null;
        }
        T result = current.getCache(key);
        if(Objects.isNull(result)) {
            log.trace("Current缓存库未命中, 查询Parent缓存库");
            T parentResult = parent.getCache(key);
            if(Objects.isNull(parentResult)) {
                log.trace("Parent缓存库未命中, 缓存不存在");
                return null;
            }
            log.trace("Parent缓存命中, 正在更新Current缓存库...");
            current.update(key, parentResult,
                    expireTime + (expireFloatRange <= 0 ? 0 : random.nextInt(expireFloatRange)));
            log.trace("Current缓存库更新完成.");
            result = parentResult;
        } else {
            // 更新该Key的过期时间
            current.update(key, result,
                    expireTime + (expireFloatRange <= 0 ? 0 : random.nextInt(expireFloatRange)));
            log.trace("Current缓存库缓存命中.");
        }
        return result;
    }

    @Override
    public T getCache(String key, long index, long length) {
        return getCache(key);
    }

    @Override
    public boolean exists(String key) {
        return current.exists(key) || parent.exists(key);
    }

    @Override
    public boolean exists(String key, Date date) {
        return current.exists(key, date) || parent.exists(key, date);
    }

    @Override
    public long length(String key) {
        return -1;
    }

    @Override
    public boolean clear() {
        return current.clear();
    }

    @Override
    public Set<String> keys() {
        Set<String> keys = new HashSet<>();
        keys.addAll(current.keys());
        keys.addAll(parent.keys());
        return keys;
    }

    @Override
    public boolean remove(String key) {
        parent.remove(key);
        current.remove(key);
        return true;
    }

    @Override
    public boolean supportedPersistence() {
        // 由于Current的缓存数据会更新到Parent上,
        // 所以只要任意一边支持持久化, 那么该缓存库就支持持久化
        return current.supportedPersistence() || parent.supportedPersistence();
    }

    @Override
    public boolean supportedList() {
        // 只有两边都支持List, 该缓存库才会支持持久化
        return current.supportedList() && parent.supportedList();
    }

    /**
     * 检查并清理已过期的Entry.
     * <p>该方法仅清理Current缓存库, 不会对上游缓存库造成影响.</p>
     */
    @Override
    public void clean() throws Exception {
        if(current instanceof Cleanable) {
            ((Cleanable) current).clean();
        } else {
            for(String key : this.current.keys()) {
                if (!current.exists(key)) {
                    current.remove(key);
                }
            }
        }
    }
}
