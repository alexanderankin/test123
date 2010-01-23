/**
 * 
 */
package launcher;

import java.awt.Desktop;

import org.gjt.sp.jedit.OperatingSystem;

public class JavaDesktopHelper {

	private static boolean desktopAPIAvailable =
		OperatingSystem.hasJava16() &&
		isClassAvailable("java.awt.Desktop") &&
		Desktop.isDesktopSupported();
	private static Desktop desktop = desktopAPIAvailable ? Desktop.getDesktop() : null;
	
	public static boolean isDesktopAPIAvailable() {
		return desktopAPIAvailable;
	}
	
	public static Desktop getDesktop() {
		return desktop;
	}
	
	public static boolean isClassAvailable(String classname) {
		try {
			Class.forName (classname, false, JavaDesktopHelper.class.getClassLoader());
			return true;
		} catch (Throwable t) {
			return false;
		}
	}
}