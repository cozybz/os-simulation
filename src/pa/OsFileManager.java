package pa;

import java.util.ArrayList;

import javax.swing.tree.DefaultMutableTreeNode;

public class OsFileManager {
	
	private OsDiskManager diskManager = null;
	public OsFileManager(OsDiskManager d)
	{
		diskManager = d;
	}
	
	public void createProcess(String path)
	{
		String[] strs = path.split("/");
		String name = strs[strs.length-1];
		FCB fcb = openFileByPath(path);
		byte[][] order =new byte[12][4]; 
		byte[] data = readFile(fcb);
		for(int i=0;i<12;i++)
			for(int j=0;j<4;j++)
				order[i][j] = data[i*4+j];
		
		OsCourse.create(name, order, path);
		//调用进程   order ->12条命令 || name ->文件名(进程名)  || path->路径
	}
	
	

	
	
	public  byte[] readFile(FCB file)
	{
		
		int directBlockNum = file.getDirectAddress();
		int indexBlockNum = file.getOneIndexAddress();
//System.out.println("@@directBlockNum "+directBlockNum);
//System.out.println("@@indexBlockNum "+indexBlockNum);
		int length = file.getLength();
		if(directBlockNum == 0)
			return null;
		
		byte[] data = new byte[length];
//System.out.println("@@length "+length);

		if(indexBlockNum == 0)
			diskManager.readBytesFromDisk(data, directBlockNum*128);
		
		if(indexBlockNum != 0)
		{
			byte[] directData = new byte[128];
			diskManager.readBytesFromDisk(directData, directBlockNum*128);
//System.out.println("@@directData "+new String(directData));
			byte indexs[] = read32FCBIndexsFromABlock(indexBlockNum);
			int v = 0;
			while(v<32 && indexs[v] !=0)
				v++;
			byte[][] indexsData = new byte[v][128];
			
			for(int i=0;i<v;i++)
				diskManager.readBytesFromDisk(indexsData[i], indexs[i]*128);
			
			for(int i=0;i<128;i++)
				data[i] = directData[i];
			
			for(int i=0;i<v-1;i++)
			{
				for(int j=0;j<128;j++)
					data[i*128+128+j] = indexsData[i][j];
			}
			for(int i=0;i<length%128;i++)
				data[128+(v-1)*128+i] = indexsData[v-1][i];	
		}
		return data;
	}
	
	public FCB openFileByPath(String path)
	{
		String[] strs = path.split("/");
		if(!strs[0].isEmpty())
			System.out.println("openFileByPath函数只支持绝对路径");
		else{
				FCB parent = null;
				for(int i=1;i<strs.length-1;i++)
					parent = SearchDirectFileByName(parent, strs[i]);
				return SearchDirectFileByName(parent, strs[strs.length-1]);
		}

		return null;
	}
	
	public FCB openDirByPath(String path)
	{	
		String[] strs = path.split("/");
		if(!strs[0].isEmpty())
			System.out.println("openDirByPath函数只支持绝对路径");
		else if(strs.length == 1){
			System.out.println("输入错误");
		}else{
				FCB parent = null;
				for(int i=1;i<strs.length-1;i++)
					{
						parent = SearchDirectFileByName(parent, strs[i]);
					}
				return SearchDirectFileByName(parent, strs[strs.length-1]);
		}

		return null;
	}
	
	public FCB SearchDirectFileByName(FCB parent,String name)
	{
		String[] strs = name.split("\\.");
		
		int length = strs.length;
		
		if(length == 1)
		{
			ArrayList<FCB> fcbs = getDirectSons(parent);
			if(fcbs == null || fcbs.size() == 0)
				return null;
			if(fcbs.size()>0)
				for(int i=0;i<fcbs.size();i++)
				{
					if(util.Tools.isSame6Bytes(fcbs.get(i).getName(), name.getBytes()) && fcbs.get(i).isDir())
						return fcbs.get(i);
				}
		}
		
		if(length == 2)
		{
			ArrayList<FCB> fcbs = getDirectSons(parent);
			
			if(fcbs == null || fcbs.size() == 0)
				return null;
			if(fcbs.size()>0)
				for(int i=0;i<fcbs.size();i++)
				{
					if(util.Tools.isSame6Bytes(fcbs.get(i).getName(), strs[0].getBytes()) && util.Tools.isSame3Bytes(fcbs.get(i).getExtend(), strs[1].getBytes()) && fcbs.get(i).isFile())
						return fcbs.get(i);
				}
		}
		
		return null;
	}
	
