## Pixiv内容搜索接口 ##
### 说明 ###
> 注意: 本接口可能会影响Pixiv对账号的行为判断（猜测，不一定会）  

该接口用于在Pixiv搜索内容。

### 接口地址 ###
```
GET https://www.pixiv.net/ajax/search/{Type}/{Content}?{Param...}
```

- 是否需要登录: `是/否`
- 是否为Pixiv标准接口返回格式: `是/否`
- 是否需要Referer请求头: `否`

### 参数 ###
#### Url参数 ####
- `Type`: 内容类型
    - `artworks` - 所有类型
    - `top` - 推荐
    - `illustrations` - 插画
    - `manga` - 漫画
    - `novels` - 小说
    - `tags` - 查询标签信息
- `Content`: 搜索内容

#### GET参数 ####
##### 必填 #####
> 注意：除 `tags` 类型外，其他内容类型都需要以下参数。
- `word`: 与搜索内容一致 (经测试似乎可以省略)
- `s_mode`: 匹配模式
    - `s_tag`: 标签，部分一致
    - `s_tag_full`: 标签，完全一致
    - `s_tc`: 标签和说明文字
- `type`: 作品类型
    - `all`: 插画、漫画和动图
    - `illust_and_ugoira`: 插画和动图
    - `illust`: 仅插画
    - `manga`: 仅漫画
    - `ugoira`: 仅动图
- `p`: 指定页数 (当指定页数超出范围后，`body.illust.data`为空数组)
- `order`: 排序设置
    - `date`: 按时间从旧到新
    - `date_d`: 按时间从新到旧
    - `(Unknown)`: 未知, 猜测是会员功能的热门搜索
- `mode`: 内容分级设置
    - `all`: 全部内容
    - `safe`: 排除成人内容
    - `r18`: 仅成人内容

##### 选填 #####
- `wlt`: 作品最低宽度(px)
- `wgt`: 作品最高宽度(px)
- `hlt`: 作品最低高度(px)
- `hgt`: 作品最高高度(px)
- `ratio`: 作品横宽比过滤 (初步测试表明，该参数无法指定横宽比，可能暂不支持该功能)
    - `0.5`: 仅横图
    - `-0.5`: 仅纵图
    - `0`: 仅正方形图
- `tool`: 限定作品绘制工具
- `scd`: 过滤作品发布时间 - 开始时间(yyyy-MM-dd)
- `scd`: 过滤作品发布时间 - 结束时间(yyyy-MM-dd)
- `(Unknown)`: 最小收藏数 (该参数为会员限定功能，后续补充)

