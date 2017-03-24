import java.util.Random;  
import java.sql.*;


class Variables extends Thread  {
	String highPriority;
	String mediumPriority;
	String lowPriority;
	String roll;
	int queryCount,min,max;
	public Connection hpCon;
	public Connection mpCon;
	public Connection lpCon;
	Statement stmt;

	Variables(){
		String highPriority = "jdbc:mysql://207.46.134.127:3306/student?autoReconnect=true&useSSL=false"; 
		String mediumPriority = "jdbc:mysql://168.63.207.217:3306/student?autoReconnect=true&useSSL=false";
		String lowPriority = "jdbc:mysql://168.63.221.124:3306/student?autoReconnect=true&useSSL=false";
		String userName= "thunderbolt";
		int queryCount = 0;
		try{	
			Class.forName("com.mysql.jdbc.Driver");  
			hpCon=DriverManager.getConnection(highPriority,userName,"");
			mpCon=DriverManager.getConnection(mediumPriority,userName,"");
			lpCon=DriverManager.getConnection(lowPriority,userName,"");
		}catch (Exception e) {
			System.out.println(e);
		}  
		min = 1000001;
		max =1028071;
	} 
}

class MysqlRR2 extends Variables	{ 

	
	public void run(){ 
		try{
		for (int i=0;i<50;i++ ) {
			int  n = (int )(Math.random() * (max-min) + min);
			roll = Integer.toString(n);
			ResultSet rs;
			synchronized(this){
     			switch(queryCount%3){
					case 0: stmt=lpCon.createStatement();break;
					case 1: stmt=mpCon.createStatement();break;
					case 2: stmt=hpCon.createStatement();break; 
				}	
     			rs=stmt.executeQuery("select * from result where REGNO='\""+roll+"\"'");  
     			queryCount++;
    		}
			while(rs.next())  
				System.out.println(rs.getString("NAME")+" "+rs.getString("REGNO")+" "+rs.getString("MARK01")+" "+rs.getString("MARK02")+" "+rs.getString("MARK03")+" "+rs.getString("MARK04")+rs.getString("MARK05")+" "+rs.getString("TOTAL")+" "+rs.getString("PASS")); 
		hpCon.close();
		mpCon.close();
		lpCon.close();
		}
		}catch(Exception e){ System.out.println(e);}
	}




	public static void main(String args[]){  
		long startTime=0l;
		int noOfThread = 200;
		MysqlCon t[] =new MysqlCon[noOfThread];
		for(int l=0;l<noOfThread;l++){
				t[l]=new MysqlCon();
		}	
		try{  
			startTime = System.currentTimeMillis();
			for(int l=0;l<noOfThread;l++){
				t[l].start();
			}
			for(int l=0;l<noOfThread;l++){
				t[l].join();
			}
		}catch(Exception e){ System.out.println(e);}
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
	  	System.out.println(elapsedTime);  
	}  
}  
//java -cp .:mysql-connector-java-5.1.41-bin.jar MysqlCon