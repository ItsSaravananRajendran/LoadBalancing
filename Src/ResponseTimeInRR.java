/*****************************************************************************************
Response time for every 100 requests in priority based algorithm.
*****************************************************************************************/ 


import java.util.Random;  
import java.sql.*;


class ResponseTimeInRR extends Thread{ 

	/****************************************************************************
	 connection variables
	*****************************************************************************/ 
	static String highPriority = "jdbc:mysql://207.46.134.127:3306/student?autoReconnect=true&useSSL=false"; 
	static String mediumPriority = "jdbc:mysql://168.63.207.217:3306/student?autoReconnect=true&useSSL=false";
	static String lowPriority = "jdbc:mysql://168.63.221.124:3306/student?autoReconnect=true&useSSL=false";
	static String userName= "thunderbolt";

	/****************************************************************************
     counter for query from different IP in different machines
	*****************************************************************************/ 
	static long queryCount = 0l;
	static long query [] = new long[5];


	/****************************************************************************
	 variables to measure the time   
	*****************************************************************************/ 
	static long elapsedTime = 0l;
	static long startTime [] = new long[5];
	static long stopTime [] =new long[5];
		
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
			received and outputs the time taken to completly every serve 100 request from 
			each  IP  
			*****************************************************************************/	
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
		int noOfThread = 200;

		/****************************************************************************
		Initialzation of the counter variables to zero
		*****************************************************************************/ 
		for (int I=0;I<5 ;I++ ) {
			query[I] = 0l;
			startTime[I]=0l;
			stopTime[I]=0l;
		}


		/****************************************************************************
		Creating threads to generate requests
		*****************************************************************************/ 
		ResponseTimeInRR t[] =new ResponseTimeInRR[noOfThread];
		for(int l=0;l<noOfThread;l++){
				t[l]=new ResponseTimeInRR();
		}	
		try{  
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