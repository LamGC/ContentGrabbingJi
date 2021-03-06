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

import org.pf4j.Plugin;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginWrapper;

import java.io.File;
import java.io.InputStream;

/**
 * 框架抽象类.
 * @author LamGC
 */
public abstract class Framework extends Plugin {

    private final File dataFolder;
    private final FrameworkContext context;
    /**
     * 这个变量是防止原 {@link Plugin#wrapper} 遭到修改而存在.
     * <p> 很迷惑为什么 {@link Plugin#getWrapper()} 加了 {@code final} 关键字而 {@link Plugin#wrapper} 却不添加 {@code final} 关键字.
     */
    private final PluginWrapper _wrapper;

    /**
     * 由 FrameworkManager 执行的构造方法.
     * <p> 不要在构造方法内做任何处理. 如果你需要, 请在 {@link #initial()} 进行初始化.
     *
     * @param wrapper 包含框架运行期间需要使用对象的包装器.
     * @param context 框架运行上下文, 由不同 ContentGrabbingJi 实例加载的 Framework 所获得的的 Context 是不一样的.
     * @param dataFolder 框架专属的数据存取目录.
     */
    public Framework(PluginWrapper wrapper, File dataFolder, FrameworkContext context) {
        super(wrapper);
        this._wrapper = wrapper;
        this.context = context;
        if (!(wrapper.getDescriptor() instanceof FrameworkDescriptor)) {
            throw new IllegalStateException("Invalid description object");
        }
        this.dataFolder = dataFolder;
    }

    /**
     * 执行初始化操作.
     * <p> 警告: 请不要在初始化过程中调用 {@link org.pf4j.PluginManager}
     *      的任何插件管理方法, 这将会导致加载错误.
     */
    protected abstract void initial();

    /**
     * 获取仅属于该框架的数据存储目录.
     *
     * <p> 调用本方法将会检查目录是否存在, 并在目录不存在时尝试创建.
     * <p> 请不要在除数据存储目录外的其他位置存储数据, 这将使用户感到困扰!
     *
     * @return 返回数据存储目录.
     */
    public final File getDataFolder() {
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            log.warn("框架 {} 数据目录创建失败, 请检查是否为应用数据目录提供了读写权限.", getDescriptor().getPluginId());
        }
        return dataFolder;
    }

    /**
     * 获取框架描述对象.
     * @return 返回框架描述对象.
     */
    public final FrameworkDescriptor getDescriptor() {
        PluginDescriptor descriptor = _wrapper.getDescriptor();
        if (descriptor instanceof FrameworkDescriptor) {
            return (FrameworkDescriptor) descriptor;
        }
        throw new ClassCastException("无法转换 Descriptor 的类型, " +
                "可能是第三方 FrameworkManager 并未传入 FrameworkDescriptor 或 FrameworkManager 遭到修改!");
    }

    /**
     * 获取框架所属平台.
     * <p> 等效于
     *      <pre> {@link #getDescriptor()}.{@link FrameworkDescriptor#getPlatform() getPlatform()}
     * @return 返回平台对象.
     */
    public final Platform getPlatform() {
        return getDescriptor().getPlatform();
    }

    /**
     * 获取当前框架对象与所属 ContentGrabbingJiBot 的上下文.
     * <p> 不同的 ContentGrabbingJiBot 实例所对应的 Context 是不一样的,
     *     除特殊情况外请不要混用(甚至在任何情况下都不要混用).
     * @return 返回上下文对象.
     */
    protected final FrameworkContext getContext() {
        return context;
    }

    /**
     * 通过 Framework 所属 ClassLoader 从框架本体(比如 Jar, Zip 或者文件夹内)获取框架资源.
     * <p> 如需获取资源请使用本方法, 单纯使用 {@link ClassLoader#getSystemClassLoader()} 来获取资源会导致无法获取.
     * @param name 资源名称.
     * @return 返回资源输入流, 如果资源不存在时返回 {@code null}.
     */
    protected final InputStream getFrameworkResourceAsStream(String name) {
        return _wrapper.getPluginClassLoader().getResourceAsStream(name);
    }

}
