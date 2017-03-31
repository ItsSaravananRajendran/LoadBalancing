/*****************************************************************************************
Response time for every 100 requests in priority based algorithm.
*****************************************************************************************/ 


import java.util.Random;  
import java.sql.*;


class ResponseTime extends Thread{ 


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
	static int[][] queryCount = new int[5][3];
	static long elapsedTime = 0l,elapsedTime1 = 0l;

	/****************************************************************************
	  counter  for query generated 
	*****************************************************************************/ 
	static int query=0;  

	/****************************************************************************
	 variables to measure the time   
	*****************************************************************************/ 
	static long startTime = 0l ,stopTime  = 0l;

	public void run(){ 
		try{
		Class.forName("com.mysql.jdbc.Driver");  
		Connection hpCon=DriverManager.getConnection(highPriority,userName,"");
		Connection mpCon=DriverManager.getConnection(mediumPriority,userName,"");
		Connection lpCon=DriverManager.getConnection(lowPriority,userName,"");
		Random rand = new Random();
		Random num = new Random();
		int min = 1000001,max =1028071;
		for (int i=0;i<50;i++ ) {
		
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
			A routine to find the machine with least number of query from a generated IP,
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
				case 0: stmt=lpCon.createStatement();break;
				case 1: stmt=mpCon.createStatement();break;
				case 2: stmt=hpCon.createStatement();break; 
			}

			/****************************************************************************
			This block takes care of sending the request, incrementing the querycounter 
			of the IP in its corresponding machine, decrementing the querycounter after 
			the result has been received and outputs the time taken to completly every serve
			100 request  
			*****************************************************************************/		
			synchronized(this){
       			queryCount[ip][mID]++;
     			rs=stmt.executeQuery("select * from result where REGNO='\""+roll+"\"'");  
     			elapsedTime =elapsedTime + stopTime - startTime;
     			queryCount[ip][mID]--;
     			if(query % 100 ==0){
						stopTime = System.currentTimeMillis();	
						elapsedTime1 = stopTime - startTime;
						System.out.println("query "+query+ " waiting time "+ elapsedTime1 );
     					startTime = System.currentTimeMillis();
				}
				query++;
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
		    System.out.println(e);
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
		}


		/****************************************************************************
		Creating threads to generate requests
		*****************************************************************************/ 
		int noOfThread = 200;
		ResponseTime t[] =new ResponseTime[noOfThread];
		for(int l=0;l<noOfThread;l++){
				t[l]=new ResponseTime();
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
	}  
}  
//java -cp .:mysql-connector-java-5.1.41-bin.jar MysqlCon//7905148
