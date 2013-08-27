package pa;

public class FCB {
	private byte[] name;
    private byte[] extend;		//扩展名：3个字节（可执行文件扩展名为exe，目录没有扩展名）
    private byte attribute;		//第0位为1表示该目录项为目录（文件夹）的登记项，为0表示是文件的登记项（FCB）；第1位表示是否隐藏，第2位表示是否为只读文件
    private byte[] length; 		//文件长度：2字节（目录没有长度，字节数） 
	private byte directAddress;			//直接地址项
    private byte oneIndexAddress;		//一级索引项      
    private byte twoIndexAddress[];		//二级索引项1个 
    


	public FCB()
    {
    	this.name = new byte[6];
    	this.extend = new byte[3];
    	this.length = new byte[2];
    	this.twoIndexAddress = new byte[2];
    	
    }
    
    public FCB(byte[] name)
    {
    	this.name = new byte[6];
    	this.setName(name);
    	this.extend = new byte[3];
    	this.length = new byte[2];
    	this.twoIndexAddress = new byte[2];
    	
    }
    public void initFromBytes(byte[] b)
    {
    	 for(int i = 0; i < 6; i++)
             this.name[i]=b[i];
         
         for(int i=0; i<3; i++)
         	 this.extend[i]=b[i+6];
         
         this.attribute = b[9];
         
         for(int i=0; i<2; i++)
        	 this.length[i] = b[i+10];
         
         this.directAddress = b[12];
         this.oneIndexAddress=b[13];
         for(int i=0; i<2; i++)
        	 this.twoIndexAddress[i] = b[i+14];
    }
    public byte[] getBytes()
    {
        byte[] bytes = new byte[16];
        
        for(int i = 0; i < 6; i++)
            bytes[i] = this.name[i];
        
        for(int i=0; i<3; i++)
        	bytes[i+6] = this.extend[i];
        
        bytes[9] = this.attribute;
        
        for(int i=0; i<2; i++)
        	bytes[i+10] = this.length[i];
        
        bytes[12] = this.directAddress;
        bytes[13] = this.oneIndexAddress;
        for(int i=0; i<2; i++)
        	bytes[i+14] = this.twoIndexAddress[i];
        return bytes;

    }
    
    public boolean isDir()		//_ _ _ 大等于4时为目录
    {
    	if((this.attribute&1)==1)
    		return true;
    	else
    		return false;
    }
    
    public boolean isFile()		
    {
    	if((this.attribute&1)!=1)
    		return true;
    	else
    		return false;
    }
    
    public boolean isReadOnly()
    {
    	if((this.attribute&4)==4)
    		return true;
    	else
    		return false;
    }
    public boolean isHide()
    {
    	if((this.attribute&2)==2)
    		return true;
    	else
    		return false;
    	
    }

    public void setReadOnly(boolean b)
    {
    	if(b)
    		attribute = (byte) (attribute | 4);
    	else
    		attribute = (byte) (attribute & 251);
    		
    }
    
    public void setHide(boolean b)
    {
    	if(b)
    		attribute = (byte) (attribute | 2);
    	else
    		attribute = (byte) (attribute & 253);
    }
    
    public void setName(byte[] fileName)  
	 {
    	for(int i=0;i<6;i++)
		{
			if(i<fileName.length)
				this.name[i]=fileName[i];
		}
	 }
    
    public byte[] getName()
    {
    	return this.name;
    }
    
    public void setExtend(byte[] extend)  
	 {
    	for(int i=0;i<3;i++)
		{
			if(i<extend.length)
				this.extend[i]=extend[i];
		}
	 }
    
    public String getStrFullName()
    {
    	if(this.isFile())
		{
    		int i = 0;
			int j = 0;
			while(i<6 && this.name[i] != 0)
				i++;
			
			while(j<3 && this.extend[j] != 0)
				j++;
		
			byte[] fname = new byte[i+j+1];
			for(int v=0; v<i+j+1;v++)
			{
				if(v<i)
					fname[v] = this.name[v];
				else if(v ==i )
					fname[v] = '.';
				else
					{
						fname[v] = this.extend[v - i-1];
					}
			}
			return new String(fname);
		}
    	
    	else
    	{
    		int i = 0;
    		while(i<6 && this.name[i] != 0)
				i++;
    		byte[] fname = new byte[i];
    		for(int j=0;j<i;j++)
    			fname[j]=this.name[j];
    		return new String(fname);
    	}
    	
    	
    }
    
    public byte[] getExtend()
    {
    	return this.extend;
    }
    
	public int getLength()   
	{
		return util.Tools.Bytes2ToInt(this.length);
	}
	
	public void setLength(int num)         
	{
		this.length[0]=util.Tools.IntToBytes(num)[2];
		this.length[1]=util.Tools.IntToBytes(num)[3];
	}
	
	 public boolean isSameName(FCB fcb)
	 {
		 boolean b = true;
		 for(int i=0;i<6;i++)
		 {
			 if(this.name[i] != fcb.name[i])
				 b = false;
		 }
		 
		 for(int i=0;i<3;i++)
		 {
			 if(this.extend[i] != fcb.extend[i])
				 b = false;
		 }
		 return b;
	 }
	 
	 
	 public void setAttribute(int attribute)           
	 {                                                   
		 if(attribute>=0 && attribute<=255)                     
		 {	
			 this.attribute=(byte)attribute;                       
		 }                                                  	                                                                                     
                                                  
	 }                                                    
	
	public int getAttribute()
	{
		return this.attribute;
	}
	
    public byte getDirectAddress() {
		return directAddress;
	}

	public void setDirectAddress(int directAddress) {
		this.directAddress = (byte) directAddress;
	}

	public byte getOneIndexAddress() {
		return oneIndexAddress;
	}

	public void setOneIndexAddress(int oneIndexAddress) {
		this.oneIndexAddress = (byte) oneIndexAddress;
	}

}
