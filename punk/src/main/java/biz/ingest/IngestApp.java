package biz.ingest;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class IngestApp {

    private static AppInfo appInfo;
    private static RestService restService = new RestService();

    public static void main(String[] args) throws IOException {
        Map<String, AppInfo> appInfoMap = loadConfig();
        appInfo = appInfoMap.get("extdatadmconnector");

        // System.out.println(getToken());

        ingestProfile(appInfo);
        // deleteIdentity(appInfo);
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
