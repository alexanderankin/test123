package projectviewer.vpt;

import javax.swing.*;

public class Test {

	public static void main(String[] args) {
		JFrame frame = new JFrame("test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		VPTRoot root = VPTRoot.getInstance();
		JTree tree = new JTree(new VPTModel(root));
		tree.setCellRenderer(new VPTCellRenderer());
		frame.getContentPane().add(tree);
		
		VPTProject prj = new VPTProject("Test"); 
		root.add(prj);
		prj.add(new VPTFile("test.java"));
		
		frame.pack();
		frame.show();
	}

}
