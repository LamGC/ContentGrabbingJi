package net.lamgc.cgj.bot.framework.cli;

import net.lamgc.cgj.bot.event.MessageEvent;

import java.util.Date;

public class ConsoleMessageEvent extends MessageEvent {

    public ConsoleMessageEvent(String message, boolean isGroup) {
        super(isGroup ? 1 : 0, 1, message);
    }

    @Override
    public int sendMessage(String message) {
        System.out.println(new Date() + " Bot: " + message);
        return 0;
    }

    @Override
    public String getImageUrl(String image) {
        return null;
    }
}
