/*
 * jcmd.c - A command interpreter for use with the Java's platform
 * Runtime.exec() on the Windows family of operating systems, designed
 * to prevent the display of console windows when executing console-based
 * programs
 * Copyright (C) 2001 John Gellene
 * jgellene@nyc.rr.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * Notwithstanding the terms of the General Public License, the author grants
 * permission to compile and link object code generated by the compilation of
 * this program with object code and libraries that are not subject to the
 * GNU General Public License, provided that the use and distribution of
 * the resulting library or executable file shall be subject to the General
 * Public License. This condition does not require a licensee of this software
 * to distribute any proprietary software (including header files and libraries)
 * that is licensed under terms prohibiting redistribution to third parties.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the jEdit program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * $Id$
 */

/*
 * To compile this file use the following command line:
 *
 * cl /nologo /ML /W4 /O2 /Ob2 /D "WIN32" /D "NDEBUG" /D "_MBCS" \
 * jcmd.c /link user32.lib /subsystem:windows /version:1.1
 */

#include <windows.h>

/*
 * struct testconsole
 *
 * A data structure for holding the results
 * of iteration through a group of windows.
 */

struct testconsole
{
	HWND hwnd;
	int nCount;
};


/*
 * Forward declarations
 */

BOOL CALLBACK FindConsoleWindow(HWND hwnd, LPARAM lparam);
BOOL CALLBACK CloseThreadWindow(HWND hwnd, LPARAM lparam);
BOOL FoundConsole(struct testconsole* pt);
void WriteProcessError(HANDLE handle);


/*
 * WinMain
 *
 * The parameter lpCmdLine holds the "real" program we want to
 * execute. We start a process with the main window hidden, then
 * we examine the windows produced by the process's main thread
 * to see if the program is running as a console program or a
 * windows program.  If it is a windows program, we terminate
 * the application and restart it without hiding the main window.
 * If it continues to run as hidden, the standard input and output
 * streams of the child process are linked to the stream of this
 * application, which in turn can be captured by the Java
 * application calling Runtime.exec().
 *
 */


int WINAPI WinMain(HINSTANCE hInst, HINSTANCE
	hPrevInstance, LPSTR lpCmdLine, int cmdShow)
{
    PROCESS_INFORMATION pi;
    STARTUPINFO         si;
	BOOL				ok;
	DWORD				result;
	struct testconsole	test = {0, 0};

	/* unused */
	hInst, hPrevInstance, cmdShow;

    memset(&si, 0, sizeof(si));
    si.cb = sizeof(si);
    si.dwFillAttribute = 0;
    si.dwFlags = STARTF_USESHOWWINDOW |	STARTF_USESTDHANDLES;
    si.wShowWindow = SW_HIDE;
    si.hStdInput = GetStdHandle(STD_INPUT_HANDLE);
    si.hStdOutput = GetStdHandle(STD_OUTPUT_HANDLE);
    si.hStdError = GetStdHandle(STD_ERROR_HANDLE);
    ok = CreateProcess(NULL, lpCmdLine, NULL, NULL,
		TRUE, 0, NULL, NULL, &si, &pi );
    if( !ok )
	{
		WriteProcessError(si.hStdError);
		return -1;
	}
	/* Make sure the process completed its startup */
	WaitForInputIdle(pi.hProcess, 1000);
	/* Iterate over top-level windows of the thread
	 * and look for a console
	 */
	EnumThreadWindows(pi.dwThreadId, FindConsoleWindow, (LPARAM)&test);
	if(!FoundConsole(&test))
	{
		/* This is a windows program.  We will terminate it
		 * as politely as possible and restart it with a normal
		 * instead of a hidden main window.
		 */
		EnumThreadWindows(pi.dwThreadId, CloseThreadWindow, 0);
		/* If we couldn't kill the window, we kill the process */
		result = WaitForSingleObject(pi.hThread, 0);
		if(result == WAIT_TIMEOUT || result == WAIT_ABANDONED)
			TerminateProcess(pi.hProcess, 1);
	    si.wShowWindow = SW_SHOW;
		ok = CreateProcess(NULL, lpCmdLine, NULL, NULL,
			TRUE, 0, NULL, NULL, &si, &pi);
		if(!ok)
		{
			WriteProcessError(si.hStdError);
			return -1;
		}
		/* we will not wait for a window-based program to exit */
		else
			return 0;
	}
	/* Now we wait for the console process to end to get the return code */
    result = WaitForSingleObject(pi.hProcess,INFINITE);
    switch(result) {
    	case WAIT_FAILED:
    	case WAIT_ABANDONED:
    	case WAIT_TIMEOUT:
        	return -1;
    	default:
        	GetExitCodeProcess(pi.hProcess, &result );
        	return result;
    }
}


