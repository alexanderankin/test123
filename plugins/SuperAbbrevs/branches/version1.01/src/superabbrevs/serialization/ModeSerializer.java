package superabbrevs.serialization;

import java.io.InputStream;
import java.io.OutputStream;

import superabbrevs.model.Mode;

public interface ModeSerializer {

	void serialize(OutputStream output, Mode mode);
	Mode deserialize(InputStream input);
}
