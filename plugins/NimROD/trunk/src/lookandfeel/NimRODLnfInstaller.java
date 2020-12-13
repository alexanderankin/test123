
/*
 * NimRODLnfInstaller.java - Look And Feel plugin
 * Copyright (C) 2020 Dale Anson
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package lookandfeel;


import com.nilo.plaf.nimrod.NimRODLookAndFeel;
import com.nilo.plaf.nimrod.NimRODTheme;

import ise.java.awt.KappaLayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.*;
import java.io.File;
import java.util.Properties;

import javax.swing.*;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.gui.ColorWellButton;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.SyntaxUtilities;


/**
 * A class for installing the NimROD look and feel
 *
 */
public class NimRODLnfInstaller implements LookAndFeelInstaller {

    private String currentThemeName = "default";

    public String getName() {
        return "NimROD";
    }

    /**
     * Install the look and feel.
     */
    public void install() throws UnsupportedLookAndFeelException {
        NimRODTheme currentTheme = loadCurrentTheme();
        NimRODLookAndFeel lnf = new NimRODLookAndFeel();
        lnf.setCurrentTheme( currentTheme );
        UIManager.setLookAndFeel( lnf );
        UIManager.put( "ClassLoader", NimRODLookAndFeel.class.getClassLoader() );
    }

    /**
     * Current theme is stored as a jEdit property.
     */
    private NimRODTheme loadCurrentTheme() {
        String themeName = jEdit.getProperty( "nimrod.currentTheme" );
        if ( themeName == null || themeName.isEmpty() ) {
            themeName = "default";
            jEdit.setProperty( "nimrod.currentTheme", themeName );
        }
        Properties currentTheme = NimRODLookAndFeelPlugin.getTheme( themeName );
        if (currentTheme == null) {
            currentTheme = NimRODLookAndFeelPlugin.getDefaultTheme();
            themeName = "default";
        }
        currentThemeName = themeName;
        System.out.println("+++++ theme name: " + themeName);
        System.out.println("+++++ currrent theme: " + currentTheme);
        for (Object key : currentTheme.keySet()) {
            System.out.println("+++++ " + key + " = " + currentTheme.get(key));   
        }
        NimRODTheme theme = new NimRODTheme();
        theme.setPrimary1( SyntaxUtilities.parseColor( currentTheme.getProperty( "nimrodlf.p1" ), null ) );
        theme.setPrimary2( SyntaxUtilities.parseColor( currentTheme.getProperty( "nimrodlf.p2" ), null ) );
        theme.setPrimary3( SyntaxUtilities.parseColor( currentTheme.getProperty( "nimrodlf.p3" ), null ) );
        theme.setSecondary1( SyntaxUtilities.parseColor( currentTheme.getProperty( "nimrodlf.s1" ), null ) );
        theme.setSecondary2( SyntaxUtilities.parseColor( currentTheme.getProperty( "nimrodlf.s2" ), null ) );
        theme.setSecondary3( SyntaxUtilities.parseColor( currentTheme.getProperty( "nimrodlf.s3" ), null ) );
        theme.setBlack( SyntaxUtilities.parseColor( currentTheme.getProperty( "nimrodlf.b" ), null ) );
        theme.setWhite( SyntaxUtilities.parseColor( currentTheme.getProperty( "nimrodlf.w" ), null ) );
        theme.setFrameOpacity( Integer.parseInt( currentTheme.getProperty( "nimrodlf.frameOpacity" ) ) );
        theme.setMenuOpacity( Integer.parseInt( currentTheme.getProperty( "nimrodlf.menuOpacity" ) ) );

        // always use the font set in jEdit
        theme.setFont( jEdit.getFontProperty( "metal.primary.font" ) );

        // set the above values as system properties so NimROD actually uses them
        // when it gets initialized. See NimRODLookAndFeel.java:159 for the keys to use in System.
        System.setProperty( "nimrodlf.p1", currentTheme.getProperty( "nimrodlf.p1", "0xe3a300" ) );
        System.setProperty( "nimrodlf.p2", currentTheme.getProperty( "nimrodlf.p2", "0xebb000" ) );
        System.setProperty( "nimrodlf.p3", currentTheme.getProperty( "nimrodlf.p3", "0xf5bc00" ) );
        System.setProperty( "nimrodlf.s1", currentTheme.getProperty( "nimrodlf.s1", "0xaba98a" ) );
        System.setProperty( "nimrodlf.s2", currentTheme.getProperty( "nimrodlf.s2", "0xb3b092" ) );
        System.setProperty( "nimrodlf.s3", currentTheme.getProperty( "nimrodlf.s3", "0xbdbb9d" ) );
        System.setProperty( "nimrodlf.b", currentTheme.getProperty( "nimrodlf.b", "0x000000" ) );
        System.setProperty( "nimrodlf.w", currentTheme.getProperty( "nimrodlf.w", "0xffffff" ) );
        System.setProperty( "nimrodlf.frameOpacity", currentTheme.getProperty( "nimrodlf.frameOpacity", "180" ) );
        System.setProperty( "nimrodlf.menuOpacity", currentTheme.getProperty( "nimrodlf.menuOpacity", "195" ) );
        File homeDir = jEdit.getPlugin( "lookandfeel.NimRODLookAndFeelPlugin" ).getPluginHome();
        File themeFile = new File( homeDir, themeName + ".properties" );
        System.setProperty( "nimrodlf.themeFile", themeFile.getAbsolutePath() );
        return theme;
    }

