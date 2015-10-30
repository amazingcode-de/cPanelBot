/**
 * @author http://www.amazingcode.de
 * @version 1.2.1
 * created on 2015-10-30
 */

package de.amazingcode;

import java.util.logging.Logger;

public class Workflow {
	private final static Logger LOGGER = Logger.getLogger(Bot.class.getName());
	/**
	 * with protocol, port and trailing slash
	 * example: http://x3demoa.cpx3demo.com:2082/
	 */
	private static final String host = "";
	private static final String username = "";
	private static final String password = "";
	
	private static final String dbName = "";
	private static final String dbUser = "";
	private static final String dbPassword = "";
	
	private static final String useragent = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:41.0) Gecko/20100101 Firefox/41.0";

	public static void main(String args[]) throws Exception {
		Bot bot = new Bot(host, username, password, useragent);
		
		try {
			bot.login();
		} catch(Exception e) {
			LOGGER.severe(e.getMessage());
			System.exit(-1); //terminate, since no further actions allowed without login
		}
		
		try {
			bot.createDatabase(dbName);
		} catch(Exception e) {
			LOGGER.severe(e.getMessage());
		}
		
		try {
			bot.createDatabaseUser(dbUser, dbPassword);
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
		
		try {
			bot.addUserToDatabase(username+"_"+dbUser, username+"_"+dbName);
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
}