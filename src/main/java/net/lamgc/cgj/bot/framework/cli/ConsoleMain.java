package net.lamgc.cgj.bot.framework.cli;

import net.lamgc.cgj.bot.boot.ApplicationBoot;
import net.lamgc.cgj.bot.event.BotEventHandler;
import net.lamgc.cgj.bot.message.MessageSenderBuilder;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ConsoleMain {

    private final static Logger log = LoggerFactory.getLogger(ConsoleMain.class);

    public static void start() throws IOException {
        MessageSenderBuilder.setCurrentMessageSenderFactory(new ConsoleMessageSenderFactory());
        ApplicationBoot.initialBot();
        LineReader lineReader = LineReaderBuilder.builder()
                .appName("CGJ")
                .history(new DefaultHistory())
                .terminal(TerminalBuilder.terminal())
                .build();

        long qqId = Long.parseLong(lineReader.readLine("会话QQ: "));
        long groupId = Long.parseLong(lineReader.readLine("会话群组号:"));
        boolean isGroup = false;
        do {
            String input = lineReader.readLine("App " + qqId + (isGroup ? "@" + groupId : "$private") + " >");
            if(input.equalsIgnoreCase("#exit")) {
                System.out.println("退出应用...");
                break;
            } else if(input.equalsIgnoreCase("#setgroup")) {
                isGroup = !isGroup;
                System.out.println("System: 群模式状态已变更: " + isGroup);
                continue;
            }
            try {
                BotEventHandler.executeMessageEvent(new ConsoleMessageEvent(isGroup ? groupId : 0, qqId, input), true);
            } catch (InterruptedException e) {
                log.error("执行时发生中断", e);
            }
        } while(true);
    }

}
