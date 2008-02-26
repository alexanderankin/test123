/*
 * jeditext.cpp - jEdit Launcher for Windows (TM).
 *
 * Context menu shell extension.
 *
 * Copyright 2004, 2005 Ollie Rutherfurd <oliver@jedit.org>
 * Based on code Copyright 1999 Tianmiao Hu
 *
 * $Id: jeditext.cpp 70 2005-01-10 20:25:50Z orutherfurd $
 */

 #include "jeditext.h"

// Always get an error while putting the following stuff to the
// jeditext.h file as class protected variables, give up and
// declare them as global stuff
FORMATETC fmte = {CF_HDROP,
          (DVTARGETDEVICE FAR *)NULL,
          DVASPECT_CONTENT,
          -1,
          TYMED_HGLOBAL
         };
STGMEDIUM medium;
HRESULT hres = 0;
UINT cbFiles = 0;

UINT        g_cRefThisDll = 0;      // Reference count of this DLL
HINSTANCE   g_hmodThisDll = NULL;   // Handle to this DLL itself

extern "C" int APIENTRY
DllMain(HINSTANCE hInstance,
        DWORD dwReason,
        LPVOID lpReserved)
{
    switch(dwReason)
    {
        case DLL_PROCESS_ATTACH:
            // Extension DLL one-time initialization
            g_hmodThisDll = hInstance;
            break;
        case DLL_PROCESS_DETACH:
            break;
    }
    return 1;   // return TRUE on success
}

static void
inc_cRefThisDLL()
{
    InterlockedIncrement((LPLONG)&g_cRefThisDll);
}

static void
dec_cRefThisDLL()
{
    InterlockedDecrement((LPLONG)&g_cRefThisDll);
}


//---------------------------------------------------------------------------
// DllCanUnloadNow
//---------------------------------------------------------------------------

STDAPI
DllCanUnloadNow(void)
{
    return (g_cRefThisDll == 0 ? S_OK : S_FALSE);
}

STDAPI
DllGetClassObject(REFCLSID rclsid, REFIID riid, LPVOID *ppvOut)
{
    *ppvOut = NULL;

    if (IsEqualIID(rclsid, CLSID_ShellExtension))
    {
        CShellExtClassFactory *pcf = new CShellExtClassFactory;
        return pcf->QueryInterface(riid, ppvOut);
    }

    return CLASS_E_CLASSNOTAVAILABLE;
}

//---------------------------------------------------------------------------
// Register/Unregister Server
//---------------------------------------------------------------------------

