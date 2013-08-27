package view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;


import javax.print.attribute.TextSyntax;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import pa.FCB;
import pa.OsDiskManager;
import pa.OsFileManager;

public class OsView {
	
	private JPanel fileTreePanel = null;							//JFrame -> TabbedPane -> FilePanel -> FileTreePanel
	private JPanel commandPanel = null;								//JFrame -> TabbedPane -> FilePanel -> CommandPanel
	private JPanel diskStatusPanelC = null;
	private JPanel diskStatusPanelD = null;
	private JPanel editFilePanel =null;								//JFrame -> TabbedPane -> FilePanel -> EditFilePanel
	
	
	private JTree fileTree = null;									//JFrame -> TabbedPane -> FilePanel -> FileTreePanel
	private JScrollPane scrollPaneJtree = null;						//JFrame -> TabbedPane -> FilePanel -> FileTreePanel
	private JPopupMenu popMenu = null;
	private JMenuItem[] menuItems = new JMenuItem[4];
	private TreePath treePath = null;
	
	private JTextField commandTextField =null;						//JFrame -> TabbedPane -> FilePanel -> CommandPanel 
	private JTextArea commandTextArea =null;						//JFrame -> TabbedPane -> FilePanel -> CommandPanel 
	private JScrollPane scrollPaneCommand = null;					//JFrame -> TabbedPane -> FilePanel -> CommandPanel 
	private ArrayList<String> commandHistory = null;
	private int commandHistoryPos = 0;
	
	private JTextArea editFileTextArea =null;						//JFrame -> TabbedPane -> FilePanel -> EditFilePanel 
	private JScrollPane scrollPaneEditFile = null;					//JFrame -> TabbedPane -> FilePanel -> EditFilePanel 
	private JButton submitButton = null;							//JFrame -> TabbedPane -> FilePanel -> EditFilePanel 
	private JButton cancleButton = null;							//JFrame -> TabbedPane -> FilePanel -> EditFilePanel 
	private String editContent = null;
	private FCB editParentFCB = null;
	private FCB editFileFCB = null;
	private String editpar = null;
	
	
	private ImageIcon freeImg = null;								//JFrame -> TabbedPane -> FilePanel -> DiskStatusPanel
	private ImageIcon useImg = null;								//JFrame -> TabbedPane -> FilePanel -> DiskStatusPanel
	private int[] freeC = null;										//JFrame -> TabbedPane -> FilePanel -> DiskStatusPanel
	private int[] freeD = null;										//JFrame -> TabbedPane -> FilePanel -> DiskStatusPanel
	private JLabel[] imgLables = null;								//JFrame -> TabbedPane -> FilePanel -> DiskStatusPanel
 	
	private JLabel stateTextLabel = null;
	private int usedBlockNumC = 0;
	private int usedBlockNumD = 0;
	private String stateText = null;
	
 	private Border border =null;	//border	

	private OsDiskManager diskManagerC = null;
 	private OsDiskManager diskManagerD = null;
 	private OsFileManager fileManagerC = null;
 	private OsFileManager fileManagerD = null;
 	private DefaultMutableTreeNode rootNode = null;
 	
 	String currentPath = null;
	String ctaStr = null;
	String ctfStr = null;
 	
	
	
	
	
	
	public OsView() {
		diskManagerC = new OsDiskManager("c");
		diskManagerD =  new OsDiskManager("d");
		fileManagerC = new OsFileManager(diskManagerC);
		fileManagerD = new OsFileManager(diskManagerD);
		commandHistory = new ArrayList<String>();
	}
	
