package net.lamgc.cgj.bot.util;

import org.junit.Assert;
import org.junit.Test;

public class GroupMuteManagerTest {

    @Test
    public void muteStateTest() {
        GroupMuteManager manager = new GroupMuteManager();
        Assert.assertNull(manager.isMute(1, true)); // 未设置的群组返回null
        Assert.assertFalse(manager.isMute(1, false)); // 未设置就返回false
        manager.setMuteState(1, true); // mute == true
        Assert.assertNotNull(manager.isMute(1, true)); // 第一次设置后不为null
        Assert.assertTrue(manager.isMute(1, false)); // 确保条件正常
        manager.setMuteState(2, true); // 不能出现不同群号的冲突
        manager.setMuteState(1, false);
        Assert.assertTrue(manager.isMute(2, false));
        Assert.assertNotNull(manager.isMute(1, true)); // 变更为false后依然不能返回null
        Assert.assertFalse(manager.isMute(1, false));
    }

    @Test
    public void invalidGroupIdTest() {
        GroupMuteManager manager = new GroupMuteManager();
        manager.setMuteState(-1, true); // 设置应该是无效的
        Assert.assertFalse(manager.isMute(-1, false)); // 由于设置无效, 返回false即可
    }

}
