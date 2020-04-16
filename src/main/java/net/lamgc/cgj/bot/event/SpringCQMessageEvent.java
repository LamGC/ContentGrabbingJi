package net.lamgc.cgj.bot.event;

import net.lz1998.cq.event.message.CQDiscussMessageEvent;
import net.lz1998.cq.event.message.CQGroupMessageEvent;
import net.lz1998.cq.event.message.CQMessageEvent;
import net.lz1998.cq.robot.CoolQ;

import java.net.InetSocketAddress;
import java.util.Objects;

public class SpringCQMessageEvent extends MessageEvent {

    private final static int TYPE_PRIVATE = 0;
    private final static int TYPE_GROUP = 1;
    private final static int TYPE_DISCUSS = 2;

    private final CoolQ cq;
    private final int type;
    private final CQMessageEvent messageEvent;

    public SpringCQMessageEvent(CoolQ cq, CQMessageEvent messageEvent) {
        super(messageEvent instanceof CQGroupMessageEvent ? (
                (CQGroupMessageEvent) messageEvent).getGroupId() :
              messageEvent instanceof CQDiscussMessageEvent ?
                ((CQDiscussMessageEvent) messageEvent).getDiscussId() : 0,
              messageEvent.getUserId(), messageEvent.getMessage());
        this.cq = Objects.requireNonNull(cq);
        if(messageEvent instanceof CQGroupMessageEvent) {
            type = TYPE_GROUP;
        } else if (messageEvent instanceof CQDiscussMessageEvent) {
            type = TYPE_DISCUSS;
        } else {
            type = TYPE_PRIVATE;
        }
        this.messageEvent = messageEvent;
    }

    @Override
    public int sendMessage(final String message) {
        switch(type) {
            case TYPE_PRIVATE:
                return cq.sendPrivateMsg(getFromQQ(), message, false).getData().getMessageId();
            case TYPE_GROUP:
            case TYPE_DISCUSS:
                return cq.sendGroupMsg(getFromGroup(), message, false).getData().getMessageId();
            default:
                return -1;
        }
    }

    /**
     * 该功能在CQ上需要CQHttp插件启用http接口调用和serve_data_files为true.
     * 如使用本方法, 则对方需开启5700端口, 且可能会影响多机器人处理.
     * @param imageFileName 图片文件名
     * @return 图片下载链接
     */
    @Override
    public String getImageUrl(String imageFileName) {
        InetSocketAddress remoteAddress = cq.getBotSession().getRemoteAddress();
        if(remoteAddress == null) {
            throw new IllegalStateException("remoteAddress failed to get");
        }
        String file = cq.getImage(imageFileName).getData().getFile().replaceAll("\\\\", "/");
        return "http://" + remoteAddress.getHostString() + ":5700/data" + file.substring(file.lastIndexOf("/data") + 5);
    }

    @Override
    public Object getRawMessage() {
        return messageEvent;
    }
}
