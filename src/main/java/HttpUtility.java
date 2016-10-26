import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import com.sun.xml.internal.bind.v2.runtime.NameList;

public class HttpUtility {
    public HttpClient setClientHeader( HashMap<String, String> headerMap ){
        // headers
        List<Header> headers = new ArrayList<Header>();
        for( String key : headerMap.keySet() ) {
            System.out.println("set http request header - " + key + ":" + headerMap.get(key));
            headers.add(new BasicHeader(key, headerMap.get(key)));
        }
        // create client
        HttpClient httpClient = HttpClientBuilder.create()
            .setDefaultHeaders(headers).build();
        
        return httpClient;
    }
    
    public HttpResponse postClient( HttpClient client, String url, String body) throws Exception {
        HttpResponse response = null;
        try {
            HttpPost method = new HttpPost(url);
            StringEntity entity = new StringEntity(body);
            method.setEntity(entity);
            System.out.println("post http request");
            response = client.execute(method);
            System.out.println("recieve http response");
        } catch(Exception ex) {
            throw ex;
        }
        return response;
    }
    
}
