/*
 * MatchTag.java
 * Copyright (C) 2000 Scott Wyatt, 2001 Andre Kaplan
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package xml;

import java.util.Stack;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;

public class MatchTag {

	public static final int T_STANDALONE_TAG = 0;
	public static final int T_START_TAG = 1;
	public static final int T_END_TAG = 2;

	public static void matchTag(JEditTextArea textArea) {
		String text = textArea.getText();
		Tag tag = getSelectedTag(textArea.getCaretPosition(), text);
		if (tag != null) {
			Tag matchingTag = getMatchingTag(text, tag);
			if (matchingTag != null) {
				textArea.setSelection(new Selection.Range(
					matchingTag.start, matchingTag.end
				));
				textArea.moveCaretPosition(matchingTag.end);
			} else
				textArea.getToolkit().beep();
		}
	}


	/**
	 * "The ampersand character (&) and the left angle bracket (&lt;) may appear
	 * in their literal form only when used as markup delimiters, or within a comment,
	 * a processing instruction, or a CDATA section" (XML 1.0).
	 */
	public static Tag getSelectedTag(int pos, String text) {

		if (pos < 0 || pos > text.length())
			return null;

		// Get the last '<' before current position.
		int startTag = text.lastIndexOf('<', pos - 1);
		if (startTag == -1 || startTag + 2 >= text.length()) // at least 2 chars after '<'
			return null;

		// Are we [in] a comment section ?
		int lastComment = text.lastIndexOf("<!--", startTag);
		if (lastComment != -1 && text.lastIndexOf("-->", startTag - 3) < lastComment)
			return null;

		// Are we [in] a processing instruction section ?
		int lastPi = text.lastIndexOf("<?", startTag);
		if (lastPi != -1 && text.lastIndexOf("?>", startTag - 2) < lastPi)
			return null;

		// Are we [in] a CDATA section ?
		int lastCDATA = text.lastIndexOf("<![CDATA[", startTag);
		if (lastCDATA != -1 && text.lastIndexOf("]]>", startTag - 3) < lastCDATA)
			return null;

		// Find the tag name and get the first '>' after startTag
		// which is NOT in a string literal.
		// STag         ::= '<' Name (S Attribute)* S? '>'
		// ETag         ::= '</' Name S? '>'
		// EmptyElemTag ::= '<' Name (S Attribute)* S? '/>'
		// Name         ::= (a non empty string WITHOUT spaces...)
		// S            ::= (#x20 | #x9 | #xD | #xA)+
		int tagType = T_START_TAG;
		int startTagName = startTag + 1;
		if (text.charAt(startTagName) == '/') {
			tagType = T_END_TAG;
			++startTagName;
		}

		int endTag = -1;
		int endTagName = -1;
		char literalChar = 0;
		for (int i = startTagName; endTag == -1 && i < text.length(); ++i) {
			switch (text.charAt(i)) {
			case '>' :
				// we have something like <> or </>, or we are outside of the tag.
				if (startTagName == i)
					return null;
				if (literalChar == 0) {
					// we are after the end of the tag...
					if (i < pos - 1)
						return null;
					if (i > 0 && text.charAt(i - 1) == '/') {
						// if we have something like </.../>
						if (tagType == T_END_TAG)
							return null;
						tagType = T_STANDALONE_TAG;
					}
					endTag = i + 1;
					if (endTagName == -1)
						endTagName = i;
				}
				break;
			case ' ' : case '\t' : case '\n' : case '\r' :
				// XML does not allow leading spaces.
				if (startTagName == i)
					return null;
				if (endTagName == -1)
					endTagName = i;
				break;
			case '"' : case '\'' :
				// a tag name cannot begin with ' or ".
				if (startTagName == i)
					return null;
				if (literalChar == 0)
					literalChar = text.charAt(i);
				else if (literalChar == text.charAt(i))
					literalChar = 0;
				break;
			default :
				break;
			}
		}

		if (endTag == -1)
			return null;

		Tag tag = new Tag();
		tag.start = startTag;
		tag.end   = endTag;
		tag.tag	  = text.substring(startTagName, endTagName);
		tag.type  = tagType;

		/*
			System.out.print("------------- getSelectedTag");
			System.out.print("tag.tag   = " + tag.tag);
			System.out.print("tag.type  = " + tag.type);
			System.out.print("tag.start = " + tag.start);
			System.out.print("tag.end   = " + tag.end);
		//*/

		return tag;
	}


	public static Tag getMatchingTag(String text, Tag tag) {
		if (tag.type == T_START_TAG)
			return findEndTag(text, tag);
		else if (tag.type == T_END_TAG)
			return findStartTag(text, tag);
		return null;
	}


	private static Tag findEndTag(String text, Tag startTag) {
		Stack tagStack = new Stack();
		for (int i = text.indexOf('<', startTag.end);
			i != -1; i = text.indexOf('<', ++i)) {
			Tag tag = getSelectedTag(i + 1, text);
			if (tag == null)
				continue;
			else if (tag.type == T_END_TAG) {
				for(;;) {
					if(tagStack.empty()) {
						if (tag.tag.equals(startTag.tag))
							return tag;
						else
							return null;
					}
					else if(tag.tag.equals(tagStack.pop()))
						break;
				}
			} else if(tag.type == T_START_TAG)
				tagStack.push(tag.tag);
		}
		return null;
	}


	private static Tag findStartTag(String text, Tag endTag) {
		Stack tagStack = new Stack();
loop:		for (int i = text.lastIndexOf('<', endTag.start - 1);
			i != -1; i = text.lastIndexOf('<', --i)) {
			System.err.println(i);
			Tag tag = getSelectedTag(i + 1, text);
			if (tag == null)
				continue;
			else if (tag.type == T_START_TAG) {
				for(int j = tagStack.size() - 1; j >= 0; j--) {
					if(tag.tag.equals(tagStack.get(j))) {
						for(int k = tagStack.size() - 1; k >= j; k--) {
							tagStack.remove(k);
						}
						continue loop;
					}
				}

				if (tag.tag.equals(endTag.tag))
					return tag;
				else
					continue;
			} else if(tag.type == T_END_TAG)
				tagStack.push(tag.tag);
		}
		return null;
	}


	public static class Tag {
		public String tag = null;
		public int type = -1;
		public int start = -1;
		public int end = -1;
	}
}

