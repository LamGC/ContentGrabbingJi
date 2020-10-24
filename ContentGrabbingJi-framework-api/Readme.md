# ContentGrabbingJi-framework-api #
为了确保 ContentGrabbingJi 能够为多个平台的用户提供服务，
我为 ContentGrabbingJi 设计了一套**框架抽象层**（`Framework Abstraction Layer`，简称 `FAL`）。  
FAL 用于为各个平台的框架提供统一的接口，保证了 ContentGrabbingJi 对各个平台框架的兼容性，
并由 ContentGrabbingJi 统一管理其**生命周期**。  
> 备注：当 QQ 机器人发生大动摇（CoolQ 机器人因个别原因关闭）的时候，我更佳确信了创建 FAL 的决定是正确的。

...
