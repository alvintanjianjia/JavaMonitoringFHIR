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

public class fhirTobaccoObjectList implements fhirObjectList {
	private List<Double>cholestrol_list = new ArrayList<>();
	private List<String>datetime = new ArrayList<>();
	private List<Double>value = new ArrayList<>();
	private boolean tobaccoUsage = false;
	String total_output = "";
	
	
	public fhirTobaccoObjectList(String userName) throws JSONException {
		try {
			//http://hapi-fhir.erc.monash.edu:8080/baseDstu3/Observation?code=http://loinc.org%7C55284-4
			URL url = new URL("http://hapi-fhir.erc.monash.edu:8080/baseDstu3/Observation?_pretty=true&code=http://loinc.org|72166-2&subject=" + userName + "&_format=json");
			System.out.println("http://hapi-fhir.erc.monash.edu:8080/baseDstu3/Observation?_pretty=true&code=http://loinc.org|72166-2&subject=" + userName + "&_format=json");
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
				//System.out.println(output);
				total_output += output;
			}
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println(total_output);
    	JSONObject tbc;
    	JSONArray arr = null;
		JSONObject obj = new JSONObject(total_output);
		if (obj.has("entry")) {
			arr = obj.getJSONArray("entry");
			Double value_tbc = 0.0;
			String datetime_tbc = "";
			for (int i = 0; i < arr.length(); i++)
			{
			    tbc= arr.getJSONObject(i);
			    if (tbc.has("resource")) {
			    	tbc = tbc.getJSONObject("resource");
			    	if (tbc.has("valueCodeableConcept")) {
			    		tbc = tbc.getJSONObject("valueCodeableConcept");
			    		if (tbc.has("text")) {
			    			String tempString = tbc.getString("text");
			    			//System.out.println(tempString + "Temp String here");
			    			if (tempString.equals("Former smoker")) {
			    				value_tbc = 100.00;
			    			} else {
			    				value_tbc = 0.00;
			    			}
			    			
			    		}
			    	}
			    }
			    tbc = arr.getJSONObject(i);
			    if(tbc.has("resource")) {
			    	tbc = tbc.getJSONObject("resource");
			    	if (tbc.has("effectiveDateTime")) {
			    		datetime_tbc = tbc.getString("effectiveDateTime");
			    	}
			    }
			    value.add(value_tbc);
			    datetime.add(datetime_tbc);
			}
			if (value.get(value.size()-1) == 100.00) {
				tobaccoUsage = true;
            }
		} 
	}

	public boolean getTobaccoUsage() {
		return this.tobaccoUsage;
	}
	
	public List<Double> getTobaccoList() {
		return cholestrol_list;
	}

	public void setTobaccoList(List<Double> tobacco_list) {
		this.cholestrol_list = cholestrol_list;
	}
	
	public List<String> getDatetime() {
		return datetime;
	}

	public void setDatetime(List<String> datetime) {
		this.datetime = datetime;
	}
	
	public List<Double> getValue() {
		return this.value;
	}

	public void setValue(List<Double> value) {
		this.value = value;
	}
	
	public Double getAverage() {
		if (value.size()==0) {
			return 0.0;
		}
		double tbc = 0;
		for (int i=0; i<value.size(); i++) {
			tbc += value.get(i);
		}
		return tbc / value.size(); 
	}
}
