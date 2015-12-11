
/*
 * CLangBeautifierPlugin.java - jEdit plugin entry point
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2015, Dale Anson
 *
 * The CLangBeautifier plugin is licensed under the GNU General Public License.
 */
package clangbeauty;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;

/**
 * plugin entry point.
 */
public class CLangBeautyPlugin extends EditPlugin {

	public static String CLANG_FORMAT_EXE_PROP = "clangbeauty.clang-format";

	public static String getCLangFormatExe() {
		return jEdit.getProperty( CLANG_FORMAT_EXE_PROP, "clang-format" );
	}
}

