package javadoc;
/**
 * @author Damien Radtke
 * class ApiAction
 * TODO: comment
 */
//{{{ Imports
import infoviewer.InfoViewerPlugin;
import java.io.File;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.View;
//}}}
public class ApiAction extends EditAction {
	private String name;
	private String uri;
	
	public static ApiAction create(String url) throws IllegalArgumentException {
		String name = JavadocPlugin.getApiName(url);
		if (name == null) {
			throw new IllegalArgumentException(
				"\""+url+"\" is not a valid api root.");
		}
		String action = actionName(name);
		return new ApiAction(url, name, action);
	}
	
	public static String actionName(String name) {
		return "javadoc."+name.toLowerCase().replace(" ", "-");
	}
	
	protected ApiAction(String url, String name, String action) {
		super(action);
		this.name = name;
		this.uri = new File(url, "index.html").toURI().toString();
	}
	
	public void invoke(View view) {
		InfoViewerPlugin.openURL(view, uri);
	}
	
	public String getLabel() {
		return name;
	}
	
	public boolean noRecord() {
		return true;
	}
	
	public boolean noRememberLast() {
		return true;
	}
	
	public boolean noRepeat() {
		return true;
	}
}
