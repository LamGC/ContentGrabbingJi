package net.lamgc.cgj.bot.framework.coolq;

import net.lamgc.cgj.bot.boot.ApplicationBoot;
import net.lamgc.cgj.bot.event.BotEventHandler;
import net.lamgc.cgj.bot.framework.coolq.message.SpringCQMessageEvent;
import net.lamgc.cgj.bot.framework.coolq.message.SpringCQMessageSenderFactory;
import net.lamgc.utils.event.EventHandler;
import net.lz1998.cq.event.message.CQDiscussMessageEvent;
import net.lz1998.cq.event.message.CQGroupMessageEvent;
import net.lz1998.cq.event.message.CQMessageEvent;
import net.lz1998.cq.event.message.CQPrivateMessageEvent;
import net.lz1998.cq.robot.CQPlugin;
import net.lz1998.cq.robot.CoolQ;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class CQPluginMain extends CQPlugin implements EventHandler {

    private final static AtomicBoolean initialState = new AtomicBoolean();

    public CQPluginMain() {

        LoggerFactory.getLogger(CQPluginMain.class)
                .info("BotEventHandler.COMMAND_PREFIX = {}", BotEventHandler.COMMAND_PREFIX);
    }

    @Override
    public int onPrivateMessage(CoolQ cq, CQPrivateMessageEvent event) {
        return processMessage(cq, event);
    }

    @Override
    public int onGroupMessage(CoolQ cq, CQGroupMessageEvent event) {
        return processMessage(cq, event);
    }

    @Override
    public int onDiscussMessage(CoolQ cq, CQDiscussMessageEvent event) {
        return processMessage(cq, event);
    }

    /**
     * 处理消息
     * @param cq CoolQ机器人对象
     * @param event 消息事件
     * @return 是否拦截消息
     */
    private static int processMessage(CoolQ cq, CQMessageEvent event) {
        SpringCQMessageSenderFactory.setCoolQ(cq);
        synchronized (initialState) {
            if(!initialState.get()) {
                ApplicationBoot.initialBot();
                initialState.set(true);
            }
        }
        if(BotEventHandler.mismatch(event.getMessage())) {
            return MESSAGE_IGNORE;
        }
        BotEventHandler.executeMessageEvent(new SpringCQMessageEvent(cq, event));
        return MESSAGE_BLOCK;
    }

}
