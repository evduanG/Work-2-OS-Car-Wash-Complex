import java.util.concurrent.Semaphore;

import javax.swing.event.EventListenerList;

public class Car extends Thread{
	
	private long time ;

	public Semaphore smp;
	
	
	EventListenerList list;
	private double whit1;
	private double whit2;
	private double whit3;

	private int id;
	
	public Car(int x, int y, int id) {
		this.id = id;
		this.list = new EventListenerList();
		this.smp=new Semaphore(0);
	}
	//add listener to list
	public void addListener(CarWashListener listener)
	{
		this.list.add(CarWashListener.class, listener);
	}
	//remove listener from list
	public void removeListener(CarWashListener listener)
	{
		this.list.remove(CarWashListener.class, listener);
	}
	//do some job and send the results to listeners
	public void run() {
		try {
			this.smp.acquire();
			alertListenerList("car #"+this.id+" 1 ");
			this.smp.acquire();
			alertListenerList("car #"+this.id+" 2 ");

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void alertListenerList(String msg) {
		CarWashEvent event = new CarWashEvent(this);
		event.setId(this.id);
		event.setMessage(msg);
		//for each listener type send the event
		for ( CarWashListener e: list.getListeners(CarWashListener.class) )
		{
			e.printProgressMessage(event);
		}

	}
	public double getWhit1() {
		return whit1;
	}
	public void setWhit1(double whit1) {
		this.whit1 = whit1;
	}
	public double getWhit2() {
		return whit2;
	}
	public void setWhit2(double whit2) {
		this.whit2 = whit2;
	}
	public double getWhit3() {
		return whit3;
	}
	public void setWhit3(double whit3) {
		this.whit3 = whit3;
	}
		

}
