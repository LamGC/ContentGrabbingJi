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
