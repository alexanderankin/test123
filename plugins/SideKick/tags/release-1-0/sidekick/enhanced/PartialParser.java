/*
 * Copyright (C) 2008, Dale Anson
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
package sidekick.enhanced;

import sidekick.SideKickParsedData;
import org.gjt.sp.jedit.Buffer;
import errorlist.DefaultErrorSource;


/**
 * Parsers that implement this interface have the ability to parse just part of a
 * buffer.  This makes it possible to combine parsers so that, for example, an html
 * parser can delegate to a javascript parser to handle javascript embedded in the
 * html file, and to a css parser to handle embedded style sections.
 */
public interface PartialParser {

    /**
     * If called by another parser to parse part of a file (for example, to parse
     * a script tag in an html document), this can be set to the offset of the
     * text to be parsed so that the node locations can be set correctly.
     *
     * @param startLine the starting line in the buffer of the text that is to
     * be parsed.
     */
    public void setStartLine(int startLine);

    /**
     * Parse the contents of the given text.  This is the entry point to use when
     * only a portion of the buffer text is to be parsed.  Note that <code>setLineOffset</code>
     * should be called prior to calling this method, otherwise, tree node positions
     * may be off.
     *
     * @param buffer       the buffer containing the text to parse
     * @param text         the text to parse
     * @param errorSource  where to send errors
     * @return             the parsed buffer as a tree
     */
    public SideKickParsedData parse( Buffer buffer, String text, DefaultErrorSource errorSource );

}