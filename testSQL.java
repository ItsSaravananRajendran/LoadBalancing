import java.sql.*;
	

class testSQL{
	static public void main(String[] args) {
		String lowPriority = "jdbc:mysql://207.46.129.72:3306/student?autoReconnect=true&useSSL=false";
		String userName= "thunderbolt";
		try{		  
			Class.forName("com.mysql.jdbc.Driver");  
			Connection lpCon=DriverManager.getConnection(lowPriority,userName,"");
			Statement stmt=lpCon.createStatement();
			String roll ="1000335";
			ResultSet rs=stmt.executeQuery("select * from result where REGNO='\""+roll+"\"'");
			while(rs.next())  
				System.out.println(rs.getString(1)); 
			//System.out.println(rs.getString(1)+"h");  		
			lpCon.close();			
		}catch(Exception e){ System.out.println(e);}
	}
}