	public void buildTree(FCB fcb,DefaultMutableTreeNode treenode)
	{
		if(fcb == null)													//根目录
		{
			ArrayList<FCB> list = getDirectSons();						//获得根目录目录项
			if(list.size()>0)											//如果目录项非空
			{
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(diskManager.FILEPATH,true);
				
				treenode.add(node);
				
				for(int i=0;i<list.size();i++)
				{
					buildTree(list.get(i), node);
				}
				
			}else{														//如果目录项空
				
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(diskManager.FILEPATH,true);
				
				treenode.add(node);
			
			}
		}
		
		if(fcb != null)
		{
			if(fcb.isDir())
			{
				ArrayList<FCB> list = getDirectSons(fcb);
				
				if(list.size()>0)											//如果目录项非空
				{
					DefaultMutableTreeNode node = new DefaultMutableTreeNode(fcb.getStrFullName(),true);
					
					treenode.add(node);
					
					for(int i=0;i<list.size();i++)
					{
						buildTree(list.get(i), node);
					}
					
				}else{														//如果目录项空
					
					DefaultMutableTreeNode node = new DefaultMutableTreeNode(fcb.getStrFullName(),true);
					
					treenode.add(node);
				
				}
				
			}
			
			if(fcb.isFile())
			{
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(fcb.getStrFullName(),false);
				treenode.add(node);
			
			}
		}
		
	}
	
	public void delFileR(FCB grander,FCB parent,FCB fcb)
	{
		if(fcb == null)
			return;
		
		if(fcb.isFile())
		{
			delFile(grander, parent, fcb);
		}
		
		if(fcb.isDir())
		{
			ArrayList<FCB> list = getDirectSons(fcb);
			
			if(list.size()>0)
			{
				for(int i=0;i<list.size();i++)
				{
					delFile(parent, fcb, list.get(i));
				}
			}
			
			delFile(grander, parent, fcb);
		}

	}
	
	public void delFile(FCB grander,FCB parent,FCB fcb)
	{
		if(fcb == null)
			return;
		
		
		if(parent == null)
		{
			int FCBPos = getAFCBPositonFromParent(parent, fcb);
			byte b ='#';
			
			if(fcb.isDir())
			{
				diskManager.writeByteToDisk(b, FCBPos);
			}
			
			if(fcb.isFile())
			{
				reSaveFile(null, fcb, null);
				diskManager.writeByteToDisk(b, FCBPos);
			}
			
			return ;
		}
		
		if(fcb.isDir())
		{
			int FCBPos = getAFCBPositonFromParent(parent, fcb);
			int parentPos = getAFCBPositonFromParent(grander,parent);
			
			byte b = '#';
			diskManager.writeByteToDisk(b, FCBPos);
			int indexBlockNum = parent.getOneIndexAddress();
			int directBlockNum = parent.getDirectAddress();
			
			if(indexBlockNum == 0)														//在直接地址中
			{
				if(isFCBBlockNull(directBlockNum))
				{
					diskManager.freeABlock(directBlockNum);
					parent.setDirectAddress(0);
				}
			}
			
			if(indexBlockNum != 0)
			{
				byte[] indexs = read32FCBIndexsFromABlock(indexBlockNum);
				int blockNum = (FCBPos + 127)/128;
				if(isFCBBlockNull(blockNum))
				{
					for(int i=0;i<32;i++)
					{
						if(indexs[i] == blockNum)
						{
							diskManager.freeABlock(blockNum);
							indexs[i] = 0;
						}
					}
				}
				int v=0;
				while(v<32 && indexs[v] == 0)
					v++;
				if(v == 32)
				{
					diskManager.freeABlock(parent.getOneIndexAddress());
					parent.setOneIndexAddress(0);
				}
			}
			
			diskManager.writeBytesToDisk(parent.getBytes(),parentPos);
			
		}
		
		if(fcb.isFile())
		{
			reSaveFile(parent, fcb, null);
			int FCBPos = getAFCBPositonFromParent(parent, fcb);
			int parentPos = getAFCBPositonFromParent(grander,parent);
			
			byte b = '#';
			diskManager.writeByteToDisk(b, FCBPos);
			int indexBlockNum = parent.getOneIndexAddress();
			int directBlockNum = parent.getDirectAddress();
			
			if(indexBlockNum == 0)														//在直接地址中
			{
				if(isFCBBlockNull(directBlockNum))
				{
					diskManager.freeABlock(directBlockNum);
					parent.setDirectAddress(0);
				}
			}
			
			if(indexBlockNum != 0)
			{
				byte[] indexs = read32FCBIndexsFromABlock(indexBlockNum);
				int blockNum = (FCBPos + 127)/128;
				if(isFCBBlockNull(blockNum))
				{
					for(int i=0;i<32;i++)
					{
						if(indexs[i] == blockNum)
						{
							diskManager.freeABlock(blockNum);
							indexs[i] = 0;
						}
					}
				}
				int v=0;
				while(v<32 && indexs[v] == 0)
					v++;
				if(v == 32)
				{
					diskManager.freeABlock(parent.getOneIndexAddress());
					parent.setOneIndexAddress(0);
				}
			}
			
			diskManager.writeBytesToDisk(parent.getBytes(),parentPos);
			
		}
	}
	
	
	
	public boolean changeFileAttribute(FCB parent,FCB fcb,byte attribute)
	{
		if(fcb == null)
			return false;
		
		
		if(fcb.isFile())
		{
			int FCBPos = getAFCBPositonFromParent(parent, fcb);
			
			if(fcb.isFile())
			{
				fcb.setAttribute(attribute);
				diskManager.writeBytesToDisk(fcb.getBytes(), FCBPos);
				return true;
			}
		}
		
		return false;

	}
	
	
	
