package com.illcode.jedit.inputreplace;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.bsh.EvalError;
import org.gjt.sp.jedit.bsh.Interpreter;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.util.Log;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

public final class InputReplacePlugin extends EditPlugin
{
    static int maxLength = 8;

    private static Interpreter intr;
    private static Path functionsPath, tablePath;
    private static HashMap<String,String> replacementMap;

    public static void inputReplace() {
        JEditTextArea textArea = jEdit.getActiveView().getTextArea();
        int caretPos = textArea.getCaretPosition();
        int caretLine = textArea.getCaretLine();
        int caretCol = caretPos - textArea.getLineStartOffset(caretLine);
        int compositionLength = Math.min(maxLength, caretCol);
        if (compositionLength < 2)
            return;
        String line = textArea.getLineText(caretLine);
        for (int len = compositionLength; len >= 2; len--) {
            String token = line.substring(caretCol - len, caretCol);
            String replacement = replacementMap.get(token);
            if (replacement != null) {
                if (replacement.length() >= 7 && replacement.endsWith("FUNC")) {  // It's a function, boi!
                    char openDelimeter = replacement.charAt(0),
                        closeDelimeter = replacement.charAt(replacement.length() - 5);  // character right before "FUNC"
                    int col = caretCol - token.length() - 2;  // start right before the closing delimeter
                    int endCol = col + 1;
                    int startCol = -1;  // this is what we're searching for
                    int delimeterCount = 1;
                    while (delimeterCount > 0) {  // we scan backward until the delimeters are balanced
                        if (col < 0) return;  // the function invocation is malformed
                        if (line.charAt(col) == openDelimeter)
                            delimeterCount--;
                        else if (line.charAt(col) == closeDelimeter)
                            delimeterCount++;

                        if (delimeterCount == 0)
                            startCol = col + 1;
                        else if (--col < 0)  // The delimeters did not match up; this is a malformed function call.
                            return;  // so just do nothing
                    }
                    // If we reached this spot, the delimeters balanced
                    String parameter = line.substring(startCol, endCol);
                    String funcName = replacement.substring(1, replacement.length() - 5);
                    len = parameter.length() + token.length() + 2;  // how many characters to delete before the caret
                    try {
                        intr.set("parameter", parameter);
                        replacement = (String) intr.eval(funcName + "(parameter)");
                    } catch (EvalError e) {
                        Log.log(Log.WARNING, InputReplacePlugin.class, "InputReplacePlugin BeanShell eval error", e);
                    }
                } // End if it's a FUNC
                textArea.setSelection(new Selection.Range(caretPos - len, caretPos));
                textArea.setSelectedText(replacement);
                break;
            }
        }
    }

    public static void reloadResources() {
        // Source the BeanShell functions
        intr.getNameSpace().clear();
        try {
            intr.source(functionsPath.toString());
        } catch (Exception e) {
            Log.log(Log.WARNING, InputReplacePlugin.class, "InputReplacePlugin BeanShell source error", e);
        }

        // And then load the replacement table
        replacementMap.clear();
        try {
            List<String> lines = Files.readAllLines(tablePath, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.length() < 4 || line.startsWith("# ") || line.matches("\\s+"))
                    continue;
                line = line.trim();
                String[] pieces = line.split("\\s+", 2);
                if (pieces.length != 2) continue;   // This is a malformed line, ignore it.

                // The various types of entries we can find are:
                if (pieces[1].matches("U\\+(?:10)?\\p{XDigit}{4}"))  {
                    // A Unicode code point written in hex
                    pieces[1] = new String(Character.toChars(Integer.parseInt(pieces[1].substring(2), 16)));
                } else if (pieces[1].length() >= 7 && pieces[1].endsWith("FUNC")) {
                    // This is valid (x)FUNC syntax.
                    // We'll stash it as a normal substitution for now, and let inputReplace() deal with the nitty gritty.
                    replacementMap.put(pieces[0], pieces[1]);
                } else {  // The default, a normal textual substitution.
                    replacementMap.put(pieces[0], pieces[1]);
                }
            }
        } catch (IOException e) {
            Log.log(Log.WARNING, InputReplacePlugin.class, "InputReplacePlugin table load error", e);
        }
    }

    public static void editTable() {
        jEdit.openFile(jEdit.getActiveView(), tablePath.toString());
    }

    public static void editUserFunctions() {
        jEdit.openFile(jEdit.getActiveView(), functionsPath.toString());
    }

    public void start() {
        intr = new Interpreter();
        replacementMap = new HashMap<>(1000);
        maxLength = jEdit.getIntegerProperty("inputreplace.max-length", maxLength);
        ensureResourcesPresent();
        reloadResources();
    }

    private void ensureResourcesPresent() {
        Path pluginHome = getPluginHome().toPath();
        functionsPath = pluginHome.resolve("input-replace-functions.bsh");
        tablePath = pluginHome.resolve("input-replace-table.txt");
        try {
            if (!Files.isDirectory(pluginHome))
                Files.createDirectories(pluginHome);
            if (!Files.exists(functionsPath)) {
                String defaultFunctions = Utils.getStringResource("/resources/input-replace-functions-default.bsh");
                Files.write(functionsPath, defaultFunctions.getBytes(StandardCharsets.UTF_8));
            }
            if (!Files.exists(tablePath)) {
                String defaultTable = Utils.getStringResource("/resources/input-replace-table-default.txt");
                Files.write(tablePath, defaultTable.getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            Log.log(Log.WARNING, InputReplacePlugin.class, "InputReplacePlugin initialization error", e);
        }
    }
}
