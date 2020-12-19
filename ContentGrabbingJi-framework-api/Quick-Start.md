# Quick Start：开发一个框架组件 #
> 提示：标题写成“框架组件”是有原因的，可能你并不是真的开发了某个平台的框架，
>而是开发了一个将 ContentGrabbingJi 与某平台框架连接在一起的一个组件，在这块地方纠结下去没有意义。

注意，开发框架组件，你不仅需要 Java （或其他与 Java 有兼容性的语言）的开发经验，你还需要会使用 Maven，Gradle 这些编译工具。  
一旦你准备就绪了，就可以开始开发一个框架组件了！  

> 嘿！如果你想开发一个框架组件，然后放出来给大家使用，那么你就要对这个框架组件负责点，
>因为大家会使用你的组件，就代表对你，和你的组件有信心，有期望，认为你做的很好，
>所以你不能辜负大家对你的期望，坚持维护框架组件，或者当你无法继续维护组件（项目）时，
>大胆的跟大家说明清楚，对框架组件项目存档，或者转手让一个信得过的人帮你继续维护它！

## 开始 ##
开始时，你需要创建一个 `framework.json` 来描述你的框架，告诉用户这个框架组件叫什么，有什么用途，谁做的，适配了什么平台。
一个完整的 `framework.json` 示例如下（使用 ContentGrabbingJi 官方开发的 QQ-Mirai 框架组件作为示例）：
```json5
{
  // 【必填】框架组件的 Id，必须是唯一的。（只能用大小写字母和半角符号，最好只用"_"、"-"和"."）
  "id": "cgj-mirai",
  //【可选】 对框架组件的说明内容，比如介绍这个组件，使用组件需要注意什么.....之类的。
  "description": "ContentGrabbingJi官方实现的, 基于 Mirai 框架的QQ平台组件",
  // 【必填】框架组件的版本号，建议遵循 语义化版本控制 规范。
  "version": "3.0.0-alpha",
  // 【可选】对 ContentGrabbingJi 的最低要求版本。
  "requiresVersion": "3.0.0",
  // 【可选】组件由谁提供（例如：xxx开发者，xxx工作室）。
  "provider": "Github@LamGC, Github@mamoe",
  // 【可选】组件以什么协议提供使用？
  "license": "AGPL-3.0",
  // 【必填】组件的主类是什么？（带有包名的类名）
  "frameworkClass": "net.lamgc.cgj.bot.framework.mirai.MiraiFramework",
  // 【可选】组件需要依赖什么其他的组件？（填写其他框架组件的 Id）
  "dependencies": [
  ],
  // 【必填】组件所支持的平台是什么？（规范请参见【平台信息】章节）
  // 如果不需要处理消息，无用户命令交互，可设为【Unknown】（identify 为【unknown】，小写的就好）
  "platform": {
    "name": "Tencent QQ",
    "identify": "qq"
  },
  // 【可选】开发组件的作者（如果人数过多，可以只填写主要开发者，不一定要填全，看你们选择）
  "authors": [
    {
      // 【必填】该作者的名称
      "name": "LamGC",
      // 【可选】有没有相关的网页？（比如 Github、Blog 等）
      "url": "https://github.com/LamGC",
      // 【可选】联系该作者所使用的邮箱。
      "email": "lam827@lamgc.net"
    }
  ],
  // 【可选】如果框架使用文本机器人功能码（比如【CQ码】之类的）的信息。
  "botCode": {
     // 【可选】如果有相关功能码，填写所有功能码格式的匹配用正则表达式。
    "patterns": [
      "(?:\\[mirai:([^:]+)\\])",
      "(?:\\[mirai:([^\\]]*)?:(.*?)?\\])",
      "(?:\\[mirai:([^\\]]*)?(:(.*?))*?\\])"
    ]
  }
}
```

也就是说，你需要准备的最小的 `framework.json` 必须是这样的：
```json5
{
  // 【必填】框架组件的 Id，必须是唯一的。
  "id": "cgj-mirai",
  // 【必填】框架组件的版本号，建议遵循 语义化版本控制 规范。
  "version": "3.0.0-alpha",
  // 【必填】组件的主类是什么？（带有包名的类名）
  "frameworkClass": "net.lamgc.cgj.bot.framework.mirai.MiraiFramework",
  // 【必填】组件所支持的平台是什么？（规范请参见【平台信息】章节）
  "platform": {
    "name": "Tencent QQ",
    "identify": "qq"
  }
}
```
只要 `framework.json` 配置了以上内容，ContentGrabbingJi 就会将其视为一个框架组件，并加载启动。

