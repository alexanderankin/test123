package com.fooware.net.proxy;

import java.io.*;
import java.net.Socket;

/**
 * Basic interface for sokect factories aka Proxy
 * @author Vadim Voituk
 * @see ProxyHTTP
 * 
 * TODO: Implement ProxyNone (ProxyDirect) null-object
 * TODO: Implement ProxySocks class 
 */
public interface Proxy {
	
	/**
	 * Opens socket to specified host and port 
	 */
	public Socket openSocket(String hostName, int port) throws IOException;
	
}
