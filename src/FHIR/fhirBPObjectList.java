package FHIR;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class fhirBPObjectList implements fhirObjectList {
	private List<Double>bp_list = new ArrayList<>();
	private List<String>diastolic_datetime = new ArrayList<>();
	private List<String>systolic_datetime = new ArrayList<>();
	private List<Double>diastolic_value = new ArrayList<>();
	private List<Double>systolic_value = new ArrayList<>();
	String total_output = "";
	
	public fhirBPObjectList(String userName) throws JSONException {
		try {
			URL url = new URL("http://hapi-fhir.erc.monash.edu:8080/baseDstu3/Observation?_pretty=true&code=http://loinc.org%7C55284-4&subject=" + userName + "&_format=json");
			System.out.println("http://hapi-fhir.erc.monash.edu:8080/baseDstu3/Observation?_pretty=true&code=http://loinc.org%7C55284-4&subject=" + userName + "&_format=json");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				total_output += output;
			}
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	JSONObject tbc;
    	JSONObject tbc2;
    	JSONObject tbc_datetime = null;
    	JSONArray arr = null;
    	JSONArray bparr = null;
		JSONObject obj = new JSONObject(total_output);
		if (obj.has("entry")) {
			arr = obj.getJSONArray("entry");
			Double value_tbc = 0.0;
			Double value_tbc_bp1 = 0.0;
			Double value_tbc_bp2 = 0.0;
			String datetime_tbc = "";
			for (int i = 0; i < arr.length(); i++)
			{
			    tbc= arr.getJSONObject(i);
			    if (tbc.has("resource")) {
			    	tbc = tbc.getJSONObject("resource");
			    	if (tbc.has("component")) {
			    		bparr = tbc.getJSONArray("component");
			    		for (int j = 0; j < bparr.length(); j++) {
			    			tbc2= bparr.getJSONObject(j);
			    			if (tbc2.has("valueQuantity")) {
					    		tbc2 = tbc2.getJSONObject("valueQuantity");
					    		if (tbc2.has("value")) {
					    			value_tbc_bp1 = tbc2.getDouble("value");
					    		}
					    		
					    	}    	
						    	if (tbc.has("effectiveDateTime")) {
						    		datetime_tbc = tbc.getString("effectiveDateTime");
						    	}
						    	if (j == 0) {
						    		diastolic_value.add(value_tbc_bp1);
						    		diastolic_datetime.add(datetime_tbc);
						    	}
						    	else {
						    		systolic_value.add(value_tbc_bp1);
								    systolic_datetime.add(datetime_tbc);
						    	}								    
			    		}
			    	}
			    }	    
			}
		}
	}

	public List<Double> getBPList() {
		return bp_list;
	}

	public void setBPList(List<Double> bp_list) {
		this.bp_list = bp_list;
	}
	
	public List<String> getDiastolicDatetime() {
		return diastolic_datetime;
	}

	public void setDiastolicDatetime(List<String> datetime) {
		this.diastolic_datetime = datetime;
	}
	
	public List<String> getSystolicDatetime() {
		return systolic_datetime;
	}

	public void setSystolicDatetime(List<String> datetime) {
		this.systolic_datetime = datetime;
	}
	
	public List<Double> getDiastolicValue() {
		return this.diastolic_value;
	}

	public void setDiastolicValue(List<Double> value) {
		this.diastolic_value = value;
	}
	
	public List<Double> getSystolicValue() {
		return this.systolic_value;
	}

	public void setSystolicValue(List<Double> value) {
		this.systolic_value = value;
	}
	
	public List<Double> getLatestReadings() {
		List<Double>latestReadings = new ArrayList<>();
		//latest diastolic reading
		latestReadings.add(this.diastolic_value.get(this.diastolic_value.size()-1));
		//systolic reading
		latestReadings.add(this.diastolic_value.get(this.systolic_value.size()-1));
		return latestReadings; 
	}
}