    /**
     * Returns a component used to configure the look and feel.
     */
    public AbstractOptionPane getOptionPane() {
        return new OptionComponent();
    }




    /**
     * The configuration component.
     */
    class OptionComponent extends AbstractOptionPane {

        /**
         * Create a new <code>OptionComponent</code>.
         */
        public OptionComponent() {
            super( "NimROD" );
            init();
        }

        /**
         * Layout this component.
         */
        public void _init() {
            JButton button = new JButton( jEdit.getProperty( "nimrod.theme.label", "Nimrod Theme Editor" ) );
            button.addActionListener( new ActionListener(){

                    public void actionPerformed( ActionEvent ae ) {
                        SwingUtilities.invokeLater( new Runnable(){

                                public void run() {
                                    NimRODTheme currentTheme = loadCurrentTheme();
                                    Configuration c = new Configuration( currentTheme );
                                    center( jEdit.getActiveView(), c );
                                    c.setVisible( true );
                                }
                            } );
                    }
                }
            );
            addComponent( button );
        }

        /**
         * Not used.
         */
        public void _save() {
        }
    }




    /**
     * Use jEdit ColorWellButton to allow selection of the following colors, use
     * the following text for tooltips, and set the initial color from the current
     * nimrod theme:
     *
     * Primary 1: Active window borders, shadows of selected items, system text (e.g. labels)
     * Primary 2: Highlighting and selection
     * Primary 3: Large colored areas, e.g. title bars
     * Secondary 1: Dark border for flush 3D style
     * Secondary 2: Inactive window borders, shadows, mouse down, dimmed text
     * Secondary 3: Canvas color, normal background color
     * Black: User text and control text
     * White: Hightlights, background for user text entry area
     *
     * Use a slider for the opacity settings:
     *
     * Menu opacity: 0 - 255
     * Frame opacity: 0 - 255
     *
     * Font:
     * NimROD has a font setting, in which is uses a font and derives a bold font,
     * but I'm just using the jEdit font set in jEdit's appearance option pane.
     *
     * use jEdit FontSelectorDialog? Just use whatever is already selected in jEdit font settings?
     */
    class Configuration extends JDialog {

        private NimRODTheme currentTheme;
        private ColorWellButton primaryButton, secondaryButton, primary1Button, primary2Button, primary3Button, secondary1Button, secondary2Button, secondary3Button, blackButton, whiteButton;
        private JSpinner frameOpacitySpinner, menuOpacitySpinner;
        private JTabbedPane tabs;

        public Configuration( NimRODTheme currentTheme ) {
            super( jEdit.getActiveView(), "NimROD Theme Configuration", true );
            this.currentTheme = currentTheme;
            init();
            pack();
        }

