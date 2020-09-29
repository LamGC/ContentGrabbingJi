# CacheStore-API #

如需开发更多缓存组件，至少需要实现以下接口：
- CacheStore
    - 不一定要实现该接口, 可以直接实现 具体的接口（比如 `SingleCacheStore`）
- CacheStoreFactory
    - 你还需要为其添加 `@Factory` 注解，否则不会生效。
- SingleCacheStore

正常情况下，该模块不需要进行更改，即使需要更改，也需要保证向后兼容性。
