
package voxspellcheck;

import java.io.DataInput;
import java.io.IOException;

import java.util.Stack;

import static voxspellcheck.VimSpell.*;

public class VimWordTrie
{
    protected byte bytes[];
    protected int indices[];
    
    protected int read_node(DataInput input,
                            int start_index,
                            boolean is_prefixtree,
                            int max_pref_cond_number) throws IOException
    {
        int index = start_index;
        int sibling_count = Utility.convert_msb(input, 1);
        //System.out.println("sibling_count "+sibling_count+" @ "+input.getFilePointer());
        if (sibling_count < 0) {
            System.out.println("ERROR: word file truncation "+sibling_count);
            return -1;
        }
        
        if (index + sibling_count >= bytes.length) {
            System.out.println("ERROR: word file truncation "+sibling_count);
            return -1;
        }
        
        bytes[index++] = (byte)sibling_count;
        
        int value;
        int prefcond_num;
        for (int i = 0; i < sibling_count; ++i) {
            value = Utility.convert_msb(input, 1);
            
            // If value isn't special record it and keep reading.
            if (value > BY_SPECIAL) {
                bytes[index++] = (byte)value;
                continue;
            }
            
            switch (value) {
            case BY_NOFLAGS:
                if (!is_prefixtree) {
                    indices[index] = 0;
                    value = 0;
                }
                break;
            case BY_INDEX:
                int node_index = Utility.convert_msb(input, 3);
                indices[index] = SHARED_MASK + node_index;
                value = Utility.convert_msb(input, 1);
                break;
            default:
                if (is_prefixtree) {
                    if (value == BY_FLAGS) {
                        // <pflags>
                        value = Utility.convert_msb(input, 1) << 24;
                    } else {
                        value = 0;
                    }
                    // <affixID>
                    value |= Utility.convert_msb(input, 1);
                    prefcond_num = Utility.convert_msb(input, 2);
                    value |= (prefcond_num << 8);
                } else {
                    // value must be BY_FLAGS or BY_FLAGS2
                    int flags = value;
                    value = Utility.convert_msb(input, 1);
                    if (flags == BY_FLAGS2) {
                        value = (Utility.convert_msb(input, 1) << 8) + value;
                    }
                    if ((value & WF_REGION) == WF_REGION) {
                        value = (Utility.convert_msb(input, 1) << 16) + value;
                    }
                    if ((value & WF_AFX) == WF_AFX) {
                        value = (Utility.convert_msb(input, 1) << 24) + value;
                    }
                }
                indices[index] = value;
                value = 0;
                break;
            }
            bytes[index++] = (byte)value;
        }
        
        // Start from 1 because the first byte is the sibling_count.
        for (int i = 1; i <= sibling_count; ++i) {
            if (bytes[start_index + i] != 0) {
                if ((indices[start_index + i] & SHARED_MASK) == SHARED_MASK) {
                    indices[start_index + i] &= ~SHARED_MASK;
                } else {
                    indices[start_index + i] = index;
                    index = read_node(input, 
                                      index, 
                                      is_prefixtree,
                                      max_pref_cond_number);
                    if (index < 0)
                        break;
                }
            }
        }
        return index;
    }
    
