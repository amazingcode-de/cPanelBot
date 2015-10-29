/**
 * @author http://www.amazingcode.de
 * @version 1.0.1
 * created on 2015-10-28
 */

package de.amazingcode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class Bot {
	private CookieStore cookieStore;
	private RequestConfig requestConfig;
	private CloseableHttpClient httpclient;
	
	private final String host;
	private final String username;
	private final String password;
	
	private final String useragent;
	
	public Bot(String host, String username, String password) {
		this.host = host;
		this.username = username;
		this.password = password;
		
		this.useragent = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:41.0) Gecko/20100101 Firefox/41.0";
		
		Registry<CookieSpecProvider> r = RegistryBuilder.<CookieSpecProvider>create()
		        .register("easy", new EasySpecProvider())
		        .build();
		
		cookieStore = new BasicCookieStore();
		
		requestConfig = RequestConfig.custom()
				.setCookieSpec("easy")
				.build();

		httpclient = HttpClients.custom()
				.setUserAgent(this.useragent)
				.setDefaultCookieStore(cookieStore)
				.setRedirectStrategy(new LaxRedirectStrategy())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
				.setDefaultCookieSpecRegistry(r)
				.setDefaultRequestConfig(requestConfig)
				.build();
	}

	public boolean login() throws ClientProtocolException, IOException {
		// get initial cookies
		HttpGet httpGet = new HttpGet(this.host);
		CloseableHttpResponse response1 = httpclient.execute(httpGet);
		try {
			HttpEntity entity1 = response1.getEntity();
			EntityUtils.consume(entity1);
		} finally {
			response1.close();
		}
		
		// actual login
		HttpPost httpPost = new HttpPost(this.host + "login/");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("user", this.username));
		nvps.add(new BasicNameValuePair("pass", this.password));
		nvps.add(new BasicNameValuePair("login", ""));
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		httpPost.addHeader("Referer", this.host);

		CloseableHttpResponse response2 = httpclient.execute(httpPost);
	
		try {
			int statusCode = response2.getStatusLine().getStatusCode();
			HttpEntity entity2 = response2.getEntity();
			EntityUtils.consume(entity2);
			return (statusCode == 200);
		} finally {
			response2.close();
		}
	}
	
	//TODO: implement whatever you want to achieve
	public void doStuff() {
		System.out.println("NOT YET IMPLEMENTED.");
	}

}
