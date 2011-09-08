/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2011 Matthieu Casanova
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package com.kpouer.jedit.remotecontrol.welcome;

import com.kpouer.jedit.remotecontrol.MessageHandler;
import com.kpouer.jedit.remotecontrol.RemoteClient;
import com.kpouer.jedit.remotecontrol.RemoteControlPlugin;
import com.kpouer.jedit.remotecontrol.RemoteServer;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * @author Matthieu Casanova
 */
public class Challenge implements MessageHandler, WelcomeService
{
	private static final String CHALLENGE = "jEdit-RemoteServer-Welcome-Challenge-";
	private static final String CHALLENGE_ANSWER = "jEdit-RemoteServer-Challenge-Answer-";
	private RemoteClient client;
	private SocketChannel sChannel;
	private String challenge;

	@Override
	public void setClient(RemoteClient client)
	{
		this.client = client;
	}

	@Override
	public void setChannel(SocketChannel sChannel)
	{
		this.sChannel = sChannel;
	}

	@Override
	public boolean accept(String line)
	{
		return true;
	}

	@Override
	public void handleMessage(String line)
	{
		if (line.equals(HELLO))
		{
			handleHello();
		}
		else if (line.startsWith(CHALLENGE_ANSWER))
		{
			handleChallengeAnswer(line);
		}
		else
		{
			Log.log(Log.WARNING, this, "Wrong Handshake " + line);
		}
	}

	private void handleChallengeAnswer(String line)
	{
		Log.log(Log.MESSAGE, this, "Challenge answer received " + line);
		String hash = line.substring(CHALLENGE_ANSWER.length());
		String localHash = getHash();
		if (localHash.equals(hash))
		{
			Log.log(Log.MESSAGE, this, "Challenge accepted");
			client.handshaked();
			try
			{
				sChannel.write(ByteBuffer.wrap(WelcomeService.HANDSHAKE_WELCOME));
			}
			catch (IOException e)
			{
				Log.log(Log.WARNING, this, e, e);
			}
		}
		else
		{
			Log.log(Log.WARNING, this, "Wrong challenge");
			RemoteControlPlugin.server.removeClient(sChannel);
		}
	}

	private void handleHello()
	{
		Log.log(Log.MESSAGE, this, "Hello received");
		try
		{
			long time = System.currentTimeMillis();
			challenge = CHALLENGE + time + "\n";
			sChannel.write(ByteBuffer.wrap(challenge.getBytes(RemoteServer.CHARSET)));
		}
		catch (IOException e)
		{
			Log.log(Log.WARNING, this, e, e);
		}
	}

	private String getHash()
	{
		String pincode = jEdit.getProperty("remotecontrol.pincode", "1234");
		try
		{
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] digest1 = digest.digest((pincode + challenge).getBytes(RemoteServer.CHARSET));
			return toHexString(digest1);
		}
		catch (NoSuchAlgorithmException e)
		{
			Log.log(Log.ERROR, this, e, e);
		}
		return "";
	}

	private static final String HEX_DIGITS = "0123456789abcdef";

	private static String toHexString(byte[] v)
	{
		StringBuilder sb = new StringBuilder(v.length * 2);
		for (int i = 0; i < v.length; i++)
		{
			int b = v[i] & 0xFF;
			sb.append(HEX_DIGITS.charAt(b >>> 4))
					.append(HEX_DIGITS.charAt(b & 0xF));
		}
		return sb.toString();
	}
}
