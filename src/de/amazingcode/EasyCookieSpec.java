/**
 * @author http://www.amazingcode.de
 * @version 1.0
 * created on 2015-10-28
 */

package de.amazingcode;

import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.cookie.DefaultCookieSpec;

class EasyCookieSpec extends DefaultCookieSpec {
	@Override
	public void validate(Cookie arg0, CookieOrigin arg1) throws MalformedCookieException {
		//allow all cookies	
	}
}
