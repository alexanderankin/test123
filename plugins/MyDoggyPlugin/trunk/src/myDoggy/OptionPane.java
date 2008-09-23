package myDoggy;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
import org.noos.xing.mydoggy.PushAwayMode;

@SuppressWarnings("serial")
public class OptionPane extends AbstractOptionPane {

	private static final String PREFIX = "options.mydoggy.";
	private static final String PUSH_AWAY_MODE_PROP = PREFIX + "pushAwayMode";
	private static final String PUSH_AWAY_MODE_LABEL = PUSH_AWAY_MODE_PROP + ".label";
	private static final String USE_ALTERNATE_LAYOUT_PROP = PREFIX + "useAlternateLayout";
	private static final String USE_ALTERNATE_LAYOUT_LABEL = USE_ALTERNATE_LAYOUT_PROP + ".label";
	private static final String FLOAT_ON_TOP_PROP = PREFIX + "floatOnTop";
	private static final String FLOAT_ON_TOP_LABEL = FLOAT_ON_TOP_PROP + ".label";
	private static final String FLOAT_OS_DECORATIONS_PROP = PREFIX + "floatOsDecorations";
	private static final String FLOAT_OS_DECORATIONS_LABEL = FLOAT_OS_DECORATIONS_PROP + ".label";
	private static final String FLOAT_ADD_TO_TASKBAR_PROP = PREFIX + "floatAddToTaskBar";
	private static final String FLOAT_ADD_TO_TASKBAR_LABEL = FLOAT_ADD_TO_TASKBAR_PROP + ".label";
	private static final String ENABLE_PREVIEW_PROP = PREFIX + "enablePreview";
	private static final String ENABLE_PREVIEW_LABEL = ENABLE_PREVIEW_PROP + ".label";
	private static final String ENABLE_ANIMATIONS_PROP = PREFIX + "enableAnimations";
	private static final String ENABLE_ANIMATIONS_LABEL = ENABLE_ANIMATIONS_PROP + ".label";
	JComboBox pushAwayMode;
	JCheckBox useAlternateLayout;
	JCheckBox floatOnTop;
	JCheckBox floatOsDecorations;
	JCheckBox floatAddToTaskBar;
	JCheckBox enablePreview;
	JCheckBox enableAnimations;
	
	public OptionPane() {
		super("mydoggy");
	}

	public static void setPushAwayModeProp(PushAwayMode mode)
	{
		jEdit.setProperty(PUSH_AWAY_MODE_PROP, mode.toString());
	}
	public static PushAwayMode getPushAwayModeProp() {
		String selected = jEdit.getProperty(PUSH_AWAY_MODE_PROP,
				PushAwayMode.HORIZONTAL.toString());
		return PushAwayMode.valueOf(selected);
	}
	public static boolean getUseAlternateLayoutProp() {
		return jEdit.getBooleanProperty(USE_ALTERNATE_LAYOUT_PROP, true);
	}
	public static boolean getFloatOnTopProp() {
		return jEdit.getBooleanProperty(FLOAT_ON_TOP_PROP, false);
	}
	public static boolean getFloatOsDecorationsProp() {
		return jEdit.getBooleanProperty(FLOAT_OS_DECORATIONS_PROP, false);
	}
	public static boolean getFloatAddToTaskBarProp() {
		return jEdit.getBooleanProperty(FLOAT_ADD_TO_TASKBAR_PROP, false);
	}
	public static boolean getEnablePreviewProp() {
		return jEdit.getBooleanProperty(ENABLE_PREVIEW_PROP, true);
	}
	public static boolean getEnableAnimationsProp() {
		return jEdit.getBooleanProperty(ENABLE_ANIMATIONS_PROP, true);
	}
	
	@Override
	protected void _init() {
		String [] pushAwayModes = new String[] {
				PushAwayMode.HORIZONTAL.toString(),
				PushAwayMode.VERTICAL.toString(),
				PushAwayMode.ANTICLOCKWISE.toString(),
				PushAwayMode.MOST_RECENT.toString()
		};
		pushAwayMode = new JComboBox(pushAwayModes);
		pushAwayMode.setSelectedItem(getPushAwayModeProp().toString());
		addComponent(jEdit.getProperty(PUSH_AWAY_MODE_LABEL), pushAwayMode);
		useAlternateLayout = new JCheckBox(
			jEdit.getProperty(USE_ALTERNATE_LAYOUT_LABEL),
			getUseAlternateLayoutProp());
		addComponent(useAlternateLayout);
		floatOnTop = new JCheckBox(
			jEdit.getProperty(FLOAT_ON_TOP_LABEL),
			getFloatOnTopProp());
		addComponent(floatOnTop);
		floatOsDecorations = new JCheckBox(
				jEdit.getProperty(FLOAT_OS_DECORATIONS_LABEL),
				getFloatOsDecorationsProp());
		addComponent(floatOsDecorations);
		floatAddToTaskBar = new JCheckBox(
				jEdit.getProperty(FLOAT_ADD_TO_TASKBAR_LABEL),
				getFloatAddToTaskBarProp());
		addComponent(floatAddToTaskBar);
		enablePreview = new JCheckBox(
				jEdit.getProperty(ENABLE_PREVIEW_LABEL),
				getEnablePreviewProp());
		addComponent(enablePreview);
		enableAnimations = new JCheckBox(
				jEdit.getProperty(ENABLE_ANIMATIONS_LABEL),
				getEnableAnimationsProp());
		addComponent(enableAnimations);
	}

	@Override
	protected void _save() {
		jEdit.setProperty(PUSH_AWAY_MODE_PROP, (String)pushAwayMode.getSelectedItem());
		jEdit.setBooleanProperty(USE_ALTERNATE_LAYOUT_PROP, useAlternateLayout.isSelected());
		jEdit.setBooleanProperty(FLOAT_ON_TOP_PROP, floatOnTop.isSelected());
		jEdit.setBooleanProperty(FLOAT_OS_DECORATIONS_PROP, floatOsDecorations.isSelected());
		jEdit.setBooleanProperty(FLOAT_ADD_TO_TASKBAR_PROP, floatAddToTaskBar.isSelected());
		jEdit.setBooleanProperty(ENABLE_PREVIEW_PROP, enablePreview.isSelected());
		jEdit.setBooleanProperty(ENABLE_ANIMATIONS_PROP, enableAnimations.isSelected());
		jEdit.propertiesChanged();
	}

}
