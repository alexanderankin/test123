/*
 * WhiteSpaceActions.java
 * Copyright (c) 2001 Andre Kaplan
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package whitespace;

import org.gjt.sp.jedit.Buffer;


public class WhiteSpaceActions
{
	private WhiteSpaceActions() {}


	private static WhiteSpaceModel getWhiteSpaceModel(Buffer buffer) {
		return (WhiteSpaceModel) buffer.getProperty(
			WhiteSpaceModel.MODEL_PROPERTY
		);
	}


	public static void toggleRemoveTrailing(Buffer buffer) {
		WhiteSpaceModel model = getWhiteSpaceModel(buffer);
		if (model == null) { return; }

		model.getRemoveTrailingWhitespace().toggleEnabled();
	}


	public static boolean isRemoveTrailingSelected(Buffer buffer) {
		WhiteSpaceModel model = getWhiteSpaceModel(buffer);
		if (model == null) { return false; }

		return model.getRemoveTrailingWhitespace().isEnabled();
	}


	public static void toggleSoftTabifyLeading(Buffer buffer) {
		WhiteSpaceModel model = getWhiteSpaceModel(buffer);
		if (model == null) { return; }

		WhiteSpaceModel.Option option = model.getSoftTabifyLeadingWhitespace();

		option.toggleEnabled();

		if (option.isEnabled()) {
			// model.getSoftTabifyLeadingWhitespace().setEnabled(false);
			model.getTabifyLeadingWhitespace().setEnabled(false);
			model.getUntabifyLeadingWhitespace().setEnabled(false);
		}
	}


	public static boolean isSoftTabifyLeadingSelected(Buffer buffer) {
		WhiteSpaceModel model = getWhiteSpaceModel(buffer);
		if (model == null) { return false; }

		return model.getSoftTabifyLeadingWhitespace().isEnabled();
	}


	public static void toggleTabifyLeading(Buffer buffer) {
		WhiteSpaceModel model = getWhiteSpaceModel(buffer);
		if (model == null) { return; }

		WhiteSpaceModel.Option option = model.getTabifyLeadingWhitespace();

		option.toggleEnabled();

		if (option.isEnabled()) {
			model.getSoftTabifyLeadingWhitespace().setEnabled(false);
			// model.getTabifyLeadingWhitespace().setEnabled(false);
			model.getUntabifyLeadingWhitespace().setEnabled(false);
		}
	}


	public static boolean isTabifyLeadingSelected(Buffer buffer) {
		WhiteSpaceModel model = getWhiteSpaceModel(buffer);
		if (model == null) { return false; }

		return model.getTabifyLeadingWhitespace().isEnabled();
	}


	public static void toggleUntabifyLeading(Buffer buffer) {
		WhiteSpaceModel model = getWhiteSpaceModel(buffer);
		if (model == null) { return; }

		WhiteSpaceModel.Option option = model.getUntabifyLeadingWhitespace();

		option.toggleEnabled();

		if (option.isEnabled()) {
			model.getSoftTabifyLeadingWhitespace().setEnabled(false);
			model.getTabifyLeadingWhitespace().setEnabled(false);
			// model.getUntabifyLeadingWhitespace().setEnabled(false);
		}
	}


	public static boolean isUntabifyLeadingSelected(Buffer buffer) {
		WhiteSpaceModel model = getWhiteSpaceModel(buffer);
		if (model == null) { return false; }

		return model.getUntabifyLeadingWhitespace().isEnabled();
	}
}

