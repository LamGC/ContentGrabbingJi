package net.lamgc.cgj.bot.framework.cli;

import net.lamgc.cgj.bot.message.MessageSender;

import java.util.Date;

public class ConsoleMessageSender implements MessageSender {
    @Override
    public synchronized int sendMessage(String message) {
        System.out.println(new Date() + " Bot: " + message);
        return 0;
    }
}
