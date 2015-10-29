/**
 * @author http://www.amazingcode.de
 * @version 1.1
 * created on 2015-10-29
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

	public static void main(String args[]) throws Exception {
		Bot bot = new Bot(host, username, password);
		
		try {
			bot.login();
		} catch(Exception e) {
			LOGGER.severe(e.getMessage());
			System.exit(-1);
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
	}
}