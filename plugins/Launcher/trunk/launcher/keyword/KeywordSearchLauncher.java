package launcher.keyword;


import java.net.URLEncoder;
import java.text.MessageFormat;

import launcher.LauncherUtils;
import launcher.text.selected.ComputeURLBrowserLauncher;

import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;

public class KeywordSearchLauncher extends ComputeURLBrowserLauncher {

	protected static final String PROP_PREFIX = KeywordSearchLauncher.class.getName();
	
	protected static final String KEYWORD_PLACEHOLDER = "%s";

	protected final String urlFormat;
	protected final String labelFormat;
	
	private String name = null;
	private String nameSuffix = null;

	public KeywordSearchLauncher(Object[] args) {
		this(PROP_PREFIX, args, false, false);
	}

	protected KeywordSearchLauncher(String prop, Object[] args) {
		this(prop, args, false, false);
	}

	public KeywordSearchLauncher(Object[] args, boolean stateful, boolean userDefined) {
		this(PROP_PREFIX, args, stateful, userDefined);
	}

	public KeywordSearchLauncher(String prop, Object[] args, boolean stateful, boolean userDefined) {
		super(	prop, args,	stateful, userDefined);
		this.labelFormat = args[0].toString();
		this.urlFormat = args[1].toString();
	}
	
	public String computeFormatterString(String formatString) {
		return formatString;
	}

	public String getNameSuffix() {
		if (nameSuffix == null)
			nameSuffix = computeNameSuffix(args);
		return nameSuffix;
	}
	
	public static String computeNameSuffix(Object[] args) {
		return "{" + args[0].toString() + "}";
	}
	
	public static String computeName(String baseName, Object[] args) {
		return baseName + computeNameSuffix(args);
	}
	
	@Override
	public String getName() {
		if (name==null) {
			name = computeName(super.getName(), args);
		}
		return name;
	}
	
	@Override
	public String getLabel() {
		return MessageFormat.format(labelFormat, args);
	}

	@Override
	protected String computeURL(View view, Object resource) {
		CharSequence keyword = LauncherUtils.resolveToCharSequence(resource);
		if (keyword == null)
			return null;
		String url = null;
		try {
			url = urlFormat.replace(KEYWORD_PLACEHOLDER,
					URLEncoder.encode(keyword.toString(), "UTF-8"));
		} catch (Exception e) {
			Log.log(Log.ERROR, this, "Error when encoding url", e);
		}
		return url;
	}

}
