/**
 * 
 */
package gatchan.jedit.lucene;

import java.util.List;

import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.TokenGroup;

public class SearchFormatter implements Formatter
{
	private List<Integer> positions;
	private int max;

	public SearchFormatter(List<Integer> positions, int max)
	{
		this.positions = positions;
		this.max = max;
	}
	public String highlightTerm(String originalText,
		TokenGroup tokenGroup)
	{
        if ((positions.size() < max) &&
        	(tokenGroup.getTotalScore() > 0))
        {
        	positions.add(tokenGroup.getStartOffset());
        }
		return originalText;
	}
}