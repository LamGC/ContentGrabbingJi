# CacheStore-API #

如需开发更多缓存组件，至少需要实现以下接口：
- CacheStore / CollectionCacheStore
    - `CacheStore` 是所有缓存容器的父接口。
    - 这两个类为抽象接口，定义了部分具体接口的公共方法。
    - 不一定要**单独**实现该接口，可以**直接实现**具体的接口（比如 `SingleCacheStore`）
- CacheStoreFactory
    - 你还需要为其添加 `@Factory` 注解，否则不会生效。
- SingleCacheStore / MapCacheStore / SetCacheStore / ListCacheStore
    - `MapCacheStore`、`SetCacheStore` 和 `ListCacheStore` 是 `CollectionCacheStore` 的子类。
    - 至少需要提供其中一种实现才能算是一个有效的缓存组件。尚未实现的部分将会由其他组件代替。

完成缓存组件的开发后，应按照 SPI 机制的要求，设置所属 CacheStoreFactory 为 Service。

正常情况下，该模块不需要进行更改，即使需要更改，也需要保证向后兼容性。
