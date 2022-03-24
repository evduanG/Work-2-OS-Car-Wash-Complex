import java.util.concurrent.Semaphore;
import javax.swing.event.EventListenerList;

public class Car implements Runnable{
	
	public static Semaphore SmpFinishS3 =new Semaphore(0);
	private long time ;
	private long delayCar;
	
	private STAGE_WHSH stag;
	
	private final String carName;
	private EventListenerList list;
	private double whit1;
	private double whit2;
	private double whit3;

	private int id;
	
	public Car(int id, long aT_forWosh) {
		this.id = id;
		this.carName =new String ("car #" +id);
		this.stag=STAGE_WHSH.Created;
		this.list = new EventListenerList();
		this.delayCar=aT_forWosh;
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

	public void run() {
		if (this.stag == STAGE_WHSH.Created) {
			alertListenerList(this.carName);
		}else {
			alertListenerList(this.carName);
			if (this.stag != STAGE_WHSH.InStation3) {
				delay(delayCar);
			}
			alertListenerList(this.carName);
		}
	}
	private static void delay(long ms){
		try { Thread.sleep(ms);}
		catch (InterruptedException e) {}
	}

	public void alertListenerList(String msg) {
		CarWashEvent event = new CarWashEvent(this);
		event.setId(this.id);
		event.setMessage(msg);
		//for each listener type send the event
		for ( CarWashListener e: list.getListeners(CarWashListener.class) )
		{
			e.actionListener(event);
		}

	}
	
	public synchronized STAGE_WHSH getStag() {
		return stag;
	}
	public synchronized void setStag(STAGE_WHSH stag) {
		this.stag = stag;
	}
	
	/**
	 * @return the current enum stag as a string 
	 */
	public synchronized String getStagStr() {
		String s =null;
		switch (this.stag) {
		case Created :
			s="Created";
			break;
		case InQueueStation1 :
			s="InQueueStation1";
			break;
		case InStation1 :
			s="InStation1";
			break;	
		case InQueueStation2 :
			s="InQueueStation2";
			break;
		case InStation2 :
			s="InStation2";
			break;
		case InStation3 :
			s="InStation3";
			break;
		case finished :
			s="finished";
			break;
		}
		return s;
	}
	
	// time 
	/**
	 * calculus the diff form the car time to the current time and swap to current time
	 * @param currentTime
	 * @return
	 */
	public synchronized long carWaitingTime(long currentTime) {
		long WaitTime = currentTime-this.time;
		this.time=currentTime;
		return WaitTime;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
}
