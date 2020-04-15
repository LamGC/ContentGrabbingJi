package net.lamgc.cgj.pixiv;

import net.lamgc.cgj.bot.BotCode;
import org.junit.Assert;
import org.junit.Test;

public class BotCodeTest {

    @Test
    public void parseTest() {
        System.out.println(BotCode.parse("[CQ:at,qq=1020304050]").toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void badHeaderTest() {
        BotCode.parse("[,qq=12345]");
        Assert.fail("Parser success");
    }

    @Test(expected = IllegalArgumentException.class)
    public void badFunctionNameTest() {
        BotCode.parse("[CQ:,qq=12345]");
        Assert.fail("Parser success");
    }

}
