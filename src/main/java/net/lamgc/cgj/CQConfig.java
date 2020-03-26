package net.lamgc.cgj;

import net.lz1998.cq.CQGlobal;
import net.lz1998.cq.EnableCQ;

@EnableCQ
public class CQConfig {

    public static void init() {
        CQGlobal.pluginList.add(CQPluginMain.class);
    }

}
