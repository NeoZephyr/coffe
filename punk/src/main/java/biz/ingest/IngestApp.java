package biz.ingest;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class IngestApp {

    private static AppInfo appInfo;
    private static RestService restService = new RestService();

    public static void main(String[] args) throws IOException, InterruptedException {
        Map<String, AppInfo> appInfoMap = loadConfig();
        appInfo = appInfoMap.get("CloudNativeTest");

        // System.out.println(getToken());

        // 1654448815593228288 biden@qq.com


        ExecutorService executorService = Executors.newFixedThreadPool(10);


        Random random = new Random();

        for (int r = 1; r < 5; r++) {

            for (int i = 1; i < 1000; i++) {
                int counter;

                if (i % 2 == 0) {
                    counter = random.nextInt(5000);
                } else {
                    counter = i;
                }

                executorService.execute(() -> {
                    restService.post(appInfo.profileUrl, getProfile1(counter), appInfo);
                });
                executorService.execute(() -> {
                    restService.post(appInfo.profileUrl, getProfile2(6000 - counter), appInfo);
                });

                if (i % 50 == 0) {
                    executorService.execute(() -> {
                        restService.post(appInfo.profileUrl, getProfile3(counter), appInfo);
                    });
                    executorService.execute(() -> {
                        restService.post(appInfo.profileUrl, getProfile3(6000 - counter), appInfo);
                    });
                }
            }
        }

        executorService.awaitTermination(100, TimeUnit.MINUTES);

        // ingestProfile(appInfo);
        // ingestDocument(appInfo);
        // deleteIdentity(appInfo);
    }

    public static Map getProfile1(int i) {
        Map data = new HashMap();
        List<Map> identities = new ArrayList<>();

        identities.add(new HashMap<String, String>() {{
            put("type", "email");
            put("value", "electorn@qq.com");
        }});
        identities.add(new HashMap<String, String>() {{
            put("type", "mobile");
            put("value", String.format("133000%05d", i));
        }});
        data.put("identities", identities);
        data.put("data", new HashMap<String, String>() {{
            put("_name", "华伦斯坦");
        }});
        data.put("messageKey", UUID.randomUUID().toString());
        return data;
    }

    public static Map getProfile2(int i) {
        Map data = new HashMap();
        List<Map> identities = new ArrayList<>();

        identities.add(new HashMap<String, String>() {{
            put("type", "email");
            put("value", "proton@qq.com");
        }});

        identities.add(new HashMap<String, String>() {{
            put("type", "mobile");
            put("value", String.format("189000%05d", i));
        }});
        data.put("identities", identities);
        data.put("data", new HashMap<String, String>() {{
            put("_name", "古斯塔夫");
        }});
        data.put("messageKey", UUID.randomUUID().toString());
        return data;
    }

    public static Map getProfile3(int i) {
        Map data = new HashMap();
        List<Map> identities = new ArrayList<>();

        identities.add(new HashMap<String, String>() {{
            put("type", "mobile");
            put("value", String.format("133000%05d", i));
        }});

        identities.add(new HashMap<String, String>() {{
            put("type", "mobile");
            put("value", String.format("189000%05d", i));
        }});
        data.put("identities", identities);
        data.put("data", new HashMap<String, String>() {{
            put("_name", "第聂伯爵");
        }});
        data.put("messageKey", UUID.randomUUID().toString());
        return data;
    }

    public static String getToken() {
        Map res = restService.post(appInfo.getTokenUrl(), Collections.emptyMap(), appInfo);
        System.out.println(JsonUtil.objToStr(res));
        return (String) res.get("access_token");
    }

    public static void ingestProfile(AppInfo appInfo) throws IOException {
        List<Map> dataList = loadData("source/profile.json");

        for (Map profile : dataList) {
            restService.post(appInfo.profileUrl, profile, appInfo);
        }
    }

    public static void ingestDocument(AppInfo appInfo) throws IOException {
        List<Map> dataList = loadData("source/document.json");

        for (Map document : dataList) {
            restService.post(appInfo.orderUrl, document, appInfo);
        }
    }

    public static void deleteIdentity(AppInfo appInfo) throws IOException {
        List<Map> dataList = loadData("source/delete_profile.json");

        for (Map profile : dataList) {
            restService.post(appInfo.profileUrl, profile, appInfo);
        }
    }

    public static Map<String, AppInfo> loadConfig() throws IOException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("config.json");
        assert url != null;
        String configText = IOUtils.toString(url, StandardCharsets.UTF_8);
        return JsonUtil.strToObj(configText, Map.class, String.class, AppInfo.class);
    }

    public static List<Map> loadData(String location) throws IOException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(location);
        assert url != null;
        String configText = IOUtils.toString(url, StandardCharsets.UTF_8);
        return JsonUtil.strToObj(configText, List.class, Map.class);
    }
}
