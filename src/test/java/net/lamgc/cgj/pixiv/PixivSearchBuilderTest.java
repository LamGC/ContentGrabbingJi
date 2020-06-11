package net.lamgc.cgj.pixiv;

import org.junit.Assert;
import org.junit.Test;

public class PixivSearchBuilderTest {

    @Test
    public void buildTest() {
        PixivSearchBuilder builder = new PixivSearchBuilder("hololive");
        builder.addIncludeKeyword("35").addIncludeKeyword("okayu").addIncludeKeyword("百鬼あやめ");
        System.out.println(builder.buildURL());
    }

    @Test
    public void equalsTest() {
        Assert.assertEquals(new PixivSearchBuilder("风景"), new PixivSearchBuilder("风景"));
    }

    @Test
    public void hashCodeTest() {
        Assert.assertEquals(new PixivSearchBuilder("风景").hashCode(), new PixivSearchBuilder("风景").hashCode());
    }

}
