/*****************************************************************************************
Response time for every 100 requests in Round Robin algorithm.
*****************************************************************************************/ 

import java.util.Random;  
import java.sql.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


class ResponseTimeInRRForEachIP extends Thread{ 

	/****************************************************************************
	 connection variables
	*****************************************************************************/ 
	static String highPriority = "jdbc:mysql://139.59.39.173:3306/student?autoReconnect=true&useSSL=false"; 
	static String mediumPriority = "jdbc:mysql://139.59.39.135:3306/student?autoReconnect=true&useSSL=false";
	static String lowPriority = "jdbc:mysql://139.59.39.93:3306/student?autoReconnect=true&useSSL=false";
	static String userName= "thunderbolt";

	/****************************************************************************
     counter for query from different IP in different machines
	*****************************************************************************/ 
	static long queryCount = 0l;

	/****************************************************************************
	 variables to measure the time   
	*****************************************************************************/ 
	static long elapsedTime = 0l;
	static long startTime =0l,stopTime =0l;
		
	public void run(){ 
		try{
		Class.forName("com.mysql.jdbc.Driver");  
		Connection hpCon=DriverManager.getConnection(highPriority,userName,"");
		Connection mpCon=DriverManager.getConnection(mediumPriority,userName,"");
		Connection lpCon=DriverManager.getConnection(lowPriority,userName,"");  
		Random rand = new Random();
		int min = 1000001,max =1028071;
		for (int i=0;i<50;i++ ) {
			
			/****************************************************************************
			To generate an random ip, in this case a random number from 0-4 
			based on the probability assigned in the condition. Now all the IP's have been 
			given an equal probability, but this can also be changed.
			*****************************************************************************/ 
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

			/****************************************************************************
			To generate a random number from min to max
			*****************************************************************************/ 
			int  n = (int )(Math.random() * (max-min) + min);
			Statement stmt=null;	
			String roll = Integer.toString(n);
			ResultSet rs;

			/****************************************************************************
			A routine to find the machine which the next request has to be sent. The 
			query counter is increment everytime when a query is generated so the next 
			request will be sent to the next machine.
			*****************************************************************************/ 
			switch((int)(queryCount % 3)){
				case 0: stmt=lpCon.createStatement();break;
				case 1: stmt=mpCon.createStatement();break;
				case 2: stmt=hpCon.createStatement();break; 
			}		


			/****************************************************************************
			This block takes care of sending the request, incrementing the querycounter 
			received and outputs the time taken to completly every serve 100 request.
			*****************************************************************************/
			synchronized(this){
     			rs=stmt.executeQuery("select * from result where REGNO='\""+roll+"\"'");  
     			if(queryCount % 100 ==0){
						stopTime = System.currentTimeMillis();	
						elapsedTime = stopTime - startTime;
						System.out.println("query "+queryCount+ " waiting time "+elapsedTime);
     					startTime = System.currentTimeMillis();
				}
       			queryCount++;
    		}


			/****************************************************************************
			This block can be uncommented if you want to print the result
			*****************************************************************************/
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
		long startTime=0l;
		int noOfThread = 200;

		/****************************************************************************
		Initialzation of the counter variables to zero
		*****************************************************************************/ 
		ResponseTimeInRRForEachIP t[] =new ResponseTimeInRRForEachIP[noOfThread];
		for(int l=0;l<noOfThread;l++){
				t[l]=new ResponseTimeInRRForEachIP();
		}	
		try{  

			/****************************************************************************
			Creating threads to generate requests
			*****************************************************************************/ 
			for(int l=0;l<noOfThread;l++){
				t[l].start();
			}
			for(int l=0;l<noOfThread;l++){
				t[l].join();
			}
		}catch(Exception e){ 
			System.out.println(e);
		}
	}  
}  