STDAPI
DllRegisterServer(void)
{
    DWORD dwDisp;
    HKEY hKey;
    LONG result = 0;
    OSVERSIONINFO osvi;
    char path[MAX_PATH] = {'\0'};
    char value[MAX_PATH] = {'\0'};

    if(!GetModuleFileName(g_hmodThisDll, path, MAX_PATH))
        return E_FAIL;

    // [HKEY_CLASSES_ROOT\CLSID\{F1763C8F-4F26-4a15-943F-2CA281BD3385}]
    // @="jEdit Shell Extension"
    result = RegCreateKeyEx(HKEY_CLASSES_ROOT,
                            "CLSID\\{F1763C8F-4F26-4a15-943F-2CA281BD3385}",
                            0,
                            NULL,
                            REG_OPTION_NON_VOLATILE,
                            KEY_WRITE,
                            NULL,
                            &hKey,
                            &dwDisp);
    if(result == ERROR_SUCCESS)
    {
        // @="jEdit Shell Extension"
        strcpy(value,"jEdit Shell Extension");
        RegSetValueEx(hKey,
                      NULL,
                      0,
                      REG_SZ,
                      (LPBYTE)value,
                      strlen(value) + 1);
        RegCloseKey(hKey);
    }
    else
        return E_FAIL;

    // [HKEY_CLASSES_ROOT\CLSID\{F1763C8F-4F26-4a15-943F-2CA281BD3385}\InProcServer32]
    // @="C:\\Sandbox\\jedit.exe\\jeditext.dll"
    // "ThreadingModel"="Apartment"
    result = RegCreateKeyEx(HKEY_CLASSES_ROOT,
                            "CLSID\\{F1763C8F-4F26-4a15-943F-2CA281BD3385}\\InProcServer32",
                            0,
                            NULL,
                            REG_OPTION_NON_VOLATILE,
                            KEY_WRITE,
                            NULL,
                            &hKey,
                            &dwDisp);
    if(result == ERROR_SUCCESS)
    {
        // @="C:\\Sandbox\\jedit.exe\\jeditext.dll"
        strcpy(value,path);
        RegSetValueEx(hKey,
                      NULL,
                      0,
                      REG_SZ,
                      (LPBYTE)value,
                      strlen(value) + 1);
        // "ThreadingModel"="Apartment"
        strcpy(value,"Apartment");
        RegSetValueEx(hKey,
                      "ThreadingModel",
                      0,
                      REG_SZ,
                      (LPBYTE)value,
                      strlen(value) + 1);
        RegCloseKey(hKey);
    }
    else
        return E_FAIL;

    // [HKEY_CLASSES_ROOT\*\shellex\ContextMenuHandlers\jedit]
    // @="{F1763C8F-4F26-4a15-943F-2CA281BD3385}"
    result = RegCreateKeyEx(HKEY_CLASSES_ROOT,
                            "*\\shellex\\ContextMenuHandlers\\jedit",
                            0,
                            NULL,
                            REG_OPTION_NON_VOLATILE,
                            KEY_WRITE,
                            NULL,
                            &hKey,
                            &dwDisp);
    if(result == ERROR_SUCCESS)
    {
        // @="{F1763C8F-4F26-4a15-943F-2CA281BD3385}"
        strcpy(value,"{F1763C8F-4F26-4a15-943F-2CA281BD3385}");
        RegSetValueEx(hKey,
                      NULL,
                      0,
                      REG_SZ,
                      (LPBYTE)value,
                      strlen(value) + 1);
        RegCloseKey(hKey);
    }
    else
        return E_FAIL;

    // [HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows\CurrentVersion\Shell Extensions\Approved]
    // "{F1763C8F-4F26-4a15-943F-2CA281BD3385}"="jEdit Shell Extension"
    // (only on NT)
    osvi.dwOSVersionInfoSize = sizeof(osvi);
    GetVersionEx(&osvi);
    if (VER_PLATFORM_WIN32_NT == osvi.dwPlatformId)
    {
        result = RegCreateKeyEx(HKEY_LOCAL_MACHINE,
                                "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Shell Extensions\\Approved",
                                0,
                                NULL,
                                REG_OPTION_NON_VOLATILE,
                                KEY_WRITE,
                                NULL,
                                &hKey,
                                &dwDisp);
        if(result == ERROR_SUCCESS)
        {
            // "{F1763C8F-4F26-4a15-943F-2CA281BD3385}"="jEdit Shell Extension"
            strcpy(value,"jEdit Shell Extension");
            RegSetValueEx(hKey,
                          "{F1763C8F-4F26-4a15-943F-2CA281BD3385}",
                          0,
                          REG_SZ,
                          (LPBYTE)value,
                          strlen(value) + 1);
            RegCloseKey(hKey);
        }
    }
    else
        return E_FAIL;

    return NOERROR;

}

