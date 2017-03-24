import java.util.Random;



class test extends Thread{
	static int count =0;
	
	public void run(){   
		Random rand = new Random();
		int min = 1000001,max =1028071;
		for (int i=0;i<10;i++ ) {
			int  n = (int )(Math.random() * (max-min) + min);
			String num = Integer.toString(n);
			synchronized(this){
				count++;
    		}
		}
	}
	
	public static void main(String args[]){
		Random rand = new Random();
		test t1=new test();
		test t2 = new test();  
		t1.start();
		t2.start();
		try{  
			t1.join();
			t2.join();
		}catch(Exception e){System.out.println(e);}  
		System.out.println(count);
	}	
}


//1000001
//1028071