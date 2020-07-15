package net.lamgc.cgj.bot.util;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 群禁言管理器.
 * <p>该管理器用于存取群组禁言状态.</p>
 */
public class GroupMuteManager {

    private final Map<Long, AtomicBoolean> muteStateMap = new Hashtable<>();

    /**
     * 查询某群是否被禁言.
     * @param groupId 群组Id
     * @param rawValue 是否返回原始值(当没有该群状态, 且本参数为true时, 将返回null)
     * @return 返回状态值, 如无该群禁言记录且rawValue = true, 则返回null
     */
    public Boolean isMute(long groupId, boolean rawValue) {
        if(groupId <= 0) {
            return false;
        }
        AtomicBoolean state = muteStateMap.get(groupId);
        if(state == null && rawValue) {
            return null;
        }
        return state != null && state.get();
    }

    /**
     * 设置机器人禁言状态.
     * <p>设置该项可防止因机器人在禁言期间反馈请求导致被封号.</p>
     * @param mute 如果被禁言, 传入true
     */
    public void setMuteState(long groupId, boolean mute) {
        if(groupId <= 0) {
            return;
        }
        if(!muteStateMap.containsKey(groupId)) {
            muteStateMap.put(groupId, new AtomicBoolean(mute));
        } else {
            muteStateMap.get(groupId).set(mute);
        }
    }

}
