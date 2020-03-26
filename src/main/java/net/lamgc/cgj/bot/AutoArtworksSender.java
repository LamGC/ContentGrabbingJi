package net.lamgc.cgj.bot;

import net.lz1998.cq.robot.CoolQ;
import org.apache.http.client.methods.HttpGet;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class AutoArtworksSender {

    private final CoolQ CQ;
    private final ReceiveType receiveType;
    private final long targetReceiveId;
    private Timer timer = new Timer();
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            HttpGet request = new HttpGet();

            // https://api.imjad.cn/pixiv/v2/?type=tags
        }
    };

    public AutoArtworksSender(CoolQ cq, ReceiveType receiveType, long receiveId) {
        this.CQ = cq;
        this.receiveType = receiveType;
        this.targetReceiveId = receiveId;
    }

    public void reset(long time) {
        if(time <= 0) {
            timer.schedule(task, new Random().nextInt(10 * 60 * 60 * 1000) + 7200000L); //2H ~ 12H
        } else {
            timer.schedule(task, time);
        }
    }

    public void sendMessage(String message, boolean auto_escape) {
        switch (receiveType) {
            case GROUP:
                CQ.sendGroupMsg(targetReceiveId, message, auto_escape);
                break;
            case Discuss:
                CQ.sendDiscussMsg(targetReceiveId, message, auto_escape);
                break;
            case PRIVATE:
                CQ.sendPrivateMsg(targetReceiveId, message, auto_escape);
                break;
        }
    }

    public enum ReceiveType {
        PRIVATE, GROUP, Discuss
    }

}
