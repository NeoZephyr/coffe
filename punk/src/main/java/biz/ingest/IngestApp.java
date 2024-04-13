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
        // Map<String, AppInfo> appInfoMap = loadConfig();
        // appInfo = appInfoMap.get("test_not_deltalake");
        // System.out.println(getToken());

        // ingestProfile(appInfo);
        // ingestDocument(appInfo);
        // deleteIdentity(appInfo);

        Map message = new HashMap();
        Map merge = new HashMap();
        Map states = new HashMap();
        Map extra = new HashMap();
        merge.put("1749336064234885120", new ArrayList<>());
        states.put("1749336064234885120", "k");
        states.put("1749336058690015232", "k");
        extra.put("key", "1749336064234885120");
        extra.put("mergeId", "data-t1p1-merge:cache:dw_bad_guy:1749336064234885120");
        message.put("projectId", "test_not_deltalake");
        message.put("tableId", "dw_bad_guy");
        message.put("uid", "1749336058690015232");
        message.put("merge", merge);
        message.put("stateChanged", false);
        message.put("states", states);
        message.put("action", "compensate");
        message.put("extra", extra);
        System.out.println(JsonUtil.objToStr(message));
    }

    public static Map getProfile1(int i) {
        Map data = new HashMap();
        List<Map> identities = new ArrayList<>();
        identities.add(new HashMap<String, String>() {{
            put("type", "mobile");
            put("value", String.format("155000%05d", i));
        }});
        data.put("identities", identities);
        data.put("data", new HashMap<String, String>() {{
            put("_name", "在大风大浪中奋勇前进");
        }});
        data.put("messageKey", UUID.randomUUID().toString());
        return data;
    }

    public static Map getProfile2(int i) {
        Map data = new HashMap();
        List<Map> identities = new ArrayList<>();
        identities.add(new HashMap<String, String>() {{
            put("type", "email");
            put("value", String.format("corning-%d@qq.com", i));
        }});

        data.put("identities", identities);
        data.put("data", new HashMap<String, String>() {{
            put("_name", "在大风大浪中奋勇前进");
        }});
        data.put("messageKey", UUID.randomUUID().toString());
        return data;
    }

    public static Map getProfile3(int i) {
        Map data = new HashMap();
        List<Map> identities = new ArrayList<>();
        identities.add(new HashMap<String, String>() {{
            put("type", "mobile");
            put("value", String.format("155000%05d", i));
        }});
        identities.add(new HashMap<String, String>() {{
            put("type", "email");
            put("value", String.format("corning-%d@qq.com", i));
        }});
        data.put("identities", identities);
        data.put("data", new HashMap<String, String>() {{
            put("_name", "在大风大浪中奋勇前进");
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
