package net.lamgc.cgj.bot.event;

import net.lamgc.utils.event.EventObject;

public abstract class MessageEvent implements EventObject {

    private final long fromGroup;
    private final long fromQQ;
    private final String message;

    public MessageEvent(long fromGroup, long fromQQ, String message) {
        this.fromGroup = fromGroup;
        this.fromQQ = fromQQ;
        this.message = message;
    }

    public abstract int sendMessage(final String message);

    public abstract Object getRawMessage();

    public long getFromGroup() {
        return fromGroup;
    }

    public long getFromQQ() {
        return fromQQ;
    }

    public String getMessage() {
        return message;
    }

}
