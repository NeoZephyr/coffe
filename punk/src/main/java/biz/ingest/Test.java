package biz.ingest;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

public class Test {

    public static void main(String[] args) throws IOException, URISyntaxException {

        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("foo", URLEncoder.encode("bar", "UTF-8"));
        // paramMap.add("x_user_name", "中文 可以吗");
        paramMap.add("x_user_name", URLEncoder.encode("中文 可以吗", "UTF-8"));

        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString("http://www.baidu.com")
                .queryParams(paramMap);
        // UriComponents uri = uriBuilder.build().encode();
        UriComponents uri = uriBuilder.build();
        System.out.println(new URI(uri.toUriString()));
        System.out.println(uri.toUriString());
        System.out.println(uri.toString());

        System.out.println(URLEncoder.encode("中文 可以吗", "utf-8"));

//        RestTemplate template = new RestTemplate();
//        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
////        Map<String, Object> paramMap = new HashMap<String, Object>();
////        paramMap.put("para1", "001");
////        paramMap.put("para2", "002");
//
//        paramMap.add("p1", "001");
//        paramMap.add("p2", "002");
//
//        String url = "http://localhost:8088/param?p=1#2";
//        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
//        builder.queryParam("p", "开发 001");
//        URI uri = builder.build().encode().toUri();
//        // String uri = builder.toUriString();
//        HttpEntity<?> entity = new HttpEntity<>(null);
//        ResponseEntity<String> result = template.exchange(uri, HttpMethod.GET, entity, String.class);
//        System.out.println(result.getBody());

//        String text = "hello snappy";
//        byte[] compressed = Snappy.compress(text.getBytes("UTF-8"));
//        byte[] uncompressed = Snappy.uncompress(compressed);
//
//        String result = new String(uncompressed, "UTF-8");
//        System.out.println(result);
//
//        String libraryName = System.mapLibraryName("snappyjava");
//        System.out.println(libraryName);
//
//        String uuid = UUID.randomUUID().toString();
//        String extractedLibFileName = String.format("snappy-%s-%s-%s", "1.1.7", uuid, "libsnappyjava.so");
//        File extractedLibFile = new File("output", extractedLibFileName);
//
//        System.out.println(extractedLibFile.getAbsoluteFile());
//        FileOutputStream writer = new FileOutputStream(extractedLibFile);

//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                System.out.println("hello");
//            }
//        }, 100, 100);

//        System.out.println(ClassUtils.getPackageName(File.class));
//        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//
//        URL resource = classLoader.getResource("com.pain.flame.punk.bean".replace(".", "/"));
//
//        System.out.println("resource: " + resource);
//
//        URL url = classLoader.getResource("org.xerial.snappy".replace(".", "/"));
//
//        String host = url.getHost();
//        String authority = url.getAuthority();
//        Object content = url.getContent();
//        int defaultPort = url.getDefaultPort();
//        String file = url.getFile();
//        String path = url.getPath();
//        int port = url.getPort();
//        String protocol = url.getProtocol();
//        String query = url.getQuery();
//        String ref = url.getRef();
//        String userInfo = url.getUserInfo();

        // file:/Users/meilb/.gradle/caches/modules-2/files-2.1/org.xerial.snappy/snappy-java/1.1.7.3/241bb74a1eb37d68a4e324a4bc3865427de0a62d/snappy-java-1.1.7.3.jar
        // !/org/xerial/snappy
//        System.out.println(String.format("host: %s\nauthority: %s\ncontent: %s\ndefaultPort: %d\n" +
//                "file: %s\npath: %s\nport: %d\nprotocol: %s\nquery: %s\nref: %s\nuserInfo: %s",
//                host, authority, content, defaultPort, file, path, port, protocol, query, ref, userInfo));
//
//
//        double a = 123.34e13d;
//        double b = 123.e12;
//
//        System.out.println(a);
//        System.out.println(b);
    }
}
