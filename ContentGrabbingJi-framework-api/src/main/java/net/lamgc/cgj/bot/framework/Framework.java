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

import org.pf4j.Plugin;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;

import java.io.File;

/**
 * 框架抽象类.
 * @author LamGC
 */
public abstract class Framework extends Plugin {

    private final File dataFolder;

    /**
     * 由 FrameworkManager 执行的构造方法.
     * <p>不要在构造方法内做任何处理. 如果你需要, 请在 {@link #initial()} 进行初始化.
     *
     * @param wrapper 包含框架运行期间需要使用对象的包装器.
     */
    public Framework(PluginWrapper wrapper, File dataFolder) {
        super(wrapper);
        this.dataFolder = dataFolder;
        try {
            initial();
        } catch (Throwable e) {
            wrapper.setFailedException(e);
            wrapper.setPluginState(PluginState.FAILED);
            log.error("An exception occurred while initializing the framework", e);
        }
    }

    /**
     * 执行初始化操作.
     */
    protected abstract void initial();

    /**
     * 获取仅属于该框架的数据存储目录.
     *
     * <p>调用本方法将会检查目录是否存在, 并在目录不存在时尝试创建.
     * <p>请不要在除数据存储目录外的其他位置存储数据, 这将使用户感到困扰!
     *
     * @return 返回数据存储目录.
     */
    public File getDataFolder() {
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            log.warn("框架 {} 数据目录创建失败.", getDescriptor().getPluginId());
        }
        return dataFolder;
    }

    /**
     * 获取框架描述对象.
     * @return 返回框架描述对象.
     */
    public FrameworkDescriptor getDescriptor() {
        PluginDescriptor descriptor = getWrapper().getDescriptor();
        if (descriptor instanceof FrameworkDescriptor) {
            return (FrameworkDescriptor) descriptor;
        }
        throw new IllegalStateException("无法转换 Descriptor 的类型, 框架管理器可能遭到修改!");
    }

}
