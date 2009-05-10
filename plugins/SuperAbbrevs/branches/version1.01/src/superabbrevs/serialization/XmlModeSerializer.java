package superabbrevs.serialization;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.thoughtworks.xstream.XStream;

import superabbrevs.Persistence;
import superabbrevs.model.Abbreviation;
import superabbrevs.model.Mode;
import superabbrevs.model.ReplacementTypes;
import superabbrevs.model.SelectionReplacementTypes;
import superabbrevs.model.Variable;
import superabbrevs.utilities.Log;

public class XmlModeSerializer implements ModeSerializer {

	private XStream xstream;

	public XmlModeSerializer() {
		xstream = new XStream();
		xstream.setMode(XStream.NO_REFERENCES);
		
		xstream.alias("mode", Mode.class);
        xstream.alias("abbreviation", Abbreviation.class);
        xstream.alias("variable", Variable.class);
        xstream.alias("replacementTypes", ReplacementTypes.class);
	}

	public Mode deserialize(InputStream input) {
		return (Mode) xstream.fromXML(input);
	}

	public void serialize(OutputStream output, Mode mode) {
        xstream.toXML(mode, output); 
	}
}
