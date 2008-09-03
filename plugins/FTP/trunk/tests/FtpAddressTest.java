package tests;

import junit.framework.TestCase;
import ftp.FtpAddress;

public class FtpAddressTest extends TestCase {
	
	public void testSFtpFull()  {
		FtpAddress addr = new FtpAddress("sftp://user@hell:pwd@voituk.kiev.ua:22/home/www@hello.com/111.html");
		assertEquals("sftp", addr.getScheme());
		assertEquals("voituk.kiev.ua", addr.getHost());
		assertEquals(22, addr.getPort());
		assertEquals("/home/www@hello.com/111.html", addr.getPath());
		assertEquals("user@hell", addr.getUser());
		assertEquals("pwd", addr.getPassword());
	}
	
	public void testFtpFullWithoutPort() {
		FtpAddress addr = new FtpAddress("ftp://user@domain@voituk.kiev.ua/home/www/ ");
		assertEquals("ftp", addr.getScheme());
		assertEquals("voituk.kiev.ua", addr.getHost());
		assertEquals(21, addr.getPort());
		assertEquals("/home/www/", addr.getPath());
		assertEquals("user@domain", addr.getUser());
		assertEquals(null, addr.getPassword());

	}
	
	public void testFtpShort() {
		FtpAddress addr = new FtpAddress("ftp://voituk.kiev.ua");
		assertEquals("ftp", addr.getScheme());
		assertEquals("voituk.kiev.ua", addr.getHost());
		assertEquals(21, addr.getPort());
		assertEquals(null, addr.getPath());
		assertEquals(null, addr.getUser());
		assertEquals(null, addr.getPassword());

	}
	
	public void testFtpShortPort() {
		FtpAddress addr = new FtpAddress("ftp://voituk.kiev.ua:2121/");
		assertEquals("ftp", addr.getScheme());
		assertEquals("voituk.kiev.ua", addr.getHost());
		assertEquals(2121, addr.getPort());
		assertEquals("/", addr.getPath());
		assertEquals(null, addr.getUser());
		assertEquals(null, addr.getPassword());
	}
	//sftp://user@hell:pwd@voituk.kiev.ua:22/~www/111.html
}
