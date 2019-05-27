package GUI;

import FHIR.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import REST.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.Gson;

public class cholestrolModel extends TimerTask implements MonitorModel  {
	
	
	private boolean modelStateChange = false;
    private List[] cholestrolData;
    private String username;
    
	
	public cholestrolModel(String userName) {
		this.username = userName;
	}
	
	public List[] getPatientCholestrolData() {
		return this.cholestrolData;
	}
	

	//To get data for cholestrol plot
    public List[] getPatientCholestrol(String userName) throws JSONException {
    	fhirCholestrolObjectList cholList = new fhirCholestrolObjectList(userName);
    	List<Double>cholestrol_list = cholList.getCholestrolList();
    	List<String>datetime =cholList.getDatetime();
    	List<Double>value = cholList.getValue();

    	//If the return size of the list is 0, i.e. No data present for the patient for cholestrol
    	//Else return the list which contains 2 lists, 1 for timestamp another for value.
    	if (datetime.size() == 0) {
    		return new List[] {};
    	} else {
    		this.cholestrolData = new List[] {datetime, value};
    		return new List[]{datetime, value};
    	}
    }
    
    
    public Double getCholestrolThreshold() {
    	return 180.00;
    }
    
    public boolean getState() {
    	return this.modelStateChange;
    }
    
    public void setState(boolean stateChange) {
    	this.modelStateChange = stateChange;
    }


	


	@Override
	public void run() {
		try {
			this.cholestrolData = getPatientCholestrol(this.username);
			this.modelStateChange = true;
			this.notifyModel();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}




    
    
    
    
    
    
    
    
}
