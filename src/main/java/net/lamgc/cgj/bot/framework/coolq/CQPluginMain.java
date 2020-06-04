package net.lamgc.cgj.bot.framework.coolq;

import net.lamgc.cgj.bot.boot.ApplicationBoot;
import net.lamgc.cgj.bot.event.BotEventHandler;
import net.lamgc.cgj.bot.framework.coolq.message.SpringCQMessageEvent;
import net.lamgc.utils.event.EventHandler;
import net.lz1998.cq.event.message.CQDiscussMessageEvent;
import net.lz1998.cq.event.message.CQGroupMessageEvent;
import net.lz1998.cq.event.message.CQMessageEvent;
import net.lz1998.cq.event.message.CQPrivateMessageEvent;
import net.lz1998.cq.robot.CQPlugin;
import net.lz1998.cq.robot.CoolQ;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CQPluginMain extends CQPlugin implements EventHandler {

    public CQPluginMain() {
        // TODO(LamGC, 2020.04.21): SpringCQ无法适配MessageSenderBuilder
        // MessageSenderBuilder.setCurrentMessageSenderFactory(new SpringCQMessageSenderFactory());
        ApplicationBoot.initialBot();
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
        if(BotEventHandler.mismatch(event.getMessage())) {
            return MESSAGE_IGNORE;
        }
        BotEventHandler.executeMessageEvent(new SpringCQMessageEvent(cq, event));
        return MESSAGE_BLOCK;
    }

}
