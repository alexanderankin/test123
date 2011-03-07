/*
This file, unless otherwise noted, is wholly the work of Dale Anson,
danson@grafidog.com. The complete contents of this file is hereby 
released into the public domain, with no rights reserved. For questions, 
concerns, or comments, please email the author.
*/

package ise.calculator;

import java.util.ArrayList;

/**
 * Represents a mathematical operation.
 * @author Dale Anson, danson@grafidog.com
 */
public class Op {
    // datatype for the result of this operation
    private String datatype = null;

    // storage for the numbers to execute the operation on
    ArrayList<Num> nums = new ArrayList<Num>();

    // storage for nested Ops
    ArrayList<Op> ops = new ArrayList<Op>();

    // storage for operation
    String operation = null;

    // should the StrictMath library be used?
    private boolean _strict = false;

    public Op() {
    }

    public Op(String op) {
        setOp(op);
    }

    public Op(String op, String type) {
        setOp(op);
        setDatatype(type);
    }

    /**
     * Set the operation.
     */
    public void setOp(String op) {
        if (op.equals("+")) {
            operation = "add";
        }
        else if (op.equals("-")) {
            operation = "subtract";
        }
        else if (op.equals("*") || op.equals("x")) {
            operation = "multiply";
        }
        // || op.equals( "" ) )
        else if (op.equals("/")) {
            operation = "divide";
        }
        else if (op.equals("%") || op.equals("\\")) {
            operation = "mod";
        }
        else {
            operation = op;
        }
    }

    /**
     * Add a number to this operation. An operation can hold any number of
     * numbers to support formulas like 5 + 4 + 3 + 2 + 1.
     * @param num a number to use in this operation
     */
    public void addNum(Num num) {
        nums.add(num);
    }

    /**
     * Sets the datatype of this calculation. Allowed values are
     * "int", "long", "float", or "double".
     */
    public void setDatatype(String p) {
        if (p.equals("int") || p.equals("long") || p.equals("float") || p.equals("double") || p.equals("bigint") || p.equals("bigdecimal")) {
            datatype = p;
        }
        else {
            throw new IllegalArgumentException("Invalid datatype: " + p + ". Must be one of int, long, float, double, bigint, or bigdouble.");
        }
    }

    /**
     * Add a nested operation.
     * @param the operation to add.
     */
    public void addConfiguredOp(Op op) {
        if (datatype != null) {
            op.setDatatype(datatype);
        }
        ops.add(op);
    }

    /**
     * Use the StrictMath library.
     */
    public void setStrict(boolean b) {
        _strict = b;
    }

    /**
     * Perform this operation.
     * @return the value resulting from the calculation as a Num.
     */
    public Num calculate() {
        if (operation == null) {
            throw new ArithmeticException("Operation not specified.");
        }

        // calculate nested Ops
        for (Op op : ops) {
            if (datatype != null) {
                op.setDatatype(datatype);
            }
            nums.add(op.calculate());
        }

        // make an array of operands
        String[] operands = new String[nums.size()];
        for (int i = 0; i < nums.size(); i++) {
            Num num = nums.get(i);
            if (datatype != null) {
                num.setDatatype(datatype);
            }
            operands[i] = num.toString();
        }

        Math math = new Math(_strict);

        Number number = null;
        number = math.calculate(operation, datatype, operands);
        if (number == null) {
            throw new ArithmeticException("Math error, no result.");
        }
        Num num = new Num();
        num.setValue(number.toString());
        num.setDatatype(datatype);
        return num;

    }
}