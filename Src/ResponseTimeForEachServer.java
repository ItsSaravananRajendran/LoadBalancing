/*****************************************************************************************
Response time for every 100 requests in Round Robin algorithm.
*****************************************************************************************/ 

import java.util.Random;  
import java.sql.*;

class ResponseTimeForEachServer extends Thread{ 

	
	/****************************************************************************
	 connection variables
	 enter the IP of the server in their respective position and UserName
	*****************************************************************************/ 
	static String highPriority = "jdbc:mysql://IP1:3306/student?autoReconnect=true&useSSL=false"; 
	static String userName= "Username";
	
	/****************************************************************************
	 variables to measure the time   
	*****************************************************************************/ 
	static long elapsedTime = 0l;
		
	public void run(){ 
		long startTime =0l,stopTime =0l;
		try{
		Class.forName("com.mysql.jdbc.Driver");  
		Connection hpCon=DriverManager.getConnection(highPriority,userName,"");
		int min = 1000001,max =1028071;
		for (int i=0;i<5;i++ ) {

			/****************************************************************************
			To generate a random number from min to max
			*****************************************************************************/ 
			int  n = (int )(Math.random() * (max-min) + min);
			Statement stmt=null;	
			String roll = Integer.toString(n);
			ResultSet rs;
			stmt=hpCon.createStatement();
			

			/****************************************************************************
			This block takes care of sending the request, incrementing the querycounter 
			received and outputs the time taken to completly every serve 100 request.
			*****************************************************************************/
			synchronized(this){
     			startTime = System.currentTimeMillis();
     			rs=stmt.executeQuery("select * from result where REGNO='\""+roll+"\"'");  
				stopTime = System.currentTimeMillis();	
				elapsedTime += stopTime - startTime;
			}
		}
		hpCon.close();
		}catch(Exception e){ //System.out.println(e);
		}
	}




	public static void main(String args[]){  
		long startTime=0l;
		int noOfThread = 200;

		/****************************************************************************
		Initialzation of the counter variables to zero
		*****************************************************************************/ 
		ResponseTimeForEachServer t[] =new ResponseTimeForEachServer[noOfThread];
		for(int l=0;l<noOfThread;l++){
				t[l]=new ResponseTimeForEachServer();
		}	
		try{  

			/****************************************************************************
			Creating threads to generate requests
			*****************************************************************************/ 
			long st = System.currentTimeMillis();
			for(int l=0;l<noOfThread;l++){
				t[l].start();
			}
			for(int l=0;l<noOfThread;l++){
				t[l].join();
			}
			long stop = System.currentTimeMillis();
			System.out.println("Time To process 1000 query in highPriority server is "+(stop -st));
			System.out.println("Average Response time for 1 query is  "+ (float)(elapsedTime)/1000);
		}catch(Exception e){ 
			System.out.println(e);
		}
	}  
}  