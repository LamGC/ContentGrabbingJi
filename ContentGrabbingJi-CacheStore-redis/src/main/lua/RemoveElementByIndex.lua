local function getRandom(n)
    local t = {
        "0","1","2","3","4","5","6","7","8","9",
        "a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z",
        "A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z",
    }
    local s = ""
    for _ = 1, n do
        s = s .. t[math.random(#t)]
    end;
    return s
end

local key = tostring(KEYS[1])
local index = tonumber(ARGV[1])

if (key == nil) or (index == nil) or (redis.call("exists", key) == false) then
    return 0
end

local listLength = redis.call("llen", key)
if listLength == 0 then
    return 0
end
if (index < 0) or (index >= listLength) or redis.call("lIndex", key, index) == nil then
    return 0
else
    local flag = getRandom(24)
    redis.call("lSet", key, index, flag);
    return redis.call("lRem", key, 0, flag);
end
