/*
Copyright (c) 2006, Dale Anson
All rights reserved.
Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:
* Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.
* Neither the name of the <ORGANIZATION> nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package sidekick.html;

import java.io.StringReader;
import java.util.*;

import javax.swing.tree.DefaultMutableTreeNode;
import errorlist.*;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.PropertiesChanged;

import sidekick.SideKickParsedData;
import sidekick.util.*;
import sidekick.html.parser.html.HtmlCollector;
import sidekick.html.parser.html.HtmlDocument;
import sidekick.html.parser.html.HtmlTreeBuilder;
import xml.parser.XmlParser;

/**
 * @author    Dale Anson
 * @version   $Revision$
 */
public class HtmlParser extends XmlParser implements EBComponent {

    private View currentView = null;

    public static boolean showAll = true;

    /** Constructor for HtmlParser  */
    public HtmlParser() {
        super("html");
    }

    /**
     * Description of the Method
     *
     * @param editPane
     */
    public void activate(EditPane editPane) {
        super.activate(editPane);
        currentView = editPane.getView();
        EditBus.addToBus(this);
    }

    /**
     * Description of the Method
     *
     * @param editPane
     */
    public void deactivate(EditPane editPane) {
        super.deactivate(editPane);
        EditBus.removeFromBus(this);
    }

    /**
     * Description of the Method
     *
     * @param msg
     */
    public void handleMessage(EBMessage msg) {
        if (msg instanceof PropertiesChanged) {
            parse();
        }
    }

    /** Description of the Method */
    public void parse() {
        if (currentView != null) {
            parse(currentView.getBuffer(), null);
        }
    }

    /**
     * Parse the contents of the given buffer.
     *
     * @param buffer       the buffer to parse
     * @param errorSource
     * @return             Description of the Returned Value
     */
    public SideKickParsedData parse(Buffer buffer, DefaultErrorSource errorSource) {

        String filename = buffer.getPath();
        SideKickParsedData parsedData = new HtmlSideKickParsedData(filename, buffer);
        DefaultMutableTreeNode root = parsedData.root;

        StringReader reader = new StringReader(buffer.getText(0, buffer.getLength()));
        HtmlTreeBuilder builder = null;
        try {
            // create the parser
            sidekick.html.parser.html.HtmlParser parser = new sidekick.html.parser.html.HtmlParser(reader);

            // set tab size so that the parser can accurately calculate line and column positions
            parser.setTabSize(buffer.getTabSize());

            // parse the buffer
            HtmlDocument document = parser.HtmlDocument();

            // set display options
            document.setShowBrackets(jEdit.getBooleanProperty(
                    "options.sidekick.html.showBrackets", true));
            document.setShowTagAttributes(jEdit.getBooleanProperty(
                    "options.sidekick.html.showTagAttributes", true));
            document.setShowCoreAttributes(jEdit.getBooleanProperty(
                    "options.sidekick.html.showCoreAttributes", true));
            document.setShowLangAttributes(jEdit.getBooleanProperty(
                    "options.sidekick.html.showLangAttributes", true));
            document.setShowScriptAttributes(jEdit.getBooleanProperty(
                    "options.sidekick.html.showScriptAttributes", true));
            document.setShowJspTags(jEdit.getBooleanProperty(
                    "options.sidekick.html.showJspElements", true));

            // collect and clean
            document.accept(new HtmlCollector());

            // make a tree
            builder = new HtmlTreeBuilder(root);
            builder.setShowAll(jEdit.getBooleanProperty(
                    "options.sidekick.html.showAllElements", true));
            builder.setShowAll(showAll);
            builder.setBuffer(buffer);
            builder.setErrorSource(errorSource);
            document.accept(builder);

            // need to convert the HtmlDocument.HtmlElements that are currently
            // the user objects in the tree nodes to SideKick Assets
            ElementUtil.convert(buffer, root);

            if ( !buffer.isDirty() && errorSource != null ) {
                /* only handle errors when buffer is saved. Otherwise, there will be a lot
                of spurious errors shown when code completion is on and the user is in the
                middle of typing something. */
                List<ParseError> parseErrors = parser.getParseErrors();
                for (ParseError pe : parseErrors) {
                    String message = pe.message;
                    Range range = pe.range;
                    // addError is lame -- what if the error spans more than one line?
                    // Need to just deal with it...
                    if (range.endLine != range.startLine) {
                        range.endColumn = range.startColumn;
                    }
                    errorSource.addError( ErrorSource.ERROR, filename, range.startLine, range.startColumn, range.endColumn, message );
                }
            }
        }
        catch (Exception e) {
            errorSource.addError(ErrorSource.ERROR, filename, 0, 0, 0, e.getMessage());
            e.printStackTrace();
        }
        finally {
            reader.close();
        }
        return parsedData;
    }

}

