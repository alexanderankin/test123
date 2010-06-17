/*
Copyright (C) 2010  Shlomy Reinstein

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

Note: The code for the ctags path and the ctags version notice
was taken from the CodeBrowser plugin by Gerd Knops. 
*/

package marker;

import javax.swing.JCheckBox;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

/** ************************************************************************** */
@SuppressWarnings("serial")
public class MarkerSetsOptions extends AbstractOptionPane
{
	private static final String PREFIX = MarkerSetsPlugin.OPTION;
	private static final String REPLACE_BUILT_IN_MARKERS = PREFIX + "replaceBuiltInMarkers";
	private static final String REPLACE_BUILT_IN_MARKERS_LABEL =
		PREFIX + "replaceBuiltInMarkers.label";
	private JCheckBox replaceBuiltInMarkers;

	public MarkerSetsOptions()
	{
		super("MarkerSets");
		setBorder(new EmptyBorder(5, 5, 5, 5));

		replaceBuiltInMarkers = new JCheckBox(jEdit.getProperty(
			REPLACE_BUILT_IN_MARKERS_LABEL), shouldReplaceBuiltInMarkers());
		addComponent(replaceBuiltInMarkers);
	}

	public void save()
	{
		jEdit.setBooleanProperty(REPLACE_BUILT_IN_MARKERS,
			replaceBuiltInMarkers.isSelected());
	}

	public static boolean shouldReplaceBuiltInMarkers()
	{
		return jEdit.getBooleanProperty(REPLACE_BUILT_IN_MARKERS);
	}
}

