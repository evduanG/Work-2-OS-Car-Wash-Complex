import java.util.Scanner;

public class CarWashComplex implements CarWashListener  {
	public static Scanner sc= new Scanner(System.in);
	
	// Variables 
	public final int N ;						/**num of stations are station No. 1 */
	public final int K ;						/**num of stations are station No. 2 */
	public final int numCar ;					/**num of stations are station No. 2 */ 
	public final int AT_forS1;
	public final int AT_forWosh;
	
	public Car [] cars;
	public Station stations1;
	public Station stations2;
	public Station stations3;

	
	public static void main(String[] args) {
		CarWashComplex comp = new CarWashComplex();
		
		//start 
		comp.stations1.start();
		comp.stations2.start();
		comp.stations3.start();

		
		
		// for loop 
		for (int i = 0; i < comp.cars.length; i++) {
			 comp.cars[i].start();
			 sleep(comp.AT_forS1);
		}
		join();
		
		

	}
	
	
	/**
	 * constructor
	 * 
	 */
	public CarWashComplex () {
		//user input 
		this.N=	(int) getUserData("Enter the number of cars ");
		this.K= (int) getUserData("Enter the number of cars ");
		this.numCar = (int) getUserData("Enter the number of cars ");
		this.AT_forS1= 1000*(int) getUserData("Enter an average time between the arrival of car to the complex , Reasonable time for use Around 1.5 seconds ");
		this.AT_forWosh =1000*(int) getUserData("Enter an average time between the arrival washing for car and the Stations , Reasonable time for use Around 3 seconds ");
		
		//car arr
		this.cars = new Car [numCar];
		for (int i = 0; i < cars.length; i++) {
			this.cars[i]=  new Car( r.nextInt(10), r.nextInt(10), i  );
			//add this class as listener for work complete event
			this.cars[i].addListener(this);
			//start each worker
			
		}
		
		// stations
		this.stations1= new Station();
		this.stations2= new Station();
		this.stations3= new Station();

	}
	
	

	//user input fanc  
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
	@Override
	public void printProgressMessage(CarWashEvent event) {
			// TODO Auto-generated method stub
		int id = event.getId();
		this.cars[id].smp.release();
		System.out.println( event.getMessage() );
		}	
	}
}




