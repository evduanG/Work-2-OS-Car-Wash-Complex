import java.util.EventListener;

public interface CarWashListener extends EventListener {

	public void printProgressMessage(CarWashEvent event);
}
