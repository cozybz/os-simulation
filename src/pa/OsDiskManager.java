package pa;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class OsDiskManager {
	
	public static final int MAXBLOCK = 128;		//磁盘块数
	public static final int MAXBYTE = 128;			//块字节数
	public static final int ONEGROUP = 10;			//每组空闲块数量
	public static final int START = 3;				//第0,1,2分别为专用块、根目录、根目录
	public String FILEPATH=null;	//模拟磁盘文件位置
	public static final int REMAINDER = MAXBLOCK % ONEGROUP;
	
	public OsDiskManager(String FILEPATH)
	{
		this.FILEPATH = FILEPATH;
		if(!isFileExist())
		{
			System.out.println("文件不存在，创建文件");
			createFile();
			initFile();
			initFCBBlocks(START-1);
			initFCBBlocks(START-2);
		}else
		{
			System.out.println("文件存在");
		}
	}
	
	public void createFile()
	{
		File file = new File(FILEPATH);
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isFileExist()
	{
		File file = new File(FILEPATH);
		return file.exists();
	}
	
	public void initFile()
	{
		byte[] disk = new byte[MAXBLOCK*MAXBYTE];
		this.writeBytesToDisk(disk, START-3);
		System.out.println("清空磁盘");
																				
		int[] superBlock = readSuperBlock();										//初始化专用块
		
		superBlock[10]=REMAINDER - START;
		
		for(int i=START;i<REMAINDER;i++)
			superBlock[i-START]=i;											
		
		writeSuperBlock(superBlock);												
		
		int save = START;															//初始化其他组
		int[] blockbuffer = new int[12];
		blockbuffer[10]=ONEGROUP;
		for(int i = REMAINDER;i<MAXBLOCK;i=i+ONEGROUP)
		{
			for(int j = 0;j<ONEGROUP;j++)
			{
				blockbuffer[j]=i+j; 
			}
			
			if(i+ONEGROUP>=MAXBLOCK)				//最后一组的第一块为0
			{
				blockbuffer[11] = blockbuffer[0];
				blockbuffer[0]=0;
			}
			
			for(int k=0;k<ONEGROUP+2;k++)
			{
				writeIntToDisk(blockbuffer[k] , save*128+4*k);
			}
			save=i;
			
		}
		
		System.out.println("成组链接 初始化完毕");
		
	}
	
	public void writeBytesToDisk(byte b[],long pos)
	{
		try {
			RandomAccessFile raf = new RandomAccessFile(FILEPATH, "rw");
			
			raf.seek(pos);
			raf.write(b);
			raf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeByteToDisk(byte b,long pos)
	{
		try {
			RandomAccessFile raf = new RandomAccessFile(FILEPATH, "rw");
			raf.seek(pos);
			raf.write(b);
			raf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readBytesFromDisk(byte b[],long pos)
	{
		try {
			RandomAccessFile raf = new RandomAccessFile(FILEPATH, "r");
			raf.seek(pos);
			raf.read(b);
			raf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public byte readByteFromDisk(long pos)
	{
		byte b = 0;
		try {
			
			RandomAccessFile raf = new RandomAccessFile(FILEPATH, "r");
			raf.seek(pos);
			b=raf.readByte();
			raf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	public void writeIntToDisk(int v,long pos)
	{
		try {
			RandomAccessFile raf = new RandomAccessFile(FILEPATH, "rw");
			raf.seek(pos);
			raf.writeInt(v);
			raf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public int readIntFromDisk(long pos)
	{
		int v=0;
		try {
			RandomAccessFile raf = new RandomAccessFile(FILEPATH, "r");
			raf.seek(pos);
			v=raf.readInt();
			raf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return v;
	}
	
	public int[] readSuperBlock()
	{
		int[] superBlock = new int[12];
		for(int i=0;i<12;i++)
			superBlock[i]=readIntFromDisk(4*i);
		return superBlock;
	}
	
	public void writeSuperBlock(int[] s)
	{
		for(int i=0;i<12;i++)
			writeIntToDisk(s[i], i*4);
	}
	
	 public void initFCBBlocks(int blockNum)
	 {
		byte b = '#';
		for(int i=0;i<8;i++)
			writeByteToDisk(b,blockNum*128+i*16);
	 }
	
	//****************************************************************************************功能
	 
	 public void format()
	 {
			initFile();
			initFCBBlocks(START-1);
			initFCBBlocks(START-2);
	 }
	 
	public int getABlock()
	{
		int[] superBlock = readSuperBlock();
		int i=0;
		if(superBlock[10]>1)
		{
			superBlock[10] = superBlock[10] -1;
			i = superBlock[superBlock[10]];
		}else{
			if(superBlock[0]==0 && superBlock[11] == 0)
			{
				i= (-1);
			}else if(superBlock[0]==0 && superBlock[11] != 0){
				i = superBlock[11];
				superBlock[11] = 0;
			}else{
				int x = superBlock[0];
				for(int j=0;j<ONEGROUP+2;j++)
				{
					superBlock[j]=readIntFromDisk(128*x+4*j);
				}
				i=x;
			}
		}
		writeSuperBlock(superBlock);
System.out.println("@@分配出 " + i);
		return i;
	}
	public void freeABlock(int blockNum)
	{
		int[] superBlock = readSuperBlock();
		if(superBlock[10]<10)
			{	
				superBlock[superBlock[10]]=blockNum;
				superBlock[10] = superBlock[10] +1;
			}
		else
		{
			for(int i=0;i<12;i++)
				writeIntToDisk(superBlock[i],blockNum*128+i*4);
			superBlock[10]=1;
			superBlock[0]=blockNum;
		}	
		writeSuperBlock(superBlock);
System.out.println("@@回收 "+blockNum);
	}
	
	public int[] getFreeBlocksPosition()
	{
		int [] state = new int[128];
		for(int i=0;i<128;i++)
			state[i]=1;
		int[] superBlock = readSuperBlock();
		
		while(superBlock[0]!=0)
		{
			for(int i=0;i<superBlock[10];i++)
				state[superBlock[i]]=0;
			
			int x = superBlock[0];
			for(int i=0;i<12;i++)
			{
				superBlock[i]=readIntFromDisk(x*128+i*4);
			}
			
			if(superBlock[0] == 0 && superBlock[11] != 0)
			{
				state[superBlock[11]] = 0;
			}
		}
			
		for(int i=1;i<superBlock[10];i++)
			state[superBlock[i]]=0;

		
		return state;
	}
	
}
