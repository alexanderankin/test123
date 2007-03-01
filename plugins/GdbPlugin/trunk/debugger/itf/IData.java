package debugger.itf;

import java.util.Vector;

public interface IData {
	String getName();
	String getValue();
	Vector<IData> getChildren();
}