### 返回数据 ###
#### 数据示例 ####
```json
{
    "error":false,
    "body":{
        "illustManga":{
            "data":[
                {
                    "illustId":"82130571",
                    "illustTitle":"空の絵",
                    "id":"82130571",
                    "title":"空の絵",
                    "illustType":0,
                    "xRestrict":0,
                    "restrict":0,
                    "sl":2,
                    "url":"https:\/\/i.pximg.net\/c\/250x250_80_a2\/img-master\/img\/2020\/06\/06\/17\/51\/14\/82130571_p0_square1200.jpg",
                    "description":"",
                    "tags":[
                        "風景",
                        "空",
                        "草",
                        "雲"
                    ],
                    "userId":"31507675",
                    "userName":"昏omeme",
                    "width":1600,
                    "height":1600,
                    "pageCount":2,
                    "isBookmarkable":true,
                    "bookmarkData":null,
                    "alt":"#風景 空の絵 - 昏omeme的插画",
                    "isAdContainer":false,
                    "titleCaptionTranslation":{
                        "workTitle":null,
                        "workCaption":null
                    },
                    "createDate":"2020-06-06T17:51:14+09:00",
                    "updateDate":"2020-06-06T17:51:14+09:00",
                    "profileImageUrl":"https:\/\/i.pximg.net\/user-profile\/img\/2020\/05\/06\/19\/21\/04\/18509741_e3166e69809c44d6926454ecaac89590_50.png"
                }, // ...
            ],
            "total":165875,
            "bookmarkRanges":[
                {
                    "min":null,
                    "max":null
                },
                {
                    "min":10000,
                    "max":null
                },
                {
                    "min":5000,
                    "max":null
                },
                {
                    "min":1000,
                    "max":null
                },
                {
                    "min":500,
                    "max":null
                },
                {
                    "min":300,
                    "max":null
                },
                {
                    "min":100,
                    "max":null
                },
                {
                    "min":50,
                    "max":null
                }
            ]
        },
        "popular":{
            "recent":[
                {
                    "illustId":"82062770",
                    "illustTitle":"Still you remember",
                    "id":"82062770",
                    "title":"Still you remember",
                    "illustType":0,
                    "xRestrict":0,
                    "restrict":0,
                    "sl":2,
                    "url":"https:\/\/i.pximg.net\/c\/250x250_80_a2\/img-master\/img\/2020\/06\/03\/18\/02\/15\/82062770_p0_square1200.jpg",
                    "description":"",
                    "tags":[
                        "オリジナル",
                        "女の子",
                        "カラス",
                        "风景",
                        "線路"
                    ],
                    "userId":"1069005",
                    "userName":"へちま",
                    "width":2000,
                    "height":1415,
                    "pageCount":1,
                    "isBookmarkable":true,
                    "bookmarkData":null,
                    "alt":"#オリジナル Still you remember - へちま的插画",
                    "isAdContainer":false,
                    "titleCaptionTranslation":{
                        "workTitle":null,
                        "workCaption":null
                    },
                    "createDate":"2020-06-03T18:02:15+09:00",
                    "updateDate":"2020-06-03T18:02:15+09:00",
                    "profileImageUrl":"https:\/\/i.pximg.net\/user-profile\/img\/2013\/05\/10\/00\/38\/05\/6213543_c94edc0d13776214467bd0c47ee6491a_50.jpg"
                }, // ...
            ],
            "permanent":[
                {
                    "illustId":"60993044",
                    "illustTitle":"無題",
                    "id":"60993044",
                    "title":"無題",
                    "illustType":0,
                    "xRestrict":0,
                    "restrict":0,
                    "sl":2,
                    "url":"https:\/\/i.pximg.net\/c\/250x250_80_a2\/img-master\/img\/2017\/01\/18\/13\/07\/46\/60993044_p0_square1200.jpg",
                    "description":"",
                    "tags":[
                        "少女",
                        "女の子",
                        "原创",
                        "オリジナル",
                        "场景",
                        "落書き",
                        "創作",
                        "背景",
                        "风景",
                        "オリジナル7500users入り"],
                    "userId":"18811972",
                    "userName":"淅洵",
                    "width":3507,
                    "height":2480,
                    "pageCount":1,
                    "isBookmarkable":true,
                    "bookmarkData":null,
                    "alt":"#少女 無題 - 淅洵的插画",
                    "isAdContainer":false,
                    "titleCaptionTranslation":{
                        "workTitle":null,
                        "workCaption":null
                    },
                    "createDate":"2017-01-18T13:07:46+09:00",
                    "updateDate":"2017-01-18T13:07:46+09:00",
                    "profileImageUrl":"https:\/\/i.pximg.net\/user-profile\/img\/2017\/05\/29\/17\/17\/49\/12623968_6cf3f1979e10643425972ae205a7920d_50.jpg"
                }, // ...
            ]
        },
        "relatedTags":[
            "風景",
            "背景",
            "風景画",
            "空",
            "雲",
            "創作",
            "ファンタジー",
            "夏",
            "青",
            "建物",
            "青空",
            "少女",
            "東京",
            "抽象画",
            "男の子",
            "透明水彩"
        ],
        "tagTranslation":{
            "風景":{
                "zh":"风景"
            },
            "背景":{
                "zh":"background"
            },
            "風景画":{
                "zh":"landscape painting"
            },
            "空":{
                "zh":"sky"
            },
            "雲":{
                "zh":"云"
            },
            "創作":{
                "zh":"原创"
            },
            "ファンタジー":{
                "zh":"奇幻"
            },
            "夏":{
                "zh":"夏天"
            },
            "青":{
                "zh":"蓝"
            },
            "建物":{
                "zh":"building"
            },
            "青空":{
                "zh":"蓝天"
            },
            "少女":{
                "zh":"young girl"
            },
            "東京":{
                "zh":"tokyo"
            },
            "抽象画":{
                "zh":"abstract art"
            },
            "男の子":{
                "zh":"男孩子"
            },
            "透明水彩":{
                "zh":"transparent watercolor"
            }
        },
        "zoneConfig":{
            "header":{
                "url":"https:\/\/pixon.ads-pixiv.net\/show?zone_id=header&format=js&s=1&up=0&a=22&ng=g&l=zh&uri=%2Fajax%2Fsearch%2Fartworks%2F_PARAM_&is_spa=1&K=59bba275c645c&ab_test_digits_first=20&yuid=FwdzEnA&suid=Pgfip96ymw5tvu9l9&num=5edb6277927"
            },
            "footer":{
                "url":"https:\/\/pixon.ads-pixiv.net\/show?zone_id=footer&format=js&s=1&up=0&a=22&ng=g&l=zh&uri=%2Fajax%2Fsearch%2Fartworks%2F_PARAM_&is_spa=1&K=59bba275c645c&ab_test_digits_first=20&yuid=FwdzEnA&suid=Pgfip96yn1fgocj2&num=5edb6277775"
            },
            "infeed":{
                "url":"https:\/\/pixon.ads-pixiv.net\/show?zone_id=illust_search_grid&format=js&s=1&up=0&a=22&ng=g&l=zh&uri=%2Fajax%2Fsearch%2Fartworks%2F_PARAM_&is_spa=1&K=59bba275c645c&ab_test_digits_first=20&yuid=FwdzEnA&suid=Pgfip96yn4t7cho88&num=5edb6277137"
            }
        },
        "extraData":{
            "meta":{
                "title":"#风景のイラスト・マンガ作品(投稿超过10万件） - pixiv",
                "description":"pixiv",
                "canonical":"https:\/\/www.pixiv.net\/tags\/%E9%A3%8E%E6%99%AF",
                "alternateLanguages":{
                    "ja":"https:\/\/www.pixiv.net\/tags\/%E9%A3%8E%E6%99%AF",
                    "en":"https:\/\/www.pixiv.net\/en\/tags\/%E9%A3%8E%E6%99%AF"
                },
                "descriptionHeader":"pixiv"
            }
        }
    }
}
```
> 注意：根据 Url 参数中 `Type` 的不同，返回数据的属性也会出现差异，  
  详见字段说明下的【请求 Url 中的 Type 与返回数据中属性的关系】表格