## 框架的主类 ##
光是编写了 `framework.json` 还远远不够，你还需要一个框架主类来让 ContentGrabbingJi 启动你的框架组件。  
框架主类并不像一般的 Java 应用一样，在某个类里写上 `main(String[])` 方法就可以了，你需要继承 `Frameowrk` 类。
```java
package org.example;

import net.lamgc.cgj.bot.framework.Framework;
import net.lamgc.cgj.bot.framework.FrameworkContext;
import net.lamgc.cgj.bot.framework.FrameworkDescriptor;
import org.pf4j.PluginWrapper;

import java.io.File;

class SimpleFrameworkMain extends Framework {
    
    /**
     * 由 FrameworkManager 执行的构造方法.
     * <p> 不要在构造方法内做任何处理. 如果你需要, 请在 {@link #initial()} 进行初始化.
     *
     * @param wrapper    包含框架运行期间需要使用对象的包装器.
     * @param dataFolder 框架专属的数据存取目录.
     * @param context    框架运行上下文, 由不同 ContentGrabbingJi 实例加载的 Framework 所获得的的 Context 是不一样的.
     */
    public SimpleFrameworkMain(PluginWrapper wrapper, File dataFolder, FrameworkContext context) {
        super(wrapper, dataFolder, context);
        // 看到上面的文档说了什么了吗？不要在这里做任何的操作！（即使你想初始化组件）
    }

    @Override
    protected void initial() {
        // 这里是初始化方法！当你的框架组件被加载后，首先会调用这个方法。
        // 可以在这里执行任何初始化的操作，但注意：不要让你的框架组件开始工作，因为时候未到！

        // 还记得你刚刚写的 framework.json 吗，ContentGrabbingJi 并没有忘记它。
        // framework.json 将会直接解析并生成一个 FrameworkDescriptor 对象，
        // 你可以通过【getDescriptor()】获取它，通过这个对象获取你在 framework.json 编写的任何内容。
        FrameworkDescriptor descriptor = getDescriptor();

        // Framework 提供了一个日志记录器，可以很方便的记录日志内容。
        // 使用【log】变量开始使用它！
        log.info("框架 {} 正在启动！当前版本为 {}，由 {} 提供。",
                descriptor.getPluginId(),
                descriptor.getVersion(),
                descriptor.getProvider());

        // 千万不要随便在任何地方存放文件，这样会给用户带来麻烦的。
        // 使用【getDataFolder()】可以获取到 ContentGrabbingJi 为框架组件分配的存储路径，这个路径是专属于框架组件的！
        File dataFolder = getDataFolder();
        log.info("框架数据的存放路径：{}", dataFolder.getAbsolutePath());
        
        log.info("框架初始化完成！");
    }

    @Override
    public void start() {
        // 是时候让你的框架组件开始工作了！
        // 当本方法被调用时，代表已经到了合适的时机让你的框架组件开始处理来自平台用户的功能请求了。
        // 你可以在这里【使用机器人帐号登录平台】，【连接第三方机器人框架以接收平台消息】等。
        
        log.info("框架已启动！");
    }

    @Override
    public void stop() {
        // 该让你的框架组件停下来了。
        // 当本方法被调用时，需要让你的框架组件停止工作（比如退出平台帐号，断开与第三方机器人框架的连接）。
        
    }

    @Override
    public void delete() {
        // 当你的框架组件要被删除时，本方法将会被调用。
        // 在这里选择是否清理框架组件的数据，或者说清理第三方运行时等等。
        // 如果你要清除包含用户数据的框架组件数据，建议你认真询问一下用户是否需要删除（用户体验提升！）。
        
    }
}
```

当你继承了 `Framework` 类后，别忘记在 `framework.json` 加上主类的类路径（比如示例中的类路径为 `org.example.SimpleFrameworkMain`）！  
```json5
{
  "id": "simple-framework",
  "version": "0.0.1-SNAPSHOT",
  "frameworkClass": "org.example.SimpleFrameworkMain",
  "platform": {
    "name": "Unknown",
    "identify": "unknown"
  }
}
```
这样你就创建好一个框架主类了。

## 处理消息 ##
框架并不需要过多的处理消息，但是需要将框架的消息形式转换成 ContentGrabbingJi 的消息形式。
以 QQ 平台为例，大多数基于 C/C++ 的机器人框架对一些特殊消息以【功能码】（例如 酷Q机器人 的 CQ 码）的形式表示在文本消息内容中（常见的功能码有【Image】，【At】和【Reply】）。  
但是这对 ContentGrabbingJi 来说：太难理解了。  
因此，框架虽然不需要处理消息并执行命令，但是还需要将接收到的消息（可能是文本形式的，或者是其他形式的）转换成 ContentGrabbingJi 所能处理的形式。  

## 我可不可以不处理消息？ ##
是的，你可以不处理消息！  
ContentGrabbingJi 计划添加运行监测组件，用于遥测应用的整体运行状态。
所以你不一定需要让框架组件处理消息，用于接收运行监测数据，并发送到运维端也是可以的！
> 官方运行监测组件计划在 ContentGrabbingJi v3.10.0 发布后开始开发。

