package com.illengineer.jcc;

/**
    Perhaps this isn't a 'real' tokenizer since it returns not the
    parts of the word, but rather only the first letter of the word
    parts, but the idea is the same.
*/
public interface Tokenizer
{
    public char[] splitIdentifer(String identifier);
}
