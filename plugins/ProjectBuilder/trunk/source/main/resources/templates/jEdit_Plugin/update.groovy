// Update config script for jEdit Plugin

//{{{ imports
import org.gjt.sp.jedit.MiscUtilities
import projectbuilder.builder.ProjectBuilder as PB
//}}}

def buildfile = "${root}/build.xml";
if (!new File(buildfile).exists()) {
	buildfile = "${root}/trunk/build.xml";
	if (!new File(buildfile).exists()) {
		return;
	}
}

def buildProps = project.getProperty("project.config.build-properties");
if (buildProps == null || buildProps.length() == 0) {
	// Check the root directory
	def root = project.getRootPath();
	def path = MiscUtilities.constructPath(root, "build.properties");
	if (new File(path).exists()) {
		buildProps = path;
	} else {
		// Check the parent of the root directory
		path = MiscUtilities.constructPath(root, "../build.properties");
		if (new File(path).exists()) {
			buildProps = path;
		}
	}
	if (buildProps == null) buildProps = "";
	project.setProperty("project.config.build-properties", buildProps);
}

def buildSupport = project.getProperty("project.config.build-support");
def installDir = project.getProperty("project.config.install-dir");
def jeditInstallDir = project.getProperty("project.config.jedit-install-dir");
if (buildSupport == null || buildSupport.length() == 0) buildSupport = "..";
if (installDir == null || installDir.length() == 0) {
	installDir = new File(System.getProperty("user.home"), ".jedit").getPath();
}
if (jeditInstallDir == null || jeditInstallDir.length() == 0) jeditInstallDir = "..";
project.setProperty("project.config.build-support", buildSupport);
project.setProperty("project.config.install-dir", installDir);
project.setProperty("project.config.jedit-install-dir", jeditInstallDir);

if (buildProps.length() > 0) {
	def tag = "\n\t<property file=\"${buildProps}\" />\n\t";
	PB.mark(buildfile, "<!-- mark:build.properties -->", "<!-- /mark:build.properties -->", tag);
	PB.mark(buildfile, "<!-- mark:properties -->", "<!-- /mark:properties -->", "\n\t");
}
else {
	// Insert the properties from manual configuration
	def tags = "\n\t<property name=\"build.support\" value=\"${buildSupport}\" />";
	tags += "\n\t<property name=\"install.dir\" value=\"${installDir}/jars\" />";
	tags += "\n\t<property name=\"jedit.install.dir\" value=\"${jeditInstallDir}\" />";
	tags += "\n\t<property name=\"jedit.plugins.dir\" value=\"\${install.dir}\" />";
	tags += "\n\t";
	PB.mark(buildfile, "<!-- mark:build.properties -->", "<!-- /mark:build.properties -->", "\n\t");
	PB.mark(buildfile, "<!-- mark:properties -->", "<!-- /mark:properties -->", tags);
}

def plugins = project.getProperty("project.config.plugins");
if (plugins == null) plugins = "";
def tokenizer = new StringTokenizer(plugins);
def tags = "\n\t";
while (tokenizer.hasMoreTokens()) {
	def token = tokenizer.nextToken();
	tags += "<pathelement path=\"\${jedit.plugins.dir}/${token}\" />\n\t";
}
PB.mark(buildfile, "<!-- mark:plugins -->", "<!-- /mark:plugins -->", tags);

tags = "\n\t";
def lib = new File(root, "lib");
if (!lib.exists()) lib = new File("${root}/trunk", "lib");
if (lib.exists()) {
	def libList = lib.list();
	for (int i = 0; i<libList.length; i++) {
		tags += "<pathelement path=\"lib/${libList[i]}\" />\n\t";
	}
}
PB.mark(buildfile, "<!-- mark:libs -->", "<!-- /mark:libs -->", tags);
