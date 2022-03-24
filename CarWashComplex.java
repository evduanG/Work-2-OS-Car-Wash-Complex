import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;


public class CarWashComplex implements CarWashListener  {
	

	public static Scanner sc= new Scanner(System.in);
	
	//============================
	// Variables 
	private final int N ;						//num of stations are station No. 1 
	private final int K ;						//num of stations are station No. 2 
	private final int numCar ;					//num of stations are station No. 2 
	
	//============================
	//Times
	private final int AT_forS1;					// Average time between cars entering the Complex
	private final int AT_forWosh;				// Average time of Car wash
	
	private long stations1Time;					// Total time that car waiting in stations 1
	private long stations2Time;					// Total time that car waiting in stations 2
	private long stations3Time;					// Total time that car waiting in stations 3

	private final long startTime;
	
	//============================

	private Car [] 		cars;
	private Executor 	stations1;
	private Executor 	stations2;
	private Executor 	stations3;
	//============================

	private Semaphore 	SmpFinishS1;			//Semaphore to check of the cars finish stations 1 and stations 3 can be opened 
	private Semaphore 	SmpFinishS;				//Semaphore to check of the cars finish and complex can be closed

/*
 * ==========================================================================
 * main 
 * ==========================================================================
 */
	
	public static void main(String[] args) {
		CarWashComplex comp = new CarWashComplex();
		
		for (int i = 0; i < comp.cars.length; i++) {
			comp.cars[i].run();
			delay(comp.AT_forS1);
		}
		try {
			comp.SmpFinishS1.acquire(comp.numCar);
			Car.SmpFinishS3.release(comp.numCar);
			comp.SmpFinishS.acquire(comp.numCar);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		((ExecutorService) comp.stations1).shutdown();
		((ExecutorService) comp.stations2).shutdown();
		((ExecutorService) comp.stations3).shutdown();
		System.out.println("========================================================");
		double avS1= (double)(comp.stations1Time/comp.numCar)/1000;
		System.out.println("av s1 : "+avS1+"second"); 
		double avS2= (double)(comp.stations2Time/comp.numCar)/1000;
		System.out.println("av s2 : "+avS2+"second"); 
		double avS3= (double)(comp.stations1Time/comp.numCar)/1000;
		System.out.println("av s1 : "+avS3+"second"); 
		double totle= comp.getDiffTime (System.currentTimeMillis());
		System.out.println("totla time  :"+totle+ "Minute"); 
		System.out.println("bay");

	}
	/**
	 * constructor
	 * 
	 */
	public CarWashComplex () {
	//	user input 
		this.N=	(int) getUserData("Enter the number of thread in stations 1  ");	
		this.K= (int) getUserData("Enter the number of thread in stations 2  ");
		this.numCar = (int) getUserData("Enter the number of cars ");
		this.AT_forS1= 1000*(int) getUserData("Enter an average time between the arrival of car to the complex , Reasonable time for use Around 1.5 seconds ");
		this.AT_forWosh =1000*(int) getUserData("Enter an average time between the arrival washing for car and the Stations , Reasonable time for use Around 3 seconds ");
		
//		this.N=3;
//		this.K=3;
//		this.numCar=10;
//		this.AT_forS1=1500;
//		this.AT_forWosh=3000;

		//Creating an array of cars
		this.cars = new Car [numCar];
		for (int i = 0; i < cars.length; i++) {
			this.cars[i]=  new Car(  i ,(long)AT_forWosh );
			//add this class as listener for work complete event
			this.cars[i].addListener(this);			
		}
		
		//Resetting semaphores
		this.SmpFinishS1 =new Semaphore(0, true);
		this.SmpFinishS = new Semaphore(0, true);

		// stations
		this.stations1= Executors.newFixedThreadPool(this.N);
		this.stations2= Executors.newFixedThreadPool(this.K);
		this.stations3= Executors.newFixedThreadPool(1);
		stations1Time=0;
		stations2Time=0;
		stations3Time=0;
		startTime =System.currentTimeMillis();
	}
	
	

	//user input method
	public static double  getUserData (final String msg) {
		double ans =0;
		boolean first =true;
		while (ans<=0) {
			if (first) {
				first=false ;
			} else {
				System.out.println("EROR ! ");
			}
			System.out.println(msg);
			ans = sc.nextDouble();
		}
		return ans;
	}
	/*
	 * ==========================================================================
	 * Time Method
	 * ==========================================================================
	 */
	private static void delay(long ms){
		try { Thread.sleep(ms);}
		catch (InterruptedException e) {}
	}
	
	public double getDiffTime(long currentTime) {
		return (double)(currentTime-this.startTime)/1000;
	}
	
	public synchronized void updating_Stations1_Time(long addTime) {
		this.stations1Time+=addTime;
	}

	public synchronized void updating_Stations2_Time(long addTime) {
		this.stations2Time+=addTime;
	}

	public synchronized void updating_Stations3_Time(long addTime) {
		this.stations3Time+=addTime;
	}
	
	/*
	 * ==========================================================================
	 * actionListener
	 * ==========================================================================
	 */
	public void actionListener (CarWashEvent event) {
		//get the time from the System
		long currentTime = System.currentTimeMillis();
		// get the index of the car 
		int id = event.getId();
		
		// Checking the Stag of the car 
		switch (cars[id].getStag()) {
		case Created:
			this.cars[id].setStag(STAGE_WHSH.InQueueStation1);
			System.out.println("The "+event.getMessage()+" Entered the Complex");
			this.cars[id].setTime(currentTime);
			this.stations1.execute(cars[id]);
			break;
		case InQueueStation1:
			this.cars[id].setStag(STAGE_WHSH.InStation1);
			System.out.println("The "+event.getMessage()+" Entered the Into Station NO.1");
			break;
		case InStation1:
			System.out.println("The "+event.getMessage()+" end the Into Station NO.1");
			this.cars[id].setStag(STAGE_WHSH.InQueueStation2);
			this.stations2.execute(cars[id]);
			this.SmpFinishS1.release();
			updating_Stations1_Time(this.cars[id].carWaitingTime(currentTime));
			break;
		case InQueueStation2:
			this.cars[id].setStag(STAGE_WHSH.InStation2);
			System.out.println("The "+event.getMessage()+" Entered the Into Station NO.2");
			break;	
		case InStation2:
			System.out.println("The "+event.getMessage()+" end the Into Station NO.2");
			this.cars[id].setStag(STAGE_WHSH.InStation3);
			this.stations3.execute(cars[id]);
			updating_Stations2_Time(this.cars[id].carWaitingTime(currentTime));
			break;
		case InStation3:
			System.out.println("The "+event.getMessage()+" Entered the Into Station NO.3");
			try {
				Car.SmpFinishS3.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.cars[id].setStag(STAGE_WHSH.finished);
			break;
		case finished:
			this.SmpFinishS.release();
			updating_Stations3_Time(this.cars[id].carWaitingTime(currentTime));
			System.out.println("The "+event.getMessage()+" end the Into Station NO.3");
			break;
		default:
			System.out.println();
			for (int i = 0; i < 10; i++) {
				System.out.println("eror car"+id+" "+cars[id].getStagStr());

			}
			break;
		}
		double diffStat = getDiffTime(currentTime);
		System.out.println("Time from strat : "+diffStat +" second");
	}
}
