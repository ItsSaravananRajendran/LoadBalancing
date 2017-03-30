import java.util.Random;  
import java.sql.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


class mySqlRR2 extends Thread{ 

	static String highPriority = "jdbc:mysql://207.46.134.127:3306/student?autoReconnect=true&useSSL=false"; 
	static String mediumPriority = "jdbc:mysql://168.63.207.217:3306/student?autoReconnect=true&useSSL=false";
	static String lowPriority = "jdbc:mysql://168.63.221.124:3306/student?autoReconnect=true&useSSL=false";
	static String userName= "thunderbolt";
	static long queryCount = 0l;
	static long elapsedTime = 0l;
	static long startTime [] = new long[5];
	static long stopTime [] =new long[5];
	static long query [] = new long[5];
		
	public void run(){ 
		try{
		Class.forName("com.mysql.jdbc.Driver");  
		Connection hpCon=DriverManager.getConnection(highPriority,userName,"");
		Connection mpCon=DriverManager.getConnection(mediumPriority,userName,"");
		Connection lpCon=DriverManager.getConnection(lowPriority,userName,"");  
		Random rand = new Random();
		int min = 1000001,max =1028071;
		for (int i=0;i<50;i++ ) {
			int ip;
			double  prob = Math.random();
			if (prob < 0.2){
				ip = 0;
			}else if (prob < 0.4){
				ip = 1;
			}else if (prob < 0.6){
				ip = 2;
			}else if (prob < 0.8){
				ip = 3;
			}else{
				ip = 4;
			}
			int  n = (int )(Math.random() * (max-min) + min);
			Statement stmt=null;	
			String roll = Integer.toString(n);
			ResultSet rs;
			switch((int)(queryCount % 3)){
				case 0: stmt=lpCon.createStatement();break;
				case 1: stmt=mpCon.createStatement();break;
				case 2: stmt=hpCon.createStatement();break; 
			}		
			synchronized(this){
     			rs=stmt.executeQuery("select * from result where REGNO='\""+roll+"\"'");  
     			if(query[ip] % 100 ==0){
					stopTime[ip] = System.currentTimeMillis();	
					long elapsedTimeForip = stopTime[ip] - startTime[ip];
					System.out.println("query["+ip+"] "+query[ip]+ " waiting time "+ elapsedTimeForip );
 					startTime[ip] = System.currentTimeMillis();
				}
				query[ip]++;
				queryCount++;	
    		}
			//while(rs.next())  
				//System.out.println(rs.getString("NAME")+" "+rs.getString("REGNO")+" "+rs.getString("MARK01")+" "+rs.getString("MARK02")+" "+rs.getString("MARK03")+" "+rs.getString("MARK04")+rs.getString("MARK05")+" "+rs.getString("TOTAL")+" "+rs.getString("PASS")); 

		}
		hpCon.close();
		mpCon.close();
		lpCon.close();			
		}catch(Exception e){ //System.out.println(e);
		}
	}




	public static void main(String args[]){  
		int noOfThread = 200;
		for (int I=0;I<5 ;I++ ) {
			query[I] = 0l;
			startTime[I]=0l;
			stopTime[I]=0l;
		}
		mySqlRR2 t[] =new mySqlRR2[noOfThread];
		for(int l=0;l<noOfThread;l++){
				t[l]=new mySqlRR2();
		}	
		try{  
			//startTime = System.currentTimeMillis();
			for(int l=0;l<noOfThread;l++){
				t[l].start();
			}
			for(int l=0;l<noOfThread;l++){
				t[l].join();
			}
		}catch(Exception e){ 
			System.out.println(e);
		}
	  	for (int J =0;J<5;J++ ) {
	  	  	 System.out.println(" Ip = "+J +", queryCount = "+query[J]);
	  	} 
	}  
}  
//java -cp .:mysql-connector-java-5.1.41-bin.jar MysqlCon