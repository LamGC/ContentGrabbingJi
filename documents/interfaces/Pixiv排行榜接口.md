## Pixiv 排行榜获取接口 ##

### 接口地址 ###
```
GET https://www.pixiv.net/ranking.php
```

- 需要登录: `是`
### 参数 ###
> 提示: 该接口参数较为复杂，请结合表格查看

- `date`: 排行榜时间，与Mode有关 (格式: yyyy-MM-dd)
- `mode`: 排行榜模式
    - `daily`: 每天
    - `weekly`: 每周
    - `monthly`: 每月
    - `rookie`: 新人
    - `original`: 原创
    - `male`: 男性偏好
    - `female`: 女性偏好
    - `daily_r18`: 每天 - 仅成人内容
    - `weekly_r18`: 每周 - 仅成人内容
    - `male_r18`: 男性偏好 - 仅成人内容
    - `female_r18`: 女性偏好 - 仅成人内容
- `content`: 排行榜内容类型
    - `all`: 全部内容 (实际使用请直接省略`content`参数)
    - `illust`: 插画
    - `ugoira`: 动图
    - `manga`: 漫画
- `p`: 排行榜页数 (如超出范围，则返回错误信息)
- `format`: 格式
    - `json`: 以Json返回数据
    - (留空): 返回完整的排行榜网页

#### 参数关系表 ####
`mode`参数与`content`参数有一些支持关系，并不是所有的`mode`参数都能被所有的`content`参数支持，故附下表。  

参数      |all|illust|ugoira|manga
   :-:    |:-:| :-:  | :-:  | :-:
daily     |`√`|`√`|`√`|`√`
weekly    |`√`|`√`|`√`|`√`
monthly   |`√`|`√`|×|`√`
rookie    |`√`|`√`|×|`√`
original  |`√`|×|×|×
male      |`√`|×|×|×
female    |`√`|×|×|×
daily_r18 |`√`|`√`|`√`|`√`
weekly_r18|`√`|`√`|`√`|`√`
male_r18  |`√`|×|×|×
female_r18|`√`|×|×|×


### 返回数据 ###
#### 数据示例 ####
```json
{
    "contents":[
        {
            "title":"【伊アオ】髪結い。",
            "date":"2020年05月31日 14:26",
            "tags":[
                "鬼滅の刃",
                "伊アオ",
                "嘴平伊之助",
                "神崎アオイ",
                "鬼滅の刃1000users入り"
            ],
            "url":"https:\/\/i.pximg.net\/c\/240x480\/img-master\/img\/2020\/05\/31\/14\/26\/41\/81987309_p0_master1200.jpg",
            "illust_type":"0",
            "illust_book_style":"0",
            "illust_page_count":"1",
            "user_name":"シロウ",
            "profile_img":"https:\/\/i.pximg.net\/user-profile\/img\/2020\/05\/01\/02\/18\/18\/18450100_ac34872504959f8cc26f086248066b39_50.png",
            "illust_content_type":{
                "sexual":0,
                "lo":false,
                "grotesque":false,
                "violent":false,
                "homosexual":false,
                "drug":false,
                "thoughts":false,
                "antisocial":false,
                "religion":false,
                "original":false,
                "furry":false,
                "bl":false,
                "yuri":false
            },
            "illust_series":false,
            "illust_id":81987309,
            "width":600,
            "height":2226,
            "user_id":174995,
            "rank":51,
            "yes_rank":83,
            "rating_count":707,
            "view_count":19759,
            "illust_upload_timestamp":1590902801,
            "attr":"",
            "is_bookmarked":false,
            "bookmarkable":true
        }, // ....
    ],
    "mode":"daily",
    "content":"all",
    "page":2,
    "prev":1,
    "next":3,
    "date":"20200601",
    "prev_date":"20200531",
    "next_date":false,
    "rank_total":500
}
```

#### 参数详解 ####
- `contents`: (`Object[]`) 排行榜数组, 最多50行排行榜信息
    - `illust_id`: (`number`) 作品Id
    - `title`: (`string`) 作品标题
    - `attr`: (`string`) 不明?
    - `tags`: (`string[]`) 原始标签数组
    - `url`: (`string`) 预览画质的原始尺寸图下载链接(存在防盗链)
    - `illust_type`: (`string` -> `number`) 作品类型
    - `illust_book_style`: (`string` -> `number`) 不明?
    - `illust_page_count`: (`string` -> `number`) 作品页数
    - `user_name`: (`string`) 画师用户名
    - `user_id`: (`number`) 画师用户Id
    - `profile_img`: (`string`) 画师用户头像
    - `illust_content_type`: (`Object`) 作品内容信息
        - 待补充
    - `illust_series`: (`boolean`) 不明?
    - `width`: (`number`) 作品宽度(建议以原图为准)
    - `height`: (`number`) 作品高度(建议以原图为准)
    - `rank`: (`number`) 本期排行榜排名
    - `yes_rank`: (`number`) 上期同排行榜排名
    - `rating_count`: 
    - `view_count`: (`number`) 浏览量
    - `illust_upload_timestamp`: (`number`) 作品上传时间戳(10位)
    - `is_bookmarked`: (`boolean`) 不明?
    - `bookmarkable`: (`boolean`) 不明?
- `mode`: (`string`) 请求的排行榜模式字段
- `content`: (`string`) 请求的内容类型
- `page`: (`number`) 当前排行榜页数
- `prev`: (`string` / `boolean`) 上一页排行榜页数, 如果该请求的页数为首页, 则为`false`
- `next`: (`string` / `boolean`) 下一页排行榜页数, 如果该请求的页数为页尾, 则为`false`
- `date`: (`string`) 排行榜日期(格式：`yyyyMMdd`)
- `prev_date`: (`string` / `boolean`) 如果存在上一期排行榜, 则该属性为上期排行榜日期字符串, 否则为`false`
- `next_date`: (`string` / `boolean`) 如果存在下一期排行榜, 则该属性为下期排行榜日期字符串, 否则为`false`
- `rank_total`: (`number`) 该排行榜的总排行数