	public ArrayList<FCB> getDirectSons(FCB fcb)
	{
		if(fcb == null)
		{
			return getDirectSons();
		}
		if(fcb.isFile())
			return null;
		
		ArrayList<FCB> fcblist = new ArrayList<FCB>();
		
		int directBlockNum = fcb.getDirectAddress();
		int indexBlockNum = fcb.getOneIndexAddress();
		if(directBlockNum != 0)
		{
			FCB[] fcbs = read8FCBFromABlock(directBlockNum);
			for(int j=0;j<8;j++)
			{
				if(fcbs[j].getBytes()[0] !='#')
				{
					fcblist.add(fcbs[j]);
				}
			}
		}
		
		
		if(indexBlockNum != 0 )
		{
			byte[] indexs = read32FCBIndexsFromABlock(indexBlockNum);
			FCB[] fcbs = null;
			int v = 0;
			while(v<32 && indexs[v] != 0)
				v++;
			for(int i=0;i<v;i++)
			{
				fcbs = read8FCBFromABlock(indexs[i]);
				for(int j=0;j<8;j++)
				{
					if(fcbs[j].getBytes()[0] != '#')
					{
						fcblist.add(fcbs[j]);
					}
				}
			}
		}
		
		return fcblist;
	}
	
	public ArrayList<FCB> getDirectSons()
	{
		ArrayList<FCB> fcblist = new ArrayList<FCB>();
		FCB[] fcbs1 = read8FCBFromABlock(1);
		FCB[] fcbs2 = read8FCBFromABlock(2);

		for(int i=0;i<8;i++)
		{

			if(fcbs1[i].getBytes()[0] != '#')
			{
				fcblist.add(fcbs1[i]);
			}
			
			if(fcbs2[i].getBytes()[0] != '#')
			{
				fcblist.add(fcbs2[i]);
			}
		}

		return fcblist;
	}
	
