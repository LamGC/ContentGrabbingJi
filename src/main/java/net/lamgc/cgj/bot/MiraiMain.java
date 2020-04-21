package net.lamgc.cgj.bot;

import net.lamgc.cgj.bot.event.BotEventHandler;
import net.lamgc.cgj.bot.event.MiraiMessageEvent;
import net.lamgc.cgj.bot.message.MessageSenderBuilder;
import net.lamgc.cgj.bot.message.MiraiMessageSenderFactory;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.japt.Events;
import net.mamoe.mirai.message.FriendMessage;
import net.mamoe.mirai.message.GroupMessage;
import net.mamoe.mirai.qqandroid.QQAndroid;
import net.mamoe.mirai.utils.BotConfiguration;
import org.apache.commons.net.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

public class MiraiMain implements Closeable {

    private final Logger log = LoggerFactory.getLogger(this.toString());

    private Bot bot;

    private final static Properties botProperties = new Properties();

    public void init() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
        try {
            Class.forName(BotEventHandler.class.getName());
        } catch (ClassNotFoundException e) {
            log.error("加载BotEventHandler时发生异常", e);
            return;
        }

        File botPropFile = new File("./bot.properties");
        try (Reader reader = new BufferedReader(new FileReader(botPropFile))) {
            botProperties.load(reader);
        } catch (IOException e) {
            log.error("机器人配置文件读取失败!", e);
            return;
        }

        bot = QQAndroid.INSTANCE.newBot(Long.parseLong(botProperties.getProperty("bot.qq", "0")), Base64.decodeBase64(botProperties.getProperty("bot.password", "")), new BotConfiguration());
        Events.subscribeAlways(GroupMessage.class, (msg) -> BotEventHandler.executor.executor(new MiraiMessageEvent(msg)));
        Events.subscribeAlways(FriendMessage.class, (msg) -> BotEventHandler.executor.executor(new MiraiMessageEvent(msg)));
        bot.login();
        MessageSenderBuilder.setCurrentMessageSenderFactory(new MiraiMessageSenderFactory(bot));
        BotEventHandler.preLoad();
        bot.join();
    }

    public void close() {
        log.warn("正在关闭机器人...");
        bot.close(null);
        log.warn("机器人已关闭.");
    }

}
