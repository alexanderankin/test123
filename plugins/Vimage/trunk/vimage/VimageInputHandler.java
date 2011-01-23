
/* 
Copyright (C) 2009 Matthew Gilbert 

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/


package vimage;

import java.awt.datatransfer.StringSelection;
import java.lang.StringBuffer;

import java.util.Vector;
import java.util.Collection;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.buffer.BufferAdapter;
import org.gjt.sp.jedit.gui.DefaultInputHandler;
import org.gjt.sp.jedit.gui.InputHandler;
import org.gjt.sp.jedit.gui.KeyEventTranslator;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.jedit.BeanShell;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.bsh.NameSpace;
import org.gjt.sp.jedit.bsh.BshMethod;
import org.gjt.sp.jedit.Registers;

class InvokeBufferListener extends BufferAdapter
{
    public boolean buffer_changed;
    
    public InvokeBufferListener()
    {
        buffer_changed = false;
    }
    
    public void contentInserted(JEditBuffer buffer, int startLine, int offset,
                                int numLines, int length)
    {
        //buffer_changed = true;
    }
    
    public void contentRemoved(JEditBuffer buffer, int startLine, int offset,
                               int numLines, int length)
    {
        //buffer_changed = true;
    }
    
    public void transactionComplete(JEditBuffer buffer)
    {
        buffer_changed = true;
    }
}

public class VimageInputHandler extends DefaultInputHandler
{
    protected VimageMap map;
    protected String mode;
    // Temporary mode that overrides the current mode. It doesn't replace, but
    // modifies the current mode (searched first). Cleared by clearState.
    protected String overlay_mode;
    // base_mode stores the base mode to return to after _exec.
    protected String base_mode;
    protected char register;
    // Cache the namespace used to run each mapped method.
    protected NameSpace namespace;
    protected VimageOperation current;
    protected Vector<VimageOperation> ops;
    // Store the last selection for imitating g-v in Vim.
    protected Selection last_selection;
    protected boolean is_recording;
    protected char record;
    protected VimageRecorder recorder;
    // Flag set when iterating through opts. Only set for iterations > 0. Mainly
    // used for letting vimage.cut append cuts when iterating.
    protected boolean iterating;

    // Static maps. 
    protected static KeyEventTranslator.Key escape = KeyEventTranslator.parseKey("ESCAPE");
    protected static KeyEventTranslator.Key action_bar = KeyEventTranslator.parseKey("C+ENTER");
    
    // "base" modes. These are the modes that should start all other ops.
    // This must be sorted, we use binarySearch below.
    protected static String[] base_modes = {"imap", "nmap", "vmap"};

    protected View view;
    protected InputHandler old_handler;

    public VimageInputHandler(View view_, VimageMap map_)
    {
        super(view_);
        this.view = view_;
        this.old_handler = view_.getInputHandler();
        this.map = map_;
        this.mode = "nmap";
        this.overlay_mode = null;
        this.base_mode = "nmap";
        this.register = '$';
        this.namespace = new NameSpace(BeanShell.getNameSpace(), "Vimage");
        this.current = new VimageOperation(this.mode);
        this.ops = new Vector<VimageOperation>();
        this.last_selection = null;
        this.is_recording = false;
        this.record = '$';
        this.recorder = new VimageRecorder();
        this.iterating = false;
        TextArea text_area = view.getTextArea();
        setBlockCaret(true);
        try {
            this.namespace.setVariable("mode", this);
        } catch (org.gjt.sp.jedit.bsh.UtilEvalError ex) {
            Log.log(Log.ERROR, this, ex);
        }
    }
    
    public void remove()
    {
        this.view.setInputHandler(this.old_handler);
    }
    
    public void runKeyInMode(String mode_, KeyEventTranslator.Key key)
    {
        BshMethod method = map.get(mode_, key);
        
        current.key = key;
        current.mode = mode;
        current.overlay_mode = overlay_mode;
        if (is_recording || mode.equals("imap"))
            recorder.add(record, current);
        
        if (method != null) {
            try {
                BeanShell.getNameSpace().setVariable("vimage_count", current.count);
                this.namespace.setVariable("count", current.count);
                BeanShell.getNameSpace().setVariable("vimage_key", key);
                this.namespace.setVariable("key", key);
                BeanShell.getNameSpace().setVariable("vimage_mode", this);
                BeanShell.runCachedBlock(method, this.view, this.namespace);
            } catch (java.lang.Exception ex) {
                Log.log(Log.DEBUG, this, ex);
            }
        }
        if (mode.equals("imap") || !key.modifiers.equals("")) {
            this.old_handler.handleKey(key, false);
        }
    }
    
    public boolean handleKey(KeyEventTranslator.Key key, boolean dry_run)
    {
        BshMethod method = null;
        if (dry_run) {
           if (overlay_mode != null)
               method = map.get(overlay_mode, key);
           if (method == null)
               method = map.get(mode, key);
           if (method == null && this.mode.endsWith("-key"))
               return true;
           if (method != null)
               return true;
           return false;
        }
        
        // Only record this info here, it can change as ops are executed (i.e.
        // -key modes will reset to base_mode by the time of invoke).
        current.key = key;
        current.mode = mode;
        current.overlay_mode = overlay_mode;
        if (is_recording || mode.equals("imap"))
            recorder.add(record, current);
        
        // Hard-coded mappings, escape and the action bar. This means they are
        // always accessible and can't be remapped.
        if (key.equals(escape)) {
            TextArea text_area = view.getTextArea();
            Selection selection = text_area.getSelectionAtOffset(text_area.getCaretPosition());
            text_area.selectNone();
            clearState();
            setMode("nmap");
            if (text_area.isOverwriteEnabled())
                text_area.setOverwriteEnabled(false);
            text_area.scrollTo(text_area.getCaretPosition(), true);
            return true;
        } else if (key.equals(action_bar)) {
            jEdit.getAction("action-bar").invoke(view);
            return true;
        }
        if (overlay_mode != null) {
            if (overlay_mode.endsWith("-key")) {
                method = map.get(overlay_mode, KeyEventTranslator.parseKey("*"));
            } else {
                method = map.get(overlay_mode, key);
            }
        }
        if (method == null) {
            method = map.get(mode, key);
        }
        if (method == null && mode.endsWith("-key")) {
            method = map.get(mode, KeyEventTranslator.parseKey("*"));
            // TODO: document why setMode here.
            setMode(this.base_mode);
        }
        
        if (method != null) {
            try {
                // TODO: explain why setting 2 namespaces?
                NameSpace ns = BeanShell.getNameSpace();
                ns.setVariable("vimage_count", current.count);
                this.namespace.setVariable("count", current.count);
                ns.setVariable("vimage_key", key);
                this.namespace.setVariable("key", key);
                ns.setVariable("vimage_mode", this);
                BeanShell.runCachedBlock(method, this.view, this.namespace);
            } catch (java.lang.Exception ex) {
                Log.log(Log.DEBUG, this, ex);
            }
            return true;
        }
        if (mode.equals("imap") || ((key.modifiers != null) && !key.modifiers.equals(""))) {
            return this.old_handler.handleKey(key, dry_run);
        }
        Log.log(Log.DEBUG, this, "Unknown map \"" + key.toString() + "\" for mode " + mode);
        clearState();
        return true;
    }
    
    protected void setBlockCaret(boolean block_caret)
    {
        EditPane[] editPanes = view.getEditPanes();
        for(int i=0; i < editPanes.length; i++) {
            TextArea text_area = editPanes[i].getTextArea();
            text_area.getPainter().setBlockCaretEnabled(block_caret);
            text_area.invalidateLine(text_area.getCaretLine());
        }
    }
    
    public void updateStatus()
    {
        view.getStatus().setMessage(this.mode + "(" + this.overlay_mode + ", " + 
                                    (is_recording ? "recording to " + record : "not recording") + "): " + current.count);
    }

    protected boolean is_base_mode(String mode)
    {
        if (java.util.Arrays.binarySearch(base_modes, mode) >= 0)
            return true;
        return false;
    }

    public String getMode()
    {
        return this.mode;
    }

    public void setMode(String mode)
    {
        if (map.getMode(mode) == null) {
            Log.log(Log.ERROR, this, "Invalid mode \"" + mode + "\"; ignoring");
            return;
        }
        if (is_base_mode(mode)) {
            if (!this.base_mode.equals(mode)) {
                TextArea text_area = view.getTextArea();
                // Switching base modes invalidates any selections.
                Selection sel = text_area.getSelectionAtOffset(text_area.getCaretPosition());
                if (sel != null) {
                    text_area.setCaretPosition(sel.getStart());
                }
                view.getTextArea().selectNone();
                this.base_mode = mode;
            }
            boolean block_caret = !mode.equals("imap");
            setBlockCaret(block_caret);
        }
        this.mode = mode;
        updateStatus();
    }

    public void clearState()
    {
        this.overlay_mode = null;
        this.register = '$';
        if (!is_recording)
            this.record = '$';
        this.ops.clear();
        
        this.current.exec = null;
        this.current.count = 0;
        this.current.overlay_mode = null;
        //current.key = null;
        
        this.iterating = false;
        this.setMode(this.base_mode);
        if (view.getTextArea().isOverwriteEnabled())
            view.getTextArea().setOverwriteEnabled(false);
    }

    protected boolean _exec(int index)
    {
        TextArea text_area = view.getTextArea();
        int pos = text_area.getCaretPosition();
        if (index >= ops.size())
            return true;
        VimageOperation op = ops.get(index);
        //Log.log(Log.DEBUG, this, "Invoking " + op + " at index " + index);
        EditAction ea = jEdit.getAction(op.exec);
        if (ea == null) {
            Log.log(Log.DEBUG, this, "Unknown action \"" + op.exec + "\"");
            return false;
        }
        // Total HACK. FIXME: There's got to be a better way.
        if (op.exec.equals("vimage.cut")) {
            view.getBuffer().beginCompoundEdit();
        }
        for (int i = 0; i < java.lang.Math.max(1, op.count); ++i) {
            // iterating is a hack for vimage.cut. Since we're iteratively
            // deleting, need some way to tell the action to append to the
            // register. All other edit ops can simply replace since the
            // selection is extended.
            if (i > 0)
                this.iterating = true;
            if (_exec(index + 1) == false)
                return false;
            ea.invoke(view);
            if (i > 0)
                this.iterating = false;

            // Record a selection if it exists.
            Selection tmp_selection = view.getTextArea().getSelectionAtOffset(view.getTextArea().getCaretPosition());
            if (tmp_selection != null)
                last_selection = tmp_selection;
        }
        if (op.exec.equals("vimage.cut")) {
            view.getBuffer().endCompoundEdit();
        }
        // Total HACK. FIXME: There's got to be a better way.
        if (op.exec.equals("vimage.copy")) {
            Selection sel = text_area.getSelectionAtOffset(text_area.getCaretPosition());
            if (sel != null)
                pos = sel.getStart();
            text_area.selectNone();
            text_area.setCaretPosition(pos);
        }
        return true;
    }
    
    protected boolean _exec()
    {
        return _exec(0);
    }

    public boolean invoke(String s)
    {
        TextArea text_area = view.getTextArea();
        JEditBuffer buffer = view.getBuffer();
        String reg_text = null;
        char orig_reg = this.register;
        // Save existing register text if selected register is upper case
        // (append mode). Like Vim, upper case registers should map to appending
        // to a lower case name.
        if (java.lang.Character.isUpperCase(this.register)) {
            orig_reg = this.register = java.lang.Character.toLowerCase(this.register);
            Registers.Register reg = Registers.getRegister(this.register);
            if (reg != null) {
                reg_text = reg.toString();
            }
        }
        if (current.exec != null) {
            Log.log(Log.ERROR, this, "setting new exec on already set op \"" + current.exec + "\" with \"" + s + "\"");
        }
        
        int pos = text_area.getCaretPosition();
        
        boolean buffer_changed = false;
        // Watch for buffer changes to record edits.
        InvokeBufferListener listener = new InvokeBufferListener();
        view.getBuffer().addBufferListener(listener);
        
        current.exec = s;
        ops.add(new VimageOperation(current));
        boolean ret;
        try {
            ret = _exec();
            // Append the new register text to the saved register text if in append mode
            if ((reg_text != null) && (this.register == orig_reg)) {
                Registers.Register reg = Registers.getRegister(this.register);
                reg.setTransferable(new StringSelection(reg_text + reg.toString()));
            }
            buffer.removeBufferListener(listener);
            // TODO: Clean this up
            if (listener.buffer_changed && !s.equals("undo") && 
                !s.equals("vimage.replay-default") && !s.equals("vimage.replay-record") &&
                !is_recording)
            {
                recorder.clear(record);
                for (VimageOperation op : ops) {
                    recorder.add(record, new VimageOperation(op));
                }
            }
        } finally {
            clearState();
        }
        // ensure the new caret location is visible (only if the same buffer is
        // loaded).
        if ((pos != text_area.getCaretPosition()) && (buffer == view.getBuffer()))
            text_area.scrollToCaret(true);
        return ret;
    }

    public void invoke(String s, String next_mode)
    {
        if (current.exec != null) {
            Log.log(Log.ERROR, this, "setting new exec on already set op \"" + current.exec + "\" with \"" + s + "\"");
        }
        current.exec = s;
        current.mode = this.mode;
        ops.add(new VimageOperation(current));
        setMode(next_mode);
        current = new VimageOperation(this.mode);
    }

    public void pushMode(String mode)
    {
        overlay_mode = mode;
        updateStatus();
    }

    public void addDigit(int d)
    {
        setCount(getCount() * 10 + d);
    }

    public Selection getLastSelection()
    {
        return this.last_selection;
    }

    public int getCount()
    {
        return this.current.count;
    }

    public void setCount(int count)
    {
        this.current.count = count;
        updateStatus();
    }

    public VimageMap getMap()
    {
        return this.map;
    }

    public void setMap(VimageMap map_)
    {
        this.map = map_;
    }

    public char getRegister()
    {
        return this.register;
    }

    public void setRegister(char reg_name)
    {
        this.register = reg_name;
    }
    
    public boolean isIterating()
    {
        return this.iterating;
    }
    
    public void startRecording(char c)
    {
        recorder.clear(c);
        this.is_recording = true;
        this.record = c;
        updateStatus();
    }
    
    public void stopRecording()
    {
        this.is_recording = false;
        this.record = '$';
        updateStatus();
    }
    
    public boolean isRecording()
    {
        return this.is_recording;
    }
    
    public void playRecording(char c)
    {
        if (this.is_recording)
            return;
        
        Collection<VimageOperation> col = recorder.get(c);
        if (col == null)
            return;
        
        recorder.setPlayback(true);
        for (VimageOperation op : col) {
            this.setCount(op.count);
            this.setMode(op.mode);
            this.overlay_mode = op.overlay_mode;
            handleKey(op.key, false);
        }
        recorder.setPlayback(false);
        
        // Save playback op as '$'.
        recorder.put('$', col);
    }
    
    public void pasteRecording(char c)
    {
        JEditBuffer b = view.getBuffer();
        StringBuffer buf = new StringBuffer();
        Collection<VimageOperation> col = recorder.get(c);
        if (col == null) {
            return;
        }
        
        for (VimageOperation op : col) {
            if (op.count > 0)
                buf.append(new Integer(op.count).toString());
            buf.append(op.key.input);
        }
        b.insert(view.getTextArea().getCaretPosition(), buf.toString());
    }
}

