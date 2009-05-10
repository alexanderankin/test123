package superabbrevs;

import java.util.ArrayList;
import java.util.LinkedList;

import superabbrevs.model.Abbreviation;

public interface TextAreaHandler {

	public abstract String getTextBeforeCaret();

	public abstract String getModeAtCursor();

	public abstract void showAbbrevsPopup(LinkedList<Abbreviation> abbrevs);

	public abstract void removeAbbrev(Abbreviation abbrev);

	public abstract void expandAbbrev(Abbreviation abbrev, boolean invokedAsACommand);

	public abstract boolean selectNextAbbrev();

	public abstract void selectPrevAbbrev();

	public abstract boolean isInTemplateMode();
	
	public abstract void showSearchDialog(ArrayList<Abbreviation> abbrevs);

}