	public void show(JPanel filePanel)
	{
		diskStatusPanelC = new JPanel();
		diskStatusPanelD = new JPanel();
		freeImg = new ImageIcon(this.getClass().getResource("free.png"));
		useImg = new ImageIcon(this.getClass().getResource("use.png"));
		freeC = diskManagerC.getFreeBlocksPosition();
		freeD = diskManagerD.getFreeBlocksPosition();
		imgLables = new JLabel[256];
		
		for(int i=0;i<128;i++)
		{
			imgLables[i] = new JLabel();
			if(freeC[i] == 0)
				imgLables[i].setIcon(freeImg);
			else
				imgLables[i].setIcon(useImg);
			diskStatusPanelC.add(imgLables[i]);
		}
		for(int i=0;i<128;i++)
		{
			imgLables[i+128] = new JLabel();
			if(freeD[i] == 0)
				imgLables[i+128].setIcon(freeImg);
			else
				imgLables[i+128].setIcon(useImg);
			diskStatusPanelD.add(imgLables[i+128]);
		}
		
		stateText = "C盘共 128 块已使用 "+usedBlockNumC+" 块"+"            D盘共128块已使用 "+usedBlockNumD+" 块";
		stateTextLabel = new JLabel(stateText);
		stateTextLabel.setBounds(215, 620, 765, 20);
		
		border = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
		border = BorderFactory.createTitledBorder(border, "C盘");
		diskStatusPanelC.setBorder(border);
		diskStatusPanelC.setBounds(15, 375, 765, 120);

		
		border = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
		border = BorderFactory.createTitledBorder(border, "D盘");
		diskStatusPanelD.setBorder(border);
		diskStatusPanelD.setBounds(15, 495, 765, 120);

	
		
		new Thread(new Runnable() {							//创建内部类 实现监听
			public void run() {
				while(true)
				{
					usedBlockNumC = 0;
					usedBlockNumD = 0;
					freeC = diskManagerC.getFreeBlocksPosition();
					freeD = diskManagerD.getFreeBlocksPosition();
					
					SwingUtilities.invokeLater(new Runnable() {
						
						@Override
						public void run() {
							for(int i=0;i<128;i++)
							{
								if(freeC[i] == 0)
									imgLables[i].setIcon(freeImg);
								else{
									imgLables[i].setIcon(useImg);
									usedBlockNumC++;
								}
							}
							
							for(int i=0;i<128;i++)
							{
								if(freeD[i] == 0)
									imgLables[i+128].setIcon(freeImg);
								else{
									imgLables[i+128].setIcon(useImg);
									usedBlockNumD++;
								}
									
							}
							stateText = "C盘共 128 块已使用 "+usedBlockNumC+" 块"+"            D盘共128块已使用 "+usedBlockNumD+" 块";
							stateTextLabel.setText(stateText);
						}
					});
					
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		
		
		
		
		fileTreePanel = new JPanel();
		rootNode = new DefaultMutableTreeNode("disk",true);
		fileManagerC.buildTree(null, rootNode);
		fileManagerD.buildTree(null, rootNode);		
		fileTree = new JTree(rootNode);
		fileTree.setCellRenderer(new MyTreeCellRenderer());
		fileTree.setRootVisible(false);
		DefaultTreeModel model = new DefaultTreeModel(rootNode, true);
		fileTree.setModel(model);
		scrollPaneJtree = new JScrollPane(fileTree);
		
		fileTreePanel.setLayout(null);
		scrollPaneJtree.setBounds(10, 18, 230, 320);
		border = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
		border = BorderFactory.createTitledBorder(border, "文件目录");
		fileTreePanel.setBorder(border);
		fileTreePanel.add(scrollPaneJtree);
		fileTreePanel.setBounds(15, 20, 250, 350);
				
		popMenu = new JPopupMenu();
		menuItems[0] = new JMenuItem("编辑");
		menuItems[1] = new JMenuItem("查看");
		menuItems[2] = new JMenuItem("删除");
		menuItems[3] = new JMenuItem("执行");
		
		menuItems[0].addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				Object[] paths = treePath.getPath();
				if("c".equals(paths[1].toString()))
				{
					
					String filePath = "/";
					String fileParentPath = "/";
					
					for(int i=2;i<paths.length;i++)
						filePath = filePath + paths[i] + "/";
					
					for(int i=2;i<paths.length-1;i++)
						fileParentPath = fileParentPath + paths[i] + "/";

					if(!"/".equals(fileParentPath))
					{
						editParentFCB = fileManagerC.openFileByPath(fileParentPath);
					}
					
					if(!"/".equals(filePath))
					{
						editFileFCB = fileManagerC.openFileByPath(filePath);
					}
					
					if(editFileFCB != null && editFileFCB.isFile())
					{
						byte[] b = fileManagerC.readFile(editFileFCB);
						if(b != null)
							editContent = new String(b);
						editpar = "c";
						editFileTextArea.setText(editContent);
						if(editFileFCB.isReadOnly())
							submitButton.setEnabled(false);
					}
					
				}
				if("d".equals(paths[1].toString()))
				{
					String filePath = "/";
					String fileParentPath = "/";
					
					for(int i=2;i<paths.length;i++)
						filePath = filePath + paths[i] + "/";
					
					for(int i=2;i<paths.length-1;i++)
						fileParentPath = fileParentPath + paths[i] + "/";

					if(!"/".equals(fileParentPath))
					{
						editParentFCB = fileManagerD.openFileByPath(fileParentPath);
					}
					
					if(!"/".equals(filePath))
					{
						editFileFCB = fileManagerD.openFileByPath(filePath);
					}
					
					if(editFileFCB != null && editFileFCB.isFile())
					{
						byte[] b = fileManagerD.readFile(editFileFCB);
						if(b != null)
							editContent = new String(b);
						editpar = "d";
						editFileTextArea.setText(editContent);
						if(editFileFCB.isReadOnly())
							submitButton.setEnabled(false);
					}
				}
			}
		});
		
		menuItems[1].addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				Object[] paths = treePath.getPath();
				if("c".equals(paths[1].toString()))
				{
					
					String filePath = "/";
					String fileParentPath = "/";
					
					for(int i=2;i<paths.length;i++)
						filePath = filePath + paths[i] + "/";
					
					for(int i=2;i<paths.length-1;i++)
						fileParentPath = fileParentPath + paths[i] + "/";

					if(!"/".equals(fileParentPath))
					{
						editParentFCB = fileManagerC.openFileByPath(fileParentPath);
					}
					
					if(!"/".equals(filePath))
					{
						editFileFCB = fileManagerC.openFileByPath(filePath);
					}
					
					if(editFileFCB != null && editFileFCB.isFile())
					{
						byte[] b = fileManagerC.readFile(editFileFCB);
						if(b != null)
							editContent = new String(b);
						editpar = "c";
						editFileTextArea.setText(editContent);
						editFileTextArea.setEditable(false);
						submitButton.setEnabled(false);
					}
					
				}
				if("d".equals(paths[1].toString()))
				{
					String filePath = "/";
					String fileParentPath = "/";
					
					for(int i=2;i<paths.length;i++)
						filePath = filePath + paths[i] + "/";
					
					for(int i=2;i<paths.length-1;i++)
						fileParentPath = fileParentPath + paths[i] + "/";

					if(!"/".equals(fileParentPath))
					{
						editParentFCB = fileManagerD.openFileByPath(fileParentPath);
					}
					
					if(!"/".equals(filePath))
					{
						editFileFCB = fileManagerD.openFileByPath(filePath);
					}
					
					if(editFileFCB != null && editFileFCB.isFile())
					{
						byte[] b = fileManagerD.readFile(editFileFCB);
						if(b != null)
							editContent = new String(b);
						editpar = "d";
						editFileTextArea.setText(editContent);
						editFileTextArea.setEditable(false);
						submitButton.setEnabled(false);
					}
				}
				
			}
		});
		
		
		menuItems[2].addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				String filePath = "/";
				String fileParentPath = "/";
				String fileGranderPath = "/";
				FCB fileFCB = null;
				FCB fileParentFCB = null;
				FCB fileGranderFCB = null;
				Object[] paths = treePath.getPath();
				
				for(int i=2;i<paths.length;i++)
					filePath = filePath + paths[i] + "/";
				
				for(int i=2;i<paths.length-1;i++)
					fileParentPath = fileParentPath + paths[i] + "/";
				
				for(int i=2;i<paths.length-2;i++)
					fileGranderPath = fileGranderPath + paths[i] + "/";

				if("c".equals(paths[1].toString()))
				{
					if(!"/".equals(filePath))
					{
						if(filePath.contains("."))
							fileFCB = fileManagerC.openFileByPath(filePath);
						else
							fileFCB = fileManagerC.openDirByPath(filePath);
					}
					if(!"/".equals(fileParentPath))
					{
						fileParentFCB = fileManagerC.openDirByPath(fileParentPath);
					}
					if(!"/".equals(fileGranderPath))
					{
						fileGranderFCB = fileManagerC.openDirByPath(fileGranderPath);
					}
					
					fileManagerC.delFileR(fileGranderFCB, fileParentFCB, fileFCB);
					
					DefaultTreeModel model  =  (DefaultTreeModel) fileTree.getModel();
					rootNode = new DefaultMutableTreeNode("disk");
					fileManagerC.buildTree(null, rootNode);
					fileManagerD.buildTree(null, rootNode);
					model.setRoot(rootNode);	
						
				}
				
				if("d".equals(paths[1].toString()))
				{
					if(!"/".equals(filePath))
					{
						if(filePath.contains("."))
							fileFCB = fileManagerD.openFileByPath(filePath);
						else
							fileFCB = fileManagerD.openDirByPath(filePath);
					}
					if(!"/".equals(fileParentPath))
					{
						fileParentFCB = fileManagerD.openDirByPath(fileParentPath);
					}
					if(!"/".equals(fileGranderPath))
					{
						fileGranderFCB = fileManagerD.openDirByPath(fileGranderPath);
					}
					
					fileManagerD.delFileR(fileGranderFCB, fileParentFCB, fileFCB);
					
					DefaultTreeModel model  =  (DefaultTreeModel) fileTree.getModel();
					rootNode = new DefaultMutableTreeNode("disk");
					fileManagerC.buildTree(null, rootNode);
					fileManagerD.buildTree(null, rootNode);
					model.setRoot(rootNode);	
						
				}
			}
		});
		
		
		menuItems[3].addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				

				Object[] paths = treePath.getPath();
				if("c".equals(paths[1].toString()))
				{
					
					String filePath = "/";
					
					for(int i=2;i<paths.length;i++)
						filePath = filePath + paths[i] + "/";
					
					FCB fileFCB = null;
					
					if(!"/".equals(filePath))
					{
						fileFCB = fileManagerC.openFileByPath(filePath);
					}

					if(fileFCB != null && fileFCB.isFile() && fileFCB.getExtend()[0] == 'e' && fileFCB.getExtend()[1] == 'x' && fileFCB.getExtend()[2] == 'e')
					{
						
						fileManagerC.createProcess(filePath);
					}
					
				}
				
				if("d".equals(paths[1].toString()))
				{
					
					String filePath = "/";
					
					for(int i=2;i<paths.length;i++)
						filePath = filePath + paths[i] + "/";
					
					FCB fileFCB = null;
					
					if(!"/".equals(filePath))
					{
						fileFCB = fileManagerD.openFileByPath(filePath);
					}
					
					if(fileFCB != null && fileFCB.isFile() && fileFCB.getExtend()[0] == 'e' && fileFCB.getExtend()[1] == 'x' && fileFCB.getExtend()[2] == 'e')
					{
						fileManagerD.createProcess(filePath);
					}
					
				}
				
				
				
			}
		});
		
		
		popMenu.add(menuItems[0]);
		popMenu.add(menuItems[1]);
		popMenu.add(menuItems[2]);
		popMenu.add(menuItems[3]);

		
		fileTree.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				int clicktimes = e.getClickCount();
				if(clicktimes == 2)
				{
					treePath = fileTree.getPathForLocation(e.getX(), e.getY());
					if(treePath != null)
					{
						Object[] paths = treePath.getPath();
						if(paths.length>1 && "c".equals(paths[1].toString()))
						{
							
							String filePath = "/";
							String fileParentPath = "/";
							
							for(int i=2;i<paths.length;i++)
								filePath = filePath + paths[i] + "/";
							
							for(int i=2;i<paths.length-1;i++)
								fileParentPath = fileParentPath + paths[i] + "/";

							if(!"/".equals(fileParentPath))
							{
								editParentFCB = fileManagerC.openFileByPath(fileParentPath);
							}
							
							if(!"/".equals(filePath))
							{
								editFileFCB = fileManagerC.openFileByPath(filePath);
							}
							
							if(editFileFCB != null && editFileFCB.isFile())
							{
								byte[] b = fileManagerC.readFile(editFileFCB);
								if(b != null)
									editContent = new String(b);
								editpar = "c";
								editFileTextArea.setText(editContent);
								if(editFileFCB.isReadOnly())
									submitButton.setEnabled(false);
							}
							
						}
						if(paths.length>1 && "d".equals(paths[1].toString()))
						{
							String filePath = "/";
							String fileParentPath = "/";
							
							for(int i=2;i<paths.length;i++)
								filePath = filePath + paths[i] + "/";
							
							for(int i=2;i<paths.length-1;i++)
								fileParentPath = fileParentPath + paths[i] + "/";

							if(!"/".equals(fileParentPath))
							{
								editParentFCB = fileManagerD.openFileByPath(fileParentPath);
							}
							
							if(!"/".equals(filePath))
							{
								editFileFCB = fileManagerD.openFileByPath(filePath);
							}
							
							if(editFileFCB != null && editFileFCB.isFile())
							{
								byte[] b = fileManagerD.readFile(editFileFCB);
								if(b != null)
									editContent = new String(b);
								editpar = "d";
								editFileTextArea.setText(editContent);
								if(editFileFCB.isReadOnly())
									submitButton.setEnabled(false);
							}
						}
							
					}
					
					
				}
					
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				treePath = fileTree.getPathForLocation(e.getX(), e.getY());
				fileTree.setSelectionPath(treePath);
				
				if(treePath != null && treePath.getPath().length>2 && e.getButton() == e.BUTTON3)
				{
					
					Object[] paths = treePath.getPath();
					String filePath = "/";
					
					for(int i=2;i<paths.length;i++)
						filePath = filePath + paths[i] + "/";
					
					FCB fcb = null;
					
					if("c".equals(paths[1].toString()))
					{
						fcb = fileManagerC.openFileByPath(filePath);
					}
					if("d".equals(paths[1].toString()))
					{
						fcb = fileManagerD.openFileByPath(filePath);
					}
					
					if(fcb != null)
					{
						menuItems[0].setVisible(false);		//编辑
						menuItems[1].setVisible(false);		//查看
						menuItems[2].setVisible(false);		//删除
						menuItems[3].setVisible(false);		//执行
						if(fcb.isFile())
						{
							menuItems[0].setVisible(true);	
							menuItems[1].setVisible(true);	
							menuItems[2].setVisible(true);
							
							if(fcb.getExtend()[0] == 'e' && fcb.getExtend()[1] == 'x' && fcb.getExtend()[2] == 'e')
							{
								menuItems[3].setVisible(true);		//执行
							}	
						}
						
						if(fcb.isDir())
						{
							menuItems[2].setVisible(true);
						}
							
						popMenu.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		
		
		
		
		commandPanel = new JPanel();
		commandPanel.setLayout(null);
		commandTextField = new JTextField();
		currentPath = "/c/";
		commandTextField.setText(currentPath);
		
		
		
		
		commandTextField.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				
				if(keyCode == KeyEvent.VK_UP && commandHistory != null)
				{
					if(commandHistory.size() > 0 && commandHistoryPos>=0)
					{
						commandTextField.setText(currentPath+commandHistory.get(commandHistoryPos));
						
						if(commandHistoryPos > 0)
							commandHistoryPos = commandHistoryPos - 1 ;
					}
				}
				
				if(keyCode == KeyEvent.VK_DOWN  && commandHistory != null)
				{
					if(commandHistoryPos >=0 && commandHistoryPos <= commandHistory.size() - 1)
					{
						
						if(commandHistoryPos == commandHistory.size() - 1)
							commandTextField.setText(currentPath);
						
						if(commandHistoryPos < commandHistory.size() - 1)
						{
							commandHistoryPos = commandHistoryPos + 1 ;
							commandTextField.setText(currentPath+commandHistory.get(commandHistoryPos));
						}
							
					}
					
				}
				
			}
		});
		
		
		
		
		commandTextField.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				
				ctaStr = commandTextArea.getText();
				ctfStr = commandTextField.getText();
				String commandStrs[] = ctfStr.split(" ");								//按空格分割
				if(commandStrs.length>1)
				{
					String[] commandStrsF = commandStrs[0].split("/");					//取得命令前部 并按 /分割
					

					switch (commandStrsF[commandStrsF.length - 1]) {					//命令前部的最后 应为命令
					case "create":{
						if(commandStrs.length == 2)										//如果命令后不为空
						{
							String[] sPath = commandStrs[1].split("/");					//取得命令的后部 并用 / 分割
							if(!sPath[0].isEmpty())										//如果后部命令以 / 开头 说明使用绝对路径名
								sPath = (currentPath + commandStrs[1]).split("/");

							if("c".equals(sPath[1]))
							{
								if(sPath.length == 3)
								{
									String[] fullname = sPath[sPath.length-1].split("\\.");
									String name =null;
									String extend = null;
									
									if(fullname.length == 1)
									{
										name = fullname[0];
									}
									
									if(fullname.length == 2)
									{
										name = fullname[0];
										extend = fullname[1];
									}
									
									if(extend != null && fileManagerC.createFile(null, null,name.getBytes(),extend.getBytes(), (byte)0) != null)
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "创建成功";
									else
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "创建失败";
									
								}
								if(sPath.length == 4)
								{
									String[] fullname = sPath[sPath.length-1].split("\\.");
									String name =null;
									String extend = null;
									
									if(fullname.length == 1)
									{
										name = fullname[0];
									}
									
									if(fullname.length == 2)
									{
										name = fullname[0];
										extend = fullname[1];
									}
									
									FCB parent = fileManagerC.openDirByPath("/"+sPath[2]);
									
									if(extend != null &&  parent!=null && fileManagerC.createFile(null, parent,name.getBytes(),extend.getBytes(), (byte)0) != null)
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "创建成功";
									else
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "创建失败";
									
									
								}
								if(sPath.length >4)
								{
									String[] fullname = sPath[sPath.length-1].split("\\.");
									String name =null;
									String extend = null;
									
									if(fullname.length == 1)
									{
										name = fullname[0];
									}
									
									if(fullname.length == 2)
									{
										name = fullname[0];
										extend = fullname[1];
									}
									String path1 = "/";
									for(int i=2;i<sPath.length-2;i++)
										path1 =path1+sPath[i] + "/";
									String path2 = path1 + sPath[sPath.length-2];
									
									FCB parent = fileManagerC.openDirByPath(path2);
									FCB grander = fileManagerC.openDirByPath(path1);
									if(extend != null &&  grander!=null && parent!=null && fileManagerC.createFile(grander, parent,name.getBytes(),extend.getBytes(), (byte)0) != null)
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "创建成功";
									else
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "创建失败";
								}
							}
							
							if("d".equals(sPath[1]))
							{
								if(sPath.length == 3)
								{
									String[] fullname = sPath[sPath.length-1].split("\\.");
									String name =null;
									String extend = null;
									
									if(fullname.length == 1)
									{
										name = fullname[0];
									}
									
									if(fullname.length == 2)
									{
										name = fullname[0];
										extend = fullname[1];
									}
									
									if(extend != null && fileManagerD.createFile(null, null,name.getBytes(),extend.getBytes(), (byte)0) != null)
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "创建成功";
									else
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "创建失败";
									
								}
								if(sPath.length == 4)
								{
									String[] fullname = sPath[sPath.length-1].split("\\.");
									String name =null;
									String extend = null;
									
									if(fullname.length == 1)
									{
										name = fullname[0];
									}
									
									if(fullname.length == 2)
									{
										name = fullname[0];
										extend = fullname[1];
									}
									
									FCB parent = fileManagerD.openDirByPath("/"+sPath[2]);
									
									if(extend != null &&  parent!=null && fileManagerD.createFile(null, parent,name.getBytes(),extend.getBytes(), (byte)0) != null)
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "创建成功";
									else
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "创建失败";
									
									
								}
								if(sPath.length >4)
								{
									String[] fullname = sPath[sPath.length-1].split("\\.");
									String name =null;
									String extend = null;
									
									if(fullname.length == 1)
									{
										name = fullname[0];
									}
									
									if(fullname.length == 2)
									{
										name = fullname[0];
										extend = fullname[1];
									}
									
									String path1 = "/";
									for(int i=2;i<sPath.length-2;i++)
										path1 =path1+sPath[i] + "/";
									String path2 = path1 + sPath[sPath.length-2];
									FCB parent = fileManagerD.openDirByPath(path2);
									FCB grander = fileManagerD.openDirByPath(path1);
									if( extend != null && grander!=null && parent!=null && fileManagerD.createFile(grander, parent,name.getBytes(),extend.getBytes(), (byte)0) != null)
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "创建成功";
									else
										ctaStr = ctaStr + '\n'+ "#System:# " + sPath[sPath.length-1] + "创建失败";
								}
							}
						}
						break;
					}
					case "copy":{
						if(commandStrs.length == 3)
						{
							String[] sPathSource = commandStrs[1].split("/");					//取得命令的后部 并用 / 分割
							String[] sPathTarget = commandStrs[2].split("/");					//取得命令的后部 并用 / 分割
							if(!sPathSource[0].isEmpty())										//如果后部命令以 / 开头 说明使用绝对路径名
								sPathSource = (currentPath + commandStrs[1]).split("/");
							
							if(!sPathTarget[0].isEmpty())										//如果后部命令以 / 开头 说明使用绝对路径名
								sPathTarget = (currentPath + commandStrs[2]).split("/");
							
							String pathnameSource = "/";
							String pathnameTarget = "/";
							String pathnameTargetParent = "/";
							String pathnameTargetGrander = "/";
							
							for(int i=2;i<sPathSource.length;i++)
								pathnameSource = pathnameSource + sPathSource[i] + "/";
							
							for(int i=2;i<sPathTarget.length;i++)
								pathnameTarget = pathnameTarget + sPathTarget[i] + "/";
							
							for(int i=2;i<sPathTarget.length-1;i++)
								pathnameTargetParent = pathnameTargetParent + sPathTarget[i] + "/";
							
							for(int i=2;i<sPathTarget.length-2;i++)
								pathnameTargetGrander = pathnameTargetGrander + sPathTarget[i] + "/";
							
							
							if("c".equals(sPathTarget[1]) && "c".equals(sPathSource[1]) )
							{
								FCB sourceFCB = fileManagerC.openFileByPath(pathnameSource);
								FCB targetParentFCB = null;
								FCB targetFCB = null;
								FCB targetGranderFCB = null;
								if(!"/".equals(pathnameTargetParent))
									targetParentFCB = fileManagerC.openFileByPath(pathnameTargetParent);
								
								if(!"/".equals(pathnameTarget))
									targetFCB = fileManagerC.openFileByPath(pathnameTarget);
								
								if(!"/".equals(pathnameTargetGrander))
									targetGranderFCB = fileManagerC.openFileByPath(pathnameTargetGrander);
								
								int flag = JOptionPane.YES_OPTION;
								
								if(targetFCB != null)
								{
									System.out.println(new String(targetFCB.getName()));
									flag = JOptionPane.showConfirmDialog(null, "文件已存在，是否覆盖","提示",JOptionPane.YES_NO_OPTION);
								}
								
								if( flag == JOptionPane.YES_OPTION && sourceFCB != null && ("/".equals(pathnameTargetParent) || targetParentFCB != null))
								{
									String[] fullname = sPathTarget[sPathTarget.length-1].split("\\.");
									String name =null;
									String extend = null;
									
									if(fullname.length == 1)
									{
										name = fullname[0];
									}
									
									if(fullname.length == 2)
									{
										name = fullname[0];
										extend = fullname[1];
									}
									
									if(targetFCB == null)
									{
										if("/".equals(pathnameTargetGrander) && "/".equals(pathnameTargetParent))
										{
											fileManagerC.createFile(null, null, name.getBytes(), extend.getBytes(), (byte)sourceFCB.getAttribute());
											FCB newFCB = fileManagerC.openFileByPath(pathnameTarget);
											fileManagerC.reSaveFile(null, newFCB, fileManagerC.readFile(sourceFCB));
										}
										if("/".equals(pathnameTargetGrander) && !"/".equals(pathnameTargetParent))
										{
											fileManagerC.createFile(null, targetParentFCB, name.getBytes(), extend.getBytes(), (byte)sourceFCB.getAttribute());
											FCB newFCB = fileManagerC.openFileByPath(pathnameTarget);
											fileManagerC.reSaveFile(targetParentFCB, newFCB, fileManagerC.readFile(sourceFCB));
										}
										if(!"/".equals(pathnameTargetGrander) && !"/".equals(pathnameTargetParent))
										{
											fileManagerC.createFile(targetGranderFCB, targetParentFCB, name.getBytes(), extend.getBytes(), (byte)sourceFCB.getAttribute());
											FCB newFCB = fileManagerC.openFileByPath(pathnameTarget);
											fileManagerC.reSaveFile(targetParentFCB, newFCB, fileManagerC.readFile(sourceFCB));
										}
									}
									
									if(targetFCB != null)
									{
										if("/".equals(pathnameTargetGrander) && "/".equals(pathnameTargetParent))
										{
											FCB newFCB = fileManagerC.openFileByPath(pathnameTarget);
											newFCB.setAttribute(sourceFCB.getAttribute());
											fileManagerC.reSaveFile(null, newFCB, fileManagerC.readFile(sourceFCB));
										}
										if("/".equals(pathnameTargetGrander) && !"/".equals(pathnameTargetParent))
										{
											FCB newFCB = fileManagerC.openFileByPath(pathnameTarget);
											newFCB.setAttribute(sourceFCB.getAttribute());
											fileManagerC.reSaveFile(targetParentFCB, newFCB, fileManagerC.readFile(sourceFCB));
										}
										if(!"/".equals(pathnameTargetGrander) && !"/".equals(pathnameTargetParent))
										{
											FCB newFCB = fileManagerC.openFileByPath(pathnameTarget);
											newFCB.setAttribute(sourceFCB.getAttribute());
											fileManagerC.reSaveFile(targetParentFCB, newFCB, fileManagerC.readFile(sourceFCB));
										}
									}
								}
							}
							
							if("d".equals(sPathTarget[1]) && "d".equals(sPathSource[1]) )
							{
								FCB sourceFCB = fileManagerD.openFileByPath(pathnameSource);
								FCB targetParentFCB = null;
								FCB targetFCB = null;
								FCB targetGranderFCB = null;
								if(!"/".equals(pathnameTargetParent))
									targetParentFCB = fileManagerD.openFileByPath(pathnameTargetParent);
								
								if(!"/".equals(pathnameTarget))
									targetFCB = fileManagerD.openFileByPath(pathnameTarget);
								
								if(!"/".equals(pathnameTargetGrander))
									targetGranderFCB = fileManagerD.openFileByPath(pathnameTargetGrander);
								
								int flag = JOptionPane.YES_OPTION;
								
								if(targetFCB != null)
								{
									flag = JOptionPane.showConfirmDialog(null, "文件已存在，是否覆盖","提示",JOptionPane.YES_NO_OPTION);
								}
								
								if( flag == JOptionPane.YES_OPTION && sourceFCB != null && ("/".equals(pathnameTargetParent) || targetParentFCB != null))
								{
									String[] fullname = sPathTarget[sPathTarget.length-1].split("\\.");
									String name =null;
									String extend = null;
									
									if(fullname.length == 1)
									{
										name = fullname[0];
									}
									
									if(fullname.length == 2)
									{
										name = fullname[0];
										extend = fullname[1];
									}
									
									if(targetFCB == null)
									{
										if("/".equals(pathnameTargetGrander) && "/".equals(pathnameTargetParent))
										{
											fileManagerD.createFile(null, null, name.getBytes(), extend.getBytes(), (byte)sourceFCB.getAttribute());
											FCB newFCB = fileManagerD.openFileByPath(pathnameTarget);
											fileManagerD.reSaveFile(null, newFCB, fileManagerD.readFile(sourceFCB));
										}
										if("/".equals(pathnameTargetGrander) && !"/".equals(pathnameTargetParent))
										{
											fileManagerD.createFile(null, targetParentFCB, name.getBytes(), extend.getBytes(), (byte)sourceFCB.getAttribute());
											FCB newFCB = fileManagerD.openFileByPath(pathnameTarget);
											fileManagerD.reSaveFile(targetParentFCB, newFCB, fileManagerD.readFile(sourceFCB));
										}
										if(!"/".equals(pathnameTargetGrander) && !"/".equals(pathnameTargetParent))
										{
											fileManagerD.createFile(targetGranderFCB, targetParentFCB, name.getBytes(), extend.getBytes(), (byte)sourceFCB.getAttribute());
											FCB newFCB = fileManagerD.openFileByPath(pathnameTarget);
											fileManagerD.reSaveFile(targetParentFCB, newFCB, fileManagerD.readFile(sourceFCB));
										}
									}
									
									if(targetFCB != null)
									{
										if("/".equals(pathnameTargetGrander) && "/".equals(pathnameTargetParent))
										{
											FCB newFCB = fileManagerD.openFileByPath(pathnameTarget);
											newFCB.setAttribute(sourceFCB.getAttribute());
											fileManagerD.reSaveFile(null, newFCB, fileManagerD.readFile(sourceFCB));
										}
										if("/".equals(pathnameTargetGrander) && !"/".equals(pathnameTargetParent))
										{
											FCB newFCB = fileManagerD.openFileByPath(pathnameTarget);
											newFCB.setAttribute(sourceFCB.getAttribute());
											fileManagerD.reSaveFile(targetParentFCB, newFCB, fileManagerD.readFile(sourceFCB));
										}
										if(!"/".equals(pathnameTargetGrander) && !"/".equals(pathnameTargetParent))
										{
											FCB newFCB = fileManagerD.openFileByPath(pathnameTarget);
											newFCB.setAttribute(sourceFCB.getAttribute());
											fileManagerD.reSaveFile(targetParentFCB, newFCB, fileManagerD.readFile(sourceFCB));
										}
									}
								}
							}
							
							if("c".equals(sPathTarget[1]) && "d".equals(sPathSource[1]) )
							{
								FCB sourceFCB = fileManagerD.openFileByPath(pathnameSource);
								FCB targetParentFCB = null;
								FCB targetFCB = null;
								FCB targetGranderFCB = null;
								if(!"/".equals(pathnameTargetParent))
									targetParentFCB = fileManagerC.openFileByPath(pathnameTargetParent);
								
								if(!"/".equals(pathnameTarget))
									targetFCB = fileManagerC.openFileByPath(pathnameTarget);
								
								if(!"/".equals(pathnameTargetGrander))
									targetGranderFCB = fileManagerC.openFileByPath(pathnameTargetGrander);
								
								int flag = JOptionPane.YES_OPTION;
								
								if(targetFCB != null)
								{
									flag = JOptionPane.showConfirmDialog(null, "文件已存在，是否覆盖","提示",JOptionPane.YES_NO_OPTION);
								}
								
								if( flag == JOptionPane.YES_OPTION && sourceFCB != null && ("/".equals(pathnameTargetParent) || targetParentFCB != null))
								{
									String[] fullname = sPathTarget[sPathTarget.length-1].split("\\.");
									String name =null;
									String extend = null;
									
									if(fullname.length == 1)
									{
										name = fullname[0];
									}
									
									if(fullname.length == 2)
									{
										name = fullname[0];
										extend = fullname[1];
									}
									
									if(targetFCB == null)
									{
										if("/".equals(pathnameTargetGrander) && "/".equals(pathnameTargetParent))
										{
											fileManagerC.createFile(null, null, name.getBytes(), extend.getBytes(), (byte)sourceFCB.getAttribute());
											FCB newFCB = fileManagerC.openFileByPath(pathnameTarget);
											fileManagerC.reSaveFile(null, newFCB, fileManagerD.readFile(sourceFCB));
										}
										if("/".equals(pathnameTargetGrander) && !"/".equals(pathnameTargetParent))
										{
											fileManagerC.createFile(null, targetParentFCB, name.getBytes(), extend.getBytes(), (byte)sourceFCB.getAttribute());
											FCB newFCB = fileManagerC.openFileByPath(pathnameTarget);
											fileManagerC.reSaveFile(targetParentFCB, newFCB, fileManagerD.readFile(sourceFCB));
										}
										if(!"/".equals(pathnameTargetGrander) && !"/".equals(pathnameTargetParent))
										{
											fileManagerC.createFile(targetGranderFCB, targetParentFCB, name.getBytes(), extend.getBytes(), (byte)sourceFCB.getAttribute());
											FCB newFCB = fileManagerC.openFileByPath(pathnameTarget);
											fileManagerC.reSaveFile(targetParentFCB, newFCB, fileManagerD.readFile(sourceFCB));
										}
									}
									
									if(targetFCB != null)
									{
										if("/".equals(pathnameTargetGrander) && "/".equals(pathnameTargetParent))
										{
											FCB newFCB = fileManagerC.openFileByPath(pathnameTarget);
											newFCB.setAttribute(sourceFCB.getAttribute());
											fileManagerC.reSaveFile(null, newFCB, fileManagerD.readFile(sourceFCB));
										}
										if("/".equals(pathnameTargetGrander) && !"/".equals(pathnameTargetParent))
										{
											FCB newFCB = fileManagerC.openFileByPath(pathnameTarget);
											newFCB.setAttribute(sourceFCB.getAttribute());
											fileManagerC.reSaveFile(targetParentFCB, newFCB, fileManagerD.readFile(sourceFCB));
										}
										if(!"/".equals(pathnameTargetGrander) && !"/".equals(pathnameTargetParent))
										{
											FCB newFCB = fileManagerC.openFileByPath(pathnameTarget);
											newFCB.setAttribute(sourceFCB.getAttribute());
											fileManagerC.reSaveFile(targetParentFCB, newFCB, fileManagerD.readFile(sourceFCB));
										}
									}
								}
							}
							
							if("d".equals(sPathTarget[1]) && "c".equals(sPathSource[1]) )
							{
								FCB sourceFCB = fileManagerC.openFileByPath(pathnameSource);
								FCB targetParentFCB = null;
								FCB targetFCB = null;
								FCB targetGranderFCB = null;
								if(!"/".equals(pathnameTargetParent))
									targetParentFCB = fileManagerD.openFileByPath(pathnameTargetParent);
								
								if(!"/".equals(pathnameTarget))
									targetFCB = fileManagerD.openFileByPath(pathnameTarget);
								
								if(!"/".equals(pathnameTargetGrander))
									targetGranderFCB = fileManagerD.openFileByPath(pathnameTargetGrander);
								
								int flag = JOptionPane.YES_OPTION;
								
								if(targetFCB != null)
								{
									flag = JOptionPane.showConfirmDialog(null, "文件已存在，是否覆盖","提示",JOptionPane.YES_NO_OPTION);
								}
								
								if( flag == JOptionPane.YES_OPTION && sourceFCB != null && ("/".equals(pathnameTargetParent) || targetParentFCB != null))
								{
									String[] fullname = sPathTarget[sPathTarget.length-1].split("\\.");
									String name =null;
									String extend = null;
									
									if(fullname.length == 1)
									{
										name = fullname[0];
									}
									
									if(fullname.length == 2)
									{
										name = fullname[0];
										extend = fullname[1];
									}
									
									if(targetFCB == null)
									{
										if("/".equals(pathnameTargetGrander) && "/".equals(pathnameTargetParent))
										{
											fileManagerD.createFile(null, null, name.getBytes(), extend.getBytes(), (byte)sourceFCB.getAttribute());
											FCB newFCB = fileManagerD.openFileByPath(pathnameTarget);
											fileManagerD.reSaveFile(null, newFCB, fileManagerC.readFile(sourceFCB));
										}
										if("/".equals(pathnameTargetGrander) && !"/".equals(pathnameTargetParent))
										{
											fileManagerD.createFile(null, targetParentFCB, name.getBytes(), extend.getBytes(), (byte)sourceFCB.getAttribute());
											FCB newFCB = fileManagerD.openFileByPath(pathnameTarget);
											fileManagerD.reSaveFile(targetParentFCB, newFCB, fileManagerC.readFile(sourceFCB));
										}
										if(!"/".equals(pathnameTargetGrander) && !"/".equals(pathnameTargetParent))
										{
											fileManagerD.createFile(targetGranderFCB, targetParentFCB, name.getBytes(), extend.getBytes(), (byte)sourceFCB.getAttribute());
											FCB newFCB = fileManagerD.openFileByPath(pathnameTarget);
											fileManagerD.reSaveFile(targetParentFCB, newFCB, fileManagerC.readFile(sourceFCB));
										}
									}
									
									if(targetFCB != null)
									{
										if("/".equals(pathnameTargetGrander) && "/".equals(pathnameTargetParent))
										{
											FCB newFCB = fileManagerD.openFileByPath(pathnameTarget);
											newFCB.setAttribute(sourceFCB.getAttribute());
											fileManagerD.reSaveFile(null, newFCB, fileManagerC.readFile(sourceFCB));
										}
										if("/".equals(pathnameTargetGrander) && !"/".equals(pathnameTargetParent))
										{
											FCB newFCB = fileManagerD.openFileByPath(pathnameTarget);
											newFCB.setAttribute(sourceFCB.getAttribute());
											fileManagerD.reSaveFile(targetParentFCB, newFCB, fileManagerC.readFile(sourceFCB));
										}
										if(!"/".equals(pathnameTargetGrander) && !"/".equals(pathnameTargetParent))
										{
											FCB newFCB = fileManagerD.openFileByPath(pathnameTarget);
											newFCB.setAttribute(sourceFCB.getAttribute());
											fileManagerD.reSaveFile(targetParentFCB, newFCB, fileManagerC.readFile(sourceFCB));
										}
									}
								}
							}
							
						}
						
						break;
					}
					
						
					case "delete":{
						if(commandStrs.length == 2)										//如果命令后不为空
						{
							
							String[] sPath = commandStrs[1].split("/");					//取得命令的后部 并用 / 分割
							if(!sPath[0].isEmpty())										//如果后部命令以 / 开头 说明使用绝对路径名													
								{
									sPath = (currentPath + commandStrs[1]).split("/");
									commandStrs[1] = currentPath + commandStrs[1];
								}
							
							if("c".equals(sPath[1]))
							{
								String pathname = "/";
								
								for(int i=2;i<sPath.length;i++)
									pathname = pathname + sPath[i] + "/";

								if(sPath.length == 3)
								{
									FCB fcb = fileManagerC.openFileByPath(pathname);
									if(fcb != null && fcb.isFile())
									{
										fileManagerC.delFile(null, null, fcb);
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除成功";
									}else{
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除失败";
									}
								}
								
								if(sPath.length == 4)
								{
									FCB fcb = fileManagerC.openFileByPath(pathname);
									FCB parent = fileManagerC.openDirByPath("/"+sPath[2]);
									if(fcb!=null && fcb.isFile() && parent !=null)
									{
										fileManagerC.delFile(null, parent, fcb);
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除成功";
									}else{
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除失败";
									}
								}
								
								if(sPath.length > 4)
								{
									FCB fcb = fileManagerC.openFileByPath(pathname);
									String path1 = "/";
									for(int i=2;i<sPath.length-2;i++)
										path1 =path1+sPath[i] + "/";
									String path2 = path1 + sPath[sPath.length-2];
									FCB parent = fileManagerC.openDirByPath(path2);
									FCB grander = fileManagerC.openDirByPath(path1);
									if(grander!=null && parent!=null && fcb!=null && fcb.isFile())
									{
										fileManagerC.delFile(grander, parent, fcb);
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除成功";
									}else{
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除失败";
									}
								}
								
							}

							
							if("d".equals(sPath[1]))
							{
								String pathname = "/";
								
								for(int i=2;i<sPath.length;i++)
									pathname = pathname + sPath[i] + "/";

								if(sPath.length == 3)
								{
									FCB fcb = fileManagerD.openFileByPath(pathname);
									if(fcb != null && fcb.isFile())
									{
										fileManagerD.delFile(null, null, fcb);
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除成功";
									}else{
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除失败";
									}
								}
								
								if(sPath.length == 4)
								{
									FCB fcb = fileManagerD.openFileByPath(pathname);
									FCB parent = fileManagerD.openDirByPath("/"+sPath[2]);
									if(fcb!=null && fcb.isFile() && parent !=null)
									{
										fileManagerD.delFile(null, parent, fcb);
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除成功";
									}else{
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除失败";
									}
								}
								
								if(sPath.length > 4)
								{
									FCB fcb = fileManagerD.openFileByPath(pathname);
									System.out.println(pathname);
									String path1 = "/";
									for(int i=2;i<sPath.length-2;i++)
										path1 =path1+sPath[i] + "/";
									String path2 = path1 + sPath[sPath.length-2];
									FCB parent = fileManagerD.openDirByPath(path2);
									FCB grander = fileManagerD.openDirByPath(path1);
									if(grander!=null && parent!=null && fcb!=null && fcb.isFile())
									{
										fileManagerD.delFile(grander, parent, fcb);
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除成功";
									}else{
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除失败";
									}
								}
							}
						}
						break;
					}
					
					
					case "move":{
						
						
						if(commandStrs.length == 3)
						{
							String[] sPathSource = commandStrs[1].split("/");					//取得命令的后部 并用 / 分割
							String[] sPathTarget = commandStrs[2].split("/");					//取得命令的后部 并用 / 分割
							if(!sPathSource[0].isEmpty())										//如果后部命令以 / 开头 说明使用绝对路径名
								sPathSource = (currentPath + commandStrs[1]).split("/");
							
							if(!sPathTarget[0].isEmpty())										//如果后部命令以 / 开头 说明使用绝对路径名
								sPathTarget = (currentPath + commandStrs[2]).split("/");
							
							String pathnameSource = "/";
							String pathnameSourceParent = "/";
							String pathnameSourceGrander = "/";
							String pathnameTarget = "/";
							String pathnameTargetParent = "/";
							String pathnameTargetGrander = "/";
							
							for(int i=2;i<sPathSource.length;i++)
								pathnameSource = pathnameSource + sPathSource[i] + "/";
							
							for(int i=2;i<sPathSource.length-1;i++)
								pathnameSourceParent = pathnameSourceParent + sPathSource[i] + "/";
							
							for(int i=2;i<sPathSource.length-2;i++)
								pathnameSourceGrander = pathnameSourceGrander + sPathSource[i] + "/";
							
							for(int i=2;i<sPathTarget.length;i++)
								pathnameTarget = pathnameTarget + sPathTarget[i] + "/";
							
							for(int i=2;i<sPathTarget.length-1;i++)
								pathnameTargetParent = pathnameTargetParent + sPathTarget[i] + "/";
							
							for(int i=2;i<sPathTarget.length-2;i++)
								pathnameTargetGrander = pathnameTargetGrander + sPathTarget[i] + "/";
							
							
							if("c".equals(sPathTarget[1]) && "c".equals(sPathSource[1]) )
							{
								FCB sourceFCB = fileManagerC.openFileByPath(pathnameSource);
								FCB sourceParentFCB = null;
								FCB targetParentFCB = null;
								FCB targetFCB = null;
								FCB targetGranderFCB = null;
								
								if(!"/".equals(pathnameSourceParent))
									sourceParentFCB = fileManagerC.openFileByPath(pathnameSourceParent);
								
								if(!"/".equals(pathnameTargetParent))
									targetParentFCB = fileManagerC.openFileByPath(pathnameTargetParent);
								
								if(!"/".equals(pathnameTarget))
									targetFCB = fileManagerC.openFileByPath(pathnameTarget);
								
								if(!"/".equals(pathnameTargetGrander))
									targetGranderFCB = fileManagerC.openFileByPath(pathnameTargetGrander);
								
								int flag = JOptionPane.YES_OPTION;
								
								if(targetFCB != null)
								{
									flag = JOptionPane.showConfirmDialog(null, "文件已存在，是否覆盖","提示",JOptionPane.YES_NO_OPTION);
								}
								
								if( flag == JOptionPane.YES_OPTION && sourceFCB != null && ("/".equals(pathnameTargetParent) || targetParentFCB != null))
								{
									String[] fullname = sPathTarget[sPathTarget.length-1].split("\\.");
									String name =null;
									String extend = null;
									
									if(fullname.length == 1)
									{
										name = fullname[0];
									}
									
									if(fullname.length == 2)
									{
										name = fullname[0];
										extend = fullname[1];
									}
									
									if(targetFCB == null && extend != null)
									{
										fileManagerC.createFile(targetGranderFCB, targetParentFCB, name.getBytes(), extend.getBytes(), (byte)0);
										targetFCB = fileManagerC.openFileByPath(pathnameTarget);
										fileManagerC.moveFCB(sourceFCB, sourceParentFCB, targetFCB, targetParentFCB);
									}else if(targetFCB != null && extend!=null)
									{
										fileManagerC.delFile(targetGranderFCB, targetParentFCB, targetFCB);
										fileManagerC.createFile(targetGranderFCB, targetParentFCB, name.getBytes(), extend.getBytes(), (byte)0);
										targetFCB = fileManagerC.openFileByPath(pathnameTarget);
										fileManagerC.moveFCB(sourceFCB, sourceParentFCB, targetFCB, targetParentFCB);
									}
									
								}
							}
							
							
							if("d".equals(sPathTarget[1]) && "d".equals(sPathSource[1]) )
							{
								FCB sourceFCB = fileManagerD.openFileByPath(pathnameSource);
								FCB sourceParentFCB = null;
								FCB targetParentFCB = null;
								FCB targetFCB = null;
								FCB targetGranderFCB = null;
								
								if(!"/".equals(pathnameSourceParent))
									sourceParentFCB = fileManagerD.openFileByPath(pathnameSourceParent);
								
								if(!"/".equals(pathnameTargetParent))
									targetParentFCB = fileManagerD.openFileByPath(pathnameTargetParent);
								
								if(!"/".equals(pathnameTarget))
									targetFCB = fileManagerD.openFileByPath(pathnameTarget);
								
								if(!"/".equals(pathnameTargetGrander))
									targetGranderFCB = fileManagerD.openFileByPath(pathnameTargetGrander);
								
								int flag = JOptionPane.YES_OPTION;
								
								if(targetFCB != null)
								{
									flag = JOptionPane.showConfirmDialog(null, "文件已存在，是否覆盖","提示",JOptionPane.YES_NO_OPTION);
								}
								
								if( flag == JOptionPane.YES_OPTION && sourceFCB != null && ("/".equals(pathnameTargetParent) || targetParentFCB != null))
								{
									String[] fullname = sPathTarget[sPathTarget.length-1].split("\\.");
									String name =null;
									String extend = null;
									
									if(fullname.length == 1)
									{
										name = fullname[0];
									}
									
									if(fullname.length == 2)
									{
										name = fullname[0];
										extend = fullname[1];
									}
									
									if(targetFCB == null && extend != null)
									{
										fileManagerD.createFile(targetGranderFCB, targetParentFCB, name.getBytes(), extend.getBytes(), (byte)0);
										targetFCB = fileManagerD.openFileByPath(pathnameTarget);
										fileManagerD.moveFCB(sourceFCB, sourceParentFCB, targetFCB, targetParentFCB);
									}else if(targetFCB != null && extend!=null)
									{
										fileManagerD.delFile(targetGranderFCB, targetParentFCB, targetFCB);
										fileManagerD.createFile(targetGranderFCB, targetParentFCB, name.getBytes(), extend.getBytes(), (byte)0);
										targetFCB = fileManagerD.openFileByPath(pathnameTarget);
										fileManagerD.moveFCB(sourceFCB, sourceParentFCB, targetFCB, targetParentFCB);
									}
									
								}
							}
							
							
							if("c".equals(sPathTarget[1]) && "d".equals(sPathSource[1]) )
							{
								FCB sourceFCB = fileManagerD.openFileByPath(pathnameSource);
								FCB sourceParentFCB = null;
								FCB sourceGranderFCB = null;
								FCB targetParentFCB = null;
								FCB targetFCB = null;
								FCB targetGranderFCB = null;
								
								if(!"/".equals(pathnameSourceParent))
									sourceParentFCB = fileManagerD.openFileByPath(pathnameSourceParent);
								
								if(!"/".equals(pathnameSourceGrander))
									sourceGranderFCB = fileManagerD.openFileByPath(pathnameSourceGrander);
								
								
								if(!"/".equals(pathnameTargetParent))
									targetParentFCB = fileManagerC.openFileByPath(pathnameTargetParent);
								
								if(!"/".equals(pathnameTarget))
									targetFCB = fileManagerC.openFileByPath(pathnameTarget);
								
								if(!"/".equals(pathnameTargetGrander))
									targetGranderFCB = fileManagerC.openFileByPath(pathnameTargetGrander);
								
								int flag = JOptionPane.YES_OPTION;
								
								if(targetFCB != null)
								{
									flag = JOptionPane.showConfirmDialog(null, "文件已存在，是否覆盖","提示",JOptionPane.YES_NO_OPTION);
								}
								
								if( flag == JOptionPane.YES_OPTION && sourceFCB != null && ("/".equals(pathnameTargetParent) || targetParentFCB != null))
								{
									String[] fullname = sPathTarget[sPathTarget.length-1].split("\\.");
									String name =null;
									String extend = null;
									
									if(fullname.length == 1)
									{
										name = fullname[0];
									}
									
									if(fullname.length == 2)
									{
										name = fullname[0];
										extend = fullname[1];
									}
									
									if(targetFCB == null)
									{
										fileManagerC.createFile(targetGranderFCB, targetParentFCB, name.getBytes(), extend.getBytes(), (byte)sourceFCB.getAttribute());
										FCB newFCB = fileManagerC.openFileByPath(pathnameTarget);
										fileManagerC.reSaveFile(targetParentFCB, newFCB, fileManagerD.readFile(sourceFCB));
										fileManagerD.delFile(sourceGranderFCB, sourceParentFCB, sourceFCB);
									}
									
									if(targetFCB != null)
									{
										FCB newFCB = fileManagerC.openFileByPath(pathnameTarget);
										newFCB.setAttribute(sourceFCB.getAttribute());
										fileManagerC.reSaveFile(targetParentFCB, newFCB, fileManagerD.readFile(sourceFCB));
										fileManagerD.delFile(sourceGranderFCB, sourceParentFCB, sourceFCB);
									}
								}
							}
							
							if("d".equals(sPathTarget[1]) && "c".equals(sPathSource[1]) )
							{
								FCB sourceFCB = fileManagerC.openFileByPath(pathnameSource);
								FCB sourceParentFCB = null;
								FCB sourceGranderFCB = null;
								FCB targetParentFCB = null;
								FCB targetFCB = null;
								FCB targetGranderFCB = null;
								
								if(!"/".equals(pathnameSourceParent))
									sourceParentFCB = fileManagerC.openFileByPath(pathnameSourceParent);
								
								if(!"/".equals(pathnameSourceGrander))
									sourceGranderFCB = fileManagerC.openFileByPath(pathnameSourceGrander);
								
								
								if(!"/".equals(pathnameTargetParent))
									targetParentFCB = fileManagerD.openFileByPath(pathnameTargetParent);
								
								if(!"/".equals(pathnameTarget))
									targetFCB = fileManagerD.openFileByPath(pathnameTarget);
								
								if(!"/".equals(pathnameTargetGrander))
									targetGranderFCB = fileManagerD.openFileByPath(pathnameTargetGrander);
								
								int flag = JOptionPane.YES_OPTION;
								
								if(targetFCB != null)
								{
									flag = JOptionPane.showConfirmDialog(null, "文件已存在，是否覆盖","提示",JOptionPane.YES_NO_OPTION);
								}
								
								if( flag == JOptionPane.YES_OPTION && sourceFCB != null && ("/".equals(pathnameTargetParent) || targetParentFCB != null))
								{
									String[] fullname = sPathTarget[sPathTarget.length-1].split("\\.");
									String name =null;
									String extend = null;
									
									if(fullname.length == 1)
									{
										name = fullname[0];
									}
									
									if(fullname.length == 2)
									{
										name = fullname[0];
										extend = fullname[1];
									}
									
									if(targetFCB == null)
									{
										fileManagerD.createFile(targetGranderFCB, targetParentFCB, name.getBytes(), extend.getBytes(), (byte)sourceFCB.getAttribute());
										FCB newFCB = fileManagerD.openFileByPath(pathnameTarget);
										fileManagerD.reSaveFile(targetParentFCB, newFCB, fileManagerC.readFile(sourceFCB));
										fileManagerC.delFile(sourceGranderFCB, sourceParentFCB, sourceFCB);
									}
									
									if(targetFCB != null)
									{
										FCB newFCB = fileManagerD.openFileByPath(pathnameTarget);
										newFCB.setAttribute(sourceFCB.getAttribute());
										fileManagerD.reSaveFile(targetParentFCB, newFCB, fileManagerC.readFile(sourceFCB));
										fileManagerC.delFile(sourceGranderFCB, sourceParentFCB, sourceFCB);
									}
								}
							}

						}
						
						
						break;
					}
					
						
					case "type":{
						if(commandStrs.length == 2)			//commandStrs 按空格分割命令 若命令后不为空
						{	
							String[] sPath = commandStrs[1].split("/");					//取得命令的后部 并用 / 分割
							if(!sPath[0].isEmpty())										//如果后部命令以 / 开头 说明使用绝对路径名
								sPath = (currentPath + commandStrs[1]).split("/");
							
							String pathname = "/";
							
							for(int i=2;i<sPath.length;i++)
								pathname = pathname + sPath[i] + "/";
							
							if("c".equals(sPath[1]))
							{
								if(sPath.length == 3)
								{
									FCB fcb = fileManagerC.openFileByPath(pathname);
									if(fcb != null)
									{
										byte [] b = fileManagerC.readFile(fcb);
										if(b != null)
											editContent = new String(b);
										editFileFCB = fcb;
										editParentFCB = null;
										editpar = "c";
										editFileTextArea.setText(editContent);
										submitButton.setEnabled(false);
										editFileTextArea.setEditable(false);
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "打开成功";
									}else{
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "打开失败";
									}
								}
								
								if(sPath.length > 3)
								{
									String path = "/";
									for(int i=2;i<sPath.length-1;i++)
										path =path+sPath[i] + "/";
									FCB parentfcb = fileManagerC.openDirByPath(path);
									FCB fcb = fileManagerC.openFileByPath(pathname);
									if(parentfcb != null && fcb != null)
									{
										byte [] b = fileManagerC.readFile(fcb);
										if(b != null)
											editContent = new String(b);
										editFileFCB = fcb;
										editParentFCB = parentfcb;
										editpar = "c";
										editFileTextArea.setText(editContent);
										submitButton.setEnabled(false);
										editFileTextArea.setEditable(false);
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "打开成功";
									}else{
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "打开失败";
									}
								}
							}
							
							
							
							if("d".equals(sPath[1]))
							{
								if(sPath.length == 3)
								{
									FCB fcb = fileManagerD.openFileByPath(pathname);
									if(fcb != null)
									{
										byte [] b = fileManagerD.readFile(fcb);
										if(b != null)
											editContent = new String(b);
										editFileFCB = fcb;
										editParentFCB = null;
										editpar = "d";
										editFileTextArea.setText(editContent);
										submitButton.setEnabled(false);
										editFileTextArea.setEditable(false);
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "打开成功";
									}else{
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "打开失败";
									}
								}
								
								if(sPath.length > 3)
								{
									String path = "/";
									for(int i=2;i<sPath.length-1;i++)
										path =path+sPath[i] + "/";
									FCB parentfcb = fileManagerD.openDirByPath(path);
									FCB fcb = fileManagerD.openFileByPath(pathname);
									if(parentfcb != null && fcb != null)
									{
										byte [] b = fileManagerD.readFile(fcb);
										if(b != null)
											editContent = new String(b);
										editFileFCB = fcb;
										editParentFCB = parentfcb;
										editpar = "d";
										editFileTextArea.setText(editContent);
										submitButton.setEnabled(false);
										editFileTextArea.setEditable(false);
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "打开成功";
									}else{
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "打开失败";
									}
								}
							}
							
							
							
						}
						break;
					}
					
						
					case "edit":{
						
						if(commandStrs.length == 2)			//commandStrs 按空格分割命令 若命令后不为空
						{	
							String[] sPath = commandStrs[1].split("/");					//取得命令的后部 并用 / 分割
							if(!sPath[0].isEmpty())										//如果后部命令以 / 开头 说明使用绝对路径名
								sPath = (currentPath + commandStrs[1]).split("/");
							
							String pathname = "/";
							
							for(int i=2;i<sPath.length;i++)
								pathname = pathname + sPath[i] + "/";
							
							
							if("c".equals(sPath[1]))
							{
								if(sPath.length == 3)
								{
									FCB fcb = fileManagerC.openFileByPath(pathname);
									if(fcb != null)
									{
										byte [] b = fileManagerC.readFile(fcb);
										if(b != null)
											editContent = new String(b);
										editFileFCB = fcb;
										editParentFCB = null;
										editpar = "c";
										editFileTextArea.setText(editContent);
										if(fcb.isReadOnly())
											submitButton.setEnabled(false);
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "打开成功";
									}else{
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "打开失败";
									}
								}
								
								if(sPath.length > 3)
								{
									String path = "/";
									for(int i=2;i<sPath.length-1;i++)
										path =path+sPath[i] + "/";
									FCB parentfcb = fileManagerC.openDirByPath(path);
									FCB fcb = fileManagerC.openFileByPath(pathname);
									if(parentfcb != null && fcb != null)
									{
										byte [] b = fileManagerC.readFile(fcb);
										if(b != null)
											editContent = new String(b);
										editFileFCB = fcb;
										editParentFCB = parentfcb;
										editpar = "c";
										editFileTextArea.setText(editContent);
										if(fcb.isReadOnly())
											submitButton.setEnabled(false);
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "打开成功";
									}else{
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "打开失败";
									}
								}
							}
							
							
							
							if("d".equals(sPath[1]))
							{
								if(sPath.length == 3)
								{
									FCB fcb = fileManagerD.openFileByPath(pathname);
									if(fcb != null)
									{
										byte [] b = fileManagerD.readFile(fcb);
										if(b != null)
											editContent = new String(b);
										editFileFCB = fcb;
										editParentFCB = null;
										editpar = "d";
										editFileTextArea.setText(editContent);
										if(fcb.isReadOnly())
											submitButton.setEnabled(false);
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "打开成功";
									}else{
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "打开失败";
									}
								}
								
								if(sPath.length > 3)
								{
									String path = "/";
									for(int i=2;i<sPath.length-1;i++)
										path =path+sPath[i] + "/";
									FCB parentfcb = fileManagerD.openDirByPath(path);
									FCB fcb = fileManagerD.openFileByPath(pathname);
									if(parentfcb != null && fcb != null)
									{
										byte [] b = fileManagerD.readFile(fcb);
										if(b != null)
											editContent = new String(b);
										editFileFCB = fcb;
										editParentFCB = parentfcb;
										editpar = "d";
										editFileTextArea.setText(editContent);
										if(fcb.isReadOnly())
											submitButton.setEnabled(false);
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "打开成功";
									}else{
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "打开失败";
									}
								}
							}
						}
						
						break;
					}
						
					case "change":{
						
						if(commandStrs.length == 3)			//commandStrs 按空格分割命令 若命令后不为空
						{	
							String[] sPath = commandStrs[1].split("/");					//取得命令的后部 并用 / 分割
							if(!sPath[0].isEmpty())										//如果后部命令以 / 开头 说明使用绝对路径名
								sPath = (currentPath + commandStrs[1]).split("/");
							
							String pathname = "/";
							
							for(int i=2;i<sPath.length;i++)
								pathname = pathname + sPath[i] + "/";
							byte attribute = -1;
							if(commandStrs[2].length() == 1)
								attribute =Byte.parseByte(commandStrs[2]);
							
							if("c".equals(sPath[1]))
							{
								if(sPath.length == 3)
								{
									FCB fcb = fileManagerC.openFileByPath(pathname);
									if(fcb != null && (attribute == 0 || attribute ==1 || attribute ==2 || attribute ==3 || attribute ==4 || attribute ==5 || attribute ==6 || attribute ==7) && fileManagerC.changeFileAttribute(null, fcb, attribute) )
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "修改成功";
									else
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "修改失败";
								}
								
								if(sPath.length > 3)
								{
									String path = "/";
									for(int i=2;i<sPath.length-1;i++)
										path =path+sPath[i] + "/";
									FCB parentfcb = fileManagerC.openDirByPath(path);
									FCB fcb = fileManagerC.openFileByPath(pathname);
									
									if(fcb != null && (attribute == 0 || attribute ==1 || attribute ==2 || attribute ==3 || attribute ==4 || attribute ==5 || attribute ==6 || attribute ==7) && fileManagerC.changeFileAttribute(parentfcb, fcb, attribute) )
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "修改成功";
									else
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "修改失败";
								}
							}
						
							
							if("d".equals(sPath[1]))
							{
								if(sPath.length == 3)
								{
									FCB fcb = fileManagerD.openFileByPath(pathname);
									if(fcb != null && (attribute == 0 || attribute ==1 || attribute ==2 || attribute ==3 || attribute ==4 || attribute ==5 || attribute ==6 || attribute ==7) && fileManagerD.changeFileAttribute(null, fcb, attribute) )
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "修改成功";
									else
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "修改失败";
								}
								
								if(sPath.length > 3)
								{
									String path = "/";
									for(int i=2;i<sPath.length-1;i++)
										path =path+sPath[i] + "/";
									FCB parentfcb = fileManagerD.openDirByPath(path);
									FCB fcb = fileManagerD.openFileByPath(pathname);
									
									if(fcb != null && (attribute == 0 || attribute ==1 || attribute ==2 || attribute ==3 || attribute ==4 || attribute ==5 || attribute ==6 || attribute ==7) && fileManagerD.changeFileAttribute(parentfcb, fcb, attribute) )
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "修改成功";
									else
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "修改失败";
								}
							}
							
						
								
						}
						break;
					}
					case "format":{
						if(commandStrs.length == 2)
						{
							if("c".equals(commandStrs[1]))
							{
								diskManagerC.format();
								
								ctaStr = ctaStr + '\n'+ "#System#: c盘格式化成功";
							}
							
							if("d".equals(commandStrs[1]))
							{
								diskManagerD.format();
								
								ctaStr = ctaStr + '\n'+ "#System#: d盘格式化成功";
							}
						}
						
						
						break;
					}
					case "makdir":{
						if(commandStrs.length == 2)										//如果命令后不为空
						{
							String[] sPath = commandStrs[1].split("/");					//取得命令的后部 并用 / 分割
							if(!sPath[0].isEmpty())										//如果后部命令以 / 开头 说明使用绝对路径名													
								sPath = (currentPath + commandStrs[1]).split("/");
							if("c".equals(sPath[1]))
							{
								if(sPath.length == 3 && !sPath[sPath.length-1].contains("."))
								{
									String name =sPath[sPath.length-1];
									
									if(fileManagerC.createDir(null, null,name.getBytes(), (byte)1) != null)
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "创建成功";
									else
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "创建失败";
									
								}
								if(sPath.length == 4 && !sPath[sPath.length-1].contains("."))
								{
									String name =sPath[sPath.length-1];
									
									FCB parent = fileManagerC.openDirByPath("/"+sPath[2]);
									
									if( parent!=null && fileManagerC.createDir(null, parent,name.getBytes(), (byte)1) != null)
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "创建成功";
									else
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "创建失败";
									
									
								}
								if(sPath.length >4 && !sPath[sPath.length-1].contains("."))
								{
									String name =sPath[sPath.length-1];
									
									String path1 = "/";
									for(int i=2;i<sPath.length-2;i++)
										path1 =path1+sPath[i] + "/";
									String path2 = path1 + sPath[sPath.length-2] + "/";

									FCB parent = fileManagerC.openDirByPath(path2);

									FCB grander = fileManagerC.openDirByPath(path1);



									if( grander!=null && parent!=null && fileManagerC.createDir(grander, parent,name.getBytes(), (byte)1) != null)
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "创建成功";
									else
										ctaStr = ctaStr + '\n'+ "#System:# " + sPath[sPath.length-1] + "创建失败";
								}
							}
							
							if("d".equals(sPath[1]))
							{
								if(sPath.length == 3 && !sPath[sPath.length-1].contains("."))
								{
									String name =sPath[sPath.length-1];
									
									if(fileManagerD.createDir(null, null,name.getBytes(), (byte)1) != null)
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "创建成功";
									else
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "创建失败";
									
								}
								if(sPath.length == 4 && !sPath[sPath.length-1].contains("."))
								{
									String name =sPath[sPath.length-1];
									
									FCB parent = fileManagerD.openDirByPath("/"+sPath[2]);
									
									if( parent!=null && fileManagerD.createDir(null, parent,name.getBytes(), (byte)1) != null)
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "创建成功";
									else
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "创建失败";
									
									
								}
								if(sPath.length >4 && !sPath[sPath.length-1].contains("."))
								{
									String name =sPath[sPath.length-1];
									
									String path1 = "/";
									for(int i=2;i<sPath.length-2;i++)
										path1 =path1+sPath[i] + "/";
									String path2 = path1 + sPath[sPath.length-2] + "/";

									FCB parent = fileManagerD.openDirByPath(path2);

									FCB grander = fileManagerD.openDirByPath(path1);



									if( grander!=null && parent!=null && fileManagerD.createDir(grander, parent,name.getBytes(), (byte)1) != null)
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "创建成功";
									else
										ctaStr = ctaStr + '\n'+ "#System:# " + sPath[sPath.length-1] + "创建失败";
								}
							}
								
						}
						break;
					}
					case "chadir":{
						if(commandStrs.length == 2)										//如果命令后不为空
						{
							
							String[] sPath = commandStrs[1].split("/");					//取得命令的后部 并用 / 分割
							if(!sPath[0].isEmpty())										//如果后部命令以 / 开头 说明使用绝对路径名													
								{
									sPath = (currentPath + commandStrs[1]).split("/");
									commandStrs[1] = currentPath + commandStrs[1];
								}
								
							if(sPath.length==2 && "c".equals(sPath[1]))
							{
								if(!commandStrs[1].endsWith("/"))
									commandStrs[1] = commandStrs[1]+"/";
								ctaStr = ctaStr + '\n'+ "#System#: 工作目录变更为" + commandStrs[1] ;
								currentPath = commandStrs[1] ;
								commandTextField.setText(currentPath);
							}
							
							if(sPath.length!=2 && "c".equals(sPath[1]))
							{
								String pathname = "/";
								for(int i=2;i<sPath.length;i++)
									pathname = pathname + sPath[i] + "/";

								FCB fcb = fileManagerC.openDirByPath(pathname);
								if(fcb != null)
									{
										if(!commandStrs[1].endsWith("/"))
											commandStrs[1] = commandStrs[1]+"/";
										ctaStr = ctaStr + '\n'+ "#System#: 工作目录变更为" + commandStrs[1] ;
										currentPath = commandStrs[1] ;
										commandTextField.setText(currentPath);
									}
								else{
									ctaStr = ctaStr + '\n'+ "#System#: 工作目录变更" + commandStrs[1] + "失败" ;
								}
							}
							
							if(sPath.length==2 && "d".equals(sPath[1]))
							{
								if(!commandStrs[1].endsWith("/"))
									commandStrs[1] = commandStrs[1]+"/";
								ctaStr = ctaStr + '\n'+ "#System#: 工作目录变更为" + commandStrs[1] ;
								currentPath = commandStrs[1] ;
								commandTextField.setText(currentPath);
							}
							
							if(sPath.length!=2 && "d".equals(sPath[1]))
							{
								String pathname = "/";
								for(int i=2;i<sPath.length;i++)
									pathname = pathname + sPath[i] + "/";
								
								FCB fcb = fileManagerD.openDirByPath(pathname);
								if(fcb != null)
									{
										if(!commandStrs[1].endsWith("/"))
											commandStrs[1] = commandStrs[1]+"/";
										ctaStr = ctaStr + '\n'+ "#System#: 工作目录变更为" + commandStrs[1] ;
										currentPath = commandStrs[1] ;
										commandTextField.setText(currentPath);
									}
								else{
									ctaStr = ctaStr + '\n'+ "#System#: 工作目录变更" + commandStrs[1] + "失败" ;
								}
							}
							
							}
						break;
					}
					case "rdir":{
					
						if(commandStrs.length == 2)										//如果命令后不为空
						{
							
							String[] sPath = commandStrs[1].split("/");					//取得命令的后部 并用 / 分割
							if(!sPath[0].isEmpty())										//如果后部命令以 / 开头 说明使用绝对路径名													
								{
									sPath = (currentPath + commandStrs[1]).split("/");
									commandStrs[1] = currentPath + commandStrs[1];
								}
							
							if("c".equals(sPath[1]))
							{
								String pathname = "/";
								
								for(int i=2;i<sPath.length;i++)
									pathname = pathname + sPath[i] + "/";

								if(sPath.length == 3)
								{
									FCB fcb = fileManagerC.openDirByPath(pathname);
									if(fcb != null && fcb.isDir())
									{
										fileManagerC.delFile(null, null, fcb);
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除成功";
									}else{
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除失败";
									}
								}
								
								if(sPath.length == 4)
								{
									FCB fcb = fileManagerC.openDirByPath(pathname);
									FCB parent = fileManagerC.openDirByPath("/"+sPath[2]);
									System.out.println(pathname);
									System.out.println("/"+sPath[2]);
									if(fcb!=null && fcb.isDir() && parent !=null)
									{
										
										fileManagerC.delFile(null, parent, fcb);
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除成功";
									}else{
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除失败";
									}
								}
								
								if(sPath.length > 4)
								{
									FCB fcb = fileManagerC.openFileByPath(pathname);
									String path1 = "/";
									for(int i=2;i<sPath.length-2;i++)
										path1 =path1+sPath[i] + "/";
									String path2 = path1 + sPath[sPath.length-2];
									System.out.println(path2);
									System.out.println(path1);
									FCB parent = fileManagerC.openDirByPath(path2);
									FCB grander = fileManagerC.openDirByPath(path1);
									if(grander!=null && parent!=null && fcb!=null && fcb.isDir())
									{
										fileManagerC.delFile(grander, parent, fcb);
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除成功";
									}else{
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除失败";
									}
								}
								
							}

							
							if("d".equals(sPath[1]))
							{
								String pathname = "/";
								
								for(int i=2;i<sPath.length;i++)
									pathname = pathname + sPath[i] + "/";

								if(sPath.length == 3)
								{
									FCB fcb = fileManagerD.openDirByPath(pathname);
									if(fcb != null && fcb.isDir())
									{
										fileManagerD.delFile(null, null, fcb);
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除成功";
									}else{
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除失败";
									}
								}
								
								if(sPath.length == 4)
								{
									FCB fcb = fileManagerD.openDirByPath(pathname);
									FCB parent = fileManagerD.openDirByPath("/"+sPath[2]);
									if(fcb!=null && fcb.isDir() && parent !=null)
									{
										fileManagerD.delFile(null, parent, fcb);
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除成功";
									}else{
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除失败";
									}
								}
								
								if(sPath.length > 4)
								{
									FCB fcb = fileManagerD.openDirByPath(pathname);
									String path1 = "/";
									for(int i=2;i<sPath.length-2;i++)
										path1 =path1+sPath[i] + "/";
									String path2 = path1 + sPath[sPath.length-2];
									FCB parent = fileManagerD.openDirByPath(path2);
									FCB grander = fileManagerD.openDirByPath(path1);
									if(grander!=null && parent!=null && fcb!=null && fcb.isDir())
									{
										fileManagerD.delFile(grander, parent, fcb);
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除成功";
									}else{
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除失败";
									}
								}
								
							}
							
						}
						break;
					}
					case "deldir":{
						
						
						if(commandStrs.length == 2)										//如果命令后不为空
						{
							
							String[] sPath = commandStrs[1].split("/");					//取得命令的后部 并用 / 分割
							if(!sPath[0].isEmpty())										//如果后部命令以 / 开头 说明使用绝对路径名													
								{
									sPath = (currentPath + commandStrs[1]).split("/");
									commandStrs[1] = currentPath + commandStrs[1];
								}
							
							if("c".equals(sPath[1]))
							{
								String pathname = "/";
								
								for(int i=2;i<sPath.length;i++)
									pathname = pathname + sPath[i] + "/";

								if(sPath.length == 3)
								{
									FCB fcb = fileManagerC.openDirByPath(pathname);
									if(fcb != null && fcb.isDir())
									{
										fileManagerC.delFileR(null, null, fcb);
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除成功";
									}else{
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除失败";
									}
								}
								
								if(sPath.length == 4)
								{
									FCB fcb = fileManagerC.openDirByPath(pathname);
									FCB parent = fileManagerC.openDirByPath("/"+sPath[2]);
									if(fcb!=null && fcb.isDir() && parent !=null)
									{
										fileManagerC.delFileR(null, parent, fcb);
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除成功";
									}else{
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除失败";
									}
								}
								
								if(sPath.length > 4)
								{
									FCB fcb = fileManagerC.openDirByPath(pathname);
									String path1 = "/";
									for(int i=2;i<sPath.length-2;i++)
										path1 =path1+sPath[i] + "/";
									String path2 = path1 + sPath[sPath.length-2];
									FCB parent = fileManagerC.openDirByPath(path2);
									FCB grander = fileManagerC.openDirByPath(path1);
									if(grander!=null && parent!=null && fcb!=null && fcb.isDir())
									{
										fileManagerC.delFileR(grander, parent, fcb);
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除成功";
									}else{
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除失败";
									}
								}
								
							}

							
							if("d".equals(sPath[1]))
							{
								String pathname = "/";
								
								for(int i=2;i<sPath.length;i++)
									pathname = pathname + sPath[i] + "/";

								if(sPath.length == 3)
								{
									FCB fcb = fileManagerD.openDirByPath(pathname);
									if(fcb != null && fcb.isDir())
									{
										fileManagerD.delFileR(null, null, fcb);
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除成功";
									}else{
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除失败";
									}
								}
								
								if(sPath.length == 4)
								{
									FCB fcb = fileManagerD.openDirByPath(pathname);
									FCB parent = fileManagerD.openDirByPath("/"+sPath[2]);
									if(fcb!=null && fcb.isDir() && parent !=null)
									{
										fileManagerD.delFileR(null, parent, fcb);
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除成功";
									}else{
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除失败";
									}
								}
								
								if(sPath.length > 4)
								{
									FCB fcb = fileManagerD.openDirByPath(pathname);
									String path1 = "/";
									for(int i=2;i<sPath.length-2;i++)
										path1 =path1+sPath[i] + "/";
									String path2 = path1 + sPath[sPath.length-2];
									FCB parent = fileManagerD.openDirByPath(path2);
									FCB grander = fileManagerD.openDirByPath(path1);
									if(grander!=null && parent!=null && fcb!=null && fcb.isDir())
									{
										fileManagerD.delFileR(grander, parent, fcb);
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除成功";
									}else{
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "删除失败";
									}
								}
								
							}
							
						}
						break;
					}
					
					case "run":{
						if(commandStrs.length == 2)			//commandStrs 按空格分割命令 若命令后不为空
						{	
							String[] sPath = commandStrs[1].split("/");					//取得命令的后部 并用 / 分割
							if(!sPath[0].isEmpty())										//如果后部命令以 / 开头 说明使用绝对路径名
								sPath = (currentPath + commandStrs[1]).split("/");
							
							String pathname = "/";
							
							for(int i=2;i<sPath.length;i++)
								pathname = pathname + sPath[i] + "/";
							
							
							if("c".equals(sPath[1]))
							{
								if(sPath.length == 3)
								{
									FCB fcb = fileManagerC.openFileByPath(pathname);
									if(fcb != null && fcb.getExtend()[0]=='e' && fcb.getExtend()[1]=='x' && fcb.getExtend()[2]=='e')
									{
										fileManagerC.createProcess(pathname);
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "开始执行";
									}else{
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "打开失败";
									}
								}
								
								if(sPath.length > 3)
								{
									String path = "/";
									for(int i=2;i<sPath.length-1;i++)
										path =path+sPath[i] + "/";
									FCB parentfcb = fileManagerC.openDirByPath(path);
									FCB fcb = fileManagerC.openFileByPath(pathname);
									if(parentfcb != null && fcb != null && fcb.getExtend()[0]=='e' && fcb.getExtend()[1]=='x' && fcb.getExtend()[2]=='e')
									{
										fileManagerC.createProcess(pathname);
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "打开成功";
									}else{
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "打开失败";
									}
								}
							}
							
							if("d".equals(sPath[1]))
							{
								if(sPath.length == 3)
								{
									FCB fcb = fileManagerD.openFileByPath(pathname);
									if(fcb != null && fcb.getExtend()[0]=='e' && fcb.getExtend()[1]=='x' && fcb.getExtend()[2]=='e')
									{
										fileManagerD.createProcess(pathname);
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "开始执行";
									}else{
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "打开失败";
									}
								}
								
								if(sPath.length > 3)
								{
									String path = "/";
									for(int i=2;i<sPath.length-1;i++)
										path =path+sPath[i] + "/";
									FCB parentfcb = fileManagerD.openDirByPath(path);
									FCB fcb = fileManagerD.openFileByPath(pathname);
									if(parentfcb != null && fcb != null && fcb.getExtend()[0]=='e' && fcb.getExtend()[1]=='x' && fcb.getExtend()[2]=='e')
									{
										fileManagerD.createProcess(pathname);
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "打开成功";
									}else{
										ctaStr = ctaStr + '\n'+ "#System#: " + sPath[sPath.length-1] + "打开失败";
									}
								}
							}
						}
						
						break;
					}
					
						
					default:
						break;
					}
				}
				
				
				
				commandTextField.setText(currentPath);
				if(!ctaStr.isEmpty())
					ctaStr = ctaStr+'\n'+ctfStr;
				else
					ctaStr = ctfStr;
				commandTextArea.setText(ctaStr);
				
				

				DefaultTreeModel model  =  (DefaultTreeModel) fileTree.getModel();
				rootNode = new DefaultMutableTreeNode("disk");
				fileManagerC.buildTree(null, rootNode);
				fileManagerD.buildTree(null, rootNode);
				model.setRoot(rootNode);	

				
				
				if(commandStrs != null)
				{
					String[] f = commandStrs[0].split("/");
					
					if(f!=null && commandStrs.length ==2)
					{
						commandHistory.add(f[f.length-1] + " " + commandStrs[1]);
						commandHistoryPos =  commandHistory.size() - 1;
					}
					
					if(f!=null && commandStrs.length == 3)
					{
						commandHistory.add(f[f.length-1] + " " + commandStrs[1] + " " +commandStrs[2]);
						commandHistoryPos =  commandHistory.size() - 1;
					}
				}
				
				
			}
		});
		commandTextField.setBounds(15, 115, 480, 20);
		commandTextArea = new JTextArea();
		commandTextArea.setEditable(false);
		scrollPaneCommand = new JScrollPane(commandTextArea);
		scrollPaneCommand.setBounds(15, 20, 480, 90);
		border = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
		border = BorderFactory.createTitledBorder(border, "命令");
		commandPanel.add(commandTextField);
		commandPanel.add(scrollPaneCommand);
		commandPanel.setBorder(border);
		commandPanel.setBounds(270, 225, 510, 145);
		
		
		
		
		
		editFilePanel = new JPanel();
		editFilePanel.setLayout(null);
		cancleButton =new JButton("取消");
		
		cancleButton.setBounds(400, 172, 60, 20);
		submitButton =new JButton("保存");
		
		submitButton.setBounds(320, 172, 60, 20);
		editFileTextArea = new JTextArea();
		scrollPaneEditFile = new JScrollPane(editFileTextArea);
		scrollPaneEditFile.setBounds(15, 20, 480, 145);
		border = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
		border = BorderFactory.createTitledBorder(border, "编辑文件");
		editFilePanel.add(submitButton);
		editFilePanel.add(cancleButton);
		editFilePanel.add(scrollPaneEditFile);
		editFilePanel.setBorder(border);
		editFilePanel.setBounds(270, 20, 510, 200);
		cancleButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				editContent = null;
				editFileFCB = null;
				editParentFCB = null;
				editFileTextArea.setText(null);
				submitButton.setEnabled(true);
				editFileTextArea.setEditable(true);
				ctaStr = ctaStr + '\n'+ "#System#: 取消成功";
				commandTextArea.setText(ctaStr);
			}
		});
		
		submitButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				editContent = editFileTextArea.getText();
				boolean flag = false;
				if("c".equals(editpar))
					flag = fileManagerC.reSaveFile(editParentFCB, editFileFCB, editContent.getBytes());
				if("d".equals(editpar))
					flag = fileManagerD.reSaveFile(editParentFCB, editFileFCB, editContent.getBytes());
				editContent = null;
				editFileFCB = null;
				editParentFCB = null;
				editFileTextArea.setText(null);
				editpar = null;
				if(flag)
					ctaStr = ctaStr + '\n'+ "#System#: 保存成功";
				else
					ctaStr = ctaStr + '\n'+ "#System#: 保存失败";
				commandTextArea.setText(ctaStr);
			}
		});
		
		
		
		filePanel.setLayout(null);
		border = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
		border = BorderFactory.createTitledBorder(border, "磁盘-文件系统");
		filePanel.setBorder(border);
		filePanel.setBounds(10, 30, 775, 532);
		filePanel.add(fileTreePanel);
		filePanel.add(commandPanel);
		filePanel.add(diskStatusPanelC);
		filePanel.add(diskStatusPanelD);
		filePanel.add(stateTextLabel);
		filePanel.add(editFilePanel);

		
