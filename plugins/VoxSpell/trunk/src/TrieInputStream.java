
/* Copyright (C) 2008 Matthew Gilbert */

package voxspellcheck;

import java.io.ByteArrayInputStream;

public class TrieInputStream extends ByteArrayInputStream
{
    public TrieInputStream(byte[] buf)
    {
        super(buf);
    }
    
    public int getPos()
    {
        return this.pos;
    }
    
    public void setPos(int pos_)
    {
        this.pos = pos_;
    }
}