	public boolean reSaveFile(FCB parent,FCB file,byte[] data)
	{
		
		boolean result = false;
		
		int FCBPos = getAFCBPositonFromParent(parent, file);
		int oldSize = castByteNumToBlockNum(file.getLength());
		int newSize = 0;
		if(data != null)
			newSize = castByteNumToBlockNum(data.length);
System.out.println("@@新文件大小 "+newSize);
		
		if(oldSize == 0 && newSize ==0)
		{
			
		}else if(newSize>33)
		{
			
		}else if(oldSize == 0 && newSize ==1)
		{
			System.out.println("@@oldSize == 0 && newSize ==1");
			int blockNum = diskManager.getABlock();
			if(blockNum != -1)
			{
				file.setDirectAddress(blockNum);
				file.setLength(data.length);
				diskManager.writeBytesToDisk(data, blockNum*128);
				
				diskManager.writeBytesToDisk(file.getBytes(), FCBPos);
				result = true;
			}
			
		}else if(oldSize == 0 && newSize >1)
		{
			System.out.println("@@oldSize == 0 && newSize >1");
			byte blockdata[][] = new byte[newSize][128];
			for(int i=0;i<newSize;i++)
			{
				for(int j=0;j<128;j++)
				{
					if((i*128+j)<data.length)
						blockdata[i][j] = data[i*128+j];
					else
						blockdata[i][j] = 0;
				}
			}
			
			int directBlockNum = diskManager.getABlock();
			
			if(directBlockNum != -1)
			{
				int indexNum = diskManager.getABlock();
				
				if(indexNum != -1)
				{
					int[] blockNum = new int[newSize-1];
					
					for(int i=0;i<newSize-1;i++)
						blockNum[i] = diskManager.getABlock();
					
					if(blockNum[newSize-2] != -1)
					{
						for(int i=0;i<newSize-1;i++)
							initFCBBlocks(blockNum[i]);
						
						initIndexsBlock(indexNum);
						
						byte indexs[] = new byte[32];
						
						for(int i=0;i<newSize-1;i++)
							indexs[i] = (byte)blockNum[i];
						
						file.setDirectAddress(directBlockNum);
						file.setOneIndexAddress(indexNum);
						file.setLength(data.length);
						for(int i=0;i<newSize-1;i++)
							diskManager.writeBytesToDisk(blockdata[i+1], blockNum[i]*128);
						
						diskManager.writeBytesToDisk(blockdata[0], directBlockNum*128);
						write32FCBIndexsIntoABlock(indexs, indexNum);
						diskManager.writeBytesToDisk(file.getBytes(), FCBPos);
						
						result = true;
						
					}else
					{
						diskManager.freeABlock(directBlockNum);
						diskManager.freeABlock(indexNum);
						for(int i=0;i<newSize-1;i++)
						{
							if(blockNum[i] != -1)
								diskManager.freeABlock(blockNum[i]);
						}
					}
				}
			}
			
		}else if(oldSize == 1 && newSize == 0)
		{
			System.out.println("@@oldSize == 1 && newSize == 0");
			int directBlockNum = file.getDirectAddress();
			diskManager.freeABlock(directBlockNum);
			file.setLength(0);
			file.setDirectAddress(0);
			diskManager.writeBytesToDisk(file.getBytes(), FCBPos);
			result = true;
		
		}else if(oldSize == 1 && newSize == 1)
		{
			System.out.println("@@oldSize == 1 && newSize == 1");
			byte[] directBlockData = new byte[128];
			for(int i=0;i<data.length;i++)
				directBlockData[i]=data[i];
			int directBlockNum = file.getDirectAddress();
			file.setLength(data.length);
			diskManager.writeBytesToDisk(file.getBytes(), FCBPos);
			diskManager.writeBytesToDisk(directBlockData, directBlockNum*128);
			result = true;
			
		}else if(oldSize == 1 && newSize > 1)
		{
			System.out.println("@@oldSize == 1 && newSize > 1");
			byte blockdata[][] = new byte[newSize][128];
			for(int i=0;i<newSize;i++)
			{
				for(int j=0;j<128;j++)
				{
					if((i*128+j)<data.length)
						blockdata[i][j] = data[i*128+j];
					else
						blockdata[i][j] = 0;
				}
			}
			
			int indexNum = diskManager.getABlock();
			if(indexNum != -1)
			{
				int[] blockNum = new int[newSize-1];
				for(int i=0;i<newSize-1;i++)
					blockNum[i] = diskManager.getABlock();
				
				if(blockNum[newSize-2] != -1)
				{
					for(int i=0;i<newSize-1;i++)
						initFCBBlocks(blockNum[i]);
					
					initIndexsBlock(indexNum);
					
					byte indexs[] = new byte[32];
					
					for(int i=0;i<newSize-1;i++)
						indexs[i] = (byte)blockNum[i];
					
					file.setOneIndexAddress(indexNum);
					file.setLength(data.length);
					
					for(int i=0;i<newSize-1;i++)
						diskManager.writeBytesToDisk(blockdata[i+1], blockNum[i]*128);
					
					diskManager.writeBytesToDisk(blockdata[0], file.getDirectAddress()*128);
					write32FCBIndexsIntoABlock(indexs, indexNum);
					diskManager.writeBytesToDisk(file.getBytes(), FCBPos);
					
					result = true;
					
				}else{
					diskManager.freeABlock(indexNum);
					for(int i=0;i<newSize-1;i++)
					{
						if(blockNum[i] != -1)
							diskManager.freeABlock(blockNum[i]);
					}
				}
			}
			
		}else if(oldSize > 1 && newSize == 0)
		{
			System.out.println("@@oldSize > 1 && newSize == 0");
			int directBlock = file.getDirectAddress();
			int indexBlock = file.getOneIndexAddress();
			byte[] indexs = read32FCBIndexsFromABlock(indexBlock);
			for(int i=0;i<32;i++)
			{
				if(indexs[i] != 0)
					diskManager.freeABlock(indexs[i]);
			}
			diskManager.freeABlock(directBlock);
			diskManager.freeABlock(indexBlock);
			
			file.setLength(0);
			file.setOneIndexAddress(0);
			file.setDirectAddress(0);
			
			diskManager.writeBytesToDisk(file.getBytes(),FCBPos);
			
			result = true;
			
			
		}else if(oldSize > 1 && newSize == 1)
		{
			System.out.println("@@oldSize > 1 && newSize == 1");
			byte[] directBlockData = new byte[128];
			
			for(int i=0;i<data.length;i++)
				directBlockData[i]=data[i];
			
			diskManager.writeBytesToDisk(directBlockData,file.getDirectAddress()*128);
			
			int indexBlock = file.getOneIndexAddress();
			byte[] indexs = read32FCBIndexsFromABlock(indexBlock);
			
			for(int i=0;i<32;i++)
			{
				if(indexs[i] != 0)
					diskManager.freeABlock(indexs[i]);
			}
			diskManager.freeABlock(indexBlock);
			
			file.setLength(data.length);
			file.setOneIndexAddress(0);
			
			diskManager.writeBytesToDisk(file.getBytes(),FCBPos);
			result = true;
			
		}else if(oldSize > 1 && newSize > 1 && oldSize>newSize)
		{
			System.out.println("@@oldSize > 1 && newSize > 1 && oldSize>newSize");
			int directBlockNum = file.getDirectAddress();
			int indexBlockNum = file.getOneIndexAddress();
			int delIndexNum= oldSize - newSize;
			byte blockdata[][] = new byte[newSize][128];
			
			for(int i=0;i<newSize;i++)
			{
				for(int j=0;j<128;j++)
				{
					if((i*128+j)<data.length)
						blockdata[i][j] = data[i*128+j];
					else
						blockdata[i][j] = 0;
				}
			}
			
			byte[] indexs = read32FCBIndexsFromABlock(indexBlockNum);
			
			int v = 0;
			while(v<32 && indexs[v]!=0)
				v++;
			for(int i=0;i<delIndexNum;i++)
			{
				diskManager.freeABlock(indexs[v-i-1]);
				indexs[v-i-1]=0;
			}
			
			diskManager.writeBytesToDisk(blockdata[0], directBlockNum*128);
			for(int i=0;i<newSize-1;i++)
			{
				diskManager.writeBytesToDisk(blockdata[i+1], indexs[i]*128);
			}
			
			file.setLength(data.length);
			write32FCBIndexsIntoABlock(indexs, indexBlockNum);
			diskManager.writeBytesToDisk(file.getBytes(), FCBPos);
			
			result = true;
		}else if(oldSize > 1 && newSize > 1 && oldSize<newSize)
		{
			System.out.println("@@oldSize > 1 && newSize > 1 && oldSize<newSize");
			int addIndexNum= newSize - oldSize;
			int []addBlockNum = new int[addIndexNum];

			for(int i=0;i<addIndexNum;i++)
				addBlockNum[i] = diskManager.getABlock();
			
			if(addBlockNum[addIndexNum-1] != -1)
			{
				
				int directBlockNum = file.getDirectAddress();
				int indexBlockNum = file.getOneIndexAddress();			
				byte blockdata[][] = new byte[newSize][128];
				
				for(int i=0;i<newSize;i++)
				{
					for(int j=0;j<128;j++)
					{
						if((i*128+j)<data.length)
							blockdata[i][j] = data[i*128+j];
						else
							blockdata[i][j] = 0;
					}
				}
				
				byte[] indexs = read32FCBIndexsFromABlock(indexBlockNum);
				
				int v = 0;
				while(v<32 && indexs[v]!=0)
					v++;
				for(int i=0;i<addIndexNum;i++)
				{
					indexs[v+i] = (byte) addBlockNum[i];
				}
				
				diskManager.writeBytesToDisk(blockdata[0], directBlockNum*128);
				for(int i=0;i<newSize-1;i++)
				{
					diskManager.writeBytesToDisk(blockdata[i+1], indexs[i]*128);
				}
				file.setLength(data.length);
				write32FCBIndexsIntoABlock(indexs, indexBlockNum);
				diskManager.writeBytesToDisk(file.getBytes(), FCBPos);
				
				result = true;
			}else{
				for(int i=0;i<addIndexNum;i++)
				{
					if(addBlockNum[i] != -1)
						diskManager.freeABlock(addBlockNum[i]);
				}
			}
	
		}else if(oldSize > 1 && newSize > 1 && oldSize == newSize)
		{
			System.out.println("@@oldSize > 1 && newSize > 1 && oldSize == newSize");
			
			int directBlockNum = file.getDirectAddress();
			int indexBlockNum = file.getOneIndexAddress();
			
			byte blockdata[][] = new byte[newSize][128];
//System.out.println("@@newSize "+newSize);
			for(int i=0;i<newSize;i++)
			{
				for(int j=0;j<128;j++)
				{
					if((i*128+j)<data.length)
						blockdata[i][j] = data[i*128+j];
					else
						blockdata[i][j] = 0;
				}
			}
//System.out.println("@@blockdata "+new String(blockdata[0]));
			byte[] indexs = read32FCBIndexsFromABlock(indexBlockNum);
//System.out.println("@@indexBlockNum "+indexBlockNum);
			diskManager.writeBytesToDisk(blockdata[0], directBlockNum*128);
			for(int i=0;i<newSize-1;i++)
			{
				diskManager.writeBytesToDisk(blockdata[i+1], indexs[i]*128);
			}
			
			file.setLength(data.length);
//System.out.println("@@length "+file.getLength());
			diskManager.writeBytesToDisk(file.getBytes(), FCBPos);
//byte[] b =new byte[128];
//diskManager.readBytesFromDisk(b, directBlockNum*128);
//System.out.println("@@ "+ new String(b));
//diskManager.readBytesFromDisk(b, indexs[0]*128);
//System.out.println("@@ "+ new String(b));
			result = true;
		}
	
		return result;
	}
	

	
	public FCB createFile(FCB grander,FCB parent,byte[] name,byte[] extend,byte attribute)
	{
		if(parent == null)
		{
			return createRootFile(name, extend, attribute);
		}
		
		if(parent.isFile())
		{
			System.out.println(new String(parent.getName()+" 不是目录"));
			return null;
		}
	
		int parentFCBPos = getAFCBPositonFromParent(grander, parent);
		
		ArrayList<FCB> fcblist = getDirectSons(parent);
		for(int i=0;i<fcblist.size();i++)
			if( util.Tools.isSame6Bytes(name,fcblist.get(i).getName()) && util.Tools.isSame3Bytes(extend,fcblist.get(i).getExtend()))
				{
					System.out.println("文件名重复");
					return null;
				}
		
		FCB newfile = null;
		
		if(parent.getDirectAddress() == 0)				//空目录时
		{
			int blockNum = diskManager.getABlock();		//申请空闲块 作为 FCB块
			
			if(blockNum != -1)
			{
				initFCBBlocks(blockNum);				//初始化FCB块
				parent.setDirectAddress(blockNum);
				diskManager.writeBytesToDisk(parent.getBytes(), parentFCBPos);
				newfile = new FCB(name);
				newfile.setExtend(extend);
				newfile.setAttribute(attribute);
				addAFCBIntoABlock(newfile, blockNum);
				return newfile;
			}
			
		}else if(parent.getOneIndexAddress() == 0)		//直接地址时
		{
			int blockNum=-1;
			
			blockNum = parent.getDirectAddress();	
			
			if(!isFCBBlockFull(blockNum))			//直接块有空闲FCB空间
			{
				newfile = new FCB(name);
				newfile.setExtend(extend);
				newfile.setAttribute(attribute);
				addAFCBIntoABlock(newfile, blockNum);
				return newfile;
			}else{										//直接块无空闲FCB空间，申请一级索引块
				int indexNum = diskManager.getABlock();	//申请空闲块 作为 一级索引块
				if(indexNum != -1)
				{
					blockNum = diskManager.getABlock();	//为索引表申请第一块
					
					if(blockNum != -1)
					{
						parent.setOneIndexAddress(indexNum);
						diskManager.writeBytesToDisk(parent.getBytes(), parentFCBPos);
						initFCBBlocks(blockNum);
						initIndexsBlock(indexNum);	
						byte[] b = new byte[32];
						b[0] = (byte) blockNum;
						write32FCBIndexsIntoABlock(b, indexNum);
						newfile = new FCB(name);
						newfile.setExtend(extend);
						newfile.setAttribute(attribute);
						addAFCBIntoABlock(newfile, blockNum);
						return newfile;
					}else{
						diskManager.freeABlock(indexNum);
					}
				}
			}
		}else if(parent.getOneIndexAddress() != 0)		//一级索引时
		{
			byte[] indexs = read32FCBIndexsFromABlock(parent.getOneIndexAddress());	//获得一级索引块中存放的索引
			
			int j =0;																//查看索引中是否存在不满的FCB块
			for(j=0;j<32;j++)
			{
				if(indexs[j] != 0 && !isFCBBlockFull(indexs[j]))
				{
					newfile = new FCB(name);
					newfile.setExtend(extend);
					newfile.setAttribute(attribute);
					addAFCBIntoABlock(newfile, indexs[j]);
					return newfile;
				}
			}
			
			int i = 0;
			while(i<32 && indexs[i]!=0)												//寻找索引表中的空闲项
					i++;
			
			if(i<32)
			{
				int blockNum = diskManager.getABlock();								//申请磁盘空闲块
				if(blockNum != -1)
				{
					initFCBBlocks(blockNum);
					indexs[i]=(byte) blockNum;
					write32FCBIndexsIntoABlock(indexs, parent.getOneIndexAddress());
					newfile = new FCB(name);
					newfile.setExtend(extend);
					newfile.setAttribute(attribute);
					addAFCBIntoABlock(newfile, blockNum);
					return newfile;
				}
			}
			
		}																//二级索引待添加××××××××××××××××××××××××××××××××××××××××××××××
		
		return null;
	}
	
