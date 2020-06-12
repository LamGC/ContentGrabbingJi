package net.lamgc.cgj.bot.framework.cli.message;

import net.lamgc.cgj.bot.message.MessageSender;
import net.lamgc.cgj.bot.message.MessageSource;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ConsoleMessageSender implements MessageSender {

    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private final MessageSource source;
    private final long id;

    ConsoleMessageSender(MessageSource source, long id) {
        this.source = source;
        this.id = id;
    }

    @Override
    public synchronized int sendMessage(String message) {
        System.out.println(dateFormat.format(new Date()) + " Bot -> " +
                (source == MessageSource.PRIVATE ? "#"  : "@") + id + ": " + message);
        return 0;
    }
}
