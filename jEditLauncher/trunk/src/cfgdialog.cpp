/////////////////////////////////////////////////////////////////////////////
//
// MainDlg.cpp : implementation of the CMainDlg class
//
/////////////////////////////////////////////////////////////////////////////

#include "resource.h"

#include "cfgdialog.h"
#include "jeditlib.h"

extern CAppModule _Module;

LRESULT CMainDlg::OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
{
    // center the dialog on the screen
    CenterWindow();

    // set icons
    HICON hIcon = (HICON)::LoadImage(_Module.GetResourceInstance(), MAKEINTRESOURCE(IDR_MAINFRAME),
        IMAGE_ICON, ::GetSystemMetrics(SM_CXICON), ::GetSystemMetrics(SM_CYICON), LR_DEFAULTCOLOR);
    SetIcon(hIcon, TRUE);
    HICON hIconSmall = (HICON)::LoadImage(_Module.GetResourceInstance(), MAKEINTRESOURCE(IDR_MAINFRAME),
        IMAGE_ICON, ::GetSystemMetrics(SM_CXSMICON), ::GetSystemMetrics(SM_CYSMICON), LR_DEFAULTCOLOR);
    SetIcon(hIconSmall, FALSE);

    // fetch settings
    get_java_path(m_szJavaPath);
    get_java_opts(m_szJavaOpts);
    get_jedit_jar(m_szJeditPath);
    get_jedit_opts(m_szJeditOpts);
    get_working_dir(m_szWorkingDir);
    // strip quotes
    strip_quotes(m_szJavaPath);
    strip_quotes(m_szJeditPath);
    strip_quotes(m_szWorkingDir);
    // display settings
    SetDlgItemText(IDC_JAVA_PATH,m_szJavaPath);
    SetDlgItemText(IDC_JAVA_OPTS,m_szJavaOpts);
    SetDlgItemText(IDC_JEDIT_PATH,m_szJeditPath);
    SetDlgItemText(IDC_JEDIT_OPTS,m_szJeditOpts);
    SetDlgItemText(IDC_WORKING_DIR,m_szWorkingDir);

    return TRUE;
}

LRESULT CMainDlg::OnOK(WORD /*wNotifyCode*/, WORD wID, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
{
    // XXX validate
    int r = 0;
    char err_msg[100] = {0};

    // copy settings
    GetDlgItemText(IDC_JAVA_PATH,m_szJavaPath,MAX_PATH);
    GetDlgItemText(IDC_JAVA_OPTS,m_szJavaOpts,MAX_PATH);
    GetDlgItemText(IDC_JEDIT_PATH,m_szJeditPath,MAX_PATH);
    GetDlgItemText(IDC_JEDIT_OPTS,m_szJeditOpts,MAX_PATH);
    GetDlgItemText(IDC_WORKING_DIR,m_szWorkingDir,MAX_PATH);
    // strip quotes
    strip_quotes(m_szJavaPath);
    strip_quotes(m_szJeditPath);
    strip_quotes(m_szWorkingDir);
    // save settings
    r = set_java_path(m_szJavaPath);
    if(!r) r = set_java_opts(m_szJavaOpts);
    if(!r) r = set_jedit_jar(m_szJeditPath);
    if(!r) r = set_jedit_opts(m_szJeditOpts);
    if(!r) r = set_working_dir(m_szWorkingDir);
    if(r)
    {
        sprintf(err_msg,"Error saving settings: %d", r);
        MessageBox(err_msg, "Error", MB_OK | MB_ICONERROR);
    }

    if(!r)
        EndDialog(wID);
    return r;
}

LRESULT CMainDlg::OnCancel(WORD /*wNotifyCode*/, WORD wID, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
{
    EndDialog(wID);
    return 0;
}

LRESULT CMainDlg::OnBnClickedBrowseJava(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
{
    CFileDialog dlg(TRUE, _T("exe"), NULL, OFN_FILEMUSTEXIST | OFN_HIDEREADONLY,  _T("Executable Files (*.exe)\0*.exe\0"), m_hWnd);
    if(dlg.DoModal() == IDOK)
    {
        lstrcpy(m_szJavaPath,dlg.m_szFileName);
        SetDlgItemText(IDC_JAVA_PATH,m_szJavaPath);
    }

    return 0;
}

LRESULT CMainDlg::OnBnClickedBrowseJedit(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
{
    CFileDialog dlg(TRUE, _T("jar"), NULL, OFN_FILEMUSTEXIST | OFN_HIDEREADONLY,  _T("Jar Files (*.jar)\0*.jar\0\0All Files (*.*)\0*.*\0"), m_hWnd);
    if(dlg.DoModal() == IDOK)
    {
        // XXX set working directory
        lstrcpy(m_szJeditPath,dlg.m_szFileName);
        SetDlgItemText(IDC_JEDIT_PATH,m_szJeditPath);
    }

    return 0;
}

LRESULT CMainDlg::OnBnClickedBrowseWorkingDir(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
{
    CFolderDialog dlg(m_hWnd,"Working Directory",BIF_RETURNONLYFSDIRS);
    if(dlg.DoModal() == IDOK)
    {
        lstrcpy(m_szWorkingDir,dlg.m_szFolderPath);
        SetDlgItemText(IDC_WORKING_DIR,m_szWorkingDir);
    }

    return 0;
}

void CMainDlg::UpdateCommandLine(void)
{
    char szCmd[MAX_PATH*10] = {0};
    create_launch_command(m_szJavaPath,m_szJavaOpts,
                          m_szJeditPath,m_szJeditOpts,
                          m_szWorkingDir,
                          NULL,   // OptList
                          NULL,   // filename
                          0,      // nfiles
                          szCmd,
                          MAX_PATH*10);
    SetDlgItemText(IDC_CMD_LINE,szCmd);
}

LRESULT CMainDlg::OnEnChangeJeditOpts(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
{
    UpdateCommandLine();
    return 0;
}

LRESULT CMainDlg::OnEnChangeJeditPath(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
{
    UpdateCommandLine();
    return 0;
}

LRESULT CMainDlg::OnEnChangeJavaOpts(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
{
    UpdateCommandLine();
    return 0;
}

LRESULT CMainDlg::OnEnChangeJavaPath(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
{
    UpdateCommandLine();
    return 0;
}