    protected int get_cap_type(String s)
    {
        int num_cap = 0;
        int num_lower = 0;
        boolean first_cap = Character.isUpperCase(s.charAt(0));
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (Character.isUpperCase(c))
                ++num_cap;
            if (Character.isLowerCase(c))
                ++num_lower;
        }
        if (num_cap == s.length())
            return WF_ALLCAP;
        else if (first_cap && (num_cap > 1))
            return WF_KEEPCAP;
        else if (first_cap)
            return WF_ONECAP;
        return 0;
    }
    
    public VimWordTrie(DataInput input, boolean is_prefixtree) throws IOException
    {
        int num_nodes = Utility.convert_msb(input, 4);
        if (num_nodes < 0) {
            throw new RuntimeException(("ERROR: word file truncation "+num_nodes));
        } else if (num_nodes == 0) {
            return;
        }
        bytes = new byte[num_nodes];
        indices = new int[num_nodes];
        read_node(input, 0, is_prefixtree, 0);
    }
    
    public boolean find(String search_string, byte search_bytes[], int word_offset)
    {
        Stack<Integer> word_index_stack = new Stack<Integer>();
        Stack<Integer> index_stack = new Stack<Integer>();
        int index = 0;
        int word_index = word_offset;
        while (true) {
            int len = bytes[index++];
            //System.out.println("len "+len);
            byte siblings[] = new byte[len];
            System.arraycopy(bytes, index, siblings, 0, len);
            //System.out.println("sibblings "+(new String(siblings)));
            //System.out.println("word_index "+word_index);
            
            if (bytes[index] == 0) {
                word_index_stack.push(word_index);
                index_stack.push(index++);
                while (len > 0 && bytes[index] == 0) {
                    ++index;
                    --len;
                }
                if (len == 0)
                    break;
            }
            
            // Looking for end-of-word now.
            if (search_bytes.length == word_index)
                break;
                    
            byte b = search_bytes[word_index++];
            // Any whitespace is treated like a space.
            if (Character.isWhitespace((char)b))
                b = (byte)' ';
            
            // FIXME: make this a binary search.
            for (int i = 0; i < len; ++i) {
                if (bytes[index+i] == b) {
                    index = index + i;
                    break;
                }
            }
            //System.out.println("Looking for '"+(char)b+"' found '"+(char)bytes[index]+"'");
            if (bytes[index] != b)
                return false;
            index = indices[index];
            // TODO: add additional space handling.
        }
        
        boolean ret = false;
        int orig_index = index;
        while (!index_stack.empty()) {
            word_index = word_index_stack.pop();
            index = index_stack.pop();
            
            //System.out.println("looking for compound @ "+word_index+":"+index);
            
            boolean word_end = false;
            if (word_index == search_bytes.length)
                word_end = true;
            
            for (int len = bytes[index - 1]; 
                 (len > 0) && (bytes[index] == 0); 
                 --len, ++index) 
            {
                int flags = indices[index];
                
                // If this is the compound, we're done.
                if (word_end &&
                    (word_offset != 0) && 
                    ((flags & WF_BANNED) == 0))
                {
                    ret = true;
                    break;
                } else if (word_offset != 0 && !word_end) {
                    // TODO: Add check for too short compound words.
                    // TODO: Add check for number of compound words
                    // > COMPOUNDWORDMAX
                    
                    // TODO: Document what this is.
                    if (!word_end && ((flags & WF_NOCOMPAFT) == 0))
                        continue;
                    
                    // TODO: Add CHECKCOMPOUNDPATTERN
                    
                    int cap_flags = get_cap_type(search_string);
                    if (word_offset != 0) {
                        if (cap_flags == WF_KEEPCAP ||
                            (cap_flags == WF_ALLCAP && 
                             ((flags & WF_FIXCAP) == WF_FIXCAP)))
                        {
                            continue;
                        }
                    }
                } else if ((flags & WF_NEEDCOMP) == WF_NEEDCOMP) {
                    continue;
                }
                
                if (word_end && (flags == 0)) {
                    ret = true;
                }
                
                /* Someday support compounding
                else if (!word_end) {
                    if (find(search_string, search_bytes, word_index)) {
                        ret = true;
                    }
                }
                */
                
                // TODO: Check for WF_KEEPCAP
                // TODO: check region.
                
                if ((flags & WF_BANNED) == WF_BANNED) {
                    System.out.println("WF_BANNED");
                    return false;
                }
                if ((flags & WF_ONECAP) == WF_ONECAP) {
                    if (Character.isUpperCase(search_string.charAt(0))) {
                        ret = true;
                    }
                }
                
                if (ret)
                    break;
            }
            if (ret)
                return true;
        }
        return false;
    }
    
    public boolean find(String search_string, byte search_bytes[])
    {
        return find(search_string, search_bytes, 0);
    }
}
