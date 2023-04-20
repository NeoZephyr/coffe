package net.http;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;

public class HttpTest {
    public static void main(String[] args) {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("name", Collections.singletonList("jack"));
        params.put("c", Collections.singletonList("123%20ee"));
        // String url = "https://master-data.dmhub.cn/data/gdm/dataTables?expandPattern=true&dataZone[eq]=DW&pattern[in]=PrimaryProfile,GeneralProfile&q=a b";
        String url = "https://master-app.dmhub.cn/dynamic/extdatadmconnector/data/gdm/dataTables?dataZone=DW&limit=999&pattern[in]=https%3A%2F%2Fdata.convertlab.com%2Fgdm%2F_global%2FPattern%2FSecondaryProfile&expand=definition&expandPattern=true&q=a +  b";
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(url).queryParams(params).build();
        String uri = uriComponents.toUriString();
        System.out.println(uri);
    }
}