	public FCB createDir(FCB grander,FCB parent,byte[] name,byte attribute)
	{
		if(parent == null)
		{
			return createRootDir(name, attribute);
		}
		
		if(parent.isFile())
		{
			System.out.println(new String(parent.getName()+" 不是目录"));
			return null;
		}
		
		int parentFCBPos = getAFCBPositonFromParent(grander, parent);
		
		ArrayList<FCB> fcblist = getDirectSons(parent);
		for(int i=0;i<fcblist.size();i++)
			if( util.Tools.isSame6Bytes(name,fcblist.get(i).getName()) && util.Tools.isSame3Bytes(new byte[3],fcblist.get(i).getExtend()))
				{
					System.out.println("文件名重复");
					return null;
				}
		
		
		FCB newfile = null;
		if(parent.getDirectAddress() == 0)				//空目录时
		{
			int blockNum = diskManager.getABlock();		//申请空闲块 作为 FCB块
			
			if(blockNum != -1)
			{
System.out.println("@@blockNum "+blockNum);
				initFCBBlocks(blockNum);				//初始化FCB块
				parent.setDirectAddress(blockNum);
				diskManager.writeBytesToDisk(parent.getBytes(), parentFCBPos);
				newfile = new FCB(name);
				newfile.setAttribute(attribute);
				addAFCBIntoABlock(newfile, blockNum);
				return newfile;
			}
			
		}else if(parent.getOneIndexAddress() == 0)		//直接地址时
		{
			int blockNum=-1;
			
			blockNum = parent.getDirectAddress();	
System.out.println("@@blockNum "+blockNum);
			if(!isFCBBlockFull(blockNum))			//直接块有空闲FCB空间
			{
				newfile = new FCB(name);
				newfile.setAttribute(attribute);
				addAFCBIntoABlock(newfile, blockNum);
				return newfile;
			}else{										//直接块无空闲FCB空间，申请一级索引块
				int indexNum = diskManager.getABlock();	//申请空闲块 作为 一级索引块
				if(indexNum != -1)
				{
					blockNum = diskManager.getABlock();	//为索引表申请第一块
					
					if(blockNum != -1)
					{
						parent.setOneIndexAddress(indexNum);
						diskManager.writeBytesToDisk(parent.getBytes(), parentFCBPos);
						initFCBBlocks(blockNum);
						initIndexsBlock(indexNum);	
						byte[] b = new byte[32];
						b[0] = (byte) blockNum;
						write32FCBIndexsIntoABlock(b, indexNum);
						newfile = new FCB(name);
						newfile.setAttribute(attribute);
						addAFCBIntoABlock(newfile, blockNum);
						return newfile;
					}else{
						diskManager.freeABlock(indexNum);
					}
				}
			}
		}else if(parent.getOneIndexAddress() != 0)		//一级索引时
		{
			byte[] indexs = read32FCBIndexsFromABlock(parent.getOneIndexAddress());	//获得一级索引块中存放的索引
			
			int j =0;																//查看索引中是否存在不满的FCB块
			for(j=0;j<32;j++)
			{
				if(indexs[j] != 0 && !isFCBBlockFull(indexs[j]))
				{
					newfile = new FCB(name);
					newfile.setAttribute(attribute);
					addAFCBIntoABlock(newfile, indexs[j]);
					return newfile;
				}
			}
			
			
			int i = 0;
			while(i<32 && indexs[i]!=0)												//寻找索引表中的空闲项
				i++;
			if(i<32)
			{
				int blockNum = diskManager.getABlock();								//申请磁盘空闲块
				if(blockNum != -1)
				{
					initFCBBlocks(blockNum);
					indexs[i]=(byte) blockNum;
					write32FCBIndexsIntoABlock(indexs, parent.getOneIndexAddress());
					newfile = new FCB(name);
					newfile.setAttribute(attribute);
					addAFCBIntoABlock(newfile, blockNum);
					return newfile;
				}
			}
			
		}																//二级索引待添加××××××××××××××××××××××××××××××××××××××××××××××
		
		return null;
	}
	
