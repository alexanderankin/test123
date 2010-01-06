package cppcheck;

import cppcheck.Runner.LineHandler;

public class OutputHandler implements LineHandler
{

	public void handle(String line)
	{
		System.err.println(line);
	}

}
