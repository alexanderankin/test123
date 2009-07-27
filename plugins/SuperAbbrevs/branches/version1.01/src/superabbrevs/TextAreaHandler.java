package superabbrevs;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import superabbrevs.model.Abbreviation;
import trie.Match;

public interface TextAreaHandler {

	public abstract String getTextBeforeCaret();

	public abstract String getModeAtCursor();

	public abstract void showAbbrevsPopup(Match<Abbreviation> match);

	public abstract void removeMatch(String match);

	public abstract void expandAbbrev(Abbreviation abbrev, boolean invokedAsACommand);

	public abstract boolean selectNextAbbrev();

	public abstract void selectPrevAbbrev();

	public abstract boolean isInTemplateMode();
	
	public abstract void showSearchDialog(ArrayList<Abbreviation> abbrevs);

	public abstract void stopTemplateMode();

}