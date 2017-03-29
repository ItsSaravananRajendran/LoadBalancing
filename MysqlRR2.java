import java.util.Random;  
import java.sql.*;


class Variables extends Thread  {
	static String highPriority = "jdbc:mysql://207.46.134.127:3306/student?autoReconnect=true&useSSL=false"; 
	static String mediumPriority = "jdbc:mysql://168.63.207.217:3306/student?autoReconnect=true&useSSL=false";
	static String lowPriority = "jdbc:mysql://168.63.221.124:3306/student?autoReconnect=true&useSSL=false";
	static String userName= "thunderbolt";
	static String roll;
	static int queryCount,min,max;
	static public Connection hpCon;
	static public Connection mpCon;
	static public Connection lpCon;
	static Statement stmt;

	Variables(){
		queryCount = 0;
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
		}
		}catch(Exception e){ System.out.println(e);}
	}




	public static void main(String args[]){  
		long startTime=0l;
		int noOfThread = 200;
		MysqlRR2 t[] =new MysqlRR2[noOfThread];
		for(int l=0;l<noOfThread;l++){
				t[l]=new MysqlRR2();
				System.out.println("Thread started");
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
//java -cp .:mysql-connector-java-5.1.41-bin.jar MysqlRR2