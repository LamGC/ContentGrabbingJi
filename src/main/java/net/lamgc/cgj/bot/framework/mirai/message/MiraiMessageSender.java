package net.lamgc.cgj.bot.framework.mirai.message;

import com.google.common.base.Strings;
import net.lamgc.cgj.bot.BotCode;
import net.lamgc.cgj.bot.boot.BotGlobal;
import net.lamgc.cgj.bot.cache.CacheStore;
import net.lamgc.cgj.bot.cache.HotDataCacheStore;
import net.lamgc.cgj.bot.cache.LocalHashCacheStore;
import net.lamgc.cgj.bot.cache.StringRedisCacheStore;
import net.lamgc.cgj.bot.message.MessageSender;
import net.lamgc.cgj.bot.message.MessageSource;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MiraiMessageSender implements MessageSender {

    private final Contact member;
    private final MessageSource source;
    private final static Logger log = LoggerFactory.getLogger(MiraiMessageSender.class);
    private final static CacheStore<String> imageIdCache = new HotDataCacheStore<>(
            new StringRedisCacheStore(BotGlobal.getGlobal().getRedisServer(), "mirai.imageId"),
            new LocalHashCacheStore<>(),
            5400000, 1800000, true);

    /**
     * 使用id构造发送器
     * @param bot 机器人对象
     * @param source 消息源类型
     * @param id id, 将会根据消息源类型判断为什么号(QQ号或群号)
     * @throws NoSuchElementException 当在机器人好友列表或群列表里没有这个好友或群的时候抛出
     */
    public MiraiMessageSender(Bot bot, MessageSource source, long id) {
        this(source == MessageSource.PRIVATE ? bot.getFriend(id) : bot.getGroup(id), source);
    }

    /**
     * 通过联系人对象构造发送器
     * @param contact 联系人
     * @param source 消息源类型
     */
    public MiraiMessageSender(Contact contact, MessageSource source) {
        this.member = contact;
        this.source = source;
    }

    @Override
    public int sendMessage(final String message) {
        log.debug("处理前的消息内容:\n{}", message);
        Message msgBody = processMessage(Objects.requireNonNull(message));
        log.debug("处理后的消息内容(可能出现乱序的情况, 但实际上顺序是没问题的):\n{}", msgBody.contentToString());
        member.sendMessage(msgBody);
        return 0;
    }

    private final static Pattern cqCodePattern = BotCode.getCodePattern();
    private Message processMessage(final String message) {
        Matcher matcher = cqCodePattern.matcher(message);
        ArrayList<String> cqCode = new ArrayList<>();
        while (matcher.find()) {
            cqCode.add(matcher.group());
        }
        String[] texts = message
                .replaceAll("&", "&38")
                .replaceAll("\\{", "&" + Character.getNumericValue('{'))
                .replaceAll(cqCodePattern.pattern(), "|{BotCode}|")
                .replaceAll("&" + Character.getNumericValue('{'), "{")
                .replaceAll("&38", "&")
                .split("\\|");

        MessageChain messages = MessageUtils.newChain();
        int codeIndex = 0;
        for(String text : texts) {
            if(text.equals("{BotCode}")) {
                BotCode code;
                try {
                    code = BotCode.parse(cqCode.get(codeIndex++));
                } catch(IllegalArgumentException e) {
                    log.warn("解析待发送消息内的BotCode时发生异常, 请检查错误格式BotCode的来源并尽快排错!", e);
                    continue;
                }
                messages = messages.plus(processBotCode(code));
            } else {
                messages = messages.plus(text);
            }
        }

        return messages;
    }

    private Message processBotCode(BotCode code) {
        switch(code.getFunctionName().toLowerCase()) {
            case "image":
                Image img;
                if(code.containsParameter("id")) {
                    img = MessageUtils.newImage(code.getParameter("id"));
                } else if(code.containsParameter("absolutePath")) {
                    img = uploadImage(code);
                } else {
                    return MessageUtils.newChain("(参数不存在)");
                }
                if(Strings.nullToEmpty(code.getParameter("flashImage"))
                        .equalsIgnoreCase("true")) {
                    return MessageUtils.flash(img);
                } else {
                    return img;
                }
            case "face":
                if(!code.containsParameter("id")) {
                    return MessageUtils.newChain("(无效的表情Id)");
                }
                int faceId = Integer.parseInt(code.getParameter("id"));
                if(faceId <= 0) {
                    return MessageUtils.newChain("(无效的表情Id)");
                }
                return new Face(faceId);
            default:
                log.warn("解析到不支持的BotCode: {}", code);
                return MessageUtils.newChain("(不支持的BotCode)");
        }
    }

    /**
     * 存在缓存的上传图片.
     * @param code 图片BotCode
     * @return Image对象
     */
    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    public Image uploadImage(BotCode code) {
        log.debug("传入BotCode信息:\n{}", code);
        String absolutePath = code.getParameter("absolutePath");
        if(Strings.isNullOrEmpty(absolutePath)) {
            throw new IllegalArgumentException("BotCode does not contain the absolutePath parameter");
        }

        String imageName = code.getParameter("imageName");
        if(!Strings.isNullOrEmpty(imageName)) {
            Image image = null;
            imageName = (source + "." + imageName).intern();
            if(!imageIdCache.exists(imageName) ||
                    Strings.nullToEmpty(code.getParameter("updateCache")).equalsIgnoreCase("true")) {
                synchronized (imageName) {
                    if(!imageIdCache.exists(imageName) ||
                            Strings.nullToEmpty(code.getParameter("updateCache")) .equalsIgnoreCase("true")) {
                        log.trace("imageName [{}] 缓存失效或强制更新, 正在更新缓存...", imageName);
                        image = uploadImage0(new File(absolutePath));
                        String cacheExpireAt;
                        long expireTime = 864000000; // 10d
                        if(!Strings.isNullOrEmpty(cacheExpireAt = code.getParameter("cacheExpireAt"))) {
                            try {
                                expireTime = Integer.parseInt(cacheExpireAt);
                            } catch (NumberFormatException e) {
                                log.warn("BotCode中的cacheExpireAt参数无效: {}", cacheExpireAt);
                            }
                        }
                        imageIdCache.update(imageName, image.getImageId(), expireTime);
                        log.trace("imageName [{}] 缓存更新完成.(有效时间: {})", imageName, expireTime);
                    } else {
                        log.trace("ImageName: [{}] 缓存命中.", imageName);
                    }
                }
            } else {
                log.trace("ImageName: [{}] 缓存命中.", imageName);
            }

            if(image == null) {
                image = MessageUtils.newImage(imageIdCache.getCache(imageName));
            }

            log.debug("ImageName: {}, ImageId: {}", imageName, image.getImageId());
            return image;
        } else {
            log.debug("未设置imageName, 无法使用缓存.");
            return uploadImage0(new File(absolutePath));
        }
    }

    private Image uploadImage0(File imageFile) {
        return member.uploadImage(imageFile);
    }

}
