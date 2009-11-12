
package voxspellcheck;

import java.io.DataInput;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;

import java.util.Set;
import java.util.TreeSet;

public class VimSpell implements SpellCheck
{
    protected final static int MAXWLEN = 250;
    
    /* Flags used for a word.  Only the lowest byte can be used, the region byte
     * comes above it. */
    protected final static int WF_REGION = 0x01; /* region byte follows */
    protected final static int WF_ONECAP = 0x02; /* word with one capital (or all capitals) */
    protected final static int WF_ALLCAP = 0x04; /* word must be all capitals */
    protected final static int WF_RARE = 0x08; /* rare word */
    protected final static int WF_BANNED = 0x10; /* bad word */
    protected final static int WF_AFX = 0x20; /* affix ID follows */
    protected final static int WF_FIXCAP = 0x40; /* keep-case word, allcap not allowed */
    protected final static int WF_KEEPCAP = 0x80; /* keep-case word */
    
    /* for <flags2>, shifted up one byte to be used in wn_flags */
    protected final static int WF_HAS_AFF = 0x0100; /* word includes affix */
    protected final static int WF_NEEDCOMP = 0x0200; /* word only valid in compound */
    protected final static int WF_NOSUGGEST = 0x0400; /* word not to be suggested */
    protected final static int WF_COMPROOT = 0x0800; /* already compounded word, COMPOUNDROOT */
    protected final static int WF_NOCOMPBEF = 0x1000; /* no compounding before this word */
    protected final static int WF_NOCOMPAFT = 0x2000; /* no compounding after this word */
    
    /* only used for su_badflags */
    protected final static int WF_MIXCAP = 0x20; /* mix of upper and lower case: macaRONI */
    
    protected final static int WF_CAPMASK = (WF_ONECAP | WF_ALLCAP | WF_KEEPCAP | WF_FIXCAP); /* flags for <pflags> */
    protected final static int WFP_RARE = 0x01; /* rare prefix */
    protected final static int WFP_NC = 0x02; /* prefix is not combining */
    protected final static int WFP_UP = 0x04; /* to-upper prefix */
    protected final static int WFP_COMPPERMIT = 0x08; /* prefix with COMPOUNDPERMITFLAG */
    protected final static int WFP_COMPFORBID = 0x10; /* prefix with COMPOUNDFORBIDFLAG */
    
    /* Flags for postponed prefixes in "sl_pidxs".  Must be above affixID (one
     * byte) and prefcondnr (two bytes). */
    protected final static int WF_RAREPFX = (WFP_RARE << 24); /* rare postponed prefix */
    protected final static int WF_PFX_NC = (WFP_NC << 24); /* non-combining postponed prefix */
    protected final static int WF_PFX_UP = (WFP_UP << 24); /* to-upper postponed prefix */
    protected final static int WF_PFX_COMPPERMIT = (WFP_COMPPERMIT << 24); /* postponed prefix with
                                                                            * COMPOUNDPERMITFLAG */
    protected final static int WF_PFX_COMPFORBID = (WFP_COMPFORBID << 24); /* postponed prefix with
                                                                            * COMPOUNDFORBIDFLAG */
    
    /* flags for <compoptions> */
    protected final static int COMP_CHECKDUP = 1; /* CHECKCOMPOUNDDUP */
    protected final static int COMP_CHECKREP = 2; /* CHECKCOMPOUNDREP */
    protected final static int COMP_CHECKCASE = 4; /* CHECKCOMPOUNDCASE */
    protected final static int COMP_CHECKTRIPLE = 8; /* CHECKCOMPOUNDTRIPLE */
    
    /* Special byte values for <byte>.  Some are only used in the tree for
     * postponed prefixes, some only in the other trees.  This is a bit messy... */
    protected final static int BY_NOFLAGS = 0; /* end of word without flags or region; for
                                          * postponed prefix: no <pflags> */
    protected final static int BY_INDEX = 1; /* child is shared, index follows */
    protected final static int BY_FLAGS = 2; /* end of word, <flags> byte follows; for
                                        * postponed prefix: <pflags> follows */
    protected final static int BY_FLAGS2 = 3; /* end of word, <flags> and <flags2> bytes
                                         * follow; never used in prefix tree */
    protected final static int BY_SPECIAL = BY_FLAGS2; /* highest special byte value */
    