#### 字段说明 ####
- `illustManga`: (`Object`) 漫画和插画的搜索结果
    - `total`: (`number`) 搜索结果总量
    - `data`: (`Object[]`) 搜索结果(仅当前页数)
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
        - `bookmarkData`: (`Unknown`) 不明?
        - `alt`: (`string`) 简略介绍信息(在图片加载失败时可提供给`img`标签使用)
        - `isAdContainer`: (`boolean`) 不明?
        - `titleCaptionTranslation`: (`Object`) 不明?
            - `workTitle`: (`Unknown`) 不明?
            - `workCaption`: (`Unknown`) 不明?
        - `createDate`: (`string`) 作品创建时间(或者是完成时间?)
        - `updateDate`: (`string`) 作品上传时间
        - `profileImageUrl`: (`string`) 作者用户头像图片链接
    - `bookmarkRanges`: (`Object[]`) 收藏数范围(推测是用于按收藏数搜索而使用)
        - `min`: (`number`) 最小收藏数, 值为 `null` 则无限制
        - `max`: (`number`) 最大收藏数, 值为 `null` 则无限制
- `illust`: (`Object`) 插画作品搜索结果
    - **与`illustManga`结构相同**
- `manga`: (`Object`) 漫画作品搜索结果
    - **与`illustManga`结构相同**
- `novel`: (`Object`) 小说搜索结果
    - **与`illustManga`结构相同**
- `popular`: (`Object`) 受欢迎的搜索结果
    - `recent`: (`Object[]`) 近期推荐
        - **与`illustManga.data.{element}`结构相同**
    - `permanent`: (`Object[]`) 旧作品推荐
        - **与`illustManga.data.{element}`结构相同**
- `relatedTags`: (`string[]`) 与搜索结果相关的原始标签名
- `tagTranslation`: (`Object`) 相关标签的翻译信息
    - `{Attr: 标签名}`: 标签名为属性名, 对应 `relatedTags` 中的原始标签名
        - `语言名(例如 中文是 zh)`: (`string`) 标签翻译名
- `zoneConfig`: (`Object`) 猜测是广告相关信息?
- `extraData`: (`Object`) 扩展信息
    - `meta`: (`Object`) 网页元数据
        - `title`: (`string`) 网页标题
        - `description`: 搜索结果说明内容
        - `descriptionHeader`: (`string`) 说明内容的Html代码
        - `alternateLanguages`: (`Object`) 不明链接?
            - `{语言名}`: 对应语言的链接

##### 请求 Url 中的 Type 与返回数据中属性的关系 #####
> 表中数据可能有错误，如发现问题，可在发起 Issue 并附上不在该表中情况（例如）的请求信息和返回数据，经确认后将会更新文档。  

类型|illustManga|illust|manga|novel
:--|:-:|:-:|:-:|:-:
`artworks`     |√|×|×|×
`top`          |√|×|×|?
`illustrations`|×|√|×|×
`manga`        |×|×|√|×
`novels`       |×|×|×|√

符号解释:  
- `√`: 该属性一定存在
- `?`: 该属性可能存在
- `×`: 该属性不存在
