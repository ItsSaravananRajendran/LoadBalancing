import java.util.Random;  
import java.sql.*;

class MysqlRR{ 

	static String highPriority = "jdbc:mysql://207.46.134.127:3306/student?autoReconnect=true&useSSL=false"; 
	static String mediumPriority = "jdbc:mysql://168.63.207.217:3306/student?autoReconnect=true&useSSL=false";
	static String lowPriority = "jdbc:mysql://168.63.221.124:3306/student?autoReconnect=true&useSSL=false";
	static String userName= "thunderbolt";


	public static String rollNoGen(){
		Random rand = new Random();
		int min = 1000001,max =1028071;
		int  n = (int )(Math.random() * (max-min) + min);
		String roll = Integer.toString(n);
		return roll;
	}


	public static void main(String args[]){  
		long startTime=System.currentTimeMillis();
		try{  
			Class.forName("com.mysql.jdbc.Driver");  
			Connection hpCon=DriverManager.getConnection(highPriority,userName,"");
			Connection mpCon=DriverManager.getConnection(mediumPriority,userName,"");
			Connection lpCon=DriverManager.getConnection(lowPriority,userName,"");  
			ResultSet rs;
			Statement stmt1=lpCon.createStatement();
			Statement stmt2=mpCon.createStatement();
			Statement stmt3=hpCon.createStatement();
			for(int i=0;i<3334;i++){
				rs=stmt1.executeQuery("select * from result where REGNO='\""+rollNoGen()+"\"'");
				rs=stmt2.executeQuery("select * from result where REGNO='\""+rollNoGen()+"\"'");
				rs=stmt3.executeQuery("select * from result where REGNO='\""+rollNoGen()+"\"'");  	
				System.out.println(i);
			}
			hpCon.close();
			mpCon.close();
			lpCon.close();
		}catch(Exception e){ System.out.println(e);}
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
	  	System.out.println(elapsedTime);  
	}  
}  
//java -cp .:mysql-connector-java-5.1.41-bin.jar MysqlCon