package uk.co.antroy.latextools; 

import sidekick.*;
import javax.swing.tree.DefaultMutableTreeNode;
import org.gjt.sp.jedit.*;
import errorlist.DefaultErrorSource;

public class LaTeXParser extends SideKickParser{
  
  private String text;
  private SideKickParsedData data;
  private Buffer buffer;
  
  public LaTeXParser(String name){
    super(name);
  }
  
  public SideKickParsedData parse(Buffer buffer, DefaultErrorSource e){
    this.buffer = buffer;
    data = new SideKickParsedData(buffer.getName());
    text = buffer.getText(0,buffer.getLength());

    parseReferences();        

    return data;
  }


  private void parseReferences(){
      int refStart = text.indexOf("\\label{");
      while (refStart >= 0) {
        int refEnd = text.indexOf("}", refStart);
        String ref = text.substring(refStart + 7, refEnd);
        data.root.add(new DefaultMutableTreeNode(LaTeXAsset.createAsset(ref.trim(), 
                                          buffer.createPosition(refStart+7),
                                          buffer.createPosition(refEnd))));
        refStart = text.indexOf("\\label{", refEnd);
      }
  }

}
