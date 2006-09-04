package jump;

import java.awt.Point;
import java.io.File;

import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.JEditTextArea;

import projectviewer.ProjectViewer;
import projectviewer.vpt.VPTProject;

public class Jump {

	public Jump() {
		String s = System.getProperty("file.separator");
		File jumpDir = new File(System.getProperty("user.home") + s + ".jedit" +
				s + "jump");

		if (!jumpDir.exists()) {
			jumpDir.mkdirs();
		}
	}

	public boolean isJumpEnabled() {
		if (!jEdit.getBooleanProperty("jump.enable", false)) {
			GUIUtilities.message(jEdit.getActiveView(), "JumpPlugin.enable",
				null);

			return false;
		}

		return true;
	}

	// Is any active VTProject loaded?
	// TODO: this method must be called just once!!!
	public boolean isProjectLoaded() {
		return null != ProjectViewer.getActiveProject(jEdit.getActiveView());
	}

	public void showFilesJump() {
		// QUESTION: May be view field must be class-field?
		View view = jEdit.getActiveView();

		if (!check()) return;

		ProjectBuffer pb = getProjectBuffer(view);
		if (pb.ctagsBuffer != null) {
			JumpPlugin.fja.showList();
		}
	}

	public void showTagsJump() {
		View view = jEdit.getActiveView();

		if (!check()) return;

		ProjectBuffer pb = getProjectBuffer(view);
		if (pb.ctagsBuffer != null) {
			JumpPlugin.tja.showList();
		}
	}

	public void showProjectJump() {
		View view = jEdit.getActiveView();

		if (!check()) return;

		ProjectBuffer pb = getProjectBuffer(view);
		if (pb.ctagsBuffer != null) {
			JumpPlugin.pja.JumpToTag();
		}
	}

	public void showFoldJump() {
		FoldJumpAction foldja = new FoldJumpAction();
		foldja.showFoldsList();
	}

	public void completeTag(boolean isGlobalSearch) {
		View view = jEdit.getActiveView();

		if (!check()) return;

		ProjectBuffer pb = getProjectBuffer(view);
		if (pb.ctagsBuffer != null) {
			JumpPlugin.pja.completeTag(isGlobalSearch);
		}
	}

	public void reloadTagsOnProject() {
		check();
	}

	public void historyJump() {
		View view = jEdit.getActiveView();

		if (!check()) return;

		ProjectBuffer pb = getProjectBuffer(view);
		if (pb.ctagsBuffer != null) {
			JumpPlugin.pja.JumpToPreviousTag();
		}
	}

	public void jumpByInput() {
		View view = jEdit.getActiveView();

		if (!check()) return;

		ProjectBuffer pb = getProjectBuffer(view);
		if (pb.ctagsBuffer != null) {
			JumpPlugin.pja.JumpToTagByInput();
		}
	}

	public static Point getListLocation() {
		JEditTextArea textArea = jEdit.getActiveView().getTextArea();
		textArea.scrollToCaret(false);

		int caret = textArea.getCaretPosition();

		//String sel = textArea.getSelectedText();
		Point location = textArea.offsetToXY(caret);
		location.y += textArea.getPainter().getFontMetrics().getHeight();
		SwingUtilities.convertPointToScreen(location, textArea.getPainter());

		return location;
	}

	private boolean check() {
		if (!isJumpEnabled()) {
			return false;
		}

		if (!isProjectLoaded()) {
			return false;
		}

		if (!JumpPlugin.isListenerAdded) {
			JumpPlugin.init();
		}

		return true;
	}

	private ProjectBuffer getProjectBuffer(View view) {
		JumpPlugin.getListener().reloadProjectForced();
		if (JumpPlugin.getActiveProjectBuffer() != null) {
			return JumpPlugin.getActiveProjectBuffer();
		} else {
			VPTProject activep = ProjectViewer.getActiveProject(view);
			ProjectBuffer b = new ProjectBuffer(activep.getName());
			b.init(activep);
			JumpPlugin.setActiveProjectBuffer(b);
			return b;
		}
	}

}

