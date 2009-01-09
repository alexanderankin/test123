#{{{ Marathon Fixture
from default import *
#}}} Marathon Fixture

def test():
	java_recorded_version = '1.6.0_11'

	if window(r'/jEdit - .* \[Project: CtagsSideKick\]'):
		doubleclick('ProjectTreePanel$PVTree', 'ctags')
		click('ProjectTreePanel$PVTree', 'CtagsSideKick')
		select('ProjectTreePanel$PVTree', '[CtagsSideKick]')
		rightclick('ProjectTreePanel$PVTree', 'CtagsSideKick')
		select_menu('Re-import files #{F5#}')
		select('ProjectTreePanel$PVTree', '[CtagsSideKick]')

		if window('Re-Import Files'):
			click('Import')
		close()

		click('ProjectTreePanel$PVTree', 'build')
		click('ProjectTreePanel$PVTree', 'ctags')
		doubleclick('ProjectTreePanel$PVTree', 'ctags')
		doubleclick('ProjectTreePanel$PVTree', 'sidekick')
		select('ProjectTreePanel$PVTree', 'CtagsSideKickTreeNode.java', 'CtagsSideKickTreeNode.java')

		window_closed(r'/jEdit - .* \[Project: CtagsSideKick\]')
	close()
