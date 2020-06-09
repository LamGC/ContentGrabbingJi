package net.lamgc.cgj.pixiv;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.bouncycastle.util.io.Streams;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Ignore
public class PixivUgoiraBuilderTest {

    @Test
    public void buildTest() throws IOException {
        File outputFile = new File("./output2.gif");
        HttpClient httpClient = HttpClientBuilder.create().setProxy(new HttpHost("127.0.0.1", 1080)).build();
        PixivUgoiraBuilder builder = new PixivUgoiraBuilder(httpClient, 81163967);
        LoggerFactory.getLogger(PixivUgoiraBuilderTest.class).info("UgoiraMeta: {}", builder.getUgoiraMeta());
        InputStream inputStream = builder.buildUgoira(true);
        Files.write(outputFile.toPath(), Streams.readAll(inputStream));
    }

}
