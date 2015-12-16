
package clangbeauty;

import common.gui.FileTextField;

import javax.swing.BorderFactory;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

/**
 * Option pane for CLangBeauty plugin
 * There are many other options available for clang-format, see http://clang.llvm.org/docs/ClangFormatStyleOptions.html
 *
 */
public class OptionPane extends AbstractOptionPane {

    private FileTextField exePathField;
    private String oldExePath;
    private StyleOptions styleOptions = new StyleOptions();

    public OptionPane() {
        super( "clangbeauty" );
    }

    protected void _init() {
        setBorder( BorderFactory.createEmptyBorder( 6, 6, 6, 6 ) );
        oldExePath = CLangBeautyPlugin.getCLangFormatExe();

        exePathField = new FileTextField( oldExePath, false );
        addComponent( jEdit.getProperty( "options.clangbeauty.clangformat.exe", "clang-format executable" ), exePathField );
    }

    protected void _save() {
        String newPath = exePathField.getTextField().getText();
        if ( newPath != oldExePath ) {
            jEdit.setProperty( "clangbeauty.clang-format.exe", newPath );
        }


        jEdit.setProperty( "clangbeauty.styleOptions", styleOptions.toString() );
    }
}
