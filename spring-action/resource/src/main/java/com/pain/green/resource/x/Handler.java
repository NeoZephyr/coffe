package com.pain.green.resource.x;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class Handler extends URLStreamHandler {

    // 可以按照固定路径格式，放在 sun.net.www.protocol 目录下
    // 比如 Handler 路径为 sun.net.www.protocol.x.Handler
    // 或者可以通过 jvm 参数进行指定
    // -Djava.protocol.handler.pkgs=com.pain.green.resource
    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        return new XURLConnection(u);
    }
}
