## 搜索推荐接口 ##
### 说明 ###
可用于优化搜索内容。

### 接口地址 ###
```
GET https://www.pixiv.net/rpc/cps.php?
```

- 是否需要登录: `是`
- 是否为Pixiv标准接口返回格式: `否`
- 是否需要Referer请求头: `否`

### 参数 ###
Url参数:
- `keyword`: 搜索内容

> 注意: 搜索内容需要进行Url编码（空格要转换成`%20`而不是`+`）
### 请求示例 ###
```
GET https://www.pixiv.net/rpc/cps.php?keyword=幸运星
```

### 返回数据 ###
#### 数据示例 ####
```json
{
    "candidates":[
        {
            "tag_name":"\u3089\u304d\u2606\u3059\u305f",
            "access_count":"68286498",
            "tag_translation":"\u5e78\u8fd0\u661f",
            "type":"tag_translation"
        }, // ...
    ]
}
```
#### 字段说明 ####
- `candidates`: (`Object[]`) 搜索推荐候选列表
    - `tag_name`: (`string`) 推荐词原名
    - `access_count`: (`string` -> `number`) 推荐词访问量
    - `tag_translation`: (`string`) 推荐词对应翻译名
    - `type`: (`string`) 推荐词类型
        - `tag_translation`: 标签翻译信息
        - `prefix`: 前缀
