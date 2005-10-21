/******************************************************************************
*	Copyright 2002 BITart Gerd Knops. All rights reserved.
*
*	Project	: CodeBrowser
*	File	: CBRoot.java
*	Author	: Gerd Knops gerti@BITart.com
*
*******************************************************************************
*                                    :mode=java:folding=indent:collapseFolds=1:
*	History:
*	020510 Creation of file
*
*******************************************************************************
*
*	Description:
*	This is the root TreeNode for the CodeBrowser display.
*	Here we take care of having the file parsed via ctags, and then
*	we create child nodes as required.
*
*	$Id$
*
*******************************************************************************
*
* DISCLAIMER
*
* BITart and Gerd Knops make no warranties, representations or commitments
* with regard to the contents of this software. BITart and Gerd Knops
* specifically disclaim any and all warranties, wether express, implied or
* statutory, including, but not limited to, any warranty of merchantability
* or fitness for a particular purpose, and non-infringement. Under no
* circumstances will BITart or Gerd Knops be liable for loss of data,
* special, incidental or consequential damages out of the use of this
* software, even if those damages were forseeable, or BITart or Gerd Knops
* was informed of their potential.
*
******************************************************************************/
package com.bitart.codebrowser;
/******************************************************************************
* Imports
******************************************************************************/

	import java.util.*;
	import java.io.*;
	import javax.swing.*;
	import javax.swing.tree.*;
	
	import org.gjt.sp.jedit.*;
	
/*****************************************************************************/
public class CBRoot implements TreeNode
{
/******************************************************************************
* Vars
******************************************************************************/

	static final boolean DEBUG=false;
	
	Vector	children=null;
	
/******************************************************************************
* Factory methods
******************************************************************************/
public CBRoot(String path,String lang)
	{
		parse(path,lang);
	}

/******************************************************************************
* Implementation
******************************************************************************/
public void parse(String path,String lang)
	{
		if(DEBUG) System.err.println("Parsing "+path);
		children=new Vector();
		//if(lang.equals("text")) return;
		Hashtable cbTypes=new Hashtable();
		Vector tv=new Vector();
		
		boolean buildxml=false;
		
		if(path.toLowerCase().endsWith("build.xml")) buildxml=true;
		
		try
		{
			//System.err.println("Starting ctags...");
			String[] args;
			
			if(!buildxml)
			{
				args=new String[]{
					jEdit.getProperty("options.codebrowser.ctags_path"),
					"--fields=KsSz",
					"--excmd=pattern",
					"--sort=no",
					"-f",
					"-",
					path
				};
			}
			else
			{
				args=new String[]{
					jEdit.getProperty("options.codebrowser.ctags_path"),
					"--fields=KsSz",
					"--excmd=pattern",
					"--sort=no",
					"--language-force=ant",
					"-f",
					"-",
					path
				};
				lang="ant";
			}
			/*
			System.err.println("Args: ");
			for(int i=0;i<args.length;i++)
			{
				System.err.println("\t"+args[i]);
			}
			*/
			Process p=Runtime.getRuntime().exec(args);
			BufferedReader in=new BufferedReader(new InputStreamReader(p.getInputStream()));
			//System.err.println("ctags started!");
			
			String line;
			while((line=in.readLine())!=null)
			{
				//System.err.println("Got line: "+line);
				// Get rid of crlf
				while(line.endsWith("\n") || line.endsWith("\r"))
				{
					line=line.substring(0,line.length()-1);
				}
				
				//
				// split off extension
				//
				int idx;
				idx=line.lastIndexOf(";\"\t");
				if(idx<0) continue;
				
				// extensions in Vector v, remove from line
				Vector v=split("\t",line.substring(idx+3));
				line=line.substring(0,idx);
				
				// Create a hash from extensions
				Hashtable info=new Hashtable();
				for(int i=0;i<v.size();i++)
				{
					String s=(String)v.elementAt(i);
					int ei=s.indexOf(':');
					if(ei<0) continue;
					info.put(s.substring(0,ei),s.substring(ei+1));
				}
				
				// item name
				idx=line.indexOf("\t");
				if(idx<0) continue;
				info.put("cb_tag_cb",line.substring(0,idx));
				line=line.substring(idx+1);
				
				// file name, not needed
				idx=line.indexOf("\t");
				if(idx<0) continue;
				//info.put("cb_file_cb",line.substring(0,idx));
				
				// pattern
				info.put("cb_pattern_cb",line.substring(idx+1));
				
				//System.err.println("Parsed into: "+info);
				
				String type=(String)info.get("kind");
				if(type==null || type.length()==0) continue;
				
				CBType t=(CBType)cbTypes.get(type);
				if(t==null)
				{
					t=new CBType(this,type,lang);
					cbTypes.put(type,t);
					tv.add(type);
				}
				t.add(info);
			}
		}
		catch(IOException ioe)
		{
			System.err.println(ioe);
		}
		//System.err.println("Done reading");
		
		
		Collections.sort(tv);
		for(int i=0;i<tv.size();i++)
		{
			children.add(cbTypes.get(tv.elementAt(i)));
		}
	}
	
public Vector split(String where,String str)
	/***********************************************************************
	* Splits the String txt on occurances of str, returns a Vector
	* of Strings.
	* @param where The String to split on.
	* @param str The String to split.
	* @return A Vector of strings.
	***********************************************************************/
	{
 		Vector v=new Vector();
		
		int idx;

		while((idx=str.indexOf(where))>=0)
		{
			String s="";
			if(idx>0) s=str.substring(0,idx);
			v.addElement(s);
			str=str.substring(idx+where.length());
		}
		v.addElement(str);

		return v;
	}
	
public void expandPaths(JTree tree)
	{
		Object[] objs={
			this,
			this
		};
		for(int i=children.size()-1;i>=0;i--)
		{
			CBType t=(CBType)children.elementAt(i);
			if(t.getState())
			{
				objs[1]=t;
				tree.expandPath(new TreePath(objs));
			}
		}
	}
	
public void setSorted(boolean flag,JTree tree)
	{
		DefaultTreeModel tm=null;
		if(tree!=null) tm=(DefaultTreeModel)tree.getModel();
		
		for(int i=0;i<children.size();i++)
		{
			CBType t=(CBType)children.elementAt(i);
			t.setSorted(flag);
			if(tm!=null && t.getState())
			{
				int l=t.getChildCount();
				int[] idc=new int[l];
				for(int j=0;j<l;j++)
				{
					idc[j]=j;
				}
				
				tm.nodesChanged(t,idc);
			}
		}
	}
	
/******************************************************************************
* TreeNode interface
******************************************************************************/
public Enumeration children()
	{
		return children.elements();
	}
	
public boolean getAllowsChildren()
	{
		return true;
	}
	
public TreeNode getChildAt(int index)
	{
		return (TreeNode)children.elementAt(index);
	}
	
public int getChildCount()
	{
		return children.size();
	}
	
public int getIndex(TreeNode child)
	{
		return children.indexOf(child);
	}
	
public TreeNode getParent()
	{
		return null;
	}
	
public boolean isLeaf()
	{
		return false;
	}
}
/*************************************************************************EOF*/

