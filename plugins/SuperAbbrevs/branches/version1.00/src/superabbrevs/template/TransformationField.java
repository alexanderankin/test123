package superabbrevs.template;

import superabbrevs.Paths;

import bsh.*;
import java.io.*;
import superabbrevs.utilities.Log;

/**
 * @author Sune Simonsen
 * class TransformationField
 * a template field that can transform the field value with abitrarily code
 */
public class TransformationField implements Field {

    private VariableField field;
    private String code;
    private Interpreter interpreter;
    private String lastEvaluated;
    private String lastResult;

    /*
     * Constructor for TransformationField
     */
    public TransformationField(VariableField field, String code, Interpreter interpreter) {
        this.field = field;
        this.code = code;
        this.interpreter = interpreter;

        try {
            interpreter.source(Paths.ABBREVS_FUNCTION_PATH);
        } catch (TargetError e) {
            // The script threw an exception
            Log.log(Log.Level.ERROR, TransformationField.class, e);
        } catch (ParseException e) {
            // Parsing error
            Log.log(Log.Level.ERROR, TransformationField.class, e);
        } catch (EvalError e) {
            // General Error evaluating script
            Log.log(Log.Level.ERROR, TransformationField.class, e);
        } catch (FileNotFoundException e) {
            // File not found
            Log.log(Log.Level.ERROR, TransformationField.class, e);
        } catch (IOException e) {
            // Input output error
            Log.log(Log.Level.ERROR, TransformationField.class, e);
        }
    }

    @Override
    public String toString() {
        String s = field.toString();

        try {
            if (!s.equals(lastEvaluated)) {
                interpreter.set("s", s);

                lastResult = (String) interpreter.eval(code);
                lastEvaluated = s;
            }
        } catch (TargetError e) {
            // The script threw an exception
            Log.log(Log.Level.ERROR, TransformationField.class, e);
            lastResult = "<target error>";
            lastEvaluated = "<target error>";
        } catch (ParseException e) {
            // Parsing error
            Log.log(Log.Level.ERROR, TransformationField.class, e);
            lastResult = "<pasing error>";
            lastEvaluated = "<pasing error>";
        } catch (EvalError e) {
            // General Error evaluating script
            Log.log(Log.Level.ERROR, TransformationField.class, e);
            lastResult = "<eval error>";
            lastEvaluated = "<eval error>";
        }

        return lastResult;
    }

    public int getLength() {
        String s = field.toString();
        if (!s.equals(lastEvaluated)) {
            toString();
        }

        return lastResult.length();
    }

    public String firstUp(String s) {
        StringBuffer res = new StringBuffer(s);
        if (0 < res.length()) {
            char first = res.charAt(0);
            res.setCharAt(0, Character.toUpperCase(first));
        }
        return res.toString();
    }
}
