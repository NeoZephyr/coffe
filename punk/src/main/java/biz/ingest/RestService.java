package biz.ingest;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

@Slf4j
public class RestService {

    private volatile RestTemplate restTemplate;

    public RestService() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        restTemplate = builder.build();
    }

    public Map post(String url, Map data, AppInfo appInfo) {
        HttpEntity entity = null;

        if (MapUtils.isEmpty(data)) {
            entity = new HttpEntity(getHttpHeaders(appInfo));
        } else {
            String reqText = JsonUtil.objToStr(data);
            entity = new HttpEntity(reqText, getHttpHeaders(appInfo));
        }

        ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        log.info("=== response: {}", res.getBody());

        return JsonUtil.strToObj(res.getBody(), Map.class);
    }

    HttpHeaders getHttpHeaders(AppInfo appInfo) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        headers.set(HeaderKeys.X_TENANT_ID, appInfo.tenantId);
        headers.set(HeaderKeys.X_WORKSPACE_ID, appInfo.projectId);
        headers.set(HeaderKeys.STD_X_TENANT_ID, appInfo.tenantId);
        headers.set(HeaderKeys.STD_X_WORKSPACE_ID, appInfo.projectId);

        if (StringUtils.isBlank(appInfo.getToken())) {
            String code = Base64.getEncoder().encodeToString(String.format("%s:%s", appInfo.getAppId(), appInfo.getAppSecret()).getBytes(StandardCharsets.UTF_8));
            headers.set("grant_type", "client_credentials");
            headers.set(HttpHeaders.AUTHORIZATION, String.format("Basic %s", code));
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        } else {
            headers.set(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", appInfo.token));
            headers.setContentType(MediaType.APPLICATION_JSON);
        }

        return headers;
    }
}
