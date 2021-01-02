local key = tostring(KEYS[1])
local value = ARGV[1]
local step = 175

if (key == nil) or (value == nil) then
    return -1
end

if not (ARGV[2] == nil) then
    step = tonumber(ARGV[2])
end

-- 步进循环搜索, 找到了立刻返回
local listLength = redis.call('llen', key);
for i = 0, listLength, step do
    local storeValue = redis.call("lrange", key, i, i + step)
    for listIndex, v in ipairs(storeValue) do
        if value == v then
            return i + listIndex - 1;
        end
    end
end

-- 如果没找到索引, 返回 0
return -1
