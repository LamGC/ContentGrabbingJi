package net.lamgc.cgj.bot;

import net.lamgc.cgj.bot.event.MessageEvent;
import net.lamgc.cgj.bot.event.VirtualLoadMessageEvent;
import net.lamgc.utils.event.EventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * 消息事件处理调试器.
 * <p>当启用了消息事件处理调试后, 将会根据调试器代号调用指定调试器</p>
 */
@SuppressWarnings("unused")
public enum MessageEventExecutionDebugger {

    /**
     * PM - 压力测试
     */
    PM ((executor, event, properties, log) -> {
        MessageEvent virtualLoadEvent = VirtualLoadMessageEvent.toVirtualLoadMessageEvent(event, false);
        int rotation = 5;
        int number = 50;
        int interval = 2500;

        try {
            rotation = Integer.parseInt(properties.getProperty("debug.pm.rotation", "5"));
        } catch(NumberFormatException e) {
            log.warn("配置项 {} 值无效, 将使用默认值.({})", "debug.pm.rotation", rotation);
        }
        try {
            number = Integer.parseInt(properties.getProperty("debug.pm.number", "50"));
        } catch(NumberFormatException e) {
            log.warn("配置项 {} 值无效, 将使用默认值.({})", "debug.pm.number", number);
        }
        try {
            interval = Integer.parseInt(properties.getProperty("debug.pm.interval", "2500"));
        } catch(NumberFormatException e) {
            log.warn("配置项 {} 值无效, 将使用默认值.({})", "debug.pm.interval", interval);
        }

        boolean interrupted = false;
        Thread currentThread = Thread.currentThread();
        for(int rotationCount = 0; rotationCount < rotation && !interrupted; rotationCount++) {
            for(int sendCount = 0; sendCount < number; sendCount++) {
                if(currentThread.isInterrupted()) {
                    interrupted = true;
                    break;
                }
                executor.executor(virtualLoadEvent);
            }
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                break;
            }
        }
    });

    public final MessageExecuteDebugger debugger;

    MessageEventExecutionDebugger(MessageExecuteDebugger debugger) {
        this.debugger = debugger;
    }

    public static Logger getDebuggerLogger(MessageEventExecutionDebugger debugger) {
        return LoggerFactory.getLogger(MessageEventExecutionDebugger.class.getName() + "." + debugger.name());
    }

    @FunctionalInterface
    public interface MessageExecuteDebugger {
        /**
         * 接收事件并根据指定需求处理
         * @param executor 事件执行器
         * @param event 消息事件对象
         * @param properties 配置项, 调试器应按'debug.[debuggerName].'为前缀存储相应调试信息
         * @throws Exception 当抛出异常则打断调试, 并输出至日志
         */
        void accept(EventExecutor executor, MessageEvent event, Properties properties, Logger logger) throws Exception;
    }

}
