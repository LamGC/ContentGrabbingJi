## Pixiv首页数据接口 ##

### 说明 ###
> 注意: 该接口涉及用户账户隐私, 不要尝试对该接口返回数据做不安全云端存储, 或未经用户允许的发送出去.  

该接口用于获取Pixiv推荐给账号的首页作品信息，每次调用都会有不同结果。  

### 接口地址 ###
```
GET https://www.pixiv.net/ajax/top/{type}?mode={mode}&lang={lang}
```

### 参数 ###
- `type`: 首页类型
    - `illust`: 插画
    - `manga`: 漫画
    - `novel`: 小说
- `mode`: 内容类型
    - `all`: 不限类型
    - `r18`: 成人内容
- `lang`: 语言(只写几个)
    - `zh`: 中文

- 是否需要登录: `是`  
- 是否为Pixiv接口标准返回格式: `是`  
- 是否需要Referer请求头: `是`

### 请求示例 ###
```
GET https://www.pixiv.net/ajax/top/illust?mode=all&lang=zh
```

### 返回数据 ###
#### 数据示例 ####
```
(内容过长, 略)
```
#### 字段说明 ####
- `page`: 网页相关内容
    - `tags`: (`Object[]`) 热门标签
        - `tag`: (`String`) 标签名
        - `count`: (`Number`) 作品数量?
        - `lev`: (`Number`) 不明?
        - `ids`: (`Number[]`) 作品数组
    - `follow`: (`Number[]`) 已关注作者的作品推荐
    - `mypixiv`: (`?[]`) 不明
    - `recommend`: (`String[]`  -> `Number[]`) 推荐作品的Id?
    - `recommendByTag`: (`Object[]`) 标签的推荐作品
        - `tag`: (`String`) 标签名
        - `ids`: ((`String[]`  -> `Number[]`) 作品Id数组
    - `ranking`: (`Object`) 排行榜前100名数据
        - `date`: (`String`) 排行榜日期(`yyyyMMdd`)
        - `items`: (`Object[]`) 排行榜简略数据
            - `rank`: (`String` -> `Number`) 排行榜名次
            - `id`: (`String` -> `Number`) 作品Id 
    - `pixivision`: (`Object[]`) Pixiv的推荐文章
        - `id`: (`String` -> `Number`) 文章Id
        - `title`: (`String`) 文章标题
        - `thumbnailUrl`: (`String`) 文章封面图的Url地址
        - `abtestId`: (`String` -> `Number`) pixivision文章地址的参数`utm_content`的值
    - `recommendUser`: (`Object[]`) 推荐用户及其作品
        - `id`: (`Number`) 用户Id
        - `illustIds`: (`String[]` -> `Number[]`) 插画作品Id
        - `novelIds`: (`String[]` -> `Number[]`) 小说作品Id
    - `contestOngoing`: (`Object[]`) 进行中的比赛活动(没找到更多信息了, 先这么说着)
        - (待完善)
    - `contestResult`: (`Object[]`) 比赛结果信息
        - `slug`: (`String`) 比赛代号?
        - `type`: (`String`) 比赛作品类型?
        - `name`: (`String`) 比赛名
        - `url`: (`String`) 结果公布链接
        - `iconUrl`: (`String`) 图标链接(不明意义的图标)
    - `editorRecommend`: (`Object[]`) 编辑推荐(小说), 后续可能会删除, 这个应该是配合活动出的数据
        - (待完善)
    - `boothFollowItemIds`: (`String[]` -> `Number[]`) 已关注用户的最新商品
    - `sketchLiveFollowIds`: (`?[]`) 不明?
    - `sketchLivePopularIds`: (`String[]` -> `Number[]`) 不明?
    - `myFavoriteTags`: (`?[]`) 关注的标签
    - `newPost`: (`String[]` -> `Number[]`) 不明?
- `thumbnails`: (`Object`) 已关注用户的作品
    - `illust`: (`Object[]`) 插画作品
        - `illustId`: (`String` -> `Number`) 作品Id(或者准确来讲是 插画Id?)
        - `illustTitle`: (`String`) 插画标题
        - `id`: (`String` -> `Number`) 作品Id
        - `title`: (`String`) 作品标题
        - `illustType`: (`Number`) 作品类型
        - `xRestrict`: (`Number`) 不明?
        - `restrict`: (`Number`) 不明?
        - `sl`: (`Number`) 不明?
        - `url`: (`String`) 作品在主页的封面图Url
        - `description`: (`String`) 作品说明?
        - `tags`: (`String[]`) 标签原始名数组(不带翻译的原始名称)
        - `userId`: (`String` -> `Number`) 用户Id
        - `userName`: (`String`) 用户名
        - `width`: (`Number`) 作品宽度
        - `height`: (`Number`) 作品高度
        - `pageCount`: (`Number`) 作品页数
        - `isBookmarkable`: (`Boolean`) 不明?
        - `bookmarkData`: (`?`) 不明?
        - `alt`: (`String`) 简略信息
        - `isAdContainer`: (`Boolean`) 广告标识?
        - `titleCaptionTranslation`: (`Object`) 不明?
            - (待完善)
        - `urls`: (`Object`) 其他封面图尺寸的链接
            - (略)
        - `seriesId`: (`?`) 不明?
        - `seriesTitle`: (`?`) 不明?
        - `profileImageUrl`: (`String`) 用户头像图链接
    - `users`: (`Object[]`) 用户信？
        - `userId`: (`String` -> `Number`) 用户Id
        - `name`: (`String`) 用户名
        - `image`: (`String`) 用户头像图链接
        - `imageBig`: (`String`) 用户大尺寸头像图链接
        - `premium`: (`Boolean`) 是否为Pixiv高级会员
        - `isFollowed`: (`Boolean`) 是否关注
        - `isMypixiv`: (`Boolean`) 不明?
        - `isBlocking`: (`Boolean`) 是否为黑名单?
        - `background`: (`?`) 不明?
        - `partial`: (`Number`) 不明?
    - `tagTranslation`: 标签翻译名
        - `key=${标签原始名}`: (`Object`) 标签翻译信息
            - `${语言代码}`: (`String`) 对应语言代码的翻译, 不一定有
    - `boothItems`: (`Object[]`) 商品信息
        - (待完善)
    - `sketchLives`: (`Object[]`) 不明?
        - (待完善)
    - `zoneConfig`: (`Object`) 不明?
        - (待完善)

