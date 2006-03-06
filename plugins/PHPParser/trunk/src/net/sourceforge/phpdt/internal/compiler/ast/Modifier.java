package net.sourceforge.phpdt.internal.compiler.ast;

import gatchan.phpparser.parser.PHPParser;
import gatchan.phpparser.parser.Token;

import java.util.List;

/**
 * @author Matthieu Casanova
 * @version $Id$
 */
public class Modifier extends AstNode
{
    public static final int FINAL = 1;
    public static final int STATIC = 2;
    public static final int PUBLIC = 4;
    public static final int PRIVATE = 8;
    public static final int PROTECTED = 16;
    public static final int CONST = 32;
    public static final int ABSTRACT = 64;

    private int kind;


    public Modifier(Token token)
    {
        super(token.sourceStart,
              token.sourceEnd,
              token.beginLine,
              token.endLine,
              token.beginColumn,
              token.endColumn);
        if ("final".equalsIgnoreCase(token.image))
            kind = FINAL;
        else if ("static".equalsIgnoreCase(token.image))
            kind = STATIC;
        else if ("public".equalsIgnoreCase(token.image))
            kind = PUBLIC;
        else if ("private".equalsIgnoreCase(token.image))
            kind = PRIVATE;
        else if ("protected".equalsIgnoreCase(token.image))
            kind = PROTECTED;
        else if ("const".equalsIgnoreCase(token.image))
            kind = CONST;
        else if ("abstract".equalsIgnoreCase(token.image))
            kind = ABSTRACT;
        else
            throw new IllegalArgumentException("Invalid modifier token " + token.image);
    }


    public String toString(int tab)
    {
        return tabString(tab) + toStringModifier(kind);
    }

    private static String toStringModifier(int kind)
    {
        switch (kind)
        {
            case FINAL :
                return "final";
            case STATIC :
                return "static";
            case PUBLIC :
                return "public";
            case PRIVATE :
                return "private";
            case PROTECTED :
                return "protected";
            case CONST :
                return "const";
            case ABSTRACT :
                return "abstract";
            default:
                throw new IllegalArgumentException("Invalid modifier kind " + kind);
        }
    }

    public void getOutsideVariable(List list)
    {
    }

    public void getModifiedVariable(List list)
    {
    }

    public void getUsedVariable(List list)
    {
    }

    public void analyzeCode(PHPParser parser)
    {
    }
}
