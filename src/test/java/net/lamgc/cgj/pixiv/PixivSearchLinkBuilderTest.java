package net.lamgc.cgj.pixiv;

import org.junit.Assert;
import org.junit.Test;

public class PixivSearchLinkBuilderTest {

    @Test
    public void buildTest() {
        PixivSearchLinkBuilder builder = new PixivSearchLinkBuilder("hololive");
        builder.addIncludeKeyword("35").addIncludeKeyword("okayu").addIncludeKeyword("百鬼あやめ");
        System.out.println(builder.buildURL());
    }

    @Test
    public void equalsTest() {
        Assert.assertEquals(new PixivSearchLinkBuilder("风景"), new PixivSearchLinkBuilder("风景"));
    }

    @Test
    public void hashCodeTest() {
        Assert.assertEquals(new PixivSearchLinkBuilder("风景").hashCode(), new PixivSearchLinkBuilder("风景").hashCode());
    }

}
