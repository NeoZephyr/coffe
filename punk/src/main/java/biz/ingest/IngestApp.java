package biz.ingest;

import foundation.lab.concurrent.CompletableFutureTest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
public class IngestApp {

    private static AppInfo appInfo;
    private static RestService restService = new RestService();

    public static void main(String[] args) throws IOException, InterruptedException {
        Map<String, AppInfo> appInfoMap = loadConfig();
        appInfo = appInfoMap.get("test_not_deltalake");
        // System.out.println(getToken());

        // concurrentMigration();
        // concurrentMerge();
        // ingestProfile(appInfo, 0, 1);

        long startTime = System.currentTimeMillis();
        Thread[] threads = new Thread[5];

        for (int i = 0; i < 5; i++) {
            int ii = i;
            threads[i] = new Thread(() -> {
                try {
                    ingestProfile(appInfo, ii * 200000, (ii + 1) * 200000);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            threads[i].start();
        }

        for (int i = 0; i < 5; i++) {
            threads[i].join();
        }

        log.info("===================== end");

        long cost = System.currentTimeMillis() - startTime;
        log.info("cost {}ms, {}s", cost, cost / 1000);

        // ingestCustomerEvent(appInfo);

        // restService.post("https://master-api.dmhub.cn/v2/customerService/findCustomerByIdentity?identityType=cp_datahubconnector_uid&identityValue=1778367678759569408", appInfo);
        // createList();

        // ingestCustomer(appInfo);
        // ingestDocument(appInfo);
        // deleteIdentity(appInfo);

        // ingestProfile(appInfo);
        // ingestEvent(appInfo);

        // long startTime = System.currentTimeMillis();
        // benchmark1();
//        benchmark2();
//        long cost = System.currentTimeMillis() - startTime;
//        log.info("cost {}ms, {}s", cost, cost / 1000);
    }

    static ThreadPoolExecutor executor = new ThreadPoolExecutor(
            5, 5, 0L,
            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

    public static void benchmark1() throws IOException {
        log.info("===== start");

        for (int i = 0; i < 10000; i++) {
            ingestProfile(appInfo, i, i + 1);
        }

        log.info("===== end");
    }

    public static void benchmark2() throws IOException, InterruptedException {
        log.info("===== start");

        List<Call> calls = new ArrayList<>(10);

        for (int i = 0; i < 10; i++) {
            calls.add(new Call());
        }

        for (int i = 0; i < 1000; i++) {
            Call call = null;
            int k = 0;

            while (true) {
                for (int j = 0; j < calls.size(); j++) {
                    if (calls.get(j).future.isDone()) {
                        call = calls.get(j);
                        break;
                    }
                }

                if (call != null) {
                    break;
                }

                k++;
                Thread.sleep(1);

                if (k > 100) {
                    log.error("wait too many times");
                }
            }

            CompletableFuture<Result> future = concurrentTask(i * 10);
            call.free = false;
            call.future = future;
        }

        List<CompletableFuture<Result>> futures = new ArrayList<>();

        for (int j = 0; j < calls.size(); j++) {
            if (!calls.get(j).free) {
                futures.add(calls.get(j).future);
            }
        }

        if (!futures.isEmpty()) {
            CompletableFuture<Void> f = CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[0]));
            try {
                f.join();
            } catch (Exception e) {
                log.info("------ fuck");
            }
        }

        executor.getQueue().size();

        log.info("===== end");
    }

    public static CompletableFuture<Result> concurrentTask(int i) {

        CompletableFuture<Result> future = CompletableFuture.supplyAsync(() -> {
            try {
                ingestProfile(appInfo, i, i + 10);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return new Result(true, null);
        }, executor);

        return future;
    }

    @Data
    static class Call {
        CompletableFuture<Result> future;
        boolean free = true;
    }

    @Data
    static class Result {
        boolean success;
        String data;

        public Result(boolean success, String data) {
            this.success = success;
            this.data = data;
        }
    }

    public static void concurrentMigration() {
        for (int i = 0; i < 10000; i += 2) {
            int m1 = i;
            int m2 = i + 1;
            String e = "USB - " + i;

            Map data1 = getProfile(m1, e);
            Map data2 = getMobileProfile(m2);
            Map data3 = getProfile(m2, e);

            restService.post(appInfo.profileUrl, data1, appInfo);
            restService.post(appInfo.profileUrl, data2, appInfo);
            restService.post(appInfo.profileUrl, data3, appInfo);
        }
    }

    public static void concurrentMerge() {
        for (int i = 0; i < 10000; i++) {
            int m = i;
            String e = "WTF - " + i;

            Map data1 = getMobileProfile(m);
            Map data2 = getEmailProfile(e);
            Map data3 = getProfile(m, e);

            restService.post(appInfo.profileUrl, data1, appInfo);
            restService.post(appInfo.profileUrl, data2, appInfo);
            restService.post(appInfo.profileUrl, data3, appInfo);
        }
    }

    public static void createList() throws IOException {
        Map data = load("source/filter.json");
        data.put("name", "伟大的领袖");
        restService.post("https://master-api.dmhub.cn/v2/lists", data, appInfo);
    }

    public static Map getMobileProfile(int m) {
        return getProfile(m, null);
    }

    public static Map getEmailProfile(String e) {
        return getProfile(null, e);
    }

    public static Map getProfile(Integer m, String e) {
        Map data = new HashMap();
        List<Map> identities = new ArrayList<>();

        if (m != null) {
            identities.add(new HashMap<String, String>() {{
                put("type", "mobile");
                put("value", String.format("161000%05d", m));
            }});
        }

        if (StringUtils.isNotBlank(e)) {
            identities.add(new HashMap<String, String>() {{
                put("type", "email");
                put("value", String.format("bet-pet-%s@qq.com", e));
            }});
        }

        data.put("identities", identities);
        data.put("data", new HashMap<String, String>() {{
            put("_name", String.format("大魔头-斯大林 - %d", m));
        }});
        data.put("messageKey", UUID.randomUUID().toString());
        return data;
    }

    public static String getToken() {
        Map res = restService.post(appInfo.getTokenUrl(), Collections.emptyMap(), appInfo);
        System.out.println(JsonUtil.objToStr(res));
        return (String) res.get("access_token");
    }

    public static void ingestProfile(AppInfo appInfo, int start, int end) throws IOException {
        for (int i = start; i < end; i++) {
            Map data = new HashMap();
            List<Map> identities = new ArrayList<>();
            int ii = i;

            for (int j = 0; j < 5; j++) {
                int finalJ = j;
                identities.add(new HashMap<String, String>() {{
                    put("type", "email");
                    put("value", String.format("omi-%d-qi-%s@qq.com", finalJ, ii));
                }});
            }

            data.put("identities", identities);
            data.put("data", new HashMap<String, String>() {{
                put("_name", String.format("欧姆与姆欧 %d", ii));
            }});
            data.put("messageKey", UUID.randomUUID().toString());

            restService.post(appInfo.profileUrl, data, appInfo);

            log.info("=== count: {}", ii - start);
        }
    }

    public static void ingestProfile(AppInfo appInfo) throws IOException {
        List<Map> dataList = loadData("source/profile.json");

        for (Map profile : dataList) {
            restService.post(appInfo.profileUrl, profile, appInfo);
        }
    }

    public static void ingestCustomerFromFile(AppInfo appInfo) throws IOException {
        List<Map> dataList = loadData("source/customer.json");

        for (Map profile : dataList) {
            restService.post(appInfo.customerUrl, profile, appInfo);
        }
    }

    public static void ingestCustomerEvent(AppInfo appInfo) {
        Map data = new HashMap();
        data.put("identityType", "cp_datahubconnector_uid");
        data.put("identityValue", "abcde");
        data.put("event", "c_gao_po_huai");
        data.put("data", new Date());
        data.put("source", "人民大会堂");
        data.put("c_break_what", "搞点破坏");

        restService.post("https://master-api.dmhub.cn/v2/customerEvents", data, appInfo);
    }

    public static void ingestCustomer(AppInfo appInfo, int start, int end) throws IOException, InterruptedException {
        for (int i = start; i < end; i++) {
            Map data = new HashMap();
            String name = String.format("洛浦同志 %06d", i);
            data.put("customer", new HashMap<String, String>() {{
                put("name", name);
            }});
//            data.put("identity", new HashMap<String, String>() {{
//                put("type", "cp_datahubconnector_uid");
//                put("value", "abcde");
//                put("name", "uuuuu");
//            }});
            restService.post(appInfo.customerUrl, data, appInfo);
            // break;
        }
    }

    public static void ingestDocument(AppInfo appInfo) throws IOException {
        List<Map> dataList = loadData("source/document.json");

        for (Map document : dataList) {
            restService.post(appInfo.orderUrl, document, appInfo);
        }
    }

    public static void ingestEvent(AppInfo appInfo) throws IOException {
        List<Map> dataList = loadData("source/event.json");

        for (Map event : dataList) {
            restService.post(appInfo.eventUrl, event, appInfo);
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

    public static Map load(String location) throws IOException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(location);
        assert url != null;
        String text = IOUtils.toString(url, StandardCharsets.UTF_8);
        return JsonUtil.strToObj(text, Map.class);
    }
}