STDAPI
DllUnregisterServer(void)
{
    char msg[1024] = {'\0'};
    char error[255] = {'\0'};
    const HKEY roots[] = {
            HKEY_CLASSES_ROOT,
            HKEY_LOCAL_MACHINE,
            HKEY_CLASSES_ROOT,
            HKEY_CLASSES_ROOT,
    };
    const char* keys[] = {
        "*\\shellex\\ContextMenuHandlers\\jedit",
        "Software\\Microsoft\\Windows\\CurrentVersion\\Shell Extensions\\Approved",
        "CLSID\\{F1763C8F-4F26-4a15-943F-2CA281BD3385}\\InProcServer32",
        "CLSID\\{F1763C8F-4F26-4a15-943F-2CA281BD3385}",
    };
    LONG results[] = {0,0,0,0};

    for(int i=0; i < 4; i++)
    {
        results[i] = RegDeleteKey(roots[i], keys[i]);
        if(results[i] != ERROR_SUCCESS && results[i] != ERROR_FILE_NOT_FOUND)
        {
            error[0] = 0;
            FormatMessage(FORMAT_MESSAGE_FROM_SYSTEM,
                          NULL,
                          results[i],
                          MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // default language
                          error,
                          255,
                          NULL);
            sprintf(msg, "Error removing: %s\n%s", keys[i], error);
            MessageBox(NULL, msg, "Problem Deleting Registry Key", MB_OK);
        }
    }

    for(i=0; i < 4; i++)
        if(results[i] != ERROR_SUCCESS && results[i] != ERROR_FILE_NOT_FOUND)
            return E_FAIL;
    return NOERROR;
}



CShellExtClassFactory::CShellExtClassFactory()
{
    m_cRef = 0L;

    inc_cRefThisDLL();
}

CShellExtClassFactory::~CShellExtClassFactory()
{
    dec_cRefThisDLL();
}


STDMETHODIMP
CShellExtClassFactory::QueryInterface(REFIID riid,
                                      LPVOID FAR *ppv)
{
    *ppv = NULL;

    // Any interface on this object is the object pointer

    if (IsEqualIID(riid, IID_IUnknown) || IsEqualIID(riid, IID_IClassFactory))
    {
        *ppv = (LPCLASSFACTORY)this;

        AddRef();

        return NOERROR;
    }

    return E_NOINTERFACE;
}

STDMETHODIMP_(ULONG)
CShellExtClassFactory::AddRef()
{
    return InterlockedIncrement((LPLONG)&m_cRef);
}

STDMETHODIMP_(ULONG)
CShellExtClassFactory::Release()
{
    if (InterlockedDecrement((LPLONG)&m_cRef))
        return m_cRef;

    delete this;

    return 0L;
}

STDMETHODIMP
CShellExtClassFactory::CreateInstance(LPUNKNOWN pUnkOuter,
                                      REFIID riid,
                                      LPVOID *ppvObj)
{
    *ppvObj = NULL;

    // Shell extensions typically don't support aggregation (inheritance)

    if (pUnkOuter)
        return CLASS_E_NOAGGREGATION;

    // Create the main shell extension object.  The shell will then call
    // QueryInterface with IID_IShellExtInit--this is how shell extensions are
    // initialized.

    LPCSHELLEXT pShellExt = new CShellExt();  //Create the CShellExt object

    if (NULL == pShellExt)
        return E_OUTOFMEMORY;

    return pShellExt->QueryInterface(riid, ppvObj);
}


STDMETHODIMP
CShellExtClassFactory::LockServer(BOOL fLock)
{
    return NOERROR;
}


CShellExt::CShellExt()
{
    /*
    hImage = LoadImage(g_hmodThisDll,
                       MAKEINTRESOURCE(101),
                       IMAGE_BITMAP,
                       0,
                       0,
                       LR_LOADTRANSPARENT);
    */
    m_cRef = 0L;
    m_pDataObj = NULL;

    inc_cRefThisDLL();
}

CShellExt::~CShellExt()
{
    if (m_pDataObj)
        m_pDataObj->Release();

    /*
    if(hImage)
        DeleteObject(hImage);
    */
    dec_cRefThisDLL();
}

STDMETHODIMP
CShellExt::QueryInterface(REFIID riid, LPVOID FAR *ppv)
{
    *ppv = NULL;

    if (IsEqualIID(riid, IID_IShellExtInit) || IsEqualIID(riid, IID_IUnknown))
    {
        *ppv = (LPSHELLEXTINIT)this;
    }
    else if (IsEqualIID(riid, IID_IContextMenu))
    {
        *ppv = (LPCONTEXTMENU)this;
    }

    if (*ppv)
    {
        AddRef();
        return NOERROR;
    }

    return E_NOINTERFACE;
}

