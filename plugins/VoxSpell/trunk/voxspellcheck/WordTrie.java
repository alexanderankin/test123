
/*
Copyright (C) 2008 Matthew Gilbert

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

package voxspellcheck;


import java.util.ArrayList;
import java.util.Stack;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class WordTrie implements SpellCheck
{
    private static class WordChar implements Comparable
    {
        public Character c;
        public Node next;
        
        public WordChar(Character c_, Node n_)
        {
            c = c_;
            next = n_;
        }
        
        public WordChar(Character c_)
        {
            c = c_;
            next = new Node();
        }
        
        public int compareTo(Object obj)
        {
            if (obj instanceof WordChar) {
                WordChar wc = (WordChar)obj;
                return c.compareTo(wc.c);
            } else if (obj instanceof Character) {
                Character other_c = (Character)obj;
                return c.compareTo(other_c);
            } else {
                throw new java.lang.IllegalArgumentException();
            }
        }
        
        public boolean equals(Object obj)
        {
            if (obj instanceof WordChar) {
                WordChar wc = (WordChar)obj;
                return c.equals(wc.c);
            } else if (obj instanceof Character) {
                Character other_c = (Character)obj;
                return c.equals(other_c);
            } else {
                throw new java.lang.IllegalArgumentException();
            }
        }
    }
    
    private static class Node
    {
        public short length;
        public WordChar[] chars;
        
        public Node()
        {
            length = 0;
            chars = new WordChar[0];
        }
        
        public boolean contains(Character c)
        {
            for (WordChar wc : chars) {
                if (wc.equals(c))
                    return true;
                if (wc.compareTo(c) > 0)
                    break;
            }
            return false;
        }
        
        public void set(Character[] array)
        {
            length = (short)array.length;
            chars = new WordChar[array.length];
            for (int i = 0; i < length; i++) {
                if (array[i].equals(Character.MIN_VALUE)) {
                    chars[i] = new WordChar(array[i], null);
                } else {
                    chars[i] = new WordChar(array[i]);
                }
            }
        }
        
        public void add(WordChar wc)
        {
            WordChar[] new_chars = new WordChar[chars.length + 1];
            int i;
            for (i = 0; i < chars.length; i++) {
                new_chars[i] = chars[i];
            }
            new_chars[i] = wc;
            java.util.Arrays.sort(new_chars);
            chars = new_chars;
            length++;
        }
        
        public WordChar get(Character c)
        {
            for (WordChar wc : chars) {
                if (wc.equals(c))
                    return wc;
                if (wc.compareTo(c) > 0)
                    break;
            }
            return null;
        }
        
        public boolean remove(Character c)
        {
            if (!contains(c))
                return false;
            
            --length;
            WordChar[] new_chars = new WordChar[length];
            int i = 0;
            for (WordChar wc : chars) {
                if (wc.equals(c))
                    continue;
                new_chars[i++] = wc;
            }
            chars = new_chars;
            return true;
        }
        
        public int length()
        {
            return chars.length;
        }
        
        public String toString()
        {
            StringBuffer buf = new StringBuffer();
            buf.append(length);
            buf.append(" - ");
            for (WordChar wc : chars) {
                buf.append(wc.c).append(' ');
            }
            return buf.toString();
        }
    }
    
    public Node root;
    
    public WordTrie()
    {
        root = new Node();
    }
    
    public boolean add(Node node, String word)
    {
        if (word.length() == 0) {
            if (node.contains(Character.MIN_VALUE)) {
                return false;
            }
            node.add(new WordChar(Character.MIN_VALUE, null));
            return true;
        }
        
        Character cur = word.charAt(0);
        if (!node.contains(cur)) {
            node.add(new WordChar(cur));
        }
        return add(node.get(cur).next, word.substring(1));
    }
    
    public void addWord(String word)
    {
        String trimmed = word.trim();
        add(root, trimmed);
    }
    
    public void addWordList(BufferedReader input)
    {
        while (true) {
            String line;
            try {
                line = input.readLine();
            } catch (java.io.IOException ex) {
                break;
            }
            if (line == null) {
                break;
            }
            
            String trimmed = line.trim();
            if (trimmed.length() == 0)
                continue;
            add(root, trimmed);
        }
    }
    
    public boolean find(Node node, String word)
    {
        if (word.length() == 0) {
            if (node.contains(Character.MIN_VALUE))
                return true;
            return false;
        }
        
        Character cur = word.charAt(0);
        if (!node.contains(cur)) {
            return false;
        }
        return find(node.get(cur).next, word.substring(1));
    }
    
    public boolean find(String word)
    {
        return find(root, word);
    }
    
    // Returns the node.next of the last character of word; or null if any
    // character in word isn't found.
    protected Node findNode(Node node, String word)
    {
        if (word.length() == 0)
            return node;
        
        Character cur = word.charAt(0);
        if (!node.contains(cur)) {
            return null;
        }
        return findNode(node.get(cur).next, word.substring(1));
    }
    
    public void write(DataOutputStream writer, Node node) throws java.io.IOException
    {
        writer.writeShort(node.length);
        for (WordChar wc : node.chars)
            writer.writeChar(wc.c);
        for (WordChar wc : node.chars) {
            if (!wc.c.equals(Character.MIN_VALUE))
                write(writer, wc.next);
        }
    }
    
    public void write(DataOutputStream writer) throws java.io.IOException
    {
        write(writer, root);
    }
    
    public void read(DataInputStream reader, Node node) throws java.io.IOException
    {
        int length;
        try {
            length = reader.readShort();
        } catch (java.io.EOFException ex) {
            return;
        }
        
        Character[] array = new Character[length];
        for (int i = 0; i < length; i++) {
            try {
                array[i] = reader.readChar();
            } catch (java.io.EOFException ex) {
                return;
            }
        }
        node.set(array);
        for (WordChar wc : node.chars) {
            if (wc.c != Character.MIN_VALUE)
                read(reader, wc.next);
        }
    }
    
    public void read(DataInputStream reader) throws java.io.IOException
    {
        read(reader, root);
    }
   
    // TODO: remove, not used
    private int getNodeCount(Node node)     // NOPMD
    {
        int count = 0;
        for (WordChar wc : node.chars) {
            if (!wc.c.equals(Character.MIN_VALUE)) {
                count += getNodeCount(wc.next);
            }
        }
        // Finally, include this node and return.
        return count++;
    }
    
    
    public void write_bf(DataOutputStream writer) throws java.io.IOException
    {
        ArrayList<Node> cur = null;
        ArrayList<Node> next = new ArrayList<Node>();
        next.add(root);
        
        do {
            cur = next;
            next = new ArrayList<Node>();
            for (Node node : cur) {
                writer.writeShort(node.length);
                for (WordChar wc : node.chars) {
                    writer.writeChar(wc.c);
                    if (!wc.c.equals(Character.MIN_VALUE))
                        next.add(wc.next);
                }
            }
        } while (next.size() > 0);
    }
    
    public void read_bf(DataInputStream reader) throws java.io.IOException
    {
        ArrayList<Node> cur = null;
        ArrayList<Node> next = new ArrayList<Node>();
        next.add(root);
        
        do {
            cur = next;
            next = new ArrayList<Node>();
            for (Node node : cur) {
                int length = reader.readShort();
                WordChar[] chars = new WordChar[length];
                node.set(new Character[0]);
                for (int i = 0; i < length; ++i) {
                    WordChar wc = new WordChar(reader.readChar());
                    chars[i] = wc;
                    node.add(wc);
                    if (!wc.c.equals(Character.MIN_VALUE))
                        next.add(wc.next);
                }
            }
        } while (next.size() > 0);
    }
    
    protected int bloom(String s)
    {
        int res = 0;
        for (Character c : s.toCharArray()) {
            int i = Character.getNumericValue(c);
            res |= (1 << (i & 0x1f));
        }
        return res;
    }
   
    protected void getWords(ArrayList<String> vec, 
                            Stack<Character> stack, 
                            Node node,
                            int filter)
    {
        for (WordChar wc : node.chars) {
            if (wc.c.equals(Character.MIN_VALUE)) {
                char[] chars = new char[stack.size()];
                for (int i = 0; i < stack.size(); ++i) {
                    chars[i] = stack.get(i);
                }
                String s = new String(chars);
                if (((filter ^ bloom(s)) & filter) == 0)
                    vec.add(s);
            } else {
                stack.push(wc.c);
                getWords(vec, stack, wc.next, filter);
                stack.pop();
            }
        }
    }
    
    public ArrayList<String> getWords()
    {
        ArrayList<String> vec = new ArrayList<String>();
        Stack<Character> stack = new Stack<Character>();
        getWords(vec, stack, root, 0);
        return vec;
    }
    
    // FIXME: This is a hacked interface to support the bloom filter.
    public ArrayList<String> getWords(String prefix)
    {
        ArrayList<String> vec = new ArrayList<String>();
        
        if (prefix.length() == 0)
            return vec;
        
        Stack<Character> stack = new Stack<Character>();
        stack.push(prefix.charAt(0));
        //for (Character c : prefix.substring(0, 1).toCharArray())
        //   stack.push(c);
        Node node = findNode(root, prefix.substring(0, 1));
        if (node != null) {
            getWords(vec, stack, node, bloom(prefix));
        }
        return vec;
    }
    
    public boolean removeWord(String word)
    {
        if (!find(word))
            return false;
        
        // First remove MIN_VALUE WordChar from final node. Then
        // loop through removing WordChar's from nodes if the next
        // pointer points to a node with no WordChars.
        
        // findNode will return the node with MIN_VALUE in it for word.
        Node node = findNode(root, word);
        node.remove(Character.MIN_VALUE);
        
        Stack<Node> stack = new Stack<Node>();
        // word has to have at least 1 letter, so push root. findNode always
        // returns node.next of the last character, so root can never be
        // returned.
        stack.push(root);
        // Unconventional loop: i is the last param to substring, so 1 past the
        // char we want. The last iteration of i will return the node of the
        // last letter in word, even though substring doesn't contain the last
        // letter. FIXME: fix findNode.
        for (int i = 1; i < word.length(); ++i)
            stack.push(findNode(root, word.substring(0, i)));
        
        int i = word.length() - 1;
        while (!stack.empty()) {
            node = stack.pop();
            WordChar wc = node.get(word.charAt(i--));
            if ((wc.next != null) && (wc.next.length() == 0))
                node.remove(wc.c);
        }
        
        return true;
    }
    
}



