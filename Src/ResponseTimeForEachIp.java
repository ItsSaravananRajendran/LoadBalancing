/*****************************************************************************************
Response time for every 100 requests from each IP in priority based algorithm.
*****************************************************************************************/ 



import java.util.Random;  
import java.sql.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


class ResponseTimeForEachIp extends Thread{ 

	/****************************************************************************
	 connection variables
	*****************************************************************************/ 
	static String highPriority = "jdbc:mysql://139.59.72.32:3306/student?autoReconnect=true&useSSL=false"; 
	static String mediumPriority = "jdbc:mysql://139.59.35.229:3306/student?autoReconnect=true&useSSL=false";
	static String lowPriority = "jdbc:mysql://139.59.35.59:3306/student?autoReconnect=true&useSSL=false";
	static String userName= "thunderbolt";
	
	/****************************************************************************
     counter for query from different IP in different machines
	*****************************************************************************/ 
	static int[][] queryCount = new int[5][3];

	/****************************************************************************
	  counter array for query generated from an IP
	*****************************************************************************/ 
	static int query[]=new int[5];
	
	/****************************************************************************
	 variables to measure the time   
	*****************************************************************************/ 
	static long elapsedTimeForip[] = new long[5] ;
	static long elapsedTimeForipAvg[] = new long[5] ;
					

	public void run(){ 
		long startTime ,stopTime;
		try{
		Class.forName("com.mysql.jdbc.Driver");  
		Connection hpCon=DriverManager.getConnection(highPriority,userName,"");
		Connection mpCon=DriverManager.getConnection(mediumPriority,userName,"");
		Connection lpCon=DriverManager.getConnection(lowPriority,userName,"");
		Random rand = new Random();
		Random num = new Random();
		int min = 1000001,max =1028071;
		char m='a';
		for (int i=0;i<2;i++ ) {
			
			/****************************************************************************
			To generate a random number from min to max
			*****************************************************************************/ 
			int  n = (int )(Math.random() * (max-min) + min);


			/****************************************************************************
			To generate an random ip, in this case a random number from 0-4 
			based on the probability assigned in the condition. Now all the IP's have been 
			given an equal probability, but this can also be changed.
			*****************************************************************************/
			int ip;
			double  prob = Math.random();
			if (prob < 0.5){
				ip = 0;
			}else if (prob < 0.7){
				ip = 1;
			}else if (prob < 0.85){
				ip = 2;
			}else if (prob < 0.92){
				ip = 3;
			}else{
				ip = 4;
			}

			/****************************************************************************
			A routine to find the machine with least number of query from a generated IP ,
			this routine also takes care of the priotity based  routing when a tie occurs
			mID contains the ID of the machine to which the request has to be sent 
			*****************************************************************************/ 
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
			switch(mID){
				case 0: stmt=lpCon.createStatement();m='L';break;
				case 1: stmt=mpCon.createStatement();m='M';break;
				case 2: stmt=hpCon.createStatement();m='H';break; 
			}	

			/****************************************************************************
			This block takes care of sending the request, incrementing the querycounter 
			of the IP in its corresponding machine, decrementing the querycounter after 
			the result has been received and outputs the time taken to completly serve
			100 request from same IP 
			*****************************************************************************/
			synchronized(this){
       			queryCount[ip][mID]++;
     			startTime = System.currentTimeMillis();
     			rs=stmt.executeQuery("select * from result where REGNO='\""+roll+"\"'");  
     			stopTime = System.currentTimeMillis();	
     			queryCount[ip][mID]--;
				query[ip]++;
				long var = stopTime - startTime;
				System.out.println(startTime+" IP = "+ip+" server handling the request = "+m+" Response time = "+var);	
				elapsedTimeForip[ip] +=  var;
 				/*if(query[ip] % 100 ==0){
					System.out.println("query["+ip+"] "+query[ip]+ " response time "+ elapsedTimeForip[ip] );
 					elapsedTimeForipAvg[ip]+= elapsedTimeForip[ip];
 					elapsedTimeForip[ip]=0l;
 				}*/
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
		}catch(Exception e){
		 // System.out.println(e);
		}
	}




	public static void main(String args[]){  

		/****************************************************************************
		Initialzation of the counter variables to zero
		*****************************************************************************/ 
		for(int j=0;j<5;j++){
			for (int i=0;i<3;i++) {
				queryCount[j][i]=0;
			}
			query[j]=0;
			elapsedTimeForip[j] = 0l;
			elapsedTimeForipAvg[j]=0l;
		}

		/****************************************************************************
		Creating threads to generate requests
		*****************************************************************************/ 
		int noOfThread = 200;
		ResponseTimeForEachIp t[] =new ResponseTimeForEachIp[noOfThread];
		for(int l=0;l<noOfThread;l++){
				t[l]=new ResponseTimeForEachIp();
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
		
		/****************************************************************************
		No of queries or requests generated for each IP
		*****************************************************************************/ 
	  	for (int J =0;J<5;J++ ) {
	  	  	 System.out.println("queryCount for Ip = "+J +" is "+query[J]+" Average response time for "+(elapsedTimeForip[J]/query[J]));
	  	  }  
	}  
}  