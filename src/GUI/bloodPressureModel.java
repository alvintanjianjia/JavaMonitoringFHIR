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

public class bloodPressureModel extends TimerTask implements MonitorModel {
	
	private String username;
	private boolean modelStateChange = false;
	private List[] bpDataSystolic;
	private List[] bpDataDiastolic;
	
	public bloodPressureModel(String userName) {
		this.username = userName;
	}
	
	
	public List[] getPatientSystolicData() {
		return this.bpDataSystolic;
	}
	
	
	public List[] getPatientDiastolicData() {
		return this.bpDataDiastolic;
	}
	
	
    //To get data for blood pressure plot
    public List[] getPatientBP(String userName) throws JSONException {
    	fhirBPObjectList bpList = new fhirBPObjectList(userName);
    	List<Double>bp_list = bpList.getBPList();
    	List<String>diastolic_datetime = bpList.getDiastolicDatetime();
    	List<Double>diastolic_value = bpList.getDiastolicValue();
    	List<String>systolic_datetime = bpList.getSystolicDatetime();
    	List<Double>systolic_value = bpList.getSystolicValue();

    	//If the return size of the list is 0, i.e. No data present for the patient for blood pressure
    	//Else return the list which contains 2 lists, 1 for timestamp another for value.
    	if (diastolic_datetime.size() == 0) {
    		return new List[] {};
    	} else {
    		return new List[]{diastolic_datetime, diastolic_value, systolic_datetime, systolic_value};
    	}
    }
    
    
    public List[] getPatientDiastolicBP(String userName) throws JSONException {
    	fhirBPObjectList bpList = new fhirBPObjectList(userName);
    	List<Double>bp_list = bpList.getBPList();
    	List<String>diastolic_datetime = bpList.getDiastolicDatetime();
    	List<Double>diastolic_value = bpList.getDiastolicValue();
    	List<String>systolic_datetime = bpList.getSystolicDatetime();
    	List<Double>systolic_value = bpList.getSystolicValue();

    	//If the return size of the list is 0, i.e. No data present for the patient for blood pressure
    	//Else return the list which contains 2 lists, 1 for timestamp another for value.
    	if (diastolic_datetime.size() == 0) {
    		return new List[] {};
    	} else {
    		this.bpDataDiastolic = new List[] {diastolic_datetime, diastolic_value};
    		return new List[]{diastolic_datetime, diastolic_value};
    	}
    }
    
    public List[] getPatientSystolicBP(String userName) throws JSONException {
    	fhirBPObjectList bpList = new fhirBPObjectList(userName);
    	List<Double>bp_list = bpList.getBPList();
    	List<String>diastolic_datetime = bpList.getDiastolicDatetime();
    	List<Double>diastolic_value = bpList.getDiastolicValue();
    	List<String>systolic_datetime = bpList.getSystolicDatetime();
    	List<Double>systolic_value = bpList.getSystolicValue();

    	//If the return size of the list is 0, i.e. No data present for the patient for blood pressure
    	//Else return the list which contains 2 lists, 1 for timestamp another for value.
    	if (diastolic_datetime.size() == 0) {
    		return new List[] {};
    	} else {
    		this.bpDataSystolic = new List[] {diastolic_datetime, diastolic_value};
    		return new List[]{systolic_datetime, systolic_value};
    	}
    }
    
    public Double getDiastolicThreshold() {
    	return 120.00;
    }
    
    public Double getSystolicThreshold() {
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
			this.bpDataDiastolic = this.getPatientDiastolicBP(this.username);
			this.modelStateChange = true;
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			this.bpDataSystolic = this.getPatientSystolicBP(this.username);
			this.modelStateChange = true;
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.notifyModel();
		
	}
    
}
