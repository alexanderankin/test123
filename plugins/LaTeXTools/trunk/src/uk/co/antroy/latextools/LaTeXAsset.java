package uk.co.antroy.latextools; 

import sidekick.*;
import javax.swing.*;
import javax.swing.text.*;

//This must implement features of TagPair.
 public class LaTeXAsset extends Asset{
    
    public LaTeXAsset(String name){
      super(name);
    }
    
    public Icon getIcon(){
      return null;
    }
      
    public String getShortString(){
      return name;
    }
    
    public String getLongString(){
      return name;
    }

    public static LaTeXAsset createAsset(String name, Position start, Position end){
      
      LaTeXAsset la = new LaTeXAsset(name);
      la.start = start;
      la.end = end;
      
      return la;
    }
    
  }
  

