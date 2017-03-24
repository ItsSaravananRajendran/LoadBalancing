import java.util.Random;  
import java.sql.*;

class mySql2 extends Thread{ 

	static String highPriority = "jdbc:mysql://207.46.134.127:3306/student?autoReconnect=true&useSSL=false"; 
	static String mediumPriority = "jdbc:mysql://168.63.207.217:3306/student?autoReconnect=true&useSSL=false";
	static String lowPriority = "jdbc:mysql://168.63.221.124:3306/student?autoReconnect=true&useSSL=false";
	static String userName= "thunderbolt";
	static int[][] queryCount = new int[5][3];
	static long elapsedTime = 0l;

	public void run(){ 
		try{
		Class.forName("com.mysql.jdbc.Driver");  
		Connection hpCon=DriverManager.getConnection(highPriority,userName,"");
		Connection mpCon=DriverManager.getConnection(mediumPriority,userName,"");
		Connection lpCon=DriverManager.getConnection(lowPriority,userName,"");  
		long startTime ,stopTime;
		Random rand = new Random();
		Random num = new Random();
		int min1=0,max1=4;
		int min = 1000001,max =1028071;
		for (int i=0;i<50;i++ ) {
			int  n = (int )(Math.random() * (max-min) + min);
			int  ip = (int )(Math.random() * (max1-min1) + min1);
			int minQuery=queryCount[ip][0],k,mID=0;
			for(k=0;k<3;k++){
				if(queryCount[ip][k]<=minQuery){
					minQuery = queryCount[ip][k];
					mID= k;
				}
			}
			Statement stmt=null;
			
			String roll = Integer.toString(n);
			ResultSet rs;
			synchronized(this){
     			queryCount[ip][mID]++;
     			switch(mID){
					case 0: stmt=lpCon.createStatement();break;
					case 1: stmt=mpCon.createStatement();break;
					case 2: stmt=hpCon.createStatement();break; 
				}	
				startTime = System.currentTimeMillis();
     			rs=stmt.executeQuery("select * from result where REGNO='\""+roll+"\"'");  
     			stopTime = System.currentTimeMillis();
     			elapsedTime =elapsedTime + stopTime - startTime;
     			queryCount[ip][mID]--;
    		}
			//while(rs.next())  
			//	System.out.println(rs.getString("NAME")+" "+rs.getString("REGNO")+" "+rs.getString("MARK01")+" "+rs.getString("MARK02")+" "+rs.getString("MARK03")+" "+rs.getString("MARK04")+rs.getString("MARK05")+" "+rs.getString("TOTAL")+" "+rs.getString("PASS")); 
		}
		hpCon.close();
		mpCon.close();
		lpCon.close();			
		}catch(Exception e){ System.out.println(e);}
	}




	public static void main(String args[]){  
		long startTime=0l;
		for(int j=0;j<5;j++){
			for (int i=0;i<3;i++) {
				queryCount[j][i]=0;
			}
		}
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
	  	System.out.println(elapsedTime);  
	}  
}  
//java -cp .:mysql-connector-java-5.1.41-bin.jar MysqlCon