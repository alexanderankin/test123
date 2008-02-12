/*
 * NewJFrame.java
 *
 * Created on 7. b�ezen 2006, 19:43
 */

package net.jakubholy.jedit.autocomplete;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;

/**
 * GUI to display and edit the list of remembered words of a buffer.
 * @author  Jakub Holy
 */
@SuppressWarnings("serial")
public class WordListEditorUI extends JDialog implements Observer {
	
	/** Name of property used to store window geometry. */
	private static final String GEOMETRY_PROP = "autoc-wordlisteditor";
	
	/** The autocomplete this editor is for. */
	private AutoComplete autoComplete;
    
    /** Creates new form NewJFrame */
    public WordListEditorUI(AutoComplete autoComplete) {
    	super( jEdit.getActiveView(), "TextAutocomplete::Word list of " 
    			+ autoComplete.getBuffer().getName(), true );
    	this.autoComplete = autoComplete;
    	autoComplete.getWordList().addObserver(this);
    	setBufferName( autoComplete.getBuffer().getName() );
        initComponents();
    }
    
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        headerPanel = new javax.swing.JPanel();
        headerLabel = new javax.swing.JLabel();
        forBufferPanel = new javax.swing.JPanel();
        forLabel = new javax.swing.JLabel();
        bufferNameLabel = new javax.swing.JLabel();
        mainPanel = new javax.swing.JPanel();
        wordList = new javax.swing.JList();
        modificationsPanel = new javax.swing.JPanel();
        addWordField = new javax.swing.JTextField();
        buttonsPanel = new javax.swing.JPanel();
        addWordButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        deleteAllButton = new javax.swing.JButton();
        footerPanel = new javax.swing.JPanel();
        closeButton = new javax.swing.JButton();
        importButton = new javax.swing.JButton();
        exportButton = new javax.swing.JButton();
        importExportPanel = new javax.swing.JPanel();
        statusLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        headerPanel.setLayout(new javax.swing.BoxLayout(headerPanel, javax.swing.BoxLayout.Y_AXIS));

        headerLabel.setFont(new java.awt.Font("Dialog", 1, 24));
        headerLabel.setText("TextAutocomplete Word List");
        headerPanel.add(headerLabel);

        forBufferPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        forLabel.setText("for");
        forBufferPanel.add(forLabel);

        bufferNameLabel.setText(getBufferName());
        forBufferPanel.add(bufferNameLabel);

        headerPanel.add(forBufferPanel);

        getContentPane().add(headerPanel, java.awt.BorderLayout.NORTH);

        mainPanel.setLayout(new java.awt.BorderLayout(0, 3));

        // mainPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        mainPanel.setBorder(javax.swing.BorderFactory.createLineBorder(Color.GRAY, 2));
        wordList.setModel( wordListModel );
        /*(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });*/
        JScrollPane wordListScroll = new JScrollPane(wordList);
        mainPanel.add(wordListScroll, java.awt.BorderLayout.CENTER);

        modificationsPanel.setLayout(new javax.swing.BoxLayout(modificationsPanel, javax.swing.BoxLayout.Y_AXIS));

        addWordField.setToolTipText("Type the word to add to the list");
        modificationsPanel.add(addWordField);

