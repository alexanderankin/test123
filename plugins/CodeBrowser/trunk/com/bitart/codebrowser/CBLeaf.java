/******************************************************************************
*	Copyright 2002 BITart Gerd Knops. All rights reserved.
*
*	Project	: CodeBrowser
*	File	: CBLeaf.java
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
*	CBLeaf is a TreeNode object representing a line from the ctags output.
*	The line is split into it's elements and the factory method receives
*	the elements in form of a Vector.
*
*	We take the name element and the pattern element from that data. The
*	pattern element is massaged into a tooltip text, and also escaped in
*	such a way that it is suitable for jEdits search engine.
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
	import javax.swing.*;
	import javax.swing.tree.*;
	
/*****************************************************************************/
public class CBLeaf implements TreeNode,Comparable
{
/******************************************************************************
* Vars
******************************************************************************/

	CBType	parent;
	String	name;
	String	pattern;
	String	toolTipText;
	String	desc;
	
/******************************************************************************
* Factory methods
******************************************************************************/
public CBLeaf(CBType parent,Hashtable info)
	{
		this.parent=parent;
		
		name=(String)info.get("cb_tag_cb");
		pattern=(String)info.get("cb_pattern_cb");
		toolTipText=pattern;
		pattern=escape(pattern.substring(1,pattern.length()-1));
		
		toolTipText=toolTipText.substring(1,toolTipText.length()-1);
		if(toolTipText.startsWith("^")) toolTipText=toolTipText.substring(1);
		if(toolTipText.endsWith("$")) toolTipText=toolTipText.substring(0,toolTipText.length()-1);
		toolTipText=toolTipText.trim();
		
		desc=name;
		
		String signature=(String)info.get("signature");
		if(signature!=null && signature.length()>0)
		{
			desc=name+signature;
		}
	}

/******************************************************************************
* Implementation
******************************************************************************/
public String getName()
	{
		return name;
	}
	
public String toString()
	{
		return desc;
	}
	
public String getPattern()
	{
		return pattern;
	}
	
public String getToolTipText()
	{
		return toolTipText;
	}
	
public String escape(String src)
	{
		int l=src.length();
		StringBuffer buf=new StringBuffer(l*2);
		
		for(int i=0;i<l;i++)
		{
			char c=src.charAt(i);
			if(!((i==0 && c=='^') || (i==l-1 && c=='$')))
			{
				if(!(
					c==' '
					|| c==';'
					|| c=='_'
					|| c=='\\'
					|| c=='/'
					|| c=='<'
					|| c=='>'
					|| (c>='0' && c<='9')
					|| (c>='A' && c<='Z')
					|| (c>='a' && c<='z')
				))
				{
					buf.append('\\');
				}
			}
			buf.append(c);
		}
		
		return buf.toString();
	}

/******************************************************************************
* Comparable interface
******************************************************************************/
public int compareTo(Object o)
	{
		return name.compareTo(((CBLeaf)o).getName());
	}	

/******************************************************************************
* TreeNode interface
******************************************************************************/
public Enumeration children()
	{
		return null;
	}
	
public boolean getAllowsChildren()
	{
		return false;
	}
	
public TreeNode getChildAt(int index)
	{
		return null;
	}
	
public int getChildCount()
	{
		return 0;
	}
	
public int getIndex(TreeNode child)
	{
		return 0;
	}
	
public TreeNode getParent()
	{
		return parent;
	}
	
public boolean isLeaf()
	{
		return true;
	}
}
/*************************************************************************EOF*/

