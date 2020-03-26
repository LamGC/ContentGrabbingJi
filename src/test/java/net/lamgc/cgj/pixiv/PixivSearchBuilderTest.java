package net.lamgc.cgj.pixiv;

import org.junit.Test;

public class PixivSearchBuilderTest {

    @Test
    public void buildTest() {
        PixivSearchBuilder builder = new PixivSearchBuilder("hololive");
        //builder.addExcludeKeyword("fubuki").addExcludeKeyword("minato");
        builder.addIncludeKeyword("35").addIncludeKeyword("okayu").addIncludeKeyword("百鬼あやめ");
        System.out.println(builder.buildURL());
    }

}
