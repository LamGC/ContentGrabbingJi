## Pixiv接口标准返回格式 ##
Pixiv大部分接口在返回数据时都会遵循以下格式：
```json
{
    "error": false,
    "message": "",
    "body": {

    }
}
```
大部分是如此(部分接口比较特别, 不是这个格式)  

属性|类型|说明
---|---|---
error|Boolean|如果接口返回错误信息, 该属性为`true`
message|String|如果`error`属性为`true`, 则该属性不为空字符串
body|Object/Array|如果`error`不为`true`, 该属性有数据, 否则属性可能不存在
