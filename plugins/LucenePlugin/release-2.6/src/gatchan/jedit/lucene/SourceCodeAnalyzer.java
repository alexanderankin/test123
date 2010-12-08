package gatchan.jedit.lucene;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharTokenizer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;

public class SourceCodeAnalyzer extends Analyzer
{
	public TokenStream tokenStream(String fieldName, Reader reader) {
		return new SourceCodeTokenizer(reader);
	}

	public TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException {
		Tokenizer tokenizer = (Tokenizer) getPreviousTokenStream();
		if (tokenizer == null) {
			tokenizer = new SourceCodeTokenizer(reader);
			setPreviousTokenStream(tokenizer);
		} else
			tokenizer.reset(reader);
		return tokenizer;
	}

	static private class SourceCodeTokenizer extends CharTokenizer
	{
		public SourceCodeTokenizer(Reader input) {
			super(input);
		}

		@Override
		protected boolean isTokenChar(char c) {
			return ((c >= 'A') && (c <= 'Z')) || ((c >= 'a') && (c <= 'z')) ||
				((c >= '0') && (c <= '9')) || (c == '_');
		}
	}
}
