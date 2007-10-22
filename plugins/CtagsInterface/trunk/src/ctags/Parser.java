package ctags;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

import db.TagDB;

public class Parser {
	
	String tagFileDir;
	
	interface TagHandler {
		void processTag(Hashtable<String, String> info);
	}
	
	void parseTagFile(String tagFile, TagHandler handler) {
		if (tagFile == null || tagFile.length() == 0)
			return;
		tagFileDir = new File(tagFile).getAbsoluteFile().getParent();
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
				handler.processTag(info);
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
		// Find the end of the pattern (pattern may include "\t")
		int idx = line.lastIndexOf(";\"\t");
		if (idx < 0)
			return null;
		// Fixed fields (tag, file, pattern/line number)
		String fields[] = line.substring(0, idx).split("\t", 3);
		if (fields.length < 3)
			return null;
		info.put(TagDB.TAGS_NAME, fields[0]);
		String file = fields[1];
		if (new File(file).isAbsolute())
			info.put(TagDB.TAGS_FILE_ID, fields[1]);
		else
			info.put(TagDB.TAGS_FILE_ID, tagFileDir + "/" + fields[1]);
		info.put(TagDB.TAGS_PATTERN, fields[2]);
		// Extensions
		fields = line.substring(idx + 3).split("\t");
		for (int i = 0; i < fields.length; i++)
		{
			String pair[] = fields[i].split(":", 2);
			if (pair.length != 2)
				continue;
			info.put(TagDB.attr2col(pair[0]), pair[1]);
		}
		return info;
	}
}
