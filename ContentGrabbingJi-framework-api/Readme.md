# ContentGrabbingJi-framework-api #
为了确保 ContentGrabbingJi 能够为多个平台的用户提供服务，
我为 ContentGrabbingJi 设计了一套**框架抽象层**（`Framework Abstraction Layer`，简称 `FAL`）。  
FAL 用于为各个平台的框架提供统一的接口，保证了 ContentGrabbingJi 对各个平台框架的兼容性，
并由 ContentGrabbingJi 统一管理其**生命周期**。  
> 备注：当 QQ 机器人发生大动摇（CoolQ 机器人因特别原因关闭，导致大量机器人平台接连关闭）的时候，
>我更加确信了创建 FAL 的决定是正确的。

开始开发一个框架组件: ![Quick-Start](./Quick-Start.md)

## 框架的生命周期 ##
ContentGrabbingJi 的框架系统基于 [Pf4j](https://github.com/pf4j/pf4j) 。  
框架的生命周期将由 ContentGrabbingJi 管理。  
由于框架系统基于 Pf4j，所以框架拥有以下生命周期：
- `CREATED`：框架已被识别并确定可以进行加载（此时框架文件已被读取并成功解析了 FrameworkDescriptor）。
- `DISABLED`：框架虽然被加载，但由于某些原因（例如不符合系统最低版本要求，被手动禁用）而被禁用。
- `RESOLVED`：框架已经成功加载并做好了启动的准备，此时框架已经初始化完成。
- `STARTED`：框架的 `start()` 被成功调用，视为已启动，此时框架可开始接收处理用户的使用请求。
- `STOPPED`：框架的 `stop()` 被成功调用，视为已停止，
此时框架应该停止接收处理请求，关闭与处理相关的功能（例如断开平台连接，停止与第三方机器人平台的通讯等）。
- `FAILED`：框架启动时出错。

由于 ContentGrabbingJi 并不能完全控制框架的运行和启停，
因此希望框架能够遵守生命周期（如果平台的连接无法关闭，或关闭后重新连接会很麻烦，那么你可以停止向框架投递事件，以达到停止工作的效果）。

