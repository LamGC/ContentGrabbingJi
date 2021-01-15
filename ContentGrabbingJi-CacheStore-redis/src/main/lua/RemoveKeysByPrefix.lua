local keyPrefix = tostring(KEYS[1])

if (keyPrefix == nil) then
    return false
end

if (string.sub(keyPrefix, string.len(keyPrefix)) ~= "*") then
    keyPrefix = keyPrefix .. "*"
end


local keys = redis.call("KEYS", keyPrefix)
local count = 0;
for _, v in ipairs(keys) do
    count = count + redis.call("DEL", v)
end

return count
