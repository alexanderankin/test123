NATIVE BROWSER PLUGIN

This plugin provides a dockable browser provided by the native, SWT-based, Swing libraries from the DJ Project (http://djproject.sourceforge.net/). It is not meant to replace a full browser but it does offer the advantage of a richer browsing supporting javascript, flash, and easy rendering of the current buffer in one-click. In fact, the main motivation for this plugin is to easily test HTML/js/flash content without leaving jEdit.
Because this plugin uses native SWT libraries which perform callback to the java application, some jars need to be installed as part of jEdit's system classpath (the one used by the system class loader). As a result this plugin requires some manual installation steps as detailed below.

Manual Installation Steps!!!

NativeBrowser requires the following jars to be loaded by the system class loader. In other words these jars need to be put in jEdit's classpath:
- DJNativeSwing.jar and DJNativeSwing-SWT.jar: These are part of DJ Native Swing (http://djproject.sourceforge.net/).
- jna.jar renamed to jna-3.2.4.jar: This jar can be found on the Java Native Access website (http://jna.dev.java.net/). Because the SVNPlugin currently uses an older version of this jar, NativeBrowser plugin requires this jar to be renamed to jna-3.2.4.jar (3.2.4 is the latest version of JNA at the time of writing, but the jar can be of any version as long as the name is jna-3.2.4.jar).
- swt.jar: This jar is specific to each platform and can be downloaded on the Eclipse SWT page (http://www.eclipse.org/swt/).

All these jars except the last one are included in the distribution zip file. Therefore upon installing the NativeBrowser plugin they are automatically placed in the the /jars subdirectory of your user settings directory (which you can find by evaluating the BeanShell expression jEdit.getSettingsDirectory()). The last jar swt.jar needs to be downloaded manually and placed in that same directory because each platform needs its own version.
With these jars in place jEdit is able to load the NativeBrower plugin without complaining about a missing jar. However jEdit won't be able to open the NativeBrowser window until these jars are also placed jEdit's classpath.
The easiest way is to modify the CLASSPATH environment variable to include each one of the 4 jars above. For example on Linux one can modify the /usr/bin/jedit script to include:
  JEJARS=~/.jedit/jars
  JECP=$JEJARS/swt.jar:$JEJARS/DJNativeSwing.jar:$JEJARS/DJNativeSwing-SWT.jar:$JEJARS/jna-3.2.4.jar
  if [ "$CLASSPATH" = "" ]; then
    CLASSPATH="/usr/share/java/jedit/jedit.jar":$JECP
  else
    CLASSPATH="/usr/share/java/jedit/jedit.jar":$JECP:$CLASSPATH
  fi

Eventually the burden of adding jars to jEdit's classpath should become a responsibility the plugin framework. Until then such manual steps will be required for libraries needing native callback to java.