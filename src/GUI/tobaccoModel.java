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

public class tobaccoModel extends TimerTask implements MonitorModel {
	
	private boolean modelStateChange = false;
	private List[] tobaccoData;
	private String username;
	private boolean tobaccoUsage;
	
	public tobaccoModel(String userName) {
		this.username = userName;
	}
	
	public List[] getPatientTobaccoData() {
		return this.tobaccoData;
	}
	
	public String getPatientTobaccoUsageString() {
		if (tobaccoUsage == true) {
    		return "Former Smoker";
    	} else {
    		return "Non Smoker";
    	}
	}
	
    //To get data for cholesterol plot
    public List[] getPatientTobaccoUse(String userName) throws JSONException {
    	fhirTobaccoObjectList tobaccoList = new fhirTobaccoObjectList(userName);
    	List<Double>cholestrol_list = tobaccoList.getTobaccoList();
    	List<String>datetime =tobaccoList.getDatetime();
    	List<Double>value = tobaccoList.getValue();

    	//If the return size of the list is 0, i.e. No data present for the patient for cholestrol
    	//Else return the list which contains 2 lists, 1 for timestamp another for value.
    	if (datetime.size() == 0) {
    		return new List[] {};
    	} else {
    		this.tobaccoData = new List[] {datetime, value};
    		return new List[]{datetime, value};
    	}
    }
    
    public String getPatientTobaccoUsage(String userName) throws JSONException {
    	fhirTobaccoObjectList tobaccoList = new fhirTobaccoObjectList(userName);
    	tobaccoUsage = tobaccoList.getTobaccoUsage();
    	if (tobaccoUsage == true) {
    		return "Former Smoker";
    	} else {
    		return "Non Smoker";
    	}
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
			this.tobaccoData = getPatientTobaccoUse(this.username);
			this.modelStateChange = true;
			this.notifyModel();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
