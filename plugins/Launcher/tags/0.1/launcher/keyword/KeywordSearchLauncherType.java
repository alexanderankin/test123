package launcher.keyword;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Properties;

import launcher.Launcher;
import launcher.LauncherPlugin;
import launcher.LauncherType;
import launcher.LauncherUtils;
import launcher.browser.BrowserLauncherType;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.OptionPane;
import org.gjt.sp.jedit.PluginJAR;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

public class KeywordSearchLauncherType extends LauncherType {
	
	public static final String SERVICE_NAME = KeywordSearchLauncherType.class.getName();
	public final String OPT_USE_DEFAULT = OPT_BASE_PREFIX + getPropertyPrefix() + ".use-default";
	
	public static final KeywordSearchLauncherType INSTANCE = new KeywordSearchLauncherType();
	
	public static final String KEYWORD_SEARCH_FILENAME = "keyword-search.properties";

	private KeywordSearchLauncherType() {
		super(SERVICE_NAME);
	}

	@Override
	public void registerLaunchers() {
		loadKeywordSearches();
	}

	@Override
	public Object resolve(Object resource) {
		return LauncherUtils.resolveToSelectedText(resource);
	}

	@Override
	public OptionPane getOptionPane() {
		return new KeywordSearchLauncherTypeOptionPane();
	}

	public Properties loadKeywordSearches() {

		Properties keywordSearches = new Properties();
		InputStream inprops = EditPlugin.getResourceAsStream(
				LauncherPlugin.class, KEYWORD_SEARCH_FILENAME);

		if (inprops != null) {
			KeywordSearchLauncherType.INSTANCE.unregisterUserDefinedLaunchers();

			try { 
				keywordSearches.load(inprops);
				inprops.close();
			} catch (Exception e) {
				Log.log(Log.ERROR, this, "Failed to load " + KEYWORD_SEARCH_FILENAME, e);
				return null;
			}
			
			registerKeywordSearches(keywordSearches);
		}
		
		return keywordSearches;

	} // }}}

	private void registerKeywordSearches(Properties keywordSearches) {
		PluginJAR pluginJAR = jEdit.getPlugin(LauncherPlugin.class.getName()).getPluginJAR();	
		for (Object _key : keywordSearches.keySet()) {
			String labelFormat = (String) _key;
			String urlFormat = keywordSearches.getProperty(labelFormat);
			String launcherName = KeywordSearchLauncher.computeName(
					KeywordSearchLauncher.PROP_PREFIX, new Object[]{labelFormat, urlFormat});
			String initCode =
				"new " + KeywordSearchLauncher.class.getName() + "(" +
					"new Object[]{\"" + labelFormat + "\", \"" + urlFormat + "\"}, false, true);"; 
			Log.log(Log.DEBUG, this, "Registering " + launcherName + " with code " + initCode);
			ServiceManager.registerService(
					KeywordSearchLauncherType.SERVICE_NAME,
					launcherName,
					initCode,
					pluginJAR);
		}
	}

	//{{{ +saveKeywordSearches(Properties) : void
	public void saveKeywordSearches(Properties keywordSearches)  {
		unregisterUserDefinedLaunchers();
		registerKeywordSearches(keywordSearches);
		
		PrintWriter out = new PrintWriter(
				new OutputStreamWriter(
					EditPlugin.getResourceAsOutputStream(
							LauncherPlugin.class, KEYWORD_SEARCH_FILENAME)
				) );

		try {
			keywordSearches.store(out, "Keyword searches");
		} catch (IOException e) {
			Log.log(Log.ERROR, this, "Failed to save into " + KEYWORD_SEARCH_FILENAME, e);
			return;
		} finally {
			if (out !=null)
				out.close();
		}
		
	} //}}}

	@Override
	public Launcher[] getDefaultLaunchersChoice() {
		return BrowserLauncherType.INSTANCE.getDefaultLaunchersChoice();
	}

	public Launcher getDefaultLauncher() {
		String defaultLauncherName =  jEdit.getProperty(
					getDefaultLauncherPropertyName());
		Launcher defaultLauncher = defaultLauncherName == null ? null :
					(Launcher)ServiceManager.getService(
							BrowserLauncherType.SERVICE_NAME, defaultLauncherName);
		if (defaultLauncher == null)
			defaultLauncher = BrowserLauncherType.INSTANCE.getDefaultLauncher();
		return defaultLauncher;
	}

}
