# 贡献指南 #

欢迎你为本项目作出贡献！本项目的发展离不开所有贡献者的贡献。  
在为本项目作出任何贡献之前，请先阅读[贡献者准则](https://github.com/LamGC/ContentGrabbingJi/blob/3.0.0/CODE_OF_CONDUCT.md)，
一旦你尝试为本项目贡献，即代表你认同并接受贡献者准则，明白且接受因违反贡献者准则所造成的后果。 

## 代码规范 ##
本项目代码规范大部分遵循 《Alibaba Java Coding Guidelines》，
详细内容可前往 https://github.com/alibaba/p3c 查阅。  
如果你的 IDE 为 Eclipse 或 Idea，则推荐你为 IDE 添加阿里巴巴根据该规范开发的规范检查插件，该插件有助于你保持代码规范。

### Commit Message ###
提交信息需要遵循以下格式：
```
[TAG] ModuleName 对提交的说明;

[TAG] FileName 对该文件的说明;
...

```
对于类代码文件而言，FileName只需要填写类名，对其他文件则需要包括后缀名。  
FileName 和 ModuleName 可按需要使用`, `追加，注意与说明空一格，  
例如: `[Add][Change] SimpleClass, MainClass 添加 SimpleClass 并调整 MainClass 的日志格式;`  
目前支持以下Tag:
- Add 文件添加
- Change 文件更改
- Update 更新文件
- Delete 文件删除
- Document 文档相关
- Fix 问题修复
- Optimize 优化相关
- Move 文件移动(例如包更改)
- Rename 更改名称(类名, 文件名等)

ModuleName 根据修改的相关子模块名填写即可，对项目而非子模块的更改，则 ModuleName 为 `Project`。

对大部分文件而言，FileName 按照以上规则填写即可，但对于依赖项而言，则要按照 GAV 坐标填写，例如：`[Add] junit:junit 添加 Junit 单元测试依赖项;`