    protected final static int SHARED_MASK = 0x8000000;
    
    protected final static int SN_REGION = 0; 	/* <regionname> section */
    protected final static int SN_CHARFLAGS = 1; 	/* charflags section */
    protected final static int SN_MIDWORD = 2; 	/* <midword> section */
    protected final static int SN_PREFCOND = 3; 	/* <prefcond> section */
    protected final static int SN_REP = 4; 	/* REP items section */
    protected final static int SN_SAL = 5; 	/* SAL items section */
    protected final static int SN_SOFO = 6; 	/* soundfolding section */
    protected final static int SN_MAP	 = 7; 	/* MAP items section */
    protected final static int SN_COMPOUND = 8; 	/* compound words section */
    protected final static int SN_SYLLABLE = 9; 	/* syllable section */
    protected final static int SN_NOBREAK = 10; 	/* NOBREAK section */
    protected final static int SN_SUGFILE = 11; 	/* timestamp for .sug file */
    protected final static int SN_REPSAL = 12; 	/* REPSAL items section */
    protected final static int SN_WORDS = 13; 	/* common words */
    protected final static int SN_NOSPLITSUGS = 14; 	/* don't split word for suggestions */
    protected final static int SN_INFO = 15; 	/* info section */
    protected final static int SN_END = 255; 	/* end of sections */
    
    protected String language;
    protected Set<String> regions;
    protected VimWordTrie lwords;
    protected VimWordTrie kwords;
    protected VimWordTrie prefixes;
    
    protected boolean read_sec_rep(byte data[]) throws IOException
    {
        DataInput input = new DataInputStream(new ByteArrayInputStream(data));
        short rep_count = (short)Utility.convert_msb(data, 0, 2);
        input.skipBytes(2);
        for (int i = 0; i < rep_count; ++i) {
            int rep_from_len = input.readUnsignedByte();
            byte rep_from[] = new byte[rep_from_len];
            input.readFully(rep_from);
            int rep_to_len = input.readUnsignedByte();
            byte rep_to[] = new byte[rep_to_len];
            input.readFully(rep_to);
            System.out.print("rep from \""+new String(rep_from)+"\"");
            System.out.println(" to \""+new String(rep_to)+"\"");
        }
        System.out.println("rep count "+rep_count);
        return true;
    }
    
    protected boolean read_sal(byte data[]) throws IOException
    {
        DataInput input = new DataInputStream(new ByteArrayInputStream(data));
        
        // Grab the length
        short sal_count = (short)Utility.convert_msb(data, 1, 2);
        
        int sal_flags = input.readUnsignedByte();
        System.out.println("flags "+sal_flags);
        // Skip the length
        input.skipBytes(2);
        for (int i = 0; i < sal_count; ++i) {
            // sal_from processing
            int sal_from_len = input.readUnsignedByte();
            byte sal_from[] = new byte[sal_from_len];
            input.readFully(sal_from);
            String sal_from_str = new String(sal_from, "UTF-8");
            
            // sal_to processing
            int sal_to_len = input.readUnsignedByte();
            byte sal_to[] = new byte[sal_to_len];
            input.readFully(sal_to);
            String sal_to_str = new String(sal_to, "UTF-8");
            
            System.out.print("sal from \""+sal_from_str+"\"");
            System.out.println(" to \""+sal_to_str+"\"");
        }
        return true;
    }
    
    protected boolean read_repsal(byte data[]) throws IOException
    {
        DataInput input = new DataInputStream(new ByteArrayInputStream(data));
        int rep_count = Utility.convert_msb(data, 0, 2);
        // Skip the length
        input.skipBytes(2);
        for (int i = 0; i < rep_count; ++i) {
            // rep_from processing
            int rep_from_len = input.readUnsignedByte();
            byte rep_from[] = new byte[rep_from_len];
            input.readFully(rep_from);
            String rep_from_str = new String(rep_from, "UTF-8");
            
            // rep_to processing
            int rep_to_len = input.readUnsignedByte();
            byte rep_to[] = new byte[rep_to_len];
            input.readFully(rep_to);
            String rep_to_str = new String(rep_to, "UTF-8");
            
            System.out.print("repsal from \""+rep_from_str+"\"");
            System.out.println(" to "+rep_to_str+"\"");
        }
        return true;
    }
    