STDMETHODIMP_(ULONG)
CShellExt::AddRef()
{
    return InterlockedIncrement((LPLONG)&m_cRef);
}

STDMETHODIMP_(ULONG)
CShellExt::Release()
{

    if (InterlockedDecrement((LPLONG)&m_cRef))
        return m_cRef;

    delete this;

    return 0L;
}


//
//  FUNCTION: CShellExt::Initialize(LPCITEMIDLIST, LPDATAOBJECT, HKEY)
//
//  PURPOSE: Called by the shell when initializing a context menu or property
//       sheet extension.
//
//  PARAMETERS:
//    pIDFolder - Specifies the parent folder
//    pDataObj  - Spefifies the set of items selected in that folder.
//    hRegKey   - Specifies the type of the focused item in the selection.
//
//  RETURN VALUE:
//
//    NOERROR in all cases.
//
//  COMMENTS:   Note that at the time this function is called, we don't know
//      (or care) what type of shell extension is being initialized.
//      It could be a context menu or a property sheet.
//

STDMETHODIMP
CShellExt::Initialize(LPCITEMIDLIST pIDFolder,
                      LPDATAOBJECT pDataObj,
                      HKEY hRegKey)
{
    // Initialize can be called more than once
    if (m_pDataObj)
        m_pDataObj->Release();

    // duplicate the object pointer and registry handle

    if (pDataObj)
    {
        m_pDataObj = pDataObj;
        pDataObj->AddRef();
    }

    return NOERROR;
}



//
//  FUNCTION: CShellExt::QueryContextMenu(HMENU, UINT, UINT, UINT, UINT)
//
//  PURPOSE: Called by the shell just before the context menu is displayed.
//       This is where you add your specific menu items.
//
//  PARAMETERS:
//    hMenu      - Handle to the context menu
//    indexMenu  - Index of where to begin inserting menu items
//    idCmdFirst - Lowest value for new menu ID's
//    idCmtLast  - Highest value for new menu ID's
//    uFlags     - Specifies the context of the menu event
//

STDMETHODIMP
CShellExt::QueryContextMenu(HMENU hMenu,
                            UINT indexMenu,
                            UINT idCmdFirst,
                            UINT idCmdLast,
                            UINT uFlags)
{
    UINT idCmd = idCmdFirst;
    char label[MAX_PATH] = {'\0'};
    char ext[MAX_PATH] = {'\0'};
    char path[MAX_PATH] = {'\0'};
    char filename[MAX_PATH] = {'\0'};
    char drive[MAX_PATH] = {'\0'};
    char directory[MAX_PATH] = {'\0'};


    hres = m_pDataObj->GetData(&fmte, &medium);
    // get number of files selected
    if (medium.hGlobal)
        cbFiles = DragQueryFile((HDROP)medium.hGlobal, (UINT)-1, 0, 0);

    InsertMenu(hMenu, indexMenu++, MF_SEPARATOR|MF_BYPOSITION, 0, NULL);

    InsertMenu(hMenu,
            indexMenu++,
            MF_STRING|MF_BYPOSITION,
            idCmd++,
            "Open with &jEdit");
    /*
    SetMenuItemBitmaps(hMenu,
                       indexMenu-1,
                       MF_BYPOSITION,
                       (HBITMAP)hImage,
                       (HBITMAP)hImage);
    */
    if(cbFiles == 2)
    {
        // Can edit up to 4 files in diff mode
        InsertMenu(hMenu,
            indexMenu++,
            MF_STRING|MF_BYPOSITION,
            idCmd++,
            "&Diff with jEdit");
    }
    else if(cbFiles == 1)
    {
        DragQueryFile((HDROP)medium.hGlobal,
                      0,
                      path,
                      MAX_PATH);
        _splitpath(path,drive,directory,filename,ext);
        if(ext && strlen(ext))
        {
            sprintf(label, "Open &*%s with jEdit", ext);
            InsertMenu(hMenu,
                       indexMenu++,
                       MF_STRING|MF_BYPOSITION,
                       idCmd++,
                       label);
        }
    }

    InsertMenu(hMenu, indexMenu++, MF_SEPARATOR|MF_BYPOSITION, 0, NULL);

    // Must return number of menu items we added.
    return ResultFromShort(idCmd-idCmdFirst);
}


