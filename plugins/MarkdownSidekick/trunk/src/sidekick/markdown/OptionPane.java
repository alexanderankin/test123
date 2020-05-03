/*
 * Copyright (c) 2019, Dale Anson
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the <ORGANIZATION> nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package sidekick.markdown;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.AbstractOptionPane;

/**
 * Option pane for the Markdown Sidekick. Currently the only options are to show
 * or not show paragraphs, block quotes, and code blocks.
 */
public class OptionPane extends AbstractOptionPane {

    private JCheckBox showParagraphs;
    private JCheckBox showQuotes;
    private JCheckBox showCode;

    public OptionPane() {
        super( "markdown" );
    }

    public void _init() {
        setBorder( BorderFactory.createEmptyBorder(6, 6, 6, 6 ) );

        showParagraphs = new JCheckBox(jEdit.getProperty("sidekick.markdown.showParagraphs.label", "Show Paragraphs"));
        showQuotes = new JCheckBox(jEdit.getProperty("sidekick.markdown.showQuotes.label", "Show Block Quotes"));
        showCode = new JCheckBox(jEdit.getProperty("sidekick.markdown.showCode.label", "Show Code Blocks"));
        
        showParagraphs.setSelected(jEdit.getBooleanProperty("sidekick.markdown.showParagraphs", true));
        showQuotes.setSelected(jEdit.getBooleanProperty("sidekick.markdown.showQuotes", true));
        showCode.setSelected(jEdit.getBooleanProperty("sidekick.markdown.showCode", true));
        
        
        addComponent(showParagraphs);
        addComponent(showQuotes);
        addComponent(showCode);
    }

    public void _save() {
    	jEdit.setBooleanProperty("sidekick.markdown.showParagraphs", showParagraphs.isSelected());
    	jEdit.setBooleanProperty("sidekick.markdown.showQuotes", showQuotes.isSelected());
    	jEdit.setBooleanProperty("sidekick.markdown.showCode", showCode.isSelected());
    }
}