package com.fooware.net.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Base64;

public class ProxyHTTP implements Proxy {
	private final String proxyHost;
	private final int proxyPort;
	private final String proxyUser;
	private final String proxyPass;

	public ProxyHTTP(String proxyHost, int proxyPort) {
		this(proxyHost, proxyPort, null, null);
	}
	
	public ProxyHTTP(String proxyHost, int proxyPort, String proxyUser, String proxyPass) {
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		this.proxyUser = proxyUser;
		this.proxyPass = proxyPass;
	}

	@Override
	public Socket openSocket(String hostName, int port) throws IOException {
		Socket socket = new Socket(proxyHost, proxyPort);
		socket.setTcpNoDelay(true);
		
		OutputStream out = socket.getOutputStream();
		InputStream in = socket.getInputStream();

		out.write(("CONNECT " + hostName + ":" + port + " HTTP/1.0\r\n").getBytes());
		if (proxyUser != null && proxyPass != null) {
			byte[] code = (proxyUser + ":" + proxyPass).getBytes();
			out.write("Proxy-Authorization: Basic ".getBytes());

			out.write(Base64.getEncoder().encode(code));
			out.write("\r\n".getBytes());
		}
		out.write("\r\n".getBytes());
		out.flush();
		
		// Its a magic code
		int foo = 0;

		StringBuilder sb = new StringBuilder();
		while (foo >= 0) {
			foo = in.read();
			if (foo != 13) {
				sb.append((char) foo);
				continue;
			}
			foo = in.read();
			if (foo != 10) {
				continue;
			}
			break;
		}
		if (foo < 0) {
			throw new IOException();
		}

		String response = sb.toString();
		String reason = "Unknow reason";
		int code = -1;
		try {
			foo = response.indexOf(' ');
			int bar = response.indexOf(' ', foo + 1);
			code = Integer.parseInt(response.substring(foo + 1, bar));
			reason = response.substring(bar + 1);
		} catch (NumberFormatException e) {
		}
		if (code != 200) {
			throw new IOException("proxy error: " + reason);
		}
		
		
		int count = 0;
		while (true) {
			count = 0;
			while (foo >= 0) {
				foo = in.read();
				if (foo != 13) {
					count++;
					continue;
				}
				foo = in.read();
				if (foo != 10) {
					continue;
				}
				break;
			}
			if (foo < 0) {
				throw new IOException();
			}
			if (count == 0)
				break;
		}
		
		return socket;
	}
}
