/*
 * @(#)QuickOpenOptionPane.java   2011.01.21 at 04:10:05 PST
 *
 * Copyright 2011 Brian Schlining
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package hohonuuli.jedit.quickopen;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @author Brian Schlining
 * @since 2011-01-16
 */
public class QuickOpenOptionPane extends AbstractOptionPane {

    private JLabel extensionLabel = new JLabel(jEdit.getProperty("options.quickopen.exclude-extensions.label"));
    private JLabel directoryLabel = new JLabel(jEdit.getProperty("options.quickopen.exclude-directories.label"));
    private JLabel maxLabel = new JLabel(jEdit.getProperty("options.quickopen.maximum-files.label"));
    private JTextField directoryTextField;
    private JTextField extensionTextField;
    private JCheckBox includeHiddenFilesCB;
    private JTextField maxTextField;
    private JCheckBox matchFirstCB;

    /**
     * Constructs ...
     */
    public QuickOpenOptionPane() {
        super("quickopen");
    }

    @Override
    protected void _init() {
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(getMatchFirstCB(), GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
                        .addComponent(getIncludeHiddenFilesCB(), GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(maxLabel)
                            .addGap(18)
                            .addComponent(getMaxTextField(), GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE))
                        .addGroup(groupLayout.createSequentialGroup()
                            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                .addComponent(extensionLabel)
                                .addComponent(directoryLabel))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                .addComponent(getDirectoryTextField(), GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
                                .addComponent(getExtensionTextField(), GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE))))
                    .addContainerGap())
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(getIncludeHiddenFilesCB())
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(extensionLabel)
                        .addComponent(getExtensionTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(directoryLabel)
                        .addComponent(getDirectoryTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(maxLabel)
                        .addComponent(getMaxTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(getMatchFirstCB())
                    .addContainerGap(140, Short.MAX_VALUE))
        );
        setLayout(groupLayout);
    }

    @Override
    protected void _save() {
        jEdit.setBooleanProperty(QuickOpenPlugin.PROP_INCLUDE_HIDDEN, getIncludeHiddenFilesCB().isSelected());
        jEdit.setProperty(QuickOpenPlugin.PROP_EXCLUDE_EXT, getExtensionTextField().getText());
        jEdit.setProperty(QuickOpenPlugin.PROP_EXCLUDE_DIR, getDirectoryTextField().getText());
        jEdit.setIntegerProperty(QuickOpenPlugin.PROP_MAX_FILES, Integer.parseInt(getMaxTextField().getText()));
        jEdit.setBooleanProperty(QuickOpenPlugin.PROP_MATCH_FIRST, getMatchFirstCB().isSelected());
    }

    private JTextField getDirectoryTextField() {
        if (directoryTextField == null) {
            directoryTextField = new JTextField();
            directoryTextField.setToolTipText(jEdit.getProperty("options.quickopen.exclude-directories.tooltip"));
            directoryTextField.setColumns(10);
            directoryTextField.setText(jEdit.getProperty(QuickOpenPlugin.PROP_EXCLUDE_DIR));
        }

        return directoryTextField;
    }

    private JTextField getExtensionTextField() {
        if (extensionTextField == null) {
            extensionTextField = new JTextField();
            extensionTextField.setToolTipText(jEdit.getProperty("options.quickopen.exclude-extensions.tooltip"));
            extensionTextField.setColumns(10);
            extensionTextField.setText(jEdit.getProperty(QuickOpenPlugin.PROP_EXCLUDE_EXT));
        }

        return extensionTextField;
    }

    private JCheckBox getIncludeHiddenFilesCB() {
        if (includeHiddenFilesCB == null) {
            includeHiddenFilesCB = new JCheckBox(jEdit.getProperty("options.quickopen.include-hidden.label"));
            includeHiddenFilesCB.setSelected(jEdit.getBooleanProperty(QuickOpenPlugin.PROP_INCLUDE_HIDDEN, false));
        }

        return includeHiddenFilesCB;
    }

    private JTextField getMaxTextField() {
        if (maxTextField == null) {
            maxTextField = new JTextField();
            maxTextField.setColumns(10);
            maxTextField.setText(jEdit.getProperty(QuickOpenPlugin.PROP_MAX_FILES));
            // Eat non-numeric keys
            maxTextField.addKeyListener(new KeyAdapter() {

                @Override
                public void keyTyped(KeyEvent e) {
                    final char c = e.getKeyChar();

                    if (c == KeyEvent.VK_ENTER) {
                        // Do nothing. Enter is normally handled by ActionListeners
                    }
                    else if (!((Character.isDigit(c)) || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {
                        getToolkit().beep();
                        e.consume();
                    }
                }

            });
        }

        return maxTextField;
    }
    private JCheckBox getMatchFirstCB() {
        if (matchFirstCB == null) {
        	matchFirstCB = new JCheckBox(jEdit.getProperty("options.quickopen.match-first.label"));
            matchFirstCB.setToolTipText(jEdit.getProperty("options.quickopen.match-first.tooltip"));
            matchFirstCB.setSelected(jEdit.getBooleanProperty(QuickOpenPlugin.PROP_MATCH_FIRST, false));
        }
        return matchFirstCB;
    }
}
