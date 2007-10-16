package ctags;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

import db.TagDB;

public class Parser {
	
	void parseTagFile(String tagFile, TagDB db) {
		if (tagFile == null || tagFile.length() == 0)
			return;
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(tagFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		try {
			String line;
			while ((line = in.readLine()) != null) {
				Hashtable<String, String> info = parse(line);
				if (info == null)
					continue;
				db.insertTag(info);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private Hashtable<String, String> parse(String line) {
		Hashtable<String, String> info =
			new Hashtable<String, String>();
		if (line.endsWith("\n") || line.endsWith("\r"))
			line = line.substring(0, line.length() - 1);
		String fields[] = line.split("\t");
		if (fields.length < 3)
			return null;
		info.put("name", fields[0]);
		info.put("file", fields[1]);
		info.put("pattern", fields[2]);
		// extensions
		for (int i = 3; i < fields.length; i++)
		{
			String pair[] = fields[i].split(":", 2);
			if (pair.length != 2)
				continue;
			info.put(pair[0], pair[1]);
		}
		return info;
	}

}
