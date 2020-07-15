# ContentGrabbingJi - 色图姬 #
一个以高性能、高效率为目标，多平台/框架支持、持续维护的Pixiv聊天机器人  

色图姬存在的目的最初是作为**爬虫 + 机器人**的形式开发，在开发过程中逐渐以多聊天平台，高效率为目标进行。

## 安装 ##
### 通过Jar文件部署 ###
1. 从项目的[版本页面](https://github.com/LamGC/ContentGrabbingJi/releases)下载最新版色图姬主程序jar文件
2. 准备一个目录, 用于存放运行数据(这里以`./runData`为例子).
3. 将通过PixivLoginProxyServer获得的Pixiv登录会话文件放置在目录中(`./runData/cookies.store`).
4. 使用命令`java -jar <CGJ.jar> buildPassword -password "QQ机器人账号的密码"`构造一个登录用的密码.
5. 在数据目录创建一个配置文件`bot.properties`, 并添加如下内容:
    ```properties
    bot.qq=<机器人QQ账号>
    bot.password=<通过buildPassword获得的密码>
    ```
6. 在数据目录创建新文件夹`setting`, 并创建一个全局配置文件`global.properties`, 然后设置如下内容:
    ```properties
    # 该配置为全局配置文件, 当群组没有特定配置时, 将使用全局配置.
    # 管理员QQ (必填)
    admin.adminId=<管理员QQ号>
    # 是否允许r18作品
    image.allowR18=false
    # 查询排行榜默认的长度(比如15就是发送1~15名的作品), 该选项请适当调整, 设置过长可能导致超出聊天平台的最长消息长度, 导致发送失败!
    ranking.itemCountLimit=15
    # 排行榜图片数量(比如排行榜长度为15, 可以设置前10名有图片, 后5名没有图片), 调整该配置可有效控制消息发送所需时间.
    ranking.imageCountLimit=15
    # 搜索结果缓存时间, 默认2小时, 单位毫秒
    cache.searchBody.expire=7200000
    # 搜索结果长度. 该选项请适当调整, 设置过长可能导致超出聊天平台的最长消息长度, 导致发送失败!
    search.itemCountLimit=8
    ```
7. 配置完成后, 准备一台Redis服务端, 用于缓存数据.
8. Redis服务器准备好后, 使用命令启动色图姬：`java -jar <CGJ.jar> botMode -botDataDir <数据目录> -redisAddress <Redis服务器地址> [-proxy 代理服务器地址]`
9. 完成！好好享受！

### 通过Docker部署 ###
使用Docker将可以更好的管理色图姬所使用的资源，和管理色图姬的运行。
(正在完善中...)

## 使用 ##
### 普通用户 ###
将色图姬部署完成，并且让色图姬在某个平台登录完成后，你就可以通过聊天平台向色图姬发起会话了！  
使用 `.cgj` 向色图姬询问帮助信息！  

另外，由于色图姬在开发过程中直接使用了原本应用在命令行中的参数解析工具，所以你需要了解一下色图姬命令的格式，  
色图姬的命令格式为：
```bash
.cgj <命令> -<参数名> <参数值> ...
```
如果色图姬无法识别你的命令，那么它会发送一次帮助信息给你。

### 管理员用户 ###
你应该注意到了，在部署过程中，你需要设置一个管理员QQ的配置，色图姬支持通过命令来管理色图姬的运行。  
目前支持的管理员命令：
```bash
# 清除缓存(慎用)
# 该操作将会清除Redis服务器内的所有数据, 以及色图姬下载到本地的所有图片缓存.
.cgjadmin cleanCache

# 设置配置项
# 如果不使用group参数, 则设置全局配置
# 注意: 配置项设置后需要使用`.cgjadmin saveProperties`才会保存到文件中, 
# 如不保存, 则仅能在本次运行中生效(或使用`.cgjadmin loadProperties`重新加载后失效).
.cgjadmin setProperty <-key 配置项名> <-value 配置项新值> [-group 指定群组]

# 查询配置项
# 如果不使用group参数, 则查询全局配置
.cgjadmin getProperty <-key 配置项名> [-group 指定群组]

# 保存所有配置
.cgjadmin saveProperties

# 读取所有配置
# 使用 reload 参数将会重载所有配置, 而不是覆盖读取
.cgjadmin loadProperties [-reload]

# 运行定时更新任务
# 可指定要更新数据的日期
.cgjadmin runUpdateTask [-date yyyy-MM-dd]

# 增加群组作品推送
# 如果增加了original参数, 则图片为原图发送
# 如果不指定group参数, 则群组为命令发送所在群组
# 最长发送时间 = 最短发送时间 + 随机时间范围
.cgjadmin addPushGroup [-group 指定群组号] [-minTime 最短发送时间] [-floatTime 随机时间范围] [-rankingStart 排行榜起始排名] 
                       [-rankingStop 排行榜结束排名] [-mode 排行榜模式] [-type 排行榜类型] [-original]

# 删除群组推送功能
# 如果不指定group参数, 则群组为命令发送所在群组
.cgjadmin removePushGroup [-group 指定群组号]

# 加载作品推送配置
.cgjadmin loadPushList

# 保存作品推送配置
.cgjadmin savePushList

# 获取被报告的作品列表
# 该命令会返回被其他用户报告其存在问题的作品列表
.cgjadmin getReportList

# 解封被报告的作品
.cgjadmin unBanArtwork <-id 被ban作品的Id>

```

## 贡献 ##
向色图姬贡献不一定需要编程知识，向色图姬项目提出意见，反馈问题同样会为色图姬项目提供很大的帮助！  
如果你在使用色图姬的过程中，遇到了Bug，可以通过色图姬项目的**Issues**使用[Bug反馈模板](https://github.com/LamGC/ContentGrabbingJi/issues/new?assignees=&labels=bug&template=Bug_Report.md&title=)向色图姬提供Bug信息。  
如果是为色图姬提供一些新功能想法，或者对色图姬有什么意见，则可以直接通过Issues发起讨论。

如果你会Java开发，又想为色图姬提供一些新功能，可以通过Fork仓库的方法，实现后发起PR，合并到色图姬项目中！  
向色图姬贡献代码，需要遵循一些贡献事项，如果你的代码不符合这些事项，PR有可能会被关闭！  
> 注意：色图姬的初衷并没有任何恶意的意图，如果尝试向色图姬提供恶意功能或代码，PR将会被拒绝、关闭。

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
