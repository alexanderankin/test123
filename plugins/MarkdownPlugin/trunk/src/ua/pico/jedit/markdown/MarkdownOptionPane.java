package ua.pico.jedit.markdown;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import org.pegdown.Extensions;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MarkdownOptionPane extends AbstractOptionPane implements ChangeListener {

	public MarkdownOptionPane() {
		super(MarkdownPlugin.NAME);

		final String label = ".label";
		final ButtonGroup targetGroup = new ButtonGroup();
		final ButtonGroup markdownGroup = new ButtonGroup();

		markdownUtil = MarkdownUtil.getInstance();
		
		// View options
		addSeparator("options.markdown.target.label");
		bufferButton = new JRadioButton(jEdit.getProperty("options.markdown.target.buffer.label"));
		clipboardButton = new JRadioButton(jEdit.getProperty("options.markdown.target.clipboard.label"));
		browserButton = new JRadioButton(jEdit.getProperty("options.markdown.target.browser.label"));
		targetGroup.add(bufferButton);
		targetGroup.add(clipboardButton);
		targetGroup.add(browserButton);
		addComponent(bufferButton);
		addComponent(clipboardButton);
		addComponent(browserButton);
		// Markdown options
		addSeparator("options.markdown.extensions.label");
		noneButton = new JRadioButton(jEdit.getProperty("options.markdown.none.label"));
		allButton = new JRadioButton(jEdit.getProperty("options.markdown.all.label"));
		chooseButton = new JRadioButton(jEdit.getProperty("options.markdown.choose.label"));
		markdownGroup.add(noneButton);
		markdownGroup.add(allButton);
		markdownGroup.add(chooseButton);
		addComponent(noneButton);
		addComponent(allButton);
		addComponent(chooseButton);
		noneButton.setSelected(true);
		// Markdown extensions
		extensions = new JCheckBox[MarkdownUtil.EXTENSION_ID.length];
		for (int i = 0; i < extensions.length; i++) {
			extensions[i] = new JCheckBox(jEdit.getProperty(MarkdownPlugin.OPTION_PREFIX + MarkdownUtil.EXTENSION_NAME[i] + label));
			extensions[i].setEnabled(false);
			addComponent(extensions[i]);
		}
		chooseButton.addChangeListener(this);
	}

	public void stateChanged(final ChangeEvent event) {
		if (chooseButton == event.getSource()) {
			if (chooseButton.isSelected()) {
				for (JCheckBox extension : extensions) {
					extension.setEnabled(true);
				}
			} else {
				for (JCheckBox extension : extensions) {
					extension.setEnabled(false);
				}
			}
		}
	}

	@Override
	protected void _init() {
		final int usedExtensions = markdownUtil.getExtensions();

		switch (markdownUtil.getTarget()) {
		case Clipboard:
			clipboardButton.setSelected(true);
			break;
		case Browser:
			browserButton.setSelected(true);
			break;
		case Buffer:
		default:
			bufferButton.setSelected(true);
		}
		switch (usedExtensions) {
		case Extensions.NONE:
			noneButton.setSelected(true);
			break;
		case Extensions.ALL:
			allButton.setSelected(true);
			break;
		default:
			chooseButton.setSelected(true);
		}
		if (chooseButton.isSelected()) {
			for (int i = 0; i < MarkdownUtil.EXTENSION_ID.length; i++) {
				if (MarkdownUtil.EXTENSION_ID[i] == (usedExtensions & MarkdownUtil.EXTENSION_ID[i])) {
					extensions[i].setSelected(true);
				}
			}
		}
	}

	@Override
	protected void _save() {
		int usedExtensions;

		if (clipboardButton.isSelected()) {
			markdownUtil.setTarget(MarkdownPlugin.Target.Clipboard);
		} else if (browserButton.isSelected()) {
			markdownUtil.setTarget(MarkdownPlugin.Target.Browser);
		} else {
			markdownUtil.setTarget(MarkdownPlugin.Target.Buffer);
		}
		if (noneButton.isSelected()) {
			usedExtensions = Extensions.NONE;
		} else if (allButton.isSelected()) {
			usedExtensions = Extensions.ALL;
		} else {
			usedExtensions = Extensions.NONE;
			for (int i = 0; i < extensions.length; i++) {
				if (extensions[i].isSelected()) {
					usedExtensions |= MarkdownUtil.EXTENSION_ID[i];
				}
			}
		}
		markdownUtil.setExtensions(usedExtensions);
	}

	private MarkdownUtil markdownUtil;
	private JRadioButton bufferButton;
	private JRadioButton clipboardButton;
	private JRadioButton browserButton;
	private JRadioButton noneButton;
	private JRadioButton allButton;
	private JRadioButton chooseButton;
	private JCheckBox extensions[];

}