	public FCB createRootFile(byte[] name,byte[] extend,byte attribute)
	{
		FCB[] fcbs = read8FCBFromABlock(1);
		for(int i=0;i<8;i++)
			if( util.Tools.isSame6Bytes(name,fcbs[i].getName()) && util.Tools.isSame3Bytes(extend,fcbs[i].getExtend()))
				{	
					System.out.println("根目录文件名重复");
					return null;
				}
		fcbs = read8FCBFromABlock(2);
		for(int i=0;i<8;i++)
			if( util.Tools.isSame6Bytes(name,fcbs[i].getName()) && util.Tools.isSame3Bytes(extend,fcbs[i].getExtend()))
				{
					System.out.println("根目录文件名重复");
					return null;
				}
		
		FCB rootfile = null;
		rootfile = new FCB(name);
		rootfile.setExtend(extend);
		rootfile.setAttribute(attribute);
		int a = addAFCBIntoABlock(rootfile, 1);
		if(a == -1)
			a = addAFCBIntoABlock(rootfile, 2);
		if(a == -1)
			rootfile = null;
		return rootfile;
	}
	
	public FCB createRootDir(byte[] name,byte attribute)
	{
		FCB[] fcbs = read8FCBFromABlock(1);
		for(int i=0;i<8;i++)
			if(util.Tools.isSame6Bytes(name,fcbs[i].getName()) && util.Tools.isSame3Bytes(new byte[3],fcbs[i].getExtend()))
				{
					System.out.println("根目录文件夹名重复");
					return null;
				}
		
		fcbs = read8FCBFromABlock(2);
		for(int i=0;i<8;i++)
			if(util.Tools.isSame6Bytes(name,fcbs[i].getName()))
				{
					System.out.println("根目录文件夹名重复");
					return null;
				}
		
		FCB rootfile = null;
		rootfile = new FCB(name);
		rootfile.setAttribute(attribute);
		int a = addAFCBIntoABlock(rootfile, 1);
		if(a == -1)
			a = addAFCBIntoABlock(rootfile, 2);
		if(a == -1)
			rootfile = null;
		return rootfile;
	}
	 
	 
	 public int castByteNumToBlockNum(int byteNum)
	 {
		 return ((byteNum+127)/128);
	 }
	 
