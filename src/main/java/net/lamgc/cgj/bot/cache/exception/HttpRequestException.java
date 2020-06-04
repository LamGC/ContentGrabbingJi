package net.lamgc.cgj.bot.cache.exception;

import java.io.IOException;
import java.util.Objects;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;

public class HttpRequestException extends IOException {

    private static final long serialVersionUID = -2229221075943552798L;

    private final StatusLine statusLine;

    private final String content;

    public HttpRequestException(HttpResponse response) throws IOException {
        this(response.getStatusLine(), EntityUtils.toString(response.getEntity()));
    }

    public HttpRequestException(StatusLine statusLine, String content) {
        super("Http Response Error: " + Objects.requireNonNull(statusLine, "statusLine is null") +
               ", Response Content: " + (content == null ? "null" : '\'' + content + '\''));
        this.statusLine = statusLine;
        this.content = content;
    }

    /**
     * 获取Http状态行
     */
    public StatusLine getStatusLine() {
        return statusLine;
    }

    /**
     * 获取Response内容
     * @return 如果没有返回, 则返回null
     */
    public String getContent() {
        return content;
    }

}