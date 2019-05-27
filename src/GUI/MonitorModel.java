package GUI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;

public interface MonitorModel {

	public Map<String, chartMonitor> observerList = new HashMap<String, chartMonitor>();
	
	//Attach function for Subject
	public default void attachModel(String key, chartMonitor chartMonitor) {
		observerList.put(key, chartMonitor);
	}
	
	//Detach function for Subject
	public default void detachModel(String key) {
		observerList.remove(key);
	}
	
	//Notify function for Subject
	public default void notifyModel() {
		for (Entry<String, chartMonitor> entry : observerList.entrySet()) {
		    entry.getValue().update();
			
		}
	}

	


	
}
