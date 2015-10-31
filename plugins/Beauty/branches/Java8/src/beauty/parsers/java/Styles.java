package beauty.parsers.java;

//Style definitions.  I got these from the AStyle documentation.
public class Styles {
    /*
    ANSI style uses broken brackets everywhere.
    int Foo(bool isBar)
    {
        if (isBar)
        {
            bar();
            return 1;
        }
        else
            return 0;
    }        
    */
    public static int ANSI = 1;
    
    /*
    Sun's java coding standard. One-line conditionals always have brackets,
    all brackets are attached. Indenting is 4 spaces with soft tabs.
    int Foo(bool isBar) {
        if (isBar) {
            bar();
            return 1;
        } else {
            return 0;
        }
    }
    */
    public static int SUN = 2;
    
    /*
    Kernighan & Richie style.  Brackets are broken from namespaces, classes,
    and method definitons, brackets are attached to statements within a method.
    int Foo(bool isBar) 
    {
        if (isBar) {
            bar();
            return 1;
        } else
            return 0;
    }
    */
    public static int KR = 3;
    
    /*
    Stroupstrup style has broken brackets on methods only, all other brackets
    are attached. Indentation is 5 spaces.
    int Foo(bool isBar) 
    {
         if (isBar) {
              bar();
              return 1;
         } else
              return 0;
    }
    */
    public static int STROUSTRUP = 4;
    
    /*
    Whitesmite uses broken, indented bracket.
    int Foo(bool isBar) 
        {
        if (isBar)
            {
            bar();
            return 1;
            }
        else
            return 0;
        }
    */    
    public static int WHITESMITH = 5;
    
    /*
    Banner uses attached and indented brackets.
    int Foo(bool isBar) {
        if (isBar) {
            bar();
            return 1;
            }
        else
            return 0;
        }
    */    
    public static int BANNER = 6;
    
    /*
    Gnu uses broken brackets and indented blocks. Indentation is 2 spaces.
    Extra indentation is added to blocks within a method.
    int Foo(bool isBar)
    {
      if (isBar)
        {
          bar();
          return 1;
        }
      else
        return 0;
    }
    */
    public static int GNU = 7;
    
    /*
    Linux brackets are broken from namespace, class, and method definitions.
    Brackets are attached to statements within a method.  Indentation is 8
    spaces.
    int Foo(bool isBar)
    {
            if (isFoo) {
                    bar();
                    return 1;
            } else
                    return 0;
    }
    */
    public static int LINUX = 8;
    
    /*
    Horstmann style has broken brackets with run-in statements:
    int Foo(bool isBar)
    {  if (isBar)
       {  bar();
          return 1;
       } else
          return 0;
    }    
    */
    public static int HORSTMANN = 9;
    
    /*
    The one true brace style. Brackets are same as Linux and adds brackets to
    one line conditionals.
    int Foo(bool isBar)
    {
        if (isFoo) {
            bar();
            return 1;
        } else {
            return 0;
        }
    }
    */
    public static int OTBS = 10;
    
    /*
    jEdit style.  Brackets are the same as ANSI style. Indentation is 8 spaces 
    is 8 spaces with no soft tabs. No space after if/while/...
    int Foo(bool isBar)
    {
            if(isFoo) 
            {
                    bar();
                    return 1;
            } 
            else
                    return 0;
    }
    */
    public static int JEDIT = 11;
}