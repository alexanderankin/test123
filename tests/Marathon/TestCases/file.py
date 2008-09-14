useFixture(default)

def test():
    java_recorded_version = '1.6.0_07'

    # Test File->Open
    if window(r'/jEdit - .*'):
        click('document-open')

        if window('File Browser'):
            select('File name', r'd:\jedit\tests\marathon\testcases\file.py')
            click('Open')
        close()
    close()

    if window(r'/jEdit - .*[/\\]?file.py'):
        s = getComponent('JEditTextArea').getText()
        assert s.find('if window(r\'/jEdit - .*[/\\\\]?file.py\'):') >= 0
        # Test File-New
        click('document-new')
    close()

    if window(r'/jEdit - Untitled-1'):
        s = getComponent('JEditTextArea').getText()
        assert len(s) == 0
    close()


