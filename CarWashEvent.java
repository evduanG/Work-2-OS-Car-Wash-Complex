import java.util.EventObject;

public class CarWashEvent extends EventObject{
	

	private String message;
	private int id;
	
	public CarWashEvent(Object source) {
		super(source);
	}
	
	public void setMessage(String msg) {
		this.message =msg;
	}
	
	public String getMessage() {
		return message;	
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
