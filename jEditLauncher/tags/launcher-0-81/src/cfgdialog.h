/////////////////////////////////////////////////////////////////////////////
//
// jEdit Launcher Config Dialog
//
// cfgdialog.h : interface of the CMainDlg class
//
/////////////////////////////////////////////////////////////////////////////

#pragma once

class CMainDlg : public CDialogImpl<CMainDlg>
{
public:
	enum { IDD = IDD_MAINDLG };

	char m_szJavaPath[MAX_PATH];
	char m_szJavaOpts[MAX_PATH];
	char m_szJeditPath[MAX_PATH];
	char m_szJeditOpts[MAX_PATH];
	char m_szWorkingDir[MAX_PATH];

	BEGIN_MSG_MAP(CMainDlg)
		MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
		COMMAND_ID_HANDLER(IDOK, OnOK)
		COMMAND_ID_HANDLER(IDCANCEL, OnCancel)
		COMMAND_HANDLER(IDC_BROWSE_JAVA, BN_CLICKED, OnBnClickedBrowseJava)
		COMMAND_HANDLER(IDC_BROWSE_JEDIT, BN_CLICKED, OnBnClickedBrowseJedit)
		COMMAND_HANDLER(IDC_BROWSE_WORKING_DIR, BN_CLICKED, OnBnClickedBrowseWorkingDir)
		COMMAND_HANDLER(IDC_JEDIT_OPTS, EN_CHANGE, OnEnChangeJeditOpts)
		COMMAND_HANDLER(IDC_JEDIT_PATH, EN_CHANGE, OnEnChangeJeditPath)
		COMMAND_HANDLER(IDC_JAVA_OPTS, EN_CHANGE, OnEnChangeJavaOpts)
		COMMAND_HANDLER(IDC_JAVA_PATH, EN_CHANGE, OnEnChangeJavaPath)
	END_MSG_MAP()

// Handler prototypes (uncomment arguments if needed):
//	LRESULT MessageHandler(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/)
//	LRESULT CommandHandler(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/)
//	LRESULT NotifyHandler(int /*idCtrl*/, LPNMHDR /*pnmh*/, BOOL& /*bHandled*/)

	LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& /*bHandled*/);
	LRESULT OnOK(WORD /*wNotifyCode*/, WORD wID, HWND /*hWndCtl*/, BOOL& /*bHandled*/);
	LRESULT OnCancel(WORD /*wNotifyCode*/, WORD wID, HWND /*hWndCtl*/, BOOL& /*bHandled*/);
	LRESULT OnBnClickedBrowseJava(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/);
	LRESULT OnBnClickedBrowseJedit(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/);
	LRESULT OnBnClickedBrowseWorkingDir(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/);
	LRESULT OnEnChangeWorkingDir(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/);
	LRESULT OnEnChangeJeditOpts(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/);
	LRESULT OnEnChangeJeditPath(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/);
	LRESULT OnEnChangeJavaOpts(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/);
	LRESULT OnEnChangeJavaPath(WORD /*wNotifyCode*/, WORD /*wID*/, HWND /*hWndCtl*/, BOOL& /*bHandled*/);

	void UpdateCommandLine(void);

	CMainDlg()
	{
		m_szJavaPath[0] = 0;
		m_szJavaOpts[0] = 0;
		m_szJeditPath[0] = 0;
		m_szJeditOpts[0] = 0;
		m_szWorkingDir[0] = 0;
	}
};
