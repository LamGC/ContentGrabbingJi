# 贡献指南 #

> 警告: 该指南尚未完成，所以在遇到与贡献有关问题时应该及时联系项目管理者以确定有关贡献事项，
而不应该因该指南未提及有关事项而擅作主张，擅作主张将会在 Review 阶段造成不必要的麻烦（甚至会被拒绝）。

欢迎你为本项目作出贡献！本项目的发展离不开所有贡献者（不仅仅只是提交了相关代码，帮助编写文档，还是提出问题或想法也算是贡献者之一！）的贡献。  
在为本项目作出任何贡献之前，请先阅读[贡献者准则](https://github.com/LamGC/ContentGrabbingJi/blob/3.0.0/CODE_OF_CONDUCT.md)，
一旦你尝试（或正在）为本项目贡献，即代表你**认同并接受**贡献者准则，**清楚明白且接受**因**违反贡献者准则**所造成的后果。 

## 代码规范 ##
本项目代码规范大部分遵循 《Alibaba Java Coding Guidelines》，
详细内容可前往 https://github.com/alibaba/p3c 查阅。  
如果你的 IDE 为 Eclipse 或 Idea，则推荐你为 IDE 添加阿里巴巴根据该规范开发的规范检查插件，该插件有助于你保持代码规范。

## Commit Message ##
提交信息需要遵循以下格式：
```
[TAG] ModuleName 对提交的说明;

[TAG] FileName/ClassName 对该文件的说明;
...

```
#### 标点符号的使用 ####
在提交信息中，符号使用**英文符号**。
#### 文件名或类名的说明 ####
对于类代码文件而言，FileName 只需要填写类名，对其他文件则需要包括后缀名。  
FileName 和 ModuleName 可按需要使用`, `追加，注意与说明空一格，  
例如: `[Add][Change] SimpleClass, MainClass 添加 SimpleClass 并调整 MainClass 的日志格式;`  
ModuleName 根据修改的相关子模块名填写即可，对项目而非子模块的更改，则 ModuleName 为 `Project`，一个例子：
```
[Change][Update][Optimize] Project, SubModule01, WebFrontend 更新项目中的Readme, 修改 SubModule01 中鉴权的实现细节, 优化Web前端模块的启动速度;

[Update] Readme.md 调整文档格式;
[Change] AuthorizationChecker 调整鉴权过程以避免潜在的错误;
[Optimize] MainClass 优化初始化速度, 移除不必要的检查步骤;
```
#### 标签的使用 ####
目前支持以下Tag:
- `Add` 文件添加
- `Change` 文件更改
- `Update` 更新文件
- `Delete` 文件删除
- `Document` 文档相关
- `Fix` 问题修复
- `Optimize` 优化相关(或者代码整理啥的)
- `Move` 文件移动(例如包更改)
- `Rename` 更改名称(类名, 文件名等)
- `Issue:Id` 指定 Commit 所关联的 Github Issue（如果与其他标签有关，可直接加到指定标签后方，该标签可不用加）
- `PR:ID` 指定 Commit 所关联的 Pull Request（可选的，可直接使用 Github 的默认 Commit Message）



对大部分文件而言，FileName 按照以上规则填写即可，但对于依赖项而言，则要按照 GAV 坐标填写，  
例如：`[Add] junit:junit 添加 Junit 单元测试依赖项;`，  
或者更新依赖项：`[Update] junit:junit 更新 Junit 以修复潜在的漏洞('4.13' -> '4.13.1');`

#### 引用 Github 中的相关 Issue 或者 PR ####
如需引用相关 Issue 或者 PR（Pull Request），可在相关 Tag 中引用，或者另起一个 Tag 并声明指定的 ID，例如：  
```
[Fix #11] Module 修复了部分功能异常情况;

...
```
或者是：
```
[Change][Issue #15] Module 增加了一些功能;

...
```
对于需要修复的相关 Issue，既可以采用第一种形式，也可以用第二种形式，单独添加一个 Issue 标签标明关联的 Issue 即可，比如这样：  
```
[Fix][Issue #17] Module 修复了返回异常的问题;

...
```
如确定修复完成，也可以直接使用 Github 会对其作出相关操作的词（？），比如：  
```
[Fix close#20] Module 修复响应码判断错误的问题;

...
```
或者是  
```
[Fix][Issue fixed#28] Module 修复模板编译错误的问题;

...
```


## 代码质量 ##
如果你想添加新的功能，请务必对新功能相关代码编写好覆盖全，情况完善的单元测试（尽可能的覆盖所有新增功能，确保对大部分情况都有足够的测试）！  
覆盖全、多角度、高度完善的单元测试是保证代码、项目质量必不可少的！  
每次提交后，请检查 Action，查看 Github 中的代码是否能通过所有的单元测试。

## 依赖管理 ##
> 请**尽可能避免引入**新依赖项，如果某个依赖项的引入，仅仅是用于某些计算，则这个引用是完全没有必要的。

对于因极少需要而引入某些依赖项时，请考虑了解依赖项内的*实现细节*，并复现在项目中，以避免引入依赖项。（当然，这是有例外的，如果某个功能较为复杂，且在该依赖项拥有很好的质量保证时，可考虑引入，具体细节可发起**issue**与我们进行讨论。）  
**另外！**引入依赖项请注意**License**，确保依赖项可以被本项目使用。

