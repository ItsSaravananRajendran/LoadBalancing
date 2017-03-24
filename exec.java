import java.util.*;
import java.sql.*;



class exec{

	static String highPriority = "jdbc:mysql://207.46.134.127:3306/student?autoReconnect=true&useSSL=false"; 
	static String mediumPriority = "jdbc:mysql://168.63.207.217:3306/student?autoReconnect=true&useSSL=false";
	static String lowPriority = "jdbc:mysql://207.46.129.72:3306/student?autoReconnect=true&useSSL=false";
	static String userName= "thunderbolt";
	static int[][] queryCount = new int[5][3];



	public static void main(String argsp[]){
	ExecutorService threadPool = Executors.newFixedThreadPool(10);
	// submit jobs to be executing by the pool
	for(int j=0;j<5;j++){
		for (int i=0;i<3;i++) {
			queryCount[j][i]=0;
		}
	}
	for (int i = 0; i < 30; i++) {
	threadPool.submit(new Runnable() {
	    public void run() {
	        // some code to run in parallel
    		try{
				Class.forName("com.mysql.jdbc.Driver");  
				Connection hpCon=DriverManager.getConnection(highPriority,userName,"");
				Connection mpCon=DriverManager.getConnection(mediumPriority,userName,"");
				Connection lpCon=DriverManager.getConnection(lowPriority,userName,""); 
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
		     			switch(mID){
							case 0: stmt=lpCon.createStatement();queryCount[ip][0]++;break;
							case 1: stmt=mpCon.createStatement();queryCount[ip][1]++;break;
							case 2: stmt=hpCon.createStatement();queryCount[ip][2]++;break; 
						}	
		     			rs=stmt.executeQuery("select * from result where REGNO='\""+roll+"\"'");  
		    		}
					while(rs.next())  
						System.out.println(rs.getString("NAME")+" "+rs.getString("REGNO")+" "+rs.getString("MARK01")+" "+rs.getString("MARK02")+" "+rs.getString("MARK03")+" "+rs.getString("MARK04")+rs.getString("MARK05")+" "+rs.getString("TOTAL")+" "+rs.getString("PASS")); 
				} 
			}
			catch(Exception e){ System.out.println(e);}
    		
		}
	 });
	}
	// once you've submitted your last job to the service it should be shut down
	threadPool.shutdown();
	// wait for the threads to finish if necessary
	threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

	}
}


