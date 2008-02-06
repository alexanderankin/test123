package com.fooware.net.proxy;

import java.io.*;
import java.net.Socket;

public interface Proxy {
	
	public Socket openSocket(String hostName, int port) throws IOException;
	
}
