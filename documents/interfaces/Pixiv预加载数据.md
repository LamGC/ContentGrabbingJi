## Pixiv预加载数据 ##
### 说明 ###
作品预加载数据仅在加载作品页面时存在，处理后删除。
### 接口地址 ###
```
GET https://www.pixiv.net/artworks/{IllustId}
```

- 是否需要登录: `是`
- 是否为Pixiv标准接口返回格式: `否`
- 是否需要Referer请求头: `否`

### 参数 ###
Url参数:
- `IllustId`: 作品Id

### 请求示例 ###
```
GET https://www.pixiv.net/artworks/82647306
```

### 返回数据 ###
> 注意: 该接口返回HTML格式数据，并不是JSON格式数据。
预加载数据需要对返回的Html数据进行解析，路径如下：
- CSS Select: meta#meta-preload-data
- html>head>meta#meta-preload-data

获得标签后，获取`content`属性即可获得预加载数据内容

#### 数据示例 ####
```json
{
    "timestamp":"2020-07-01T11:32:30+09:00",
    "illust":{
        "82647306":{
            "illustId":"82647306",
            "illustTitle":"水着キャルちゃん！",
            "illustComment":"水着のキャルちゃんはかわいいぞ！！",
            "id":"82647306",
            "title":"水着キャルちゃん！",
            "description":"水着のキャルちゃんはかわいいぞ！！",
            "illustType":0,
            "createDate":"2020-06-29T12:28:06+00:00",
            "uploadDate":"2020-06-29T12:28:06+00:00",
            "restrict":0,
            "xRestrict":0,
            "sl":2,
            "urls":{
                "mini":"https://i.pximg.net/c/48x48/img-master/img/2020/06/29/21/28/06/82647306_p0_square1200.jpg",
                "thumb":"https://i.pximg.net/c/250x250_80_a2/img-master/img/2020/06/29/21/28/06/82647306_p0_square1200.jpg",
                "small":"https://i.pximg.net/c/540x540_70/img-master/img/2020/06/29/21/28/06/82647306_p0_master1200.jpg",
                "regular":"https://i.pximg.net/img-master/img/2020/06/29/21/28/06/82647306_p0_master1200.jpg",
                "original":"https://i.pximg.net/img-original/img/2020/06/29/21/28/06/82647306_p0.jpg"
            },
            "tags":{
                "authorId":"55859246",
                "isLocked":false,
                "tags":[
                    {
                        "tag":"プリンセスコネクト!Re:Dive",
                        "locked":true,
                        "deletable":false,
                        "userId":"55859246",
                        "translation":{
                            "en":"公主连结Re:Dive"
                        },
                        "userName":"秋鳩むぎ"
                    }, // ...
                ],
                "writable":true
            },
            "alt":"#プリンセスコネクト!Re:Dive 水着キャルちゃん！ - 秋鳩むぎ的插画",
            "storableTags":[
                "_bee-JX46i",
                "nAtxkwJ5Sy",
                "q303ip6Ui5"
            ],
            "userId":"55859246",
            "userName":"秋鳩むぎ",
            "userAccount":"pigeonwheat",
            "userIllusts":{
                "82647306":{
                    "illustId":"82647306",
                    "illustTitle":"水着キャルちゃん！",
                    "id":"82647306",
                    "title":"水着キャルちゃん！",
                    "illustType":0,
                    "xRestrict":0,
                    "restrict":0,
                    "sl":2,
                    "url":"https://i.pximg.net/c/250x250_80_a2/img-master/img/2020/06/29/21/28/06/82647306_p0_square1200.jpg",
                    "description":"水着のキャルちゃんはかわいいぞ！！",
                    "tags":[
                        "プリンセスコネクト!Re:Dive",
                        "キャル(プリコネ)",
                        "おへそ"
                    ],
                    "userId":"55859246",
                    "userName":"秋鳩むぎ",
                    "width":2000,
                    "height":3000,
                    "pageCount":1,
                    "isBookmarkable":true,
                    "bookmarkData":null,
                    "alt":"#プリンセスコネクト!Re:Dive 水着キャルちゃん！ - 秋鳩むぎ的插画",
                    "isAdContainer":false,
                    "titleCaptionTranslation":{
                        "workTitle":null,
                        "workCaption":null
                    },
                    "createDate":"2020-06-29T21:28:06+09:00",
                    "updateDate":"2020-06-29T21:28:06+09:00",
                    "seriesId":null,
                    "seriesTitle":null
                }
            },
            "likeData":false,
            "width":2000,
            "height":3000,
            "pageCount":1,
            "bookmarkCount":39,
            "likeCount":31,
            "commentCount":2,
            "responseCount":0,
            "viewCount":239,
            "isHowto":false,
            "isOriginal":false,
            "imageResponseOutData":[
            ],
            "imageResponseData":[
            ],
            "imageResponseCount":0,
            "pollData":null,
            "seriesNavData":null,
            "descriptionBoothId":null,
            "descriptionYoutubeId":null,
            "comicPromotion":null,
            "fanboxPromotion":null,
            "contestBanners":[
            ],
            "isBookmarkable":true,
            "bookmarkData":null,
            "contestData":null,
            "zoneConfig":{
                "responsive":{
                    "url":"https://pixon.ads-pixiv.net/show?zone_id=illust_responsive&amp;format=js&amp;s=1&amp;up=0&amp;a=22&amp;ng=w&amp;l=zh&amp;uri=%2Fartworks%2F_PARAM_&amp;is_spa=1&amp;K=59bba275c645c&amp;ab_test_digits_first=32&amp;yuid=NDJ3gQk&amp;suid=Pggb9mua3yznnr7lz&amp;num=5efbf5be273&amp;t=_bee-JX46i&amp;t=b8b4-hqot7&amp;t=kY01H5r3Pd"
                },
                "rectangle":{
                    "url":"https://pixon.ads-pixiv.net/show?zone_id=illust_rectangle&amp;format=js&amp;s=1&amp;up=0&amp;a=22&amp;ng=w&amp;l=zh&amp;uri=%2Fartworks%2F_PARAM_&amp;is_spa=1&amp;K=59bba275c645c&amp;ab_test_digits_first=32&amp;yuid=NDJ3gQk&amp;suid=Pggb9mua42776dfuu&amp;num=5efbf5be810&amp;t=_bee-JX46i&amp;t=b8b4-hqot7&amp;t=kY01H5r3Pd"
                },
                "500x500":{
                    "url":"https://pixon.ads-pixiv.net/show?zone_id=bigbanner&amp;format=js&amp;s=1&amp;up=0&amp;a=22&amp;ng=w&amp;l=zh&amp;uri=%2Fartworks%2F_PARAM_&amp;is_spa=1&amp;K=59bba275c645c&amp;ab_test_digits_first=32&amp;yuid=NDJ3gQk&amp;suid=Pggb9mua442sjsueo&amp;num=5efbf5be568&amp;t=_bee-JX46i&amp;t=b8b4-hqot7&amp;t=kY01H5r3Pd"
                },
                "header":{
                    "url":"https://pixon.ads-pixiv.net/show?zone_id=header&amp;format=js&amp;s=1&amp;up=0&amp;a=22&amp;ng=w&amp;l=zh&amp;uri=%2Fartworks%2F_PARAM_&amp;is_spa=1&amp;K=59bba275c645c&amp;ab_test_digits_first=32&amp;yuid=NDJ3gQk&amp;suid=Pggb9mua45spzoimt&amp;num=5efbf5be155&amp;t=_bee-JX46i&amp;t=b8b4-hqot7&amp;t=kY01H5r3Pd"
                },
                "footer":{
                    "url":"https://pixon.ads-pixiv.net/show?zone_id=footer&amp;format=js&amp;s=1&amp;up=0&amp;a=22&amp;ng=w&amp;l=zh&amp;uri=%2Fartworks%2F_PARAM_&amp;is_spa=1&amp;K=59bba275c645c&amp;ab_test_digits_first=32&amp;yuid=NDJ3gQk&amp;suid=Pggb9mua47f9zcoim&amp;num=5efbf5be400&amp;t=_bee-JX46i&amp;t=b8b4-hqot7&amp;t=kY01H5r3Pd"
                },
                "expandedFooter":{
                    "url":"https://pixon.ads-pixiv.net/show?zone_id=multiple_illust_viewer&amp;format=js&amp;s=1&amp;up=0&amp;a=22&amp;ng=w&amp;l=zh&amp;uri=%2Fartworks%2F_PARAM_&amp;is_spa=1&amp;K=59bba275c645c&amp;ab_test_digits_first=32&amp;yuid=NDJ3gQk&amp;suid=Pggb9mua4928ct0yw&amp;num=5efbf5be471&amp;t=_bee-JX46i&amp;t=b8b4-hqot7&amp;t=kY01H5r3Pd"
                },
                "logo":{
                    "url":"https://pixon.ads-pixiv.net/show?zone_id=logo_side&amp;format=js&amp;s=1&amp;up=0&amp;a=22&amp;ng=w&amp;l=zh&amp;uri=%2Fartworks%2F_PARAM_&amp;is_spa=1&amp;K=59bba275c645c&amp;ab_test_digits_first=32&amp;yuid=NDJ3gQk&amp;suid=Pggb9mua4aqu6i4sr&amp;num=5efbf5be844&amp;t=_bee-JX46i&amp;t=b8b4-hqot7&amp;t=kY01H5r3Pd"
                }
            },
            "extraData":{
                "meta":{
                    "title":"#プリンセスコネクト!Re:Dive 水着キャルちゃん！ - 秋鳩むぎ的插画 - pixiv",
                    "description":"この作品 「水着キャルちゃん！」 は 「プリンセスコネクト!Re:Dive」「キャル(プリコネ)」 等のタグがつけられた「秋鳩むぎ」さんのイラストです。 「水着のキャルちゃんはかわいいぞ！！」",
                    "canonical":"https://www.pixiv.net/artworks/82647306",
                    "alternateLanguages":{
                        "ja":"https://www.pixiv.net/artworks/82647306",
                        "en":"https://www.pixiv.net/en/artworks/82647306"
                    },
                    "descriptionHeader":"本作「水着キャルちゃん！」为附有「プリンセスコネクト!Re:Dive」「キャル(プリコネ)」等标签的插画。",
                    "ogp":{
                        "description":"水着のキャルちゃんはかわいいぞ！！",
                        "image":"https://embed.pixiv.net/decorate.php?illust_id=82647306",
                        "title":"#プリンセスコネクト!Re:Dive 水着キャルちゃん！ - 秋鳩むぎ的插画 - pixiv",
                        "type":"article"
                    },
                    "twitter":{
                        "description":"水着のキャルちゃんはかわいいぞ！！",
                        "image":"https://embed.pixiv.net/decorate.php?illust_id=82647306",
                        "title":"水着キャルちゃん！",
                        "card":"summary_large_image"
                    }
                }
            },
            "titleCaptionTranslation":{
                "workTitle":null,
                "workCaption":null
            }
        }
    },
    "user":{
        "55859246":{
            "userId":"55859246",
            "name":"秋鳩むぎ",
            "image":"https://i.pximg.net/user-profile/img/2020/06/29/21/20/14/18907670_b3f87d819f705ec418f120cd57f9dc41_50.jpg",
            "imageBig":"https://i.pximg.net/user-profile/img/2020/06/29/21/20/14/18907670_b3f87d819f705ec418f120cd57f9dc41_170.jpg",
            "premium":false,
            "isFollowed":false,
            "isMypixiv":false,
            "isBlocking":false,
            "background":null,
            "partial":0
        }
    }
}
```
#### 字段说明 ####
- `timestamp`: (`string`) 请求时间
- `illust`: (`Object`) 作品预加载信息
    - `{illustId}`: 作品ID(跟页面请求的IllustId一样)
        - `illustId`: (`string` -> `number`) 作品Id
        - `illustTitle`: (`string`) 作品标题
        - `illustComment`: (`string`) 作品说明
        - `id`: (`string` -> `number`) 与`illustId`一致, 猜测是以兼容旧版本为目录而存在
        - `title`: (`string`) 与`illustTitle`一致, 猜测是以兼容旧版本为目录而存在
        - `description`: (`string`) 作品说明
        - `illustType`: (`number`) 作品类型
            - `0`: 插画作品
            - `1`: 漫画作品
            - `2`: 动图作品
        - `createDate`: (`string`) 作品创建时间(或者是完成时间?)
        - `updateDate`: (`string`) 作品上传时间
        - `restrict`: (`number`) 作品限制级(意义不明, 可能是兼容性问题?)?
        - `xRestrict`: (`number`) 作品是否为限制级, 基本准确, 少部分不一定(看Pixiv审核怎么理解了)
            - `0`: 非限制级内容(即非R18作品)
            - `1`: 限制级内容(即R18作品)
        - `sl`: (`number`) 不明?
        - `urls`: (`string`) 作品图片链接, 需要`Referer`请求头
            - `mini`: (`string`) 小尺寸预览图
            - `thumb`: (`string`) 小尺寸预览图
            - `small`: (`string`) 小尺寸预览图
            - `regular`: (`string`) 经压缩，没啥画质损失的原尺寸预览图
            - `original`: (`string`) 原图
        - `tags`: (`Object`) 作品标签信息
            - `authorId`: (`string` -> `number`) 作者用户Id
            - `isLocked`: (`boolean`) 标签是否锁定(即不可被访客更改)
            - `tags`: (`Object[]`) 标签信息数组
                - `tag`: (`string`) 标签原始名
                - `locked`: (`boolean`) 标签是否不可更改
                - `deletable`: (`boolean`) 标签能否被删除?
                - `userId`: (`string` -> `number`) 用户Id
                - `translation`: (`Object`) 标签翻译
                    - `{语种}`: 翻译名
                - `userName`: (`string`) 用户名
        - `alt`: (`string`) 简略介绍信息(在图片加载失败时可提供给`img`标签使用)
        - `storableTags`: (`string[]`) 不明?
        - `userId`: (`string` -> `number`) 作者用户Id
        - `userName`: (`string`) 作者用户名
        - `userAccount`: (`string`) 作者登录名
        - `userIllusts`: (`Object`) 作品信息?
            - `{IllustId}`: 与请求IllustId一样
                - (请转到：Pixiv作品信息获取接口.md)
        - `likeData`: (`boolean?`) 不明?
        - `width`: (`number`) 作品长度
        - `height`: (`number`) 作品高度
        - `pageCount`: (`number`) 作品页数
        - `bookmarkCount`: (`number`) 作品公开的收藏数
        - `likeCount`: (`number`) 作品喜欢(点赞)数
        - `commentCount`: (`number`) 作品评论数
        - `responseCount`: (`number`) 作品响应数?
        - `viewCount`: (`number`) 作品阅览数
        - `isHowto`: (`boolean`) 不明?
        - `isOriginal`: (`boolean`) 不明?
        - `imageResponseOutData`: (`Unknown[]`) 不明?
        - `imageResponseData`: (`Unknown[]`) 不明?
        - `imageResponseCount`: (`number`) 不明?
        - `pollData`: (`Unknown`) 不明?
        - `seriesNavData`: (`Unknown`)  不明?
        - `descriptionBoothId`: (`Unknown`)  不明?
        - `descriptionYoutubeId`: (`Unknown`)  不明?
        - `comicPromotion`: (`Unknown`)  不明?
        - `fanboxPromotion`: (`Unknown`)  不明?
        - `contestBanners`: (`Unknown[]`)  不明? 
        // TODO 待补充

        - `isBookmarkable`: (`boolean`) 不明?
        - `bookmarkData`: (`Unknown`) 不明?
        - `contestData`: (`Unknown`) 不明?
        - `zoneConfig`: (`Object`) 猜测是广告信息?
            - (基本不用, 忽略...)
        - `extraData`: (`Object`) 扩展数据
            - `meta`: (`Object`) 元数据
                - `title`: (`string`) 网页标题
                - `description`: (`string`) Pixiv生成的作品说明
                - `canonical`: (`string`) 作品页面链接
                - `alternateLanguages`: (`Object`) 不同语言的作品页面链接
                    - `{语种}`: (`string`) 对应语种的作品链接
                - `descriptionHeader`: (`string`) 说明文档(不过似乎是对应了会话所属账号的语种?)
                - `ogp`: (`Object`) 猜测是某平台的分享数据?
                    - `description`: (`string`) 说明内容
                    - `image`: (`string`) 预览图链接
                    - `title`: (`string`) 分享标题
                    - `type`: (`string`) 分享类型?
                - `twitter`: (`Object`)
                    - `description`: (`string`) 说明内容
                    - `image`: (`string`) 预览图链接
                    - `title`: (`string`) 分享标题
                    - `card`: (`string`) 分享类型?
        - `titleCaptionTranslation`: (`Object`) 不明?
            - `workTitle`: (`Unknown`) 不明?
            - `workCaption`: (`Unknown`) 不明?
- `user`: (`Object`) 作者预加载信息
    - `{userId}`: 可通过`illust.{illustId}.userId`获得
        - `userId`: (`string` -> `number`) 作者用户Id
        - `name`: (`string`) 作者用户名
        - `image`: (`string`) 作者用户头像(小尺寸)
        - `imageBig`: (`string`) 作者用户头像(大尺寸)
        - `premium`: (`boolean`) 作者是否为Pixiv会员
        - `isFollowed`: (`boolean`) 当前会话用户是否已关注
        - `isMypixiv`: (`boolean`) 是否为当前会话本人?
        - `isBlocking`: (`boolean`) 是否正在被封禁
        - `background`: (`Object`) 背景图片?
        - `partial`: (`number`) 不明?
