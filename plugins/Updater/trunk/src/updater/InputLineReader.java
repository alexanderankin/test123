package updater;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class InputLineReader extends InputStreamReader
{

	public InputLineReader(InputStream in)
	{
		super(in);
	}

	public String readLine()
	{
		StringBuilder sb = new StringBuilder();
		int i;
		try
		{
			while ((i = read()) != -1)
			{
				if (i == '\n')
					break;
				sb.append((char) i);
			}
		}
		catch (IOException e)
		{
			return null;
		}
		return (i == -1) ? null : sb.toString();
	}

}
