/**
 * @author http://www.amazingcode.de
 * @version 1.0
 * created on 2015-10-27
 */

package de.amazingcode;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

public class Workflow {
	/**
	 * with protocol, port and trailing slash
	 * example: http://x3demoa.cpx3demo.com:2082/
	 */
	private static final String host = "http://x3demoa.cpx3demo.com:2082/";
	private static final String username = "x3demoa";
	private static final String password = "x3demoa";

	public static void main(String args[]) throws ClientProtocolException, IOException {
		Bot bot = new Bot(host, username, password);
		
		if(bot.login()) {
			System.out.println("Login successful!");
		}
		
		// use bot to automate some stuff in cPanel...
		bot.doStuff();
	}
}