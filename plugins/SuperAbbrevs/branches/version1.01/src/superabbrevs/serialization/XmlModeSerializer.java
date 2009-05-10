package superabbrevs.serialization;

import java.io.InputStream;
import java.io.OutputStream;

import superabbrevs.model.Mode;

public class XmlModeSerializer implements ModeSerializer {

	public XmlModeSerializer() {
	}
	
	public void serialize(Mode mode) {
	}

	public Mode deserialize(String modeName) {
		return null;
	}

	public Mode deserialize(InputStream input, String modeName) {
		return null;
	}

	public void serialize(OutputStream output, Mode mode) {

	}
}
