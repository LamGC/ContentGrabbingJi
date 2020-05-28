## Pixiv内容搜索接口 ##
### 说明 ###
> 注意: 本接口可能会影响Pixiv对账号的行为判断（猜测，不一定会）  

该接口用于在Pixiv搜索内容。

### 接口地址 ###
```
GET https://www.pixiv.net/ajax/search/{Type}/{Content}?{Param...}
```

- 需要登录: `是`

### Url参数 ###
- `Type`: 内容类型
    - illustrations(插画)
    - top(推荐)
    - manga(漫画)
    - novels(小说)
- `Content`: 搜索内容

### 参数 ###
#### 必填 ####
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

#### 选填 ####
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