STDMETHODIMP
CShellExt::GetCommandString(UINT idCmd,
                            UINT uFlags,
                            UINT FAR *reserved,
                            LPSTR pszName,
                            UINT cchMax)
{
    if (uFlags == GCS_HELPTEXT && cchMax > 35)
    {
        if(idCmd == 0)
            lstrcpy(pszName, "Open the selected file(s) with jEdit");
        else if(cbFiles == 2)
        {
            lstrcpy(pszName, "Diff the selected files with jEdit");
        }
        else
        {
            lstrcpy(pszName, "Open matching files with jEdit");
        }
    }

    return NOERROR;
}


//
//  FUNCTION: CShellExt::InvokeCommand(LPCMINVOKECOMMANDINFO)
//
//  PURPOSE: Called by the shell after the user has selected on of the
//       menu items that was added in QueryContextMenu().
//
//  PARAMETERS:
//    lpcmi - Pointer to an CMINVOKECOMMANDINFO structure
//

STDMETHODIMP
CShellExt::InvokeCommand(LPCMINVOKECOMMANDINFO lpcmi)
{
    HRESULT hr = E_INVALIDARG;

    // If HIWORD(lpcmi->lpVerb) then we have been called programmatically
    // and lpVerb is a command that should be invoked.  Otherwise, the shell
    // has called us, and LOWORD(lpcmi->lpVerb) is the menu ID the user has
    // selected.  Actually, it's (menu ID - idCmdFirst) from QueryContextMenu().
    if (!HIWORD(lpcmi->lpVerb))
    {
        UINT idCmd = LOWORD(lpcmi->lpVerb);

        switch (idCmd)
        {
        case 0:
            hr = OpenInJEdit(lpcmi->hwnd,
                             lpcmi->lpDirectory,
                             lpcmi->lpVerb,
                             lpcmi->lpParameters,
                             lpcmi->nShow);
            break;
        case 1:
            if(cbFiles == 2)
            {
                hr = DiffWithJEdit(lpcmi->hwnd,
                                   lpcmi->lpDirectory,
                                   lpcmi->lpVerb,
                                   lpcmi->lpParameters,
                                   lpcmi->nShow);
            }
            else
            {
                hr = OpenByExtInJEdit(lpcmi->hwnd,
                                      lpcmi->lpDirectory,
                                      lpcmi->lpVerb,
                                      lpcmi->lpParameters,
                                      lpcmi->nShow);
            }
            break;
        }
    }
    return hr;
}

STDMETHODIMP
CShellExt::DiffWithJEdit(HWND hParent,
                         LPCSTR pszWorkingDir,
                         LPCSTR pszCmd,
                         LPCSTR pszParam,
                         int iShowCmd)
{
    LaunchConfig config;
    char file1[MAX_PATH] = {'\0'};
    char file2[MAX_PATH] = {'\0'};

    if(cbFiles != 2)
        return E_FAIL;  // expecting 2 args

    DragQueryFile((HDROP)medium.hGlobal,
                      0,
                      file1,
                      MAX_PATH);
    DragQueryFile((HDROP)medium.hGlobal,
                      1,
                      file2,
                      MAX_PATH);

    if(get_launch_config(&config))
    {
        MessageBox(hParent,
                   "Failed to get jEdit & Java configuration from registry.\n"
                   "\n"
                   "See 'jeditlauncher.log' in your HOME directory for details.",
                   "Can't open jEdit",
                   MB_OK);
        return E_FAIL;
    }

    if(!diff_files_in_jedit(config.java,
                            config.java_opts,
                            config.jedit_jar,
                            config.jedit_opts,
                            config.working_dir,
                            file1,
                            file2))
        return NOERROR;
    return E_FAIL;

}

