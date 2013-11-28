/*:folding=indent:
* UtilityMacros.java - various useful macros.
* Copyright (C) 2003 Anthony Roy
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package uk.co.antroy.latextools.macros;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.gjt.sp.util.Log;

public class UtilityMacros {

    public static Icon getIcon(String name){
        String filename = "/images/" + name;
        Icon icon = null;
          if (filename != null) {
            try{
              icon = new ImageIcon(UtilityMacros.class.getResource(filename.toString()));
            }catch (Exception e){
              Log.log(Log.ERROR, UtilityMacros.class,filename.toString() + "Not found");
              e.printStackTrace();
            }
          }
          return icon;
       }
    
}
