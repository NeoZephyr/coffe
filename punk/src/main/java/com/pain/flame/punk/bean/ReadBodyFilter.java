package com.pain.flame.punk.bean;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.io.output.StringBuilderWriter;

import javax.servlet.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.nio.charset.StandardCharsets;

public class ReadBodyFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        PushbackInputStream pushbackInputStream = new PushbackInputStream(request.getInputStream());
//
//        int n;
//        long count = 0;
//        byte[] buffer = new byte[8109];
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//
//        while ((n = pushbackInputStream.read(buffer)) != -1) {
//            outputStream.write(buffer, 0, n);
//            count += n;
//        }
//
//        pushbackInputStream.unread((int) count);
//        String requestBody = outputStream.toString(StandardCharsets.UTF_8);

        // String requestBody = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
        System.out.println("fuck filter");
        chain.doFilter(request, response);
    }
}
