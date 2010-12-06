package ua.pico.jedit.markdown;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;

import java.util.Arrays;

/**
 * Markdown utility class
 * @author [Vitaliy Berdinskikh UR6LAD](mailto:ur6lad@i.ua)
 */
public class MarkdownUtil extends EditPlugin {

	static final String NONE_EXTENSIONS = "none";
	static final String ALL_EXTENSIONS = "all";
	static final String[] EXTENSION_NAME = new String[] {
		"abbreviations", "autolinks", "hardwraps", "quotes", "smarts",
		"smartypants", "tables", "noBlocks", "noInline", "noHypertext"
	};
	static final int[] EXTENSION_ID = new int[] {
		Extensions.ABBREVIATIONS, Extensions.AUTOLINKS, Extensions.HARDWRAPS,
		Extensions.QUOTES, Extensions.SMARTS, Extensions.SMARTYPANTS,
		Extensions.TABLES, Extensions.SUPPRESS_HTML_BLOCKS,
		Extensions.SUPPRESS_INLINE_HTML, Extensions.SUPPRESS_ALL_HTML
	};
	static final String TARGET = "target";

	public static MarkdownUtil getInstance() {
		return singleton;
	}

	/**
	 * Returns the current set of pegdown extensions.
	 * @return Set of pegdown extensions.
	 */
	public int getExtensions() {
		return extensions;
	}

	/**
	 * Sets the new set of pegdown extensions and create a new processor.
	 * @param extensions The value to extensions.
	 */
	public void setExtensions(final int extensions) {
		this.extensions = saveExtensions(extensions);
		processor = new PegDownProcessor(this.extensions);
	}

	/**
	 * Return current target (buffer, clipboard or browser).
	 * @see MarkdownPlugin.Target
	 */
	public MarkdownPlugin.Target getTarget() {
		return target;
	}

	/**
	 * Set the new target.
	 * @see MarkdownPlugin.Target
	 */
	public void setTarget(final MarkdownPlugin.Target target) {
		jEdit.setProperty(MarkdownPlugin.OPTION_PREFIX + TARGET, target.name());
		this.target = target;
	}

	public PegDownProcessor getProcessor() {
		return processor;
	}

	private static MarkdownUtil singleton;

	private PegDownProcessor processor;
	private int extensions;
	private MarkdownPlugin.Target target;
	
	static {
		singleton = new MarkdownUtil();
	}

	private MarkdownUtil() {
		assert EXTENSION_NAME.length == EXTENSION_ID.length : "names: " + EXTENSION_NAME.length + " != ids: " + EXTENSION_ID.length;
		extensions = readExtensions();
		processor = new PegDownProcessor(extensions);
		try {
			target = Enum.valueOf(MarkdownPlugin.Target.class, jEdit.getProperty(MarkdownPlugin.OPTION_PREFIX + TARGET, MarkdownPlugin.Target.Buffer.name()));
		} catch (IllegalArgumentException iaex) {
			Log.log(Log.WARNING, MarkdownUtil.class, iaex.getMessage());
			target = MarkdownPlugin.Target.Buffer;
			jEdit.setProperty(MarkdownPlugin.OPTION_PREFIX + TARGET, target.name());
			Log.log(Log.NOTICE, MarkdownUtil.class, "Set target as '" + target + "'.");
		}
	}

	private int readExtensions() {
		int valid_extensions;

		if (jEdit.getBooleanProperty(MarkdownPlugin.OPTION_PREFIX + NONE_EXTENSIONS, false)) {
			valid_extensions = Extensions.NONE;
		} else if (jEdit.getBooleanProperty(MarkdownPlugin.OPTION_PREFIX + ALL_EXTENSIONS, true)) {
			valid_extensions = Extensions.ALL;
		} else {
			valid_extensions = Extensions.NONE;
			for (int i = 0; i < EXTENSION_ID.length; i++) {
				if (jEdit.getBooleanProperty(MarkdownPlugin.OPTION_PREFIX + EXTENSION_NAME[i], false)) {
					valid_extensions |= EXTENSION_ID[i];
				}
			}
		}

		return valid_extensions;
	}

	private synchronized int saveExtensions(final int extensions) {
		final boolean properties[] = new boolean[EXTENSION_NAME.length];
		int valid_extensions;

		Arrays.fill(properties, false);
		if (Extensions.NONE == extensions) {
			valid_extensions = extensions;
			jEdit.setBooleanProperty(MarkdownPlugin.OPTION_PREFIX + NONE_EXTENSIONS, true);
			jEdit.setBooleanProperty(MarkdownPlugin.OPTION_PREFIX + ALL_EXTENSIONS, false);
		} else if (Extensions.ALL == (extensions & Extensions.ALL)) {
			valid_extensions = Extensions.ALL;
			jEdit.setBooleanProperty(MarkdownPlugin.OPTION_PREFIX + NONE_EXTENSIONS, false);
			jEdit.setBooleanProperty(MarkdownPlugin.OPTION_PREFIX + ALL_EXTENSIONS, true);
		} else {
			jEdit.setBooleanProperty(MarkdownPlugin.OPTION_PREFIX + NONE_EXTENSIONS, false);
			jEdit.setBooleanProperty(MarkdownPlugin.OPTION_PREFIX + ALL_EXTENSIONS, false);
			valid_extensions = Extensions.NONE;
			for (int i = 0; i < EXTENSION_ID.length; i++) {
				if (EXTENSION_ID[i] == (extensions & EXTENSION_ID[i])) {
					properties[i] = true;
					valid_extensions |= EXTENSION_ID[i];
				}
			}
		}
		for (int i = 0; i < EXTENSION_ID.length; i++) {
			jEdit.setBooleanProperty(MarkdownPlugin.OPTION_PREFIX + EXTENSION_NAME[i], properties[i]);
		}

		return valid_extensions;
	}

}

