import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.data.Config;
import com.data.ResponseData;
import com.data.Results;

public class Main {
    public static void main( String[] args ) {
        String fileName = "./config/param.json";
        if( args.length < 1 ) {
            System.out.println("CSVファイルが指定されていません。");
            System.out.println("第一引数にCSVファイルを指定してください");
        }
        String csvFileName = args[0];
        Common com = new Common();
        try {
            System.out.println("read parameter file");
            String body = com.readFileBody(fileName);
            Config conf = com.readConfig(body);

            System.out.println("create http request");
            String httpBody = com.createAzureBody(conf, csvFileName);
            HttpUtility http = new HttpUtility();
            HashMap<String, String> headerMap = new HashMap<String, String>(){{
                put("Content-Type","application/json");
                put("Authorization", "Bearer " + conf.api_key);
            }};
            HttpClient hClient = http.setClientHeader(headerMap);
            System.out.println("http request Body - " + httpBody);
            HttpResponse response = http.postClient(hClient, conf.url, httpBody);
            System.out.println("Status code - " + response.getStatusLine().getStatusCode());
            String data = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8); 
            ResponseData ret = com.parseResult(data);
            outputReslut(ret.Results);
        } catch( Exception ex ) {
            ex.printStackTrace();
        }
    }
    
    public static void outputReslut( Results ret ) {
        System.out.println("result ---------- ");
        System.out.println("-- type - " + ret.output1.type);
        ArrayList<String> colNames = ret.output1.value.ColumnNames;
        ArrayList<String> colTypes = ret.output1.value.ColumnTypes;
        ArrayList<String> headerList = new ArrayList<String>();
        for( int i = 0; i < colNames.size(); i++ ) {
            headerList.add(colNames.get(i) + "(" + colTypes.get(i) + ")");
        }
        System.out.println(String.join(",", headerList));
        for( ArrayList<String> valList : ret.output1.value.Values ) {
            System.out.println(String.join(",", valList));
        }
    }
}
