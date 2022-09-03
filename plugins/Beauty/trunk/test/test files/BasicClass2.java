class Test {
    public static void main( String[] args ) {
        // Prefix & postfix
        ++x;
        if (x == 6)
            --x;
        
        while (x == 6)
            ++x;
        
        do
            --x;
        while(x == 9);
        
        for (;;)
            ++x;
        
        if (x == 5) 
            ++z;
        else if (x == 7)
            --z;
        
        if (x == 9)
            z = 9;
        else 
            x = 2;
    }
}
