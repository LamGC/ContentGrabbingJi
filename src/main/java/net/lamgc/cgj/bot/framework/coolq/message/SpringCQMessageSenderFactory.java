package net.lamgc.cgj.bot.framework.coolq.message;

import net.lamgc.cgj.bot.message.MessageSender;
import net.lamgc.cgj.bot.message.MessageSenderFactory;
import net.lamgc.cgj.bot.message.MessageSource;
import net.lz1998.cq.robot.CoolQ;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class SpringCQMessageSenderFactory implements MessageSenderFactory {

    private final static AtomicReference<CoolQ> coolQ = new AtomicReference<>();

    /**
     * 设置CoolQ对象.
     * <p>该方法仅接受第一次设置的CoolQ对象, 其他对象将会忽略.</p>
     * @param coolQObj CoolQ对象
     */
    public static void setCoolQ(CoolQ coolQObj) {
        if(coolQ.get() == null) {
            coolQ.set(coolQObj);
        }
    }

    @Override
    public MessageSender createMessageSender(MessageSource source, long id) {
        return new SpringCQMessageSender(
                Objects.requireNonNull(coolQ.get(), "CoolQ object not ready"), source, id);
    }
}