	 public byte[] read32FCBIndexsFromABlock(int blockNum)
	 {
		 byte[] indexs = new byte[32];
		 diskManager.readBytesFromDisk(indexs, blockNum*128);
		 return indexs;
	 }
	 
	 public void write32FCBIndexsIntoABlock(byte[] indexs,int blockNum)
	 {
		 diskManager.writeBytesToDisk(indexs,blockNum*128);
	 }
	 
	 public FCB[] read8FCBFromABlock(int blockNum)
	 {
		 FCB[] fcbs =new FCB[8];
		 for(int i=0;i<8;i++)
			 fcbs[i] = new FCB();
		 byte[] b = new byte[16];
		 for(int i=0;i<8;i++)
		 {
			 diskManager.readBytesFromDisk(b,blockNum*128+16*i);
			 fcbs[i].initFromBytes(b);
		 }
			 
		 return fcbs;
	 }
	 
	 public void write8FCBIntoABlock(FCB[] fcbs,int blockNum)
	 {
		 for(int i=0;i<8;i++) 
			 diskManager.writeBytesToDisk(fcbs[i].getBytes(), blockNum*128+16*i);
	 }
	 
	 public int searchAFCBFromABlock(FCB fcb,int blockNum)
	 {
		 FCB[] fcbs = read8FCBFromABlock(blockNum);
		 for(int i=0;i<8;i++)
		 {
			 if(fcb !=null && fcbs[i].getBytes()[0] != '#')
			 {
				 if(fcb.isSameName(fcbs[i]))
					 return i; 
			 }
			 
		 }
				
		 return -1;
	 }
	 