        private void init() {

            JPanel contentPane = new JPanel( new BorderLayout() );
            contentPane.setBorder( BorderFactory.createEmptyBorder( 11, 11, 11, 11 ) );
            tabs = new JTabbedPane();
            JPanel basicPanel = getBasicPanel();
            JPanel advancedPanel = getAdvancedPanel();
            JPanel themePanel = getThemePanel();

            tabs.addTab( "Basic", basicPanel );
            tabs.addTab( "Advanced", advancedPanel );
            tabs.addTab( "Themes", themePanel );

            contentPane.add( tabs, BorderLayout.CENTER );
            setContentPane( contentPane );
        }

        private JPanel getBasicPanel() {
            JPanel contentPanel = new JPanel( new BorderLayout() );
            AbstractOptionPane basicPanel = new AbstractOptionPane( null );

            // Basic Panel: NimROD calls these "selection" and "background" and calculates the
            // remaining colors from these values
            JLabel primaryLabel = new JLabel( jEdit.getProperty( "nimrod.primary.text", "Primary Color" ) );
            primaryLabel.setToolTipText( jEdit.getProperty( "nimrod.primary1.tooltip", "Primary: Borders, highlighting, title bars" ) );
            primaryButton = new ColorWellButton( currentTheme.getPrimaryControl() );
            basicPanel.addComponent( primaryLabel, primaryButton );

            JLabel secondaryLabel = new JLabel( jEdit.getProperty( "nimrod.secondary.text", "Secondary Color" ) );
            secondaryLabel.setToolTipText( jEdit.getProperty( "nimrod.secondary1.tooltip", "Secondary: Borders and backgrounds" ) );
            secondaryButton = new ColorWellButton( currentTheme.getControl() );
            basicPanel.addComponent( secondaryLabel, secondaryButton );

            // need save, ok, and cancel buttons
            KappaLayout kl = new KappaLayout();
            JPanel buttonPanel = new JPanel( kl );
            JButton saveButton = new JButton( jEdit.getProperty( "nimrod.Save", "Save" ) );
            saveButton.setMnemonic( KeyEvent.VK_S );
            JButton okButton = new JButton( jEdit.getProperty( "nimrod.Ok", "Ok" ) );
            okButton.setMnemonic( KeyEvent.VK_O );
            JButton cancelButton = new JButton( jEdit.getProperty( "nimrod.Cancel", "Cancel" ) );
            cancelButton.setMnemonic( KeyEvent.VK_C );
            buttonPanel.add( "0, 0, 1, 1, W, w, 3", saveButton );
            buttonPanel.add( "1, 0, 1, 1, W, w, 3", KappaLayout.createHorizontalStrut( 66 ) );
            buttonPanel.add( "2, 0, 1, 1, E, w, 3", okButton );
            buttonPanel.add( "3, 0, 1, 1, E, w, 3", cancelButton );
            kl.makeColumnsSameWidth( 2, 3 );

            saveButton.addActionListener( new ActionListener(){

                    public void actionPerformed( ActionEvent ae ) {
                        String themeName = currentThemeName;
                        if ( !themeName.equals( "default" ) ) {
                            int response = JOptionPane.showConfirmDialog( null, "Update current theme " + themeName + "?", "Update Theme?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE );
                            if ( response != JOptionPane.YES_OPTION ) {
                                themeName = JOptionPane.showInputDialog( null, "Enter name for theme:", "Theme Name", JOptionPane.QUESTION_MESSAGE );
                                if ( themeName == null || themeName.isEmpty() ) {
                                    return;
                                }
                            }
                        }

                        Configuration.this.setVisible( false );
                        Configuration.this.dispose();

                        save( themeName );
                        jEdit.setProperty( "nimrod.currentTheme", themeName );
                        try {
                            NimRODLnfInstaller.this.install();
                        }
                        catch ( Exception e ) {
                            JOptionPane.showMessageDialog( null, e.getMessage(), jEdit.getProperty( "nimrod.loadError.text", "Error Installing NimROD Look and Feel" ), JOptionPane.ERROR_MESSAGE );
                            e.printStackTrace();
                        }
                    }
                }
            );

            okButton.addActionListener( new ActionListener(){

                    public void actionPerformed( ActionEvent ae ) {
                        Configuration.this.setVisible( false );
                        Configuration.this.dispose();
                        save( null );
                        try {
                            NimRODLnfInstaller.this.install();
                        }
                        catch ( Exception e ) {
                            JOptionPane.showMessageDialog( null, e.getMessage(), jEdit.getProperty( "nimrod.loadError.text", "Error Installing NimROD Look and Feel" ), JOptionPane.ERROR_MESSAGE );
                            e.printStackTrace();
                        }
                    }
                }
            );

            cancelButton.addActionListener( new ActionListener(){

                    public void actionPerformed( ActionEvent ae ) {
                        Configuration.this.setVisible( false );
                        Configuration.this.dispose();
                    }
                }
            );

            contentPanel.add( basicPanel, BorderLayout.CENTER );
            contentPanel.add( buttonPanel, BorderLayout.SOUTH );
            return contentPanel;
        }

        private JPanel getAdvancedPanel() {

            JPanel contentPanel = new JPanel( new BorderLayout() );

            // Advanced Panel:
            // color choose buttons
            // note that getPrimaryX and getSecondaryX are protected in NimRODTheme and MetalTheme,
            // but they are accessible through other methods that aren't named as clearly
            AbstractOptionPane advancedPanel = new AbstractOptionPane( null );

            JLabel primary1Label = new JLabel( jEdit.getProperty( "nimrod.primary1.text", "Primary 1" ) );
            primary1Label.setToolTipText( jEdit.getProperty( "nimrod.primary1.tooltip", "Primary 1: Active window borders, shadows of selected items, system text (e.g. labels)" ) );
            primary1Button = new ColorWellButton( currentTheme.getPrimaryControlDarkShadow() );
            advancedPanel.addComponent( primary1Label, primary1Button );

            JLabel primary2Label = new JLabel( jEdit.getProperty( "nimrod.primary2,text", "Primary 2" ) );
            primary2Label.setToolTipText( jEdit.getProperty( "nimrod.primary2.tooltip", "Primary 2: Highlighting and selection" ) );
            primary2Button = new ColorWellButton( currentTheme.getPrimaryControlShadow() );
            advancedPanel.addComponent( primary2Label, primary2Button );

            JLabel primary3Label = new JLabel( jEdit.getProperty( "nimrod.primary3.text", "Primary 3" ) );
            primary3Label.setToolTipText( jEdit.getProperty( "nimrod.primary3.tooltip", "Primary 3: Large colored areas, e.g. title bars" ) );
            primary3Button = new ColorWellButton( currentTheme.getPrimaryControl() );
            advancedPanel.addComponent( primary3Label, primary3Button );

            advancedPanel.addSeparator();

            JLabel secondary1Label = new JLabel( jEdit.getProperty( "nimrod.secondary1.text", "Secondary 1" ) );
            secondary1Label.setToolTipText( jEdit.getProperty( "nimrod.secondary1.tooltip", "Secondary 1: Dark border for flush 3D style" ) );
            secondary1Button = new ColorWellButton( currentTheme.getControlDarkShadow() );
            advancedPanel.addComponent( secondary1Label, secondary1Button );

            JLabel secondary2Label = new JLabel( jEdit.getProperty( "nimrod.secondary2.text", "Secondary 2" ) );
            secondary2Label.setToolTipText( jEdit.getProperty( "nimrod.secondary2.tooltip", "Secondary 2: Inactive window borders, shadows, mouse down, dimmed text" ) );
            secondary2Button = new ColorWellButton( currentTheme.getControlShadow() );
            advancedPanel.addComponent( secondary2Label, secondary2Button );

            JLabel s3Label = new JLabel( jEdit.getProperty( "nimrod.secondary3.text", "Secondary 3" ) );
            s3Label.setToolTipText( jEdit.getProperty( "nimrod.secondary3.tooltip", "Secondary 3: Canvas color, normal background color" ) );
            secondary3Button = new ColorWellButton( currentTheme.getControl() );
            advancedPanel.addComponent( s3Label, secondary3Button );

            advancedPanel.addSeparator();

            JLabel blackLabel = new JLabel( jEdit.getProperty( "nimrod.black.text", "Black" ) );
            blackLabel.setToolTipText( jEdit.getProperty( "nimrod.black.tooltip", "Black: User text and control text" ) );
            blackButton = new ColorWellButton( currentTheme.getUserTextColor() );
            advancedPanel.addComponent( blackLabel, blackButton );

            JLabel whiteLabel = new JLabel( jEdit.getProperty( "nimrod.white.text", "White" ) );
            whiteLabel.setToolTipText( jEdit.getProperty( "nimrod.white.tooltip", "White: Highlights, background for user text entry area" ) );
            whiteButton = new ColorWellButton( currentTheme.getControlHighlight() );
            advancedPanel.addComponent( whiteLabel, whiteButton );

            // opacity spinners
            JLabel foLabel = new JLabel( jEdit.getProperty( "nimrod.frameOpacity.text", "Frame Opacity" ) );
            SpinnerNumberModel frameOpacity = new SpinnerNumberModel( currentTheme.getFrameOpacity(), 0, 255, 1 );
            frameOpacitySpinner = new JSpinner( frameOpacity );
            advancedPanel.addComponent( foLabel, frameOpacitySpinner );

            JLabel moLabel = new JLabel( jEdit.getProperty( "nimrod.menuOpacity.text", "Menu Opacity" ) );
            SpinnerNumberModel menuOpacity = new SpinnerNumberModel( currentTheme.getMenuOpacity(), 0, 255, 1 );
            menuOpacitySpinner = new JSpinner( menuOpacity );
            advancedPanel.addComponent( moLabel, menuOpacitySpinner );

            // need save, ok, and cancel buttons
            KappaLayout kl = new KappaLayout();
            JPanel buttonPanel = new JPanel( kl );
            JButton saveButton = new JButton( jEdit.getProperty( "nimrod.Save", "Save" ) );
            saveButton.setMnemonic( KeyEvent.VK_S );
            JButton okButton = new JButton( jEdit.getProperty( "nimrod.Ok", "Ok" ) );
            okButton.setMnemonic( KeyEvent.VK_O );
            JButton cancelButton = new JButton( jEdit.getProperty( "nimrod.Cancel", "Cancel" ) );
            cancelButton.setMnemonic( KeyEvent.VK_C );
            buttonPanel.add( "0, 0, 1, 1, W, w, 3", saveButton );
            buttonPanel.add( "1, 0, 1, 1, W, w, 3", KappaLayout.createHorizontalStrut( 66 ) );
            buttonPanel.add( "2, 0, 1, 1, E, w, 3", okButton );
            buttonPanel.add( "3, 0, 1, 1, E, w, 3", cancelButton );
            kl.makeColumnsSameWidth( 2, 3 );

            saveButton.addActionListener( new ActionListener(){

                    public void actionPerformed( ActionEvent ae ) {
                        String themeName = currentThemeName;
                        if ( !themeName.equals( "default" ) ) {
                            int response = JOptionPane.showConfirmDialog( null, "Update current theme " + themeName + "?", "Update Theme?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE );
                            if ( response != JOptionPane.YES_OPTION ) {
                                themeName = JOptionPane.showInputDialog( null, "Enter name for theme:", "Theme Name", JOptionPane.QUESTION_MESSAGE );
                                if ( themeName == null || themeName.isEmpty() ) {
                                    return;
                                }
                            }
                        }
                        save( themeName );
                    }
                }
            );

            okButton.addActionListener( new ActionListener(){

                    public void actionPerformed( ActionEvent ae ) {
                        Configuration.this.setVisible( false );
                        Configuration.this.dispose();
                        save( null );
                        try {
                            NimRODLnfInstaller.this.install();
                        }
                        catch ( Exception e ) {
                            JOptionPane.showMessageDialog( null, e.getMessage(), jEdit.getProperty( "nimrod.loadError.text", "Error Installing NimROD Look and Feel" ), JOptionPane.ERROR_MESSAGE );
                            e.printStackTrace();
                        }
                    }
                }
            );

            cancelButton.addActionListener( new ActionListener(){

                    public void actionPerformed( ActionEvent ae ) {
                        Configuration.this.setVisible( false );
                        Configuration.this.dispose();
                    }
                }
            );

            contentPanel.add( advancedPanel, BorderLayout.CENTER );
            contentPanel.add( buttonPanel, BorderLayout.SOUTH );
            return contentPanel;
        }

        private JPanel getThemePanel() {
            JPanel contentPanel = new JPanel( new BorderLayout() );
            JList<String> themeList = new JList<String>( NimRODLookAndFeelPlugin.getThemeList() );
            JScrollPane scrollPane = new JScrollPane( themeList );

            // need ok, cancel, delete buttons
            KappaLayout kl = new KappaLayout();
            JPanel buttonPanel = new JPanel( kl );
            JButton deleteButton = new JButton( jEdit.getProperty( "nimrod.Delete", "Delete" ) );
            deleteButton.setMnemonic( KeyEvent.VK_S );
            JButton okButton = new JButton( jEdit.getProperty( "nimrod.Ok", "Ok" ) );
            okButton.setMnemonic( KeyEvent.VK_O );
            JButton cancelButton = new JButton( jEdit.getProperty( "nimrod.Cancel", "Cancel" ) );
            cancelButton.setMnemonic( KeyEvent.VK_C );
            buttonPanel.add( "0, 0, 1, 1, W, w, 3", deleteButton );
            buttonPanel.add( "1, 0, 1, 1, W, w, 3", KappaLayout.createHorizontalStrut( 66 ) );
            buttonPanel.add( "2, 0, 1, 1, E, w, 3", okButton );
            buttonPanel.add( "3, 0, 1, 1, E, w, 3", cancelButton );
            kl.makeColumnsSameWidth( 2, 3 );

            deleteButton.addActionListener( new ActionListener(){

                    public void actionPerformed( ActionEvent ae ) {
                        String themeName = themeList.getSelectedValue();
                        if ( themeName == null ) {
                            return;
                        }
                        int response = JOptionPane.showConfirmDialog( null, "Delete theme \"" + themeName + "\"?" );
                        if ( response == JOptionPane.YES_OPTION ) {
                            NimRODLookAndFeelPlugin.deleteTheme( themeName );
                            themeList.setListData( NimRODLookAndFeelPlugin.getThemeList() );
                        }
                    }
                }
            );

            okButton.addActionListener( new ActionListener(){

                    public void actionPerformed( ActionEvent ae ) {
                        String themeName = themeList.getSelectedValue();
                        if ( themeName == null ) {
                            return;
                        }
                        jEdit.setProperty( "nimrod.currentTheme", themeName );

                        Configuration.this.setVisible( false );
                        Configuration.this.dispose();
                        try {
                            NimRODLnfInstaller.this.install();
                        }
                        catch ( Exception e ) {
                            JOptionPane.showMessageDialog( null, e.getMessage(), jEdit.getProperty( "nimrod.loadError.text", "Error installing NimROD Look and Feel" ), JOptionPane.ERROR_MESSAGE );
                            e.printStackTrace();
                        }
                    }
                }
            );

            cancelButton.addActionListener( new ActionListener(){

                    public void actionPerformed( ActionEvent ae ) {
                        Configuration.this.setVisible( false );
                        Configuration.this.dispose();
                    }
                }
            );

            contentPanel.add( scrollPane, BorderLayout.CENTER );
            contentPanel.add( buttonPanel, BorderLayout.SOUTH );
            return contentPanel;
        }

        private void save( String themeName ) {
            if ( tabs.getSelectedIndex() == 0 ) {

                // basic tab is selected, need to generate the additional color values
                Color primaryColor = primaryButton.getSelectedColor();
                int r = primaryColor.getRed();
                int g = primaryColor.getGreen();
                int b = primaryColor.getBlue();

                Color primary1 = new Color( ( r > 20 ? r - 20 : 0 ), ( g > 20 ? g - 20 : 0 ), ( b > 20 ? b - 20 : 0 ) );
                Color primary2 = new Color( ( r > 10 ? r - 10 : 0 ), ( g > 10 ? g - 10 : 0 ), ( b > 10 ? b - 10 : 0 ) );
                primary1Button.setSelectedColor( primary1 );
                primary2Button.setSelectedColor( primary2 );
                primary3Button.setSelectedColor( primaryColor );

                Color secondaryColor = secondaryButton.getSelectedColor();
                r = secondaryColor.getRed();
                g = secondaryColor.getGreen();
                b = secondaryColor.getBlue();
                Color secondary1 = new Color( ( r > 20 ? r - 20 : 0 ), ( g > 20 ? g - 20 : 0 ), ( b > 20 ? b - 20 : 0 ) );
                Color secondary2 = new Color( ( r > 10 ? r - 10 : 0 ), ( g > 10 ? g - 10 : 0 ), ( b > 10 ? b - 10 : 0 ) );
                secondary1Button.setSelectedColor( secondary1 );
                secondary2Button.setSelectedColor( secondary2 );
                secondary3Button.setSelectedColor( secondaryColor );
            }

            jEdit.setColorProperty( "nimrod.primary.value", primaryButton.getSelectedColor() );
            jEdit.setColorProperty( "nimrodlf.p1", primary1Button.getSelectedColor() );
            jEdit.setColorProperty( "nimrodlf.p2", primary2Button.getSelectedColor() );
            jEdit.setColorProperty( "nimrodlf.p3", primary3Button.getSelectedColor() );
            jEdit.setColorProperty( "nimrod.secondary.value", secondaryButton.getSelectedColor() );
            jEdit.setColorProperty( "nimrodlf.s1", secondary1Button.getSelectedColor() );
            jEdit.setColorProperty( "nimrodlf.s2", secondary2Button.getSelectedColor() );
            jEdit.setColorProperty( "nimrodlf.s3", secondary3Button.getSelectedColor() );
            jEdit.setColorProperty( "nimrodlf.b", blackButton.getSelectedColor() );
            jEdit.setColorProperty( "nimrodlf.w", whiteButton.getSelectedColor() );
            jEdit.setIntegerProperty( "nimrodlf.frameOpacity", ( ( SpinnerNumberModel )frameOpacitySpinner.getModel() ).getNumber().intValue() );
            jEdit.setIntegerProperty( "nimrodlf.menuOpacity", ( ( SpinnerNumberModel )menuOpacitySpinner.getModel() ).getNumber().intValue() );

            if ( themeName != null && !themeName.isEmpty() ) {
                Properties props = new Properties();
                props.setProperty( "nimrod.primary.value", jEdit.getProperty( "nimrod.primary.value" ) );
                props.setProperty( "nimrodlf.p1", jEdit.getProperty( "nimrodlf.p1" ) );
                props.setProperty( "nimrodlf.p2", jEdit.getProperty( "nimrodlf.p2" ) );
                props.setProperty( "nimrodlf.p3", jEdit.getProperty( "nimrodlf.p3" ) );
                props.setProperty( "nimrod.secondary.value", jEdit.getProperty( "nimrod.secondary.value" ) );
                props.setProperty( "nimrodlf.s1", jEdit.getProperty( "nimrodlf.s1" ) );
                props.setProperty( "nimrodlf.s2", jEdit.getProperty( "nimrodlf.s2" ) );
                props.setProperty( "nimrodlf.s3", jEdit.getProperty( "nimrodlf.s3" ) );
                props.setProperty( "nimrodlf.b", jEdit.getProperty( "nimrodlf.b" ) );
                props.setProperty( "nimrodlf.w", jEdit.getProperty( "nimrodlf.w" ) );
                props.setProperty( "nimrodlf.frameOpacity", jEdit.getProperty( "nimrodlf.frameOpacity" ) );
                props.setProperty( "nimrodlf.menuOpacity", jEdit.getProperty( "nimrodlf.menuOpacity" ) );
                NimRODLookAndFeelPlugin.saveTheme( themeName, props );
            }
        }
    }

    /**
     * Centers <code>you</code> on <code>me</code>. Useful for centering
     * dialogs on their parent frames.
     *
     * @param me   Component to use as basis for centering.
     * @param you  Component to center on <code>me</code>.
     */
    public static void center( java.awt.Component me, java.awt.Component you ) {
        java.awt.Rectangle my = me.getBounds();
        java.awt.Dimension your = you.getSize();
        int x = my.x + ( my.width - your.width ) / 2;
        if ( x < 0 ) {
            x = 0;
        }
        int y = my.y + ( my.height - your.height ) / 2;
        if ( y < 0 ) {
            y = 0;
        }
        you.setLocation( x, y );
    }
}
