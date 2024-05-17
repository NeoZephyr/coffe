package biz.ingest;

import lombok.Data;

@Data
public class AppInfo {
    String tenantId;
    String projectId;
    String appId;
    String appSecret;

    String tokenUrl;
    String profileUrl;
    String customerUrl;
    String eventUrl;
    String orderUrl;
    String token;
}
