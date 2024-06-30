package loadBalancer;

import java.util.List;
import java.util.Map;

public class Response {
    private final String content;
    private final List<Integer> stream;
    private final Map<String, List<String>> headers;
    private final boolean shouldBeByteStream;
    /*
     * @param content -> the response as character string,
     * @param headers -> all the headers from the backend
     * @param shouldBeByteStream -> to know whether the payload is text or other media type
     * @param stream -> Byte Stream 
     */
    Response(String content, Map<String, List<String>> headers, boolean shouldBeByteStream, List<Integer> stream) {
        this.content = content;
        this.headers = headers;
        this.shouldBeByteStream = shouldBeByteStream;
        this.stream = stream;
    }

    public String getContent() {
        return this.content;
    }

    public Map<String, List<String>> getHeaders() {
        return this.headers;
    }

    public boolean shouldBeByteStream() {
        return this.shouldBeByteStream;
    }
    public List<Integer> getStream() {
        return this.stream;
    }
}
