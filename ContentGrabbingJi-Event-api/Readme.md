# Event 模块 #
Event 模块用于定义 ContentGrabbingJi 所使用事件系统的规范。  
通过定义事件系统的规范接口，可增加项目模块配置的灵活性。  

虽说如此，但 Event 模块并未计划允许用户任意更换，仅允许于开发阶段更换。

## 组件 ##
### EventExecutor 事件执行器
事件执行器用于接收任意事件，并将事件交给可处理该事件的对象方法。  
根据实现不同，事件执行器可能是同步的，也可能是异步的，需检查 `isAsync` 方法返回值以了解相关信息。

### EventObject 事件对象
当某一对象实现 `EventObject` 时，对象即为事件对象，
事件对象可交由事件执行器分发到处理方法中。

### HandlerRegistry 事件处理注册器
用于注册并存储所有 EventHandler 内符合条件的处理方法。  
在 EventExecutor 需要时将会请求可处理事件的所有处理方法。

### EventHandler 事件处理器
当某一对象实现 `EventHandler` 时，则表明该对象包含用于处理指定事件的处理方法。  
HandlerRegistry 将会扫描内部方法，并将符合要求的方法注册为处理方法。

### EventObject 事件对象
事件对象接口，实现了该接口的对象将视为一种事件对象。  
事件对象可通过 EventExecutor 投递到任何可处理该事件的处理方法进行处理。

### AbstractEventObject 抽象事件对象
实现了 EventObject 中 `getEventId` 方法的抽象对象。

### Cancelable 可取消事件
当事件实现了该接口时，代表事件可以被取消。  
当事件被取消后，EventExecutor 将会终止处理该事件，同时事件将通知所有已注册的 Observer。

### SupportedCancel 支持取消接口
当 EventExecutor 实现该接口时，表明 EventExecutor 拥有在事件取消时终止事件的能力，  
需要注意的是，即使 EventExecutor 未实现该接口，也不一定代表 EventExecutor 不支持终止事件的处理。

## 相关实现 ##
目前已有默认的实现存在于 Core 模块中，目前暂不需要开发其他实现。
