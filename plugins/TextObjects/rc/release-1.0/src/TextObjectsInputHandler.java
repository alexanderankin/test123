
/* Copyright (C) 2008 Matthew Gilbert */

package textobjects;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DefaultInputHandler;
import org.gjt.sp.jedit.gui.InputHandler;
import org.gjt.sp.jedit.gui.KeyEventTranslator;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.jedit.textarea.TextArea;

public class TextObjectsInputHandler extends DefaultInputHandler
{
    private enum State {WAITING_FOR_AMOUNT, WAITING_FOR_OBJECT, WAITING_FOR_ESCAPE};
    private View view;
    private InputHandler old_handler;
    private boolean whole;
    private State state;
    private int start_pos;
    private Selection selection;
    private boolean vimposter;
    private Character object_key;
    
    private static KeyEventTranslator.Key right = KeyEventTranslator.parseKey("RIGHT");
    private static KeyEventTranslator.Key left = KeyEventTranslator.parseKey("LEFT");
    private static KeyEventTranslator.Key up = KeyEventTranslator.parseKey("UP");
    private static KeyEventTranslator.Key down = KeyEventTranslator.parseKey("DOWN");
    private static KeyEventTranslator.Key esc = KeyEventTranslator.parseKey("ESCAPE");
    
    // Support for Vimposter
    private static KeyEventTranslator.Key v_left = KeyEventTranslator.parseKey("h");
    private static KeyEventTranslator.Key v_up = KeyEventTranslator.parseKey("k");
    
    private static String blocks = "{[(<}])>";
    private static String quotes = "\"'`";
    
    private void init(View view_, InputHandler old_)
    {
        this.view = view_;
        this.old_handler = old_;
        this.state = State.WAITING_FOR_AMOUNT;
        this.whole = false;
        this.vimposter = false;
        this.object_key = null;
        this.start_pos = this.view.getTextArea().getCaretPosition();
    }
    
    public TextObjectsInputHandler(View view_, InputHandler old_)
    {
        super(view_);
        init(view_, old_);
    }
    
    public TextObjectsInputHandler(View view_, InputHandler old_, boolean whole_)
    {
        super(view_);
        init(view_, old_);
        this.whole = whole_;
        this.state = State.WAITING_FOR_OBJECT;
        
        // FIXME: HACK this is being used by Vimposter, so we enable the
        // vimposter shortcuts.
        this.vimposter = true;
    }
    
    private Selection handleObject(KeyEventTranslator.Key key)
    {
        Selection res = null;
        TextArea ta = view.getTextArea();
        int index;
        check:
        {
            index = blocks.indexOf(key.input);
            if (index != -1) {
                int pair_index = (index + (blocks.length() / 2)) % blocks.length();
                int open = Math.min(index, pair_index);
                int close = Math.max(index, pair_index);
                String pair = blocks.substring(open, open + 1) + 
                blocks.substring(close, close + 1);
                res = TextObjectsPlugin.block(ta, ta.getCaretPosition(), whole, pair);
                // Store the object key for blocks only.
                this.object_key = key.input;
                break check;
            }
            
            index = quotes.indexOf(key.input);
            if (index != -1) {
                res = TextObjectsPlugin.quote(ta, ta.getCaretPosition(), whole, quotes.charAt(index));
                break check;
            }
            
            if (key.input == 'w') {
                res = TextObjectsPlugin.word(ta, ta.getCaretPosition(), whole);
                break check;
            }
            
            if (key.input == 'p') {
                res = TextObjectsPlugin.paragraph(ta, ta.getCaretPosition(), whole);
                break check;
            }
            
            if (key.input == 'c') {
                res = TextObjectsPlugin.comment(ta, ta.getCaretPosition(), whole);
                break check;
            }
            
            if (key.input == 's') {
                res = TextObjectsPlugin.sentence(ta, ta.getCaretPosition(), whole);
                break check;
            }
        }
        return res;
    }
    
    private void finishObject(Selection res)
    {
        if ((res != null) /*&& (res.getStart() != res.getEnd())*/) {
            this.selection = res;
            this.view.getTextArea().moveCaretPosition(res.getEnd() - 1);
            this.view.getTextArea().addToSelection(res);
            this.state = State.WAITING_FOR_ESCAPE;
        } else {
            this.view.setInputHandler(this.old_handler);
        }
    }
    
    public boolean handleKey(KeyEventTranslator.Key key, boolean dry_run)
    {
        if (this.state != State.WAITING_FOR_ESCAPE && 
            key.modifiers != null) 
        {
            this.view.setInputHandler(this.old_handler);
            return true;
        }
        
        if (state == State.WAITING_FOR_AMOUNT) {
            if (key.input == 'a') {
                whole = true;
            } else if (key.input == 'i') {
                whole = false;
            } else {
                this.view.setInputHandler(this.old_handler);
                return true;
            }
            state = State.WAITING_FOR_OBJECT;
        } else if (state == State.WAITING_FOR_OBJECT) {
            Selection res = handleObject(key);
            finishObject(res);
        } else {
            String key_str = key.toString();
            
            if ((this.object_key != null) && 
                (this.object_key == key.input))
            {
                TextArea ta = this.view.getTextArea();
                int offset = ta.getCaretPosition();
                Selection orig = ta.getSelectionAtOffset(offset);
                offset += this.whole ? 1 : 2;
                ta.setCaretPosition(offset);
                Selection res = handleObject(key);
                if (res != null) {
                    finishObject(res);
                } else {
                    ta.setCaretPosition(orig.getEnd() - 1);
                    ta.setSelection(orig);
                }
            } else {
                if (key_str.equals(left.toString()) || 
                    key_str.equals(up.toString())) 
                {
                    this.view.getTextArea().moveCaretPosition(this.selection.getStart());
                } else if (this.vimposter && 
                           (key_str.equals(v_left.toString()) ||
                            key_str.equals(v_up.toString())))
                {
                    this.view.getTextArea().moveCaretPosition(this.selection.getStart());
                } else if (key_str.equals(esc.toString())) {
                    this.view.getTextArea().setCaretPosition(this.start_pos);
                    this.view.getTextArea().setSelection((Selection)null);
                }
                this.view.setInputHandler(this.old_handler);
                return this.old_handler.handleKey(key, dry_run);
            }
        }

        return true;
    }
}