        addWordButton.setText("Add word");
        addWordButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addWordButtonActionPerformed(evt);
            }
        });

        buttonsPanel.add(addWordButton);

        deleteButton.setText("Delete selected");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        buttonsPanel.add(deleteButton);

        deleteAllButton.setText("Delete all");
        deleteAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteAllButtonActionPerformed(evt);
            }
        });

        buttonsPanel.add(deleteAllButton);

        modificationsPanel.add(buttonsPanel);

        mainPanel.add(modificationsPanel, java.awt.BorderLayout.SOUTH);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        
        ////////////////////////////////////////////////////////////////////////////////////////////////

        importButton.setText("Import words");
        importButton.setToolTipText("Import a list of words (one per line) from a file");
        importButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	int oldWordsCount = autoComplete.getWordList().size();
                try 
                {
                	setIgnoreWordListEvents(true);
                	int importedCnt = autoComplete.importWordList();
                	setIgnoreWordListEvents(false);
                	rereadWords();
                	statusLabel.setText( statusLabel.getText() + "; Imported "+importedCnt+" words." );
                	requestFocus();
                }
				catch (FileNotFoundException e) 
				{ showError("The selected input file couldn't be found:\n" + e.getMessage()); } 
				catch (MaxWordsExceededException mwe) 
				{
					errorMaxWordsExceeded(mwe);
					int importedCnt = autoComplete.getWordList().size() - oldWordsCount;
					rereadWords();
					statusLabel.setText( statusLabel.getText() + "; Imported "+importedCnt+" words." );
				} 
				catch (IOException e) 
				{ showError("I couldn't import the words because an I/O error occured:\n" + e.getMessage()); }
            }
        });
        
        exportButton.setText("Export words");
        exportButton.setToolTipText("Export the words into a file (one per line)");
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	try 
            	{ autoComplete.exportWordList(); }
				catch (IOException e) 
				{ showError("I couldn't export the words because an I/O error occured:\n" + e.getMessage()); }
            }
        });
        
        importExportPanel.add( importButton );
        importExportPanel.add( exportButton );
        /*
        footerPanel.setLayout( new BoxLayout(footerPanel, BoxLayout.Y_AXIS) );
        
        JPanel closeBttnPanel = new JPanel();
        closeBttnPanel.add( closeButton  );
        closeBttnPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        footerPanel.add( closeBttnPanel );
        
        //statusLabel.setBorder( javax.swing.BorderFactory.createLoweredBevelBorder() );
        statusLabel.setPreferredSize(new java.awt.Dimension(0, 20));
        statusLabel.setAlignmentY(0.0F);
        footerPanel.add( statusLabel );
        */
        footerPanel.setLayout( new BorderLayout() );
        JPanel footerButtonsPanel = new JPanel(new GridLayout(2,1));
        footerButtonsPanel.add( importExportPanel );
        
        
        JPanel closeBttnPanel = new JPanel();
        closeBttnPanel.add( closeButton  );
        closeBttnPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        footerButtonsPanel.add( closeBttnPanel );
        
        footerPanel.add( footerButtonsPanel, BorderLayout.CENTER );
        //statusLabel.setBorder( javax.swing.BorderFactory.createLoweredBevelBorder() );
        statusLabel.setPreferredSize(new java.awt.Dimension(0, 20));
        statusLabel.setAlignmentY(0.0F);
        footerPanel.add( statusLabel, BorderLayout.SOUTH );

        getContentPane().add(footerPanel, java.awt.BorderLayout.SOUTH);
        
        ////////////////////////////////////////////////////////////////////////////////////////////////
        
        // Set the content of the word list
        this.rereadWords();
        
        //this.setLocationRelativeTo(null);
        GUIUtilities.loadGeometry(this, GEOMETRY_PROP);
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        dispose();
    }//GEN-LAST:event_closeButtonActionPerformed

    private void deleteAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteAllButtonActionPerformed
    	autoComplete.getWordList().clear();
    	wordListModel.clear();
    }//GEN-LAST:event_deleteAllButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) 
    {
    	int[] selected = wordList.getSelectedIndices();
    	for (int i = 0; i < selected.length; i++)
    	{
    		autoComplete.forgetWord( (String)wordListModel.get(selected[i]) );
    		//wordListModel.remove(selected[i]); // Modifies the indices? 
		}
    	rereadWords();
    }

    private void addWordButtonActionPerformed(java.awt.event.ActionEvent evt) 
    {
    	if(addWordField.getText().trim().length() > 0)
    	{ 
    		try 
    		{
				autoComplete.rememberWord( addWordField.getText() );
	    		//  We don't know at which position will the word go so let it up to the WordList
	        	rereadWords();
			} 
    		catch (MaxWordsExceededException mwe) 
    		{
    			// Notify the user
    			errorMaxWordsExceeded(mwe);
			}
    	}
    } // addWordButtonActionPerformed

    /** Notify user about the exception {@link MaxWordsExceededException}. */
	private void errorMaxWordsExceeded(MaxWordsExceededException mwe) {
		String msg = "Sorry, I couldn't remember the last word (" + mwe.getLastWord()
			+ ")\n because the maximal number of words to remember ("
			+ PreferencesManager.getPreferencesManager().getMaxCountOfWords() 
			+ ")\n would be exceeded. You may change the maximum in the plugin's options.";
		showError(msg);
	}

	/**
	 * Notify the user about an error.
	 * @param msg Description of the exception/problem.
	 */
	private void showError(String msg) {
		GUIUtilities.error(
				this, 
				"plugin.net.jakubholy.jedit.autocomplete.TextAutocompletePlugin.errorMessage", 
				new Object[]{ msg });
	}
    
    /** Reread the list of remebered words and re-display them on the list. */
    private void rereadWords() 
    {
    	Completion[] words = autoComplete.getWordList().getAllWords();
    	wordListModel.clear();
    	for (int i = 0; i < words.length; i++) 
    	{ wordListModel.addElement( words[i].getWord() ); }
    	statusLabel.setText("Count of words: " + words.length);
    	//wordList.revalidate();
    }
    
    /* *
     * @param args the command line arguments
     * /
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new WordListEditorUI().setVisible(true);
            }
        });
    }*/
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addWordButton;
    private javax.swing.JTextField addWordField;
    private javax.swing.JLabel bufferNameLabel;
    private javax.swing.JButton closeButton;
    private javax.swing.JButton deleteAllButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JLabel forLabel;
    private javax.swing.JLabel headerLabel;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel footerPanel;
    private javax.swing.JPanel forBufferPanel;
    private javax.swing.JPanel modificationsPanel;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JList wordList;
    private DefaultListModel wordListModel = new DefaultListModel();
    private javax.swing.JButton importButton;
    private javax.swing.JButton exportButton;
    private javax.swing.JPanel importExportPanel;
    private javax.swing.JLabel statusLabel;
    // End of variables declaration//GEN-END:variables
    String bufferName = "<unknown buffer name>";

	/** Should I ignore modifications to the underlying word list (not redisplaying it)?*/
    private boolean ignoreWordListEvents = false;
    
    public String getBufferName()
    { return this.bufferName; }
    
    
    public void setBufferName(String bufferName)
    { this.bufferName = bufferName; }

	public void update(Observable o, Object event) 
	{
		// If the word list has changed
		if(event instanceof WordListEvent && !ignoreWordListEvents )
		{ rereadWords(); }
		
	}

	public void dispose() 
	{
		GUIUtilities.saveGeometry(this, GEOMETRY_PROP);
		autoComplete.getWordList().deleteObserver(this);
		super.dispose();
	}

	private void setIgnoreWordListEvents(boolean ignoreWordListEvents) {
		this.ignoreWordListEvents = ignoreWordListEvents;
	}
	
	
}
