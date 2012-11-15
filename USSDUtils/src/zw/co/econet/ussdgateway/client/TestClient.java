/*
 * To change this template, choose Tools | Templates * and open the template in the editor.
 */
package zw.co.econet.ussdgateway.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author oswin
 */
public class TestClient {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://localhost:9080/mCommerce/ussd");

            HttpEntity requestBody = new StringEntity("<?xml version=\"1.0\"?>"
                    + "<messageRequest xmlns=\"http://econet.co.zw/intergration/messagingSchema\">"
                    + "<transactionTime>2009-05-26T17:43:34.000+02:00</transactionTime>"
                    + "<transactionID>88888888888814</transactionID>"
                    + "<sourceNumber>263773303584</sourceNumber>"
                    + " <destinationNumber>440</destinationNumber>"
                    + " <message>1</message>"
                    + " <stage>FIRST</stage>"
                    + " <channel>USSD</channel>"
                    + "</messageRequest>", "UTF-8");

     
            post.setEntity(requestBody);
            HttpResponse response = client.execute(post);

            System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (ClientProtocolException ex) {
            Logger.getLogger(TestClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TestClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
