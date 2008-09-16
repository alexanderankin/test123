useFixture(default)

def test():
    java_recorded_version = '1.6.0_07'

    inputFile = r'd:\jedit\tests\marathon\testcases\file.py'
    if window(r'/jEdit - .*'):
        select_menu('File>>Close All')
        # Test File->Open
        click('document-open')

        if window('File Browser'):
            select('File name', inputFile)
            click('Open')
        close()
    close()

    if window(r'/jEdit - .*[/\\]?file.py'):
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
        close()
    close()