	 public boolean moveFCB(FCB sourceFCB,FCB sourceParentFCB,FCB targetFCB,FCB targetParentFCB)
	 {
		 int posTarget = getAFCBPositonFromParent(targetParentFCB, targetFCB);
		 int posSource = getAFCBPositonFromParent(sourceParentFCB, sourceFCB);
		 
		 if(posTarget == 0 || posSource == 0)
			 return false;
		 
		 sourceFCB.setName(targetFCB.getName());
		 sourceFCB.setExtend(targetFCB.getExtend());
		 diskManager.writeBytesToDisk(sourceFCB.getBytes(), posTarget);
		 byte b = '#';
		 diskManager.writeByteToDisk(b, posSource);
		 return true;
	 }
	 
	 public int getAFCBPositonFromParent(FCB parent,FCB file)
	 {
		 int fcbPosition = 0;
		 if(parent != null)
		 { 
			 
			 fcbPosition = searchAFCBFromABlock(file, parent.getDirectAddress())*16+parent.getDirectAddress()*128;				//查找子FCB在磁盘中的起始位置（字节）
			 
			 if(parent.getOneIndexAddress() != 0)
			 {
				 byte[] indexs = read32FCBIndexsFromABlock(parent.getOneIndexAddress());
				 int i=0;
				 int j=0;
				 for(i=0;i<32;i++)
				 {
					 FCB[] fcbs = read8FCBFromABlock(indexs[i]);
					 for(j=0;j<8;j++)
					 {
						 if(fcbs[j].getBytes()[0]!='#' && fcbs[j].isSameName(file))
							break;
					 }
					 if(j<8)
					fcbPosition = indexs[i]*128+j*16;
				 }
			 }
			 return fcbPosition;
		 }
		 
		 if(parent == null)
		 {
			 fcbPosition = searchAFCBFromABlock(file,1);
			 if(fcbPosition != -1)
			 {
				 return 128+fcbPosition*16;
			 }else{
				 fcbPosition = searchAFCBFromABlock(file,2);
				 if(fcbPosition != -1)
				 {
					 
					 return 128+fcbPosition*16;
				 }
			 }
		 }
		 return -1;
	 }
	 
	 public int delAFCBFromABlock(FCB fcb,int blockNum)
	 {
		 int v = searchAFCBFromABlock(fcb, blockNum);
		 if(v != -1)
			 {
			 	byte b ='#';
			 	diskManager.writeByteToDisk(b , blockNum*128+v*16);
			 }
		 return v;
			 
	 }
	 
	 public boolean isFCBBlockFull(int blockNum)
	 {
		 for(int i=0;i<8;i++)
			 {
			 	if(diskManager.readByteFromDisk(blockNum*128+i*16)=='#')
			 		{
			 			return false;
			 		}
			 }
		 return true;
	 }
	 
	 public boolean isFCBBlockNull(int blockNum)
	 {
		 for(int i=0;i<8;i++)
		 {
			 if(diskManager.readByteFromDisk(blockNum*128+i*16) != '#')
			 return false;
		 }
		 return true;
	 }
	 
	 public int addAFCBIntoABlock(FCB fcb,int blockNum)
	 {
		 int v = -1;
		 for(int i=0;i<8;i++)
			 if(diskManager.readByteFromDisk(blockNum*128+i*16)=='#')
			 {
				 diskManager.writeBytesToDisk(fcb.getBytes(), blockNum*128+i*16);
				 v = i;
				 break;
			 }
		 return v;
	 }
	 
	 public void initIndexsBlock(int blockNum)
	 {
		byte[] indexs = new byte[32];
		
		write32FCBIndexsIntoABlock(indexs, blockNum);
	 }
	 
	 public void initFCBBlocks(int blockNum)
	 {
		byte b = '#';
		
		for(int i=0;i<8;i++)
			diskManager.writeByteToDisk(b,blockNum*128+i*16);
	 }
	 
	 //**************************************页************************************
	 
	 public  void getPage(int pNum,String path,byte[][] b)
	 {
		 FCB file = openFileByPath(path);
		 byte[] data = readFile(file);
		 
		 for(int i = 0 ;i <4;i++)
			 for(int j=0;j<4;j++)
			 {
				 if(i*4+pNum*4+j<data.length)
					 b[i][j] = data[i*4+pNum*4+j];
				 else
					 b[i][j] = -10;
			 }
	 }
	 
	 public void saveLog(String name,String result)
	 {
		 FCB logfile = openFileByPath("/log.txt");
		 if(logfile == null)
		 {
			 createFile(null, null, "log".getBytes(), "txt".getBytes(), (byte)0);
			 logfile = openFileByPath("/log.txt");
		 }
		 if(logfile != null)
		 {
			 String oldLog ="";
			 byte b[] = readFile(logfile);
			 if(b != null && b.length > 0)
				 oldLog =new String (b);
			 String newLog = oldLog+"##"+name+"##"+result;
			 reSaveFile(null, logfile, newLog.getBytes());
		 }
		 
		 
	 }
}
