package net.lamgc.cgj.util;

import org.junit.Assert;
import org.junit.Test;

public class LockerMapTest {

    @Test
    public void createAndFinalizeTest() {
        LockerMap<String> map = new LockerMap<>();
        Locker<String> locker = map.createLocker("Test", true);
        Assert.assertEquals(locker, map.createLocker("Test", true));
        locker.lock();
        locker.unlock();
        System.gc();
    }

}
