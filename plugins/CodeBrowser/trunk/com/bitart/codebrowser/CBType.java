/******************************************************************************
*	Copyright 2002 BITart Gerd Knops. All rights reserved.
*
*	Project	: CodeBrowser
*	File	: CBType.java
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
*	CBType is a TreeNode object representing a certain category (such as
*	functions, variables etc) of the ctags results.
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
	
	import org.gjt.sp.jedit.*;
	
/*****************************************************************************/
public class CBType implements TreeNode,Comparable
{
/******************************************************************************
* Vars
******************************************************************************/

	Vector	children=null;
	Vector	unsortedChildren=null;
	Vector	sortedChildren=null;
	String	type;
	String	desc;
	String	lang;
	CBRoot	parent;
	
/******************************************************************************
* Factory methods
******************************************************************************/
public CBType(CBRoot parent,String type,String lang)
	{
		this.parent=parent;
		this.type=type;
		this.lang=lang;
		
		unsortedChildren=new Vector();
		children=unsortedChildren;
		
		desc=type;
		if(desc.length()>1)
		{
			desc=type.substring(0,1).toUpperCase()+type.substring(1);
		}
	}

/******************************************************************************
* Implementation
******************************************************************************/
public void setState(boolean flag)
	{
		jEdit.setBooleanProperty("codebrowser.expanded."+lang+"."+type,flag);
	}
	
public boolean getState()
	{
		return jEdit.getBooleanProperty("codebrowser.expanded."+lang+"."+type);
	}
	
public void add(Hashtable info)
	{
		unsortedChildren.add(new CBLeaf(this,info));
	}
	
public void setSorted(boolean flag)
	{
		if(flag)
		{
			if(sortedChildren==null)
			{
				sortedChildren=(Vector)(unsortedChildren.clone());
				Collections.sort(sortedChildren);
			}
			children=sortedChildren;
		}
		else
		{
			children=unsortedChildren;
		}
	}
	
public String getType()
	{
		return type;
	}
	
public String toString()
	{
		return desc+" ("+children.size()+")";
	}
	
/******************************************************************************
* Comparable interface
******************************************************************************/
public int compareTo(Object o)
	{
		return type.compareTo(((CBType)o).getType());
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
		return parent;
	}
	
public boolean isLeaf()
	{
		return false;
	}
}
/*************************************************************************EOF*/

