//       \includegraphics*[width=7cm]{graphics\complexes.png}
//      :latex.root='D:\Projects\Thesis\src\Thesis.tex':
package uk.co.antroy.latextools;

import javax.swing.*;
import org.gjt.sp.util.Log;

public class UtilityMacros {

    public static Icon getIcon(String name){
        String filename = "/images/" + name;
        Icon icon = null;
          if (filename != null) {
            try{
              icon = new ImageIcon(UtilityMacros.class.getResource(filename.toString()));
            }catch (Exception e){
              Log.log(Log.DEBUG,UtilityMacros.class,filename.toString() + "Not found");
            }
          }
          return icon;
       }
    
}