/*
 *  This function looks for one of the two window class
 *  names used by versions of Windows for Console windows.
 *  If found, the testconsole structure receives the handle.
 *  The count element of the structure is incremented for
 *  the window being examined.
 */

BOOL CALLBACK FindConsoleWindow(HWND hwnd, LPARAM lparam)
{
	char		window_class[32];
	struct		testconsole *ptest = (struct testconsole*)lparam;

	++ptest->nCount;
	GetClassName(hwnd, window_class, sizeof (window_class));
	if(strcmp(window_class, "ConsoleWindowClass") == 0
		|| strcmp(window_class, "tty") == 0)
	{
		ptest->hwnd = hwnd;
		/* returning FALSE will end the iteration
		 * in EnumThreadWindows
		 */
		return FALSE;
	}
	return TRUE;
}


/*
 * CloseThreadWindow
 *
 * This function examines the window to determine whether
 * it is a candidate for closing.  A candidate must be:
 * (1) a top-level window
 * (2) without an owner
 * (3) lacking the WS_POPUP style (thus, a frame window)
 *
 * To close the window, we first send it a WM_CLOSE message,
 * If that doesn't work, we send a system command message
 * to close (similar to pressing Alt-F4).  If that doesn't work,
 * the main routine will terminate the window's process.
 */

BOOL CALLBACK CloseThreadWindow(HWND hwnd, LPARAM lparam)
{
	HWND		hwndParent, hwndPopup;

	/* unused */
	lparam;

	if(!IsWindow(hwnd))
		return TRUE;
	while((hwndParent = GetParent(hwnd)) != 0)
	{
		/*
		 * In the unlikely event that a popup child window
		 * was immediately activated, we will first close
		 * the popup
		 */
		hwndPopup = GetLastActivePopup(hwndParent);
		if(hwndPopup != hwndParent)
		{
			SendMessage(hwndPopup, WM_QUIT, 0, 0);
		}
		hwnd = hwndParent;
	}
	if(GetWindow(hwnd, GW_OWNER) == 0)
	{
		DWORD dwStyle = GetWindowLong(hwnd, GWL_STYLE);
		if(dwStyle != 0 && (dwStyle & WS_POPUP) == 0)
		{
			SendMessage(hwnd, WM_CLOSE, 0, 0);
			if(IsWindow(hwnd))
				SendMessage(hwnd, WM_SYSCOMMAND, SC_CLOSE, 0);
			/* Whether it worked or not, we are done
			 * iterating through windows.
			 */
			return FALSE;
		}
	}
	return TRUE;
}


/*
 * FoundConsole
 *
 * We have a console if:
 * (1) we could not find any windows (because the console
 * window is built-in), or
 * (2) we found a window from one of the two console window
 * classes used by windows - FindConsole() put that window's
 * handle into the testconsole structure.
 */

BOOL FoundConsole(struct testconsole* pt)
{
	return pt->nCount == 0 || pt->hwnd != 0;
}


/*
 * WriteProcessError
 *
 * This function writes a simple error message to
 * the stream referenced by the handle.
 */

void WriteProcessError(HANDLE handle)
{
	char szMsg[256];
	DWORD dwLength;
	LPVOID lpMsgBuf;
	strcpy(szMsg, "Could not create jcmd process: ");
	FormatMessageA(
    	FORMAT_MESSAGE_ALLOCATE_BUFFER |
    	FORMAT_MESSAGE_FROM_SYSTEM |
    	FORMAT_MESSAGE_IGNORE_INSERTS,
    	NULL,
    	GetLastError(),
    	MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT),
    	(LPSTR) &lpMsgBuf,
    	0,
   		NULL
	);
	strcat(szMsg, (LPCSTR)lpMsgBuf);
	LocalFree(lpMsgBuf);
	WriteFile(handle, szMsg, strlen(szMsg), &dwLength, 0);
}

/* end jcmd.c */

