import javax.swing.*;
import java.awt.event.KeyListener;
import java.awt.Container;

//import org.euler.layout.*; //Import my layout manager.  May not be necessary.

public class ColumnInsertDialog extends JDialog{
    private JTextField text;
//    private JLabel textLabel;
    
    //This is a test.  I want to see how well jedit responds to my typing.
    public ColumnInsertDialog(KeyListener listener){
        Container cPane = this.getContentPane();
        text = new JTextField(40);
        text.addKeyListener(listener);
        text.setText("Input Text here");
        text.selectAll();
        cPane.add(text);
//        cPane.setSize(200,60);
        this.pack();
//        this.setSize(200,60);
        this.setVisible(true);
    }
    
    public String getText(){
        return text.getText();
    }
}
