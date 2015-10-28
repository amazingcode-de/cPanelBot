/**
 * @author http://www.amazingcode.de
 * @version 1.0
 * created on 2015-10-28
 */

package de.amazingcode;

import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.protocol.HttpContext;

class EasySpecProvider implements CookieSpecProvider {
	@Override
	public CookieSpec create(HttpContext context) {
		return new EasyCookieSpec();
	}
}
