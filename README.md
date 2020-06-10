# ContentGrabbingJi #
Pixiv爬虫一只，同时也是一个机器人/插件！

## 支持的机器人平台 ##
- [Mirai](https://github.com/mamoe/mirai)
- [CoolQ](https://cqp.cc)(基于[`SpringCQ`](https://github.com/lz1998/spring-cq), 不支持多账号使用, 需要使用[`CQHttp`](https://cqhttp.cc/)插件)

## Usage ##
> 注意: 运行色图姬前, 你需要准备一个Pixiv账号的会话Cookie存储文件, 否则色图姬将无法运行.  
> 详见[PixivLoginProxyServer](https://github.com/LamGC/PixivLoginProxyServer)项目的[Readme](https://github.com/LamGC/PixivLoginProxyServer/blob/master/README.md).  

### Arguments ###
> ENV参数名为环境变量名, 用于给Docker容器提供设置方式.  

- 通用参数
  - `-proxy` / `ENV: CGJ_PROXY`: (**可选**) 设置代理
    - 格式: `协议://地址:端口`
    - 示例: `socks5://127.0.0.1:1080`
- 机器人参数
  - `-botDataDir` / `ENV: CGJ_BOT_DATA_DIR`: (**可选**) 设置`botMode`运行模式下机器人数据存储目录
    - 格式: `路径`
    - 示例: `./data`
    - 默认: `./`
  - `-redisAddress` / `ENV: CGJ_REDIS_URI`: (**必填, 计划支持可选**) Redis服务器地址
    - 格式: `地址:端口`
    - 示例: `127.0.0.1:6379`

> 例如以BotMode启动应用: `java -jar CGJ.jar botMode -proxy "socks5://127.0.0.1:1080 -redisAddress 127.0.0.1:6379`

### Commands ###
- `pluginMode`: CoolQ插件模式(依赖[酷Q机器人](https://cqp.cc/), 支持与CoolQ其他插件共存, 性能耗损大)
- `botMode`: Mirai独立模式(机器人独立运行, 不支持与其他插件共存, 性能耗损小)
- `collectionDownload`: 收藏下载, 以原图画质下载Cookie所属账号的所有收藏作品
- `getRecommends`: 将访问主页获得的推荐作品全部以原图画质下载
- `getRankingIllust`: 以原图画质下载指定排行榜类型的全部作品
- `search`: 搜索指定内容并获取相关作品信息(不下载)

> 注意: 除去机器人模式外, 其他功能后续可能会出现改动.

## LICENSE ##
本项目基于 `GNU Affero General Public License 3.0` 开源许可协议开源，  
你可以在本项目目录下的 `LICENSE` 文件查阅协议副本，  
或浏览 [https://www.gnu.org/licenses/agpl-3.0.html](https://www.gnu.org/licenses/agpl-3.0.html) 查阅协议副本。
```
    ContentGrabbingJi - A pixiv robot used in chat
    Copyright (C) 2020  LamGC (lam827@lamgc.net)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/agpl-3.0.html>.
```
尤其注意：根据协议，如果你基于本项目开发本项目的衍生版本并通过网络为他人提供服务，则必须遵守该协议，向 *服务的使用者* 提供 **为其服务的衍生版本** 的 **完整源代码**。