STDMETHODIMP
CShellExt::OpenByExtInJEdit(HWND hParent,
                            LPCSTR pszWorkingDir,
                            LPCSTR pszCmd,
                            LPCSTR pszParam,
                            int iShowCmd)
{
    LaunchConfig config;
    char** files = 0;
    int nfiles = 0;
    char* globs[] = {'\0'};
    char glob[MAX_PATH] = {'\0'};

    char directory[MAX_PATH] = {'\0'};
    char drive[MAX_PATH] = {'\0'};
    char ext[MAX_PATH] = {'\0'};
    char filename[MAX_PATH] = {'\0'};
    char path[MAX_PATH] = {'\0'};

    DragQueryFile((HDROP)medium.hGlobal,
                  0,
                  path,
                  MAX_PATH);

    // split path into chunks, so we can find
    // all matching files in the directory
    _splitpath(path,drive,directory,filename,ext);
    // <drive>\<dir>\*<ext>
    sprintf(glob,"%s%s*%s", drive, directory, ext);

    // find number of files
    globs[0] = glob;
    nfiles = -1 * expand_globs((const char**)globs,
                               1,
                               files,
                               0);

    // allocate file list
    int nalloc = (nfiles) * sizeof(char*);

    files = (char**)malloc(nalloc);
    if(!files)
        return E_OUTOFMEMORY;

    // fill the file list
    nfiles = expand_globs((const char**)&globs,
                          1,
                          files,
                          nfiles);

    if(get_launch_config(&config))
    {
        MessageBox(hParent,
                   "Failed to get jEdit & Java configuration from registry.\n"
                   "\n"
                   "See 'jeditlauncher.log' in your HOME directory for details.",
                   "Can't open jEdit",
                   MB_OK);
        return E_FAIL;
    }

    // XXX handle error
    open_files_in_jedit(config.java,
                        config.java_opts,
                        config.jedit_jar,
                        config.jedit_opts,
                        config.working_dir,
                        NULL,   // no command-line options
                        (const char**)files,
                        nfiles);

    return NOERROR;
}

STDMETHODIMP
CShellExt::OpenInJEdit(HWND hParent,
                       LPCSTR pszWorkingDir,
                       LPCSTR pszCmd,
                       LPCSTR pszParam,
                       int iShowCmd)
{
    int r = 0;
    LaunchConfig config;
    char** files = 0;

    files = (char**)malloc(sizeof(char*) * cbFiles);
    if(!files)
        return E_OUTOFMEMORY;

    for(int i=0; i < cbFiles; i++)
    {
        files[i] = (char*)malloc(sizeof(char) * MAX_PATH);
        if(!files[i])
            return E_OUTOFMEMORY;
        DragQueryFile((HDROP)medium.hGlobal,
                      i,
                      files[i],
                      MAX_PATH);
    }

    if(get_launch_config(&config))
    {
        MessageBox(hParent,
                   "Failed to get jEdit & Java configuration from registry.\n"
                   "\n"
                   "See 'jeditlauncher.log' in your HOME directory for details.",
                   "Can't open jEdit",
                   MB_OK);
        return E_FAIL;
    }

    r = open_files_in_jedit(config.java,
                            config.java_opts,
                            config.jedit_jar,
                            config.jedit_opts,
                            config.working_dir,
                            NULL,   // no command-line options
                            (const char**)files,
                            cbFiles);

    // free files and file list
    for(i=0; i < cbFiles; i++)
        free(files[i]);
    free(files);

    if(r)
        return E_FAIL;
    return NOERROR;
}
