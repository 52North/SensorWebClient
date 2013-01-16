package org.n52.server.ses.feeder.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.n52.oxf.util.web.HttpClient;
import org.n52.oxf.util.web.HttpClientException;
import org.n52.oxf.util.web.ProxyAwareHttpClient;
import org.n52.oxf.util.web.SimpleHttpClient;

public class IOHelper {
    
    public static InputStream sendGetMessage(String serviceURL, String queryString) throws IOException {
        try {
            HttpClient httpClient = new ProxyAwareHttpClient(new SimpleHttpClient());
            HttpResponse response = httpClient.executeGet(serviceURL + "?" + queryString);
            HttpEntity responseEntity = response.getEntity();
            return responseEntity.getContent();
        } catch (HttpClientException e) {
            throw new IOException("Sending GET request failed.", e);
        }
    }

    public static InputStream sendPostMessage(String serviceURL, String request) throws IOException {
        try {
            HttpClient httpClient = new ProxyAwareHttpClient(new SimpleHttpClient());
            HttpResponse response = httpClient.executePost(serviceURL, request, ContentType.TEXT_XML);
            HttpEntity responseEntity = response.getEntity();
            return responseEntity.getContent();
        } catch (HttpClientException e) {
            throw new IOException("Sending POST request failed.", e);
        }
    }

}
