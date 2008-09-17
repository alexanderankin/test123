useFixture(default)

import os

def enterString(s):
    for i in range(len(s)):
        keystroke('JEditTextArea', s[i])

def test():
    java_recorded_version = '1.6.0_07'

    baseDir = r'd:\jedit\tests\marathon\testcases\\'
    inputFile = baseDir + r'file.py'

    if window(r'/jEdit - .*'):
        select_menu('File>>Close All')
        # Test File-Open
        click('document-open')

        if window('File Browser'):
            select('File name', inputFile)
            click('Open')
        close()
    close()

    if window(r'/jEdit - .*[/\\]?file.py(\s.*)?'):
        s = getComponent('JEditTextArea').getText()
        l1 = s.splitlines(true)
        l2 = open(inputFile).readlines()
        l2.pop()
        assert l1 == l2

        # Test File-New
        click('document-new')

        if window(r'/jEdit - Untitled-1'):
            s = getComponent('JEditTextArea').getText()
            assert len(s) == 0

        # Test File-Save as
        enterString('Some dummy text to test the Save As operation.')
        select_menu('File>>Save As...')

        outFile = baseDir + r'dummy.txt'
        try:
            os.remove(outFile)
        except:
            pass
        if window('File Browser'):
            select('File name', outFile)
            click('Save')
        close()
        sleep(1)
        assert os.path.exists(outFile) == 1

        # Test File-Save
        keystroke('JEditTextArea', 'Enter')
        enterString(r'A second dummy text to test the Save As operation.')
        select_menu('File>>Save')
        l = open(inputFile).readlines()
        assert len(l) >= 2

    close()


