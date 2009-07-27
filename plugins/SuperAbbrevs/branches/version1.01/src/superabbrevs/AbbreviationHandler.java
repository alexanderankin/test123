package superabbrevs;

import java.util.Set;

import superabbrevs.model.Abbreviation;
import trie.Match;

public interface AbbreviationHandler {
    public Match<Abbreviation> getAbbrevs(String modeName, String text);
    public Set<Abbreviation> getAbbrevs(String modeName);
}
