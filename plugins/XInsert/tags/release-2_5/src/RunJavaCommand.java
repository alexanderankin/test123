/*
 *
 * RunJavaCommand.java
 * Copyright (C) 2001 Dominic Stolerman
 * dstolerman@jedit.org
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

import org.gjt.sp.jedit.View;
import java.lang.reflect.*;
import org.gjt.sp.util.Log;
/**
 *  
 *
 * @author     Dominic Stolerman
 */
public class RunJavaCommand extends Object implements Command {

	private final String command;


	public RunJavaCommand(String command) {
		this.command = command;
	}

	public void run(ScriptContext sc) {
		View parent = sc.getView();
		XTreeNode node = sc.getNode();
		boolean stringArg = false;
		String arg = "";
		String cleanedCmd = command.substring(1, command.length() - 1).trim();
		int bracket = cleanedCmd.indexOf("(");
		if(bracket == -1) {
			XScripter.doError(command, "\"(\" expected");
			return;
		}

		// find the LAST "." before bracket - this is the class
		int classEnds = cleanedCmd.lastIndexOf(".", bracket);
		String clazzName = cleanedCmd.substring(0, classEnds);
		String methodName = cleanedCmd.substring(classEnds + 1, bracket);
		if(cleanedCmd.charAt(bracket + 1) != ')') {
			stringArg = true;
			String _arg = cleanedCmd.substring(bracket + 1, cleanedCmd.length());
			if(_arg.startsWith("$")) {
				arg = XScripter.getSubstituteFor(parent, _arg.substring(1), node);
			}
			else {
				arg = _arg;
			}
		}
		try {
			Class clazz = Class.forName(clazzName);
			Method method = null;
			System.out.println("ClassName=" + clazzName + "/" + clazz.getName() + " methodName=" + methodName);
			
			Object[] args = null; //Arguments for method
			if(stringArg) {
				method = clazz.getMethod(methodName, new Class[] {String.class});
				//method = clazz.getMethod(methodName, new Class[] {Object.class, String.class});
				args = new Object[1];
				args[0] = arg;
			}
			else {
				//method = clazz.getMethod(methodName, new Class[] {Object.class});
				method = clazz.getMethod(methodName, null);
			}
			
			Object obj = method.invoke(null, args);
			if(obj == null) {
				return;
			}
			else {
				InsertTextCommand.insertText(obj.toString(), sc);
			}
			/*
			Object[] args = null;
			Method[] methods = clazz.getMethods();
			/*
			 * Debugging code
			 * for (int i = 0; i < methods.length; i++) {
			 * String methodString = methods[i].getName();
			 * String returnString = methods[i].getReturnType().getName();
			 * Log.log(Log.DEBUG, XScripter.class, "Method: " + methodString + " Return Type: " + returnString);
			 * Class[] parameterTypes = methods[i].getParameterTypes();
			 * Log.log(Log.DEBUG, XScripter.class, "   Parameter Types");
			 * for (int k = 0; k < parameterTypes.length; k ++) {
			 * String parameterString = parameterTypes[k].getName();
			 * Log.log(Log.DEBUG, XScripter.class," " + parameterString);
			 * }
			 * System.out.println();
			 * }
			 
			for(int i = 0; i < methods.length; i++) {
				if(methods[i].getName().equals(methodName)) {
					Class[] parameterTypes = methods[i].getParameterTypes();
					if(stringArg && parameterTypes.length == 2) {
						if(parameterTypes[0].getName().equals(ScriptContext.class.getName()) && 
								parameterTypes[1].getName().equals(String.class.getName())) {
							method = methods[i];
							args = new Object[]{sc, arg};
							break;
						}
					}
					else if(!stringArg && parameterTypes.length == 1) {
						if(parameterTypes[0].getName().equals(ScriptContext.class.getName())) {
							method = methods[i];
							args = new Object[]{sc};
							break;
						}
					}
				}
			}
			if(method != null) {
				for(int i = 0; i < args.length; i++) {
					Log.log(Log.DEBUG, this, args[i].getClass().getName());
				}
				Object obj = method.invoke(null, args);
				if(obj == null) {
					return;
				}
				else {
					InsertTextCommand.insertText(obj.toString(), sc);
				}
			}
			else {
				XScripter.doError(command, "Method not found");
				return;
			}
			
*/
		}
		catch(Exception e) {
			XScripter.doError(command, e);
		}
	}
}


