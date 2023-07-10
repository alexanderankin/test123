Default_String : constant String := 
    "This is the long string returned by" & 
    " default.  It is broken into multiple" & 
    " Ada source lines for convenience.";
    
    type Signed_Whole_16 is range - 2**15 .. 2**15-1 ;
    type Address_Area    is array ( Natural range <> ) of Signed_Whole_16;
    
    Register : Address_Area ( 16 #7FF0# .. 16 #7FFF# ) ;
    Memory   : Address_Area (       0 .. 16 #7FEC# ) ;
    
    Register (Pc) := Register (A) ;
    
    X := Signed_Whole_16(Radius*Sin(Angle)) ;
    
    Register ( Index ) := Memory ( Base_Address + Index * Element_Length ) ;
    
    Get ( Value => Sensor ) ;
    
    Error_Term := 1.0 - ( Cos ( Theta ) ** 2 + Sin ( Theta ) ** 2 ) ;
    
    Z      := X ** 3 ;
    Y      := C * X + B;
    Volume := Length * Width * Height;
    
    some text then a <<label>> then some more text
    
    >>
    
    loop
        if Input_Found then
            Count_Characters;
            
            else  --not Input_Found
                Reset_State;
                Character_Total := 
                    First_Part_Total  * First_Part_Scale_Factor  + 
                    Second_Part_Total * Second_Part_Scale_Factor + 
                    Default_String'Length + Delimiter_Size;
                end if;
                
            end loop;