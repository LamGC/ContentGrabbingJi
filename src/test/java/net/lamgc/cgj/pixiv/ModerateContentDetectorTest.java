package net.lamgc.cgj.pixiv;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Ignore
public class ModerateContentDetectorTest {

    private final static AdultContentDetector acd = new ModerateContentDetector("d91b6c3fa2bba9ee8f9e68827ba0d937");
    private final static Logger log = LoggerFactory.getLogger(ModerateContentDetector.class);

    @Test
    public void checkTest() throws Exception {
        log.info("Detect: {}, isAdult: {}",
                acd.detect(80840411, false, 0),
                acd.isAdultContent(80840411, false, 0));
    }


}
