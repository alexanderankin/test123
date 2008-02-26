/*
 * jeditext.cpp - jEdit Launcher for Windows (TM).
 *
 * Context menu shell extension.
 *
 * Copyright 2004, 2005 Ollie Rutherfurd <oliver@jedit.org>
 * Based on code Copyright 1999 Tianmiao Hu
 *
 * $Id: jeditext.h 72 2005-01-10 20:36:02Z orutherfurd $
 */
#if !defined __JEDITEXT_H__
#define __JEDITEXT_H__

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#ifndef STRICT
#define STRICT
#endif

#define INC_OLE2		// WIN32, get ole2 from windows.h

#include <windows.h>
#include <windowsx.h>
#include <shlobj.h>

#include "jeditlib.h"

#define ResultFromShort(i)  ResultFromScode(MAKE_SCODE(SEVERITY_SUCCESS, 0, (USHORT)(i)))

// Initialize GUIDs (should be done only and at-least once per DLL/EXE)
//
#pragma data_seg(".text")
#define INITGUID
#include <initguid.h>
#include <shlguid.h>

DEFINE_GUID(CLSID_ShellExtension, 0xf1763c8f, 0x4f26, 0x4a15, 0x94, 0x3f, 0x2c, 0xa2, 0x81, 0xbd, 0x33, 0x85);

class CShellExtClassFactory : public IClassFactory
{
protected:
	ULONG m_cRef;

public:
	CShellExtClassFactory();
	~CShellExtClassFactory();

	// IUnknown
	STDMETHODIMP			QueryInterface(REFIID, LPVOID FAR*);
	STDMETHODIMP_(ULONG)	AddRef();
	STDMETHODIMP_(ULONG)	Release();

	// IClassFactory
	STDMETHODIMP	CreateInstance(LPUNKNOWN, REFIID, LPVOID FAR*);
	STDMETHODIMP	LockServer(BOOL);
};

typedef CShellExtClassFactory *LPCSHELLEXTCLASSFACTORY;

class CShellExt : public IContextMenu, 
						 IShellExtInit
{
protected:
	ULONG m_cRef;
	LPDATAOBJECT m_pDataObj;
	//HANDLE hImage;	// bitmap for menu
	STDMETHODIMP	OpenFiles(void);
	STDMETHODIMP	DiffFiles(void);

public:
	CShellExt();
	~CShellExt();

	//IUnknown members
	STDMETHODIMP QueryInterface(REFIID, LPVOID FAR *);
	STDMETHODIMP_(ULONG) AddRef();
	STDMETHODIMP_(ULONG) Release();

	//IShell members
	STDMETHODIMP QueryContextMenu(HMENU hMenu,
	    UINT indexMenu,
	    UINT idCmdFirst,
	    UINT idCmdLast,
	    UINT uFlags);

	STDMETHODIMP InvokeCommand(LPCMINVOKECOMMANDINFO lpcmi);

	STDMETHODIMP GetCommandString(UINT idCmd,
	    UINT uFlags,
	    UINT FAR *reserved,
	    LPSTR pszName,
	    UINT cchMax);

	//IShellExtInit methods
	STDMETHODIMP Initialize(LPCITEMIDLIST pIDFolder,
	    LPDATAOBJECT pDataObj,
	    HKEY hKeyID);

	STDMETHODIMP DiffWithJEdit(HWND hParent,
							   LPCSTR pszWorkingDir,
							   LPCSTR pszCmd,
							   LPCSTR pszParam,
							   int iShowCmd);

	STDMETHODIMP OpenByExtInJEdit(HWND hParent,
								  LPCSTR pszWorkingDir,
								  LPCSTR pszCmd,
								  LPCSTR pszParam,
								  int iShowCmd);

	STDMETHODIMP OpenInJEdit(HWND hParent,
							 LPCSTR pszWorkingDir,
							 LPCSTR pszCmd,
							 LPCSTR pszParam,
							 int iShowCmd);
};

typedef CShellExt *LPCSHELLEXT;
#pragma data_seg()

#endif	// __JEDITEXT_H__

// :deepIndent=true:tabSize=4:

