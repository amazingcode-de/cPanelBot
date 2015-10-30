/**
 * @author http://www.amazingcode.de
 * @version 1.2
 * created on 2015-10-30
 */

package de.amazingcode;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIUtils;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Bot {
	private final static Logger LOGGER = Logger.getLogger(Bot.class.getName());
	private CookieStore cookieStore;
	private RequestConfig requestConfig;
	private CloseableHttpClient httpclient;

	private final String host;
	private final String username;
	private final String password;
	private final String useragent;

	private String userPath;

	public Bot(String host, String username, String password, String useragent) {
		this.host = host;
		this.username = username;
		this.password = password;
		this.useragent = useragent;

		Registry<CookieSpecProvider> r = RegistryBuilder.<CookieSpecProvider> create()
				.register("easy", new EasySpecProvider()).build();

		cookieStore = new BasicCookieStore();

		requestConfig = RequestConfig.custom().setCookieSpec("easy").build();

		httpclient = HttpClients.custom().setUserAgent(this.useragent).setDefaultCookieStore(cookieStore)
				.setRedirectStrategy(new LaxRedirectStrategy()).setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
				.setDefaultCookieSpecRegistry(r).setDefaultRequestConfig(requestConfig).build();
	}

	public void login() throws Exception {
		// get initial cookies
		HttpGet httpGet = new HttpGet(this.host);
		CloseableHttpResponse response1 = httpclient.execute(httpGet);
		response1.close();

		// actual login
		HttpPost httpPost = new HttpPost(this.host + "login/");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("user", this.username));
		nvps.add(new BasicNameValuePair("pass", this.password));
		nvps.add(new BasicNameValuePair("login", ""));
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		httpPost.addHeader("Referer", this.host);

		HttpClientContext context = HttpClientContext.create();
		CloseableHttpResponse response2 = httpclient.execute(httpPost, context);

		try {
			if (response2.getStatusLine().getStatusCode() != 200) {
				throw new Exception("Login failed.");
			}

			// extract user path
			HttpHost target = context.getTargetHost();
			List<URI> redirectLocations = context.getRedirectLocations();
			String location = URIUtils.resolve(httpPost.getURI(), target, redirectLocations).toString();
			int start = location.indexOf("/", this.host.length() - 1) + 1;
			int end = location.lastIndexOf("/") + 1;
			this.userPath = location.substring(start, end);
			LOGGER.info("Extracted User Path " + this.userPath + " successfully.");
			LOGGER.info("Logged in as " + this.username + " successfully.");
		} finally {
			response2.close();
		}
	}

	public void createDatabase(String dbName) throws Exception {
		HttpPost httpPost = new HttpPost(this.host + this.userPath + "sql/addb.html");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("db", dbName));
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		httpPost.addHeader("Referer", this.host);

		CloseableHttpResponse response = httpclient.execute(httpPost);

		try {
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new Exception("Database Creation failed.");
			}
			HttpEntity entity = response.getEntity();
			String html = EntityUtils.toString(entity);
			if (html.indexOf("wurde hinzugefügt") < 0) {
				Document doc = Jsoup.parse(html);
				String errorMessage = doc.getElementById("addbErrorMsg").text();
				throw new Exception("Database Creation failed. (" + errorMessage + ")");
			}
			EntityUtils.consume(entity);
			LOGGER.info("Database " + dbName + " created successfully.");

		} finally {
			response.close();
		}
	}

	public void createDatabaseUser(String dbUser, String dbPassword) throws Exception {
		HttpPost httpPost = new HttpPost(this.host + this.userPath + "sql/adduser.html");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("pass", dbPassword));
		nvps.add(new BasicNameValuePair("pass2", dbPassword));
		nvps.add(new BasicNameValuePair("user", dbUser));
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		httpPost.addHeader("Referer", this.host);

		CloseableHttpResponse response = httpclient.execute(httpPost);

		try {
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new Exception("Database User Creation failed.");
			}
			HttpEntity entity = response.getEntity();
			String html = EntityUtils.toString(entity);
			if (html.indexOf("erfolgreich") < 0) {
				Document doc = Jsoup.parse(html);
				String errorMessage = doc.getElementById("adduserErrorMsg").text();
				throw new Exception("Database User Creation failed. (" + errorMessage + ")");
			}
			EntityUtils.consume(entity);
			LOGGER.info("Database User " + dbUser + " created successfully.");

		} finally {
			response.close();
		}
	}

	public void addUserToDatabase(String dbUser, String dbName) throws Exception {
		HttpPost httpPost = new HttpPost(this.host + this.userPath + "sql/userrights.html");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("user", dbUser));
		nvps.add(new BasicNameValuePair("db", dbName));
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		httpPost.addHeader("Referer", this.host);

		CloseableHttpResponse response = httpclient.execute(httpPost);

		try {
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new Exception("Add User " + dbUser + " to database " + dbName + " failed.");
			}
		} finally {
			response.close();
		}
		
		String[] privileges = {"ALTER", "ALTER ROUTINE", "CREATE", "CREATE ROUTINE", "CREATE TEMPORARY TABLES",
				"CREATE VIEW", "DELETE", "DROP", "EVENT", "EXECUTE", "INDEX", "INSERT", "LOCK TABLES",
				"REFERENCES", "SELECT", "SHOW VIEW", "TRIGGER", "UPDATE"};
		
		httpPost = new HttpPost(this.host + this.userPath + "sql/addusertodb.html");
		nvps.add(new BasicNameValuePair("ALL", "ALL"));
		nvps.add(new BasicNameValuePair("update", ""));
		for(String privilege: privileges) {
			nvps.add(new BasicNameValuePair("privileges", privilege));
		}	
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		httpPost.addHeader("Referer", this.host);

		response = httpclient.execute(httpPost);

		try {
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new Exception("Add User " + dbUser + " to database " + dbName + " failed.");
			}
			LOGGER.info("Added User " + dbUser + " to database " + dbName + " successfully.");
		} finally {
			response.close();
		}
	}

}
