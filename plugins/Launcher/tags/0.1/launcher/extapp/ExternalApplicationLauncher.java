package launcher.extapp;


import java.io.File;

import launcher.Launcher;

import org.gjt.sp.jedit.View;

public abstract class ExternalApplicationLauncher extends Launcher {

	protected final String applicationPath;
	protected final String shortLabel;
	private String name = null;
	private String nameSuffix = null;
	
	public ExternalApplicationLauncher(String prop, Object[] args) {
		this(prop, args, false, false);
	}

	public ExternalApplicationLauncher(String prop, Object[] args, boolean stateful, boolean userDefined) {
		super(prop, args, stateful, userDefined);
		this.applicationPath = args[0].toString();
		this.shortLabel = new File(applicationPath).getName();
	}
	
	public String getNameSuffix() {
		if (nameSuffix == null)
			nameSuffix = computeNameSuffix(args);
		return nameSuffix;
	}
	
	public static String computeNameSuffix(Object[] args) {
		StringBuffer buffer = new StringBuffer("{");
		boolean needComma = false;
		for (Object arg : args) {
			if (needComma)
				buffer.append(',');
			buffer.append(arg);
			needComma = true;
		}
		buffer.append('}');
		return buffer.toString();
	}
	
	public static String computeName(String baseName, Object[] args) {
		return baseName + computeNameSuffix(args);
	}
	
	public String getApplicationPath() {
		return applicationPath;
	}

	@Override
	public String getName() {
		if (name==null) {
			name = computeName(super.getName(), args);
		}
		return name;
	}
	
	@Override
	public String getShortLabel() {
		return shortLabel;
	}
	
	@Override
	public String getCode(View view, Object resolvedResource) {
		if (false)
				try {
					launcher.LauncherUtils.runCmd(new String[]{applicationPath,
						resolvedResource.toString()});
				} catch (Exception e) {}
		// The code above is just for compilation purposes and should correspond to
		// the code returned below. Any compilation error here should help ensure
		// the "as above so below" principle ;)
		return "try {\n" +
					"\tlauncher.LauncherUtils.runCmd(new String[]{\"" + applicationPath + "\",\n" +
						"\t\t\"" + resolvedResource.toString() + "\"});\n" +
				"} catch (Exception e) {}";
	}
	
}
