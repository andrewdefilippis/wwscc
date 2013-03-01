package org.wwscc.storage;

import static org.junit.Assert.*;
import java.math.BigInteger;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.auth.AuthenticationException;
import org.junit.Test;
import org.junit.Before;

public class SRPTests {

	SRPAuthentication totest;
	String username = "ww2013:series";
	String password = "mypassword";
	BigInteger N, g, k, a, A, salt, B, M1, M2, K;
	
	@Before
	public void setup()
	{
		// generated by srppeer.py
		Base64 b64 = new Base64();
		N = new BigInteger(1, b64.decode("7q8Kua2zjdacM/gK+o/F6GByYYd1/zwLnqIxTJwlZXbWdN90luqB0zg7SBPWksbg4NXY4lC5i+SOSVwdYIna0V3H17RhVNa2zo70rWmxXUmCVZspe88YhcUp9WZmDlfsaO28PAVybMAv1Mv0l26qmv1ROP6DdkNbn8YdL8DrBuM="));
		g = new BigInteger(1, b64.decode("Ag=="));
		k = new BigInteger(1, b64.decode("/k5+VIdhcY7vPz63NFSRbdRwD4E="));
		a = new BigInteger(1, b64.decode("u31grVh63RZPaWLu3NP3qrcPsHOdHV476kitTc1h0yE="));
		A = new BigInteger(1, b64.decode("PHSLzyDWG3JikSZnqySESoPnhR0WckRCVNeESWGMXCMA98YYYrAIYOd6utRldOYRqzSoMp6EMLBJPtsPCXWKJMkCX5ot9/jCz+b/E/ZDc+/uoi2SVNuj1vpd5nYYI93Rl+1GZg/B3Gfyd4vchaWBF9VG/k8r2MROxyNjDmtT9bs="));
		salt = new BigInteger(1, b64.decode("vmyBVA=="));
		B = new BigInteger(1, b64.decode("JQGu3GrFB5xjDByhUde+th/rJsaxhs99W5Yc+uqhpqbEiX/FBujws0OC2xovZ+6yAuWPXwOm9dsBTz+E7cfIo5sJKlN02C+REitGqoOnjR+oLXpKWcpr9mJugsgbY+VM6pdu2TzIy+J5AQ5DqHO3p9/5BrkCZRL8KGRnqR1t6lg="));
		M1 = new BigInteger(1, b64.decode("nKMN8uxslnYyojJMaec/iXdvTxI="));
		M2 = new BigInteger(1, b64.decode("v5y/gw4ka75awNuS9/aMqDLp0UA="));
		K = new BigInteger(1, b64.decode("hbxNOZRru+Z2nnOl0rNxezhWTy4="));
	}
		
	@Test
	public void testClient() throws AuthenticationException 
	{
		totest = new SRPAuthentication("", username, password);
		assertEquals(SRPAuthentication.N, N);
		assertEquals(SRPAuthentication.g, g);
		assertEquals(SRPAuthentication.k, k);
		//totest.start();
		totest.a = a;
		totest.A = A;  // set our random to what we want
		totest.server1(salt, B);
		assertEquals(totest.M1, M1);
		totest.server2(M2);
		assertEquals(totest.K, K);
	}

}
