package superabbrevs;

import java.util.ArrayList;
import java.util.LinkedList;

import superabbrevs.model.Abbrev;

public interface TextAreaHandler {

	public abstract String getTextBeforeCaret();

	public abstract String getModeAtCursor();

	public abstract void showAbbrevsPopup(LinkedList<Abbrev> abbrevs);

	public abstract void removeAbbrev(Abbrev abbrev);

	public abstract void expandAbbrev(Abbrev abbrev, boolean invokedAsACommand);

	public abstract boolean selectNextAbbrev();

	public abstract void selectPrevAbbrev();

	public abstract boolean isInTemplateMode();
	
	public abstract void showSearchDialog(ArrayList<Abbrev> abbrevs);

}