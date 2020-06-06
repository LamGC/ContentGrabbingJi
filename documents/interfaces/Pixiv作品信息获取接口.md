## Pixiv作品信息批量获取接口 ##
### 说明 ###
接口可一次获取多个作品的基础信息


### 接口地址 ###
```
GET https://www.pixiv.net/ajax/illust/recommend/illusts
```

- 需要登录: `否`
- 是否为Pixiv接口标准返回格式: `是`  
### 参数 ###
- `illust_ids[]`: 作品Id, 可重复添加该参数


### 请求示例 ###
```
GET https://www.pixiv.net/ajax/illust/recommend/illusts?illust_ids[]=82030844&illust_ids[]=82029098&illust_ids[]=82028913
```

### 返回数据 ###
#### 数据示例 ####
```json
{
    "error":false,
    "message":"",
    "body":{
        "illusts":[
            {
                "illustId":"82030844",
                "illustTitle":"3rd anniversary",
                "id":"82030844",
                "title":"3rd anniversary",
                "illustType":0,
                "xRestrict":0,
                "restrict":0,
                "sl":2,
                "url":"https:\/\/i.pximg.net\/c\/360x360_70\/custom-thumb\/img\/2020\/06\/02\/11\/24\/49\/82030844_p0_custom1200.jpg",
                "description":"",
                "tags":[
                    "\u30a2\u30ba\u30fc\u30eb\u30ec\u30fc\u30f3",
                    "\u6bd4\u53e1(\u30a2\u30ba\u30fc\u30eb\u30ec\u30fc\u30f3)",
                    "\u6c34\u7740",
                    "\u8db3\u88cf",
                    "\u88f8\u8db3",
                    "\u30a2\u30ba\u30fc\u30eb\u30ec\u30fc\u30f35000users\u5165\u308a",
                    "\u30a2\u30ba\u30fc\u30eb\u30ec\u30fc\u30f310000users\u5165\u308a",
                    "\u30dd\u30cb\u30fc\u30c6\u30fc\u30eb",
                    "\u30db\u30eb\u30bf\u30fc\u30cd\u30c3\u30af"],
                "userId":"6662895",
                "userName":"ATDAN-",
                "width":1500,
                "height":844,
                "pageCount":1,
                "isBookmarkable":true,
                "bookmarkData":null,
                "alt":"#\u30a2\u30ba\u30fc\u30eb\u30ec\u30fc\u30f3 3rd anniversary - ATDAN-\u7684\u63d2\u753b",
                "isAdContainer":false,
                "titleCaptionTranslation":{
                    "workTitle":null,
                    "workCaption":null
                },
                "createDate":"2020-06-02T01:29:40+09:00",
                "updateDate":"2020-06-02T11:24:49+09:00",
                "profileImageUrl":"https:\/\/i.pximg.net\/user-profile\/img\/2016\/01\/11\/21\/46\/50\/10371466_80f6ad67eab3b8abd44a2fb74ddd1ba1_50.jpg",
                "type":"illust"
            }, // ...
        ]
    }
}

```
#### 参数详解 ####
- `illusts`: (`Object[]`) 存储查询作品信息的数组
    - `illustId`: (`string` -> `number`) 作品Id
    - `illustTitle`: (`string`) 作品标题
    - `id`: (`string` -> `number`) 与`illustId`一致, 猜测是以兼容旧版本为目录而存在
    - `title`: (`string`) 与`illustTitle`一致, 猜测是以兼容旧版本为目录而存在
    - `illustType`: (`number`) 作品类型
        - `0`: 插画作品
        - `1`: 漫画作品
        - `2`: 动图作品
    - `xRestrict`: (`number`) 作品是否为限制级, 基本准确, 少部分不一定(看Pixiv审核怎么理解了)
        - `0`: 非限制级内容(即非R18作品)
        - `1`: 限制级内容(即R18作品)
    - `restrict`: (`number`) 作品限制级(意义不明, 可能是兼容性问题?)?
    - `sl`: (`number`) 不明?
    - `url`: (`string`) 作品预览图链接, 需要`Referer`请求头
    - `description`: (`string`) 作品说明
    - `tags`: (`string[]`) 作品标签数组
    - `userId`: (`string` -> `number`) 作者用户Id
    - `userName`: (`string`) 作者用户名
    - `width`: (`number`) 作品长度
    - `height`: (`number`) 作品高度
    - `pageCount`: (`number`) 作品页数
    - `isBookmarkable`: (`boolean`) 不明?
    - `alt`: (`string`) 简略介绍信息(在图片加载失败时可提供给`img`标签使用)
    - `isAdContainer`: (`boolean`) 不明?
    - `titleCaptionTranslation`: (`Object`) 不明?
        - `workTitle`: (`Unknown`) 不明?
        - `workCaption`: (`Unknown`) 不明?
    - `createDate`: (`string`) 作品创建时间(或者是完成时间?)
    - `updateDate`: (`string`) 作品上传时间
    - `profileImageUrl`: (`string`) 作者用户头像图片链接
    - `type`: (`string`) 作品类型名