//		tabbelPane.setSize(795, 670);
//		tabbelPane.add("文件系统",filePanel);
		
//		
//		osFrame = new JFrame();
//		osFrame.add(tabbelPane);
//		osFrame.setResizable(false);
//		osFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		osFrame.setTitle("操作系统");
//		osFrame.setSize(800, 700);
//		osFrame.setLayout(null);
//		osFrame.setLocationRelativeTo(null);	    	  
//		osFrame.setVisible(true);
		
		close();
	}
	
	public void close()
	{
		fileTree.setVisible(false);
		diskStatusPanelC.setVisible(false);
		diskStatusPanelD.setVisible(false);
		commandTextField.setEditable(false);
		submitButton.setEnabled(false);
		cancleButton.setEnabled(false);
		editFileTextArea.setEditable(false);
	}
	
	public void open()
	{
		fileTree.setVisible(true);
		diskStatusPanelC.setVisible(true);
		diskStatusPanelD.setVisible(true);
		commandTextField.setEditable(true);
		submitButton.setEnabled(true);
		cancleButton.setEnabled(true);
		editFileTextArea.setEditable(true);
	}
	
 	public OsDiskManager getDiskManagerC() {
		return diskManagerC;
	}

	public void setDiskManagerC(OsDiskManager diskManagerC) {
		this.diskManagerC = diskManagerC;
	}

	public OsDiskManager getDiskManagerD() {
		return diskManagerD;
	}

	public void setDiskManagerD(OsDiskManager diskManagerD) {
		this.diskManagerD = diskManagerD;
	}

	public OsFileManager getFileManagerC() {
		return fileManagerC;
	}

	public void setFileManagerC(OsFileManager fileManagerC) {
		this.fileManagerC = fileManagerC;
	}

	public OsFileManager getFileManagerD() {
		return fileManagerD;
	}

	public void setFileManagerD(OsFileManager fileManagerD) {
		this.fileManagerD = fileManagerD;
	}

	public DefaultMutableTreeNode getRootNode() {
		return rootNode;
	}

	public void setRootNode(DefaultMutableTreeNode rootNode) {
		this.rootNode = rootNode;
	}
	
}



