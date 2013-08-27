package view;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;

public class MyTreeCellRenderer extends DefaultTreeCellRenderer {

	private static final ImageIcon diskIcon = new ImageIcon(
			MyTreeCellRenderer.class.getResource("disk.png"));

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
				row, hasFocus);
		DefaultMutableTreeNode node=(DefaultMutableTreeNode)value;
		TreeNode[] path = node.getPath();
		if (path.length == 2) {
			setIcon(diskIcon);
		} else {

			if (leaf) {
				setIcon(getDefaultLeafIcon());
			} else if (expanded) {
				setIcon(getDefaultOpenIcon());
			} else {
				setIcon(getDefaultClosedIcon());
			}
		}

		return this;
	}
}