    protected boolean read_words(byte data[]) throws IOException
    {
        Set<String> words = new TreeSet<String>();
        StringBuffer word = new StringBuffer();
        for (int i = 0; i < data.length; ++i) {
            if (data[i] == '\0') {
                System.out.println(word);
                words.add(word.toString());
                word.setLength(0);
                continue;
            }
            word.append((char)data[i]);
        }
        return true;
    }
    
    protected boolean read_map(byte data[]) throws IOException
    {
        return true;
    }
    
    protected boolean read_prefcond(byte data[]) throws IOException
    {
        DataInput input = new DataInputStream(new ByteArrayInputStream(data));
        short prefcond_count = (short)Utility.convert_msb(input, 2);
        if (prefcond_count < 0) {
            System.out.println("ERROR: invalid prefcond_count");
            return false;
        }
        
        for (int i = 0; i < prefcond_count; ++i) {
            int prefcond_len = Utility.convert_msb(input, 1);
            byte condition[] = new byte[prefcond_len];
            input.readFully(condition);
            System.out.println(new String(condition));
        }
            
        return true;
    }
    
    protected boolean read_sections(DataInput input) throws IOException
    {
        int section_id = input.readUnsignedByte();
        while (section_id != SN_END) {
            int section_flags = input.readUnsignedByte();
            int section_len = input.readInt();
            
            System.out.println("id = "+section_id+" len = "+section_len);
            
            byte section_data[] = new byte[section_len];
            input.readFully(section_data);
            
            if (section_id == SN_REGION) {
                for (int i = 0; i < section_data.length; i += 2) {
                    byte r[] = new byte[2];
                    System.arraycopy(section_data, i, r, 0, r.length);
                    regions.add(new String(r));
                }
            } else if (section_id == SN_CHARFLAGS) {
                System.out.println("charflags sec len "+section_len);
            } else if (section_id == SN_MIDWORD) {
                System.out.println("midword chars \""+new String(section_data)+"\"");
            } else if (section_id == SN_REP) {
                read_sec_rep(section_data);
            } else if (section_id == SN_SAL) {
                read_sal(section_data);
            } else if (section_id == SN_REPSAL) {
                read_repsal(section_data);
            } else if (section_id == SN_WORDS) {
                read_words(section_data);
            } else if (section_id == SN_PREFCOND) {
                read_prefcond(section_data);
            }
            
            section_id = input.readUnsignedByte();
        }
        
        lwords = new VimWordTrie(input, false);
        kwords = new VimWordTrie(input, false);
        prefixes = new VimWordTrie(input, true);
        
        return true;
    }
    
    protected static boolean read_header(DataInput input) throws IOException
    {
        byte file_id[] = new byte[8];
        input.readFully(file_id);
        if (!new String(file_id).equals("VIMspell")) {
            System.out.println("ERROR: incorrect file_id; exiting");
            return false;
        }
        
        int version = input.readUnsignedByte();
        
        return true;
    }
    
    public VimSpell(String language_, DataInput input) throws IOException
    {
        language = language_;
        regions = new TreeSet<String>();
        read_header(input);
        read_sections(input);
    }
    
    public boolean find(String s)
    {
        int flags;
        s = s.trim();
        byte orig[];
        byte lower[];
        try {
            orig = s.getBytes("UTF-8");
            lower = s.toLowerCase().getBytes("UTF-8");
        } catch (java.io.UnsupportedEncodingException ex) {
            System.out.println(ex);
            return false;
        }
        if (lwords.find(s, lower))
            return true;
        if (kwords.find(s, orig))
            return true;
        return false;
    }
    
    public String getLanguage()
    {
        return language;
    }
    
    public String[] getRegions()
    {
        String t[] = new String[regions.size()];
        return regions.toArray(t);
    }
}
