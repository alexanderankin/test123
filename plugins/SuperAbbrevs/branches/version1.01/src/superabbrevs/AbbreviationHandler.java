package superabbrevs;

import java.util.LinkedList;
import java.util.Set;

import superabbrevs.model.Abbreviation;

public interface AbbreviationHandler {
    public LinkedList<Abbreviation> getAbbrevs(String modeName, String text);
    public void invalidateMode(String mode);
    public Set<Abbreviation> getAbbrevs(String modeName);
}
