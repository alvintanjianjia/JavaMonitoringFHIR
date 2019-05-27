package FHIR;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class fhirPatientObjectList implements fhirObjectList {
	
	private HashMap<String, String> hashmap = new HashMap<String, String>();
	
	String total_output = "";
	public fhirPatientObjectList(String userName) {
		try {
			URL url = new URL("http://hapi-fhir.erc.monash.edu:8080/baseDstu3/Encounter?practitioner=" + userName + "&_include=Encounter:appointment&_include=Encounter:patient&_include=Encounter:practitioner&_count=500&_pretty=true");
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			JSONObject tbc = new JSONObject();
			JSONObject obj = new JSONObject(total_output);
			JSONArray arr = obj.getJSONArray("entry");
			String patient;
			for (int i = 0; i < arr.length(); i++)
			{
			    tbc = arr.getJSONObject(i);
			    if (tbc.has("resource")) {
			    	tbc = tbc.getJSONObject("resource");
			    	if(tbc.has("subject")) {
			    		tbc = tbc.getJSONObject("subject");
			    		if(tbc.has("reference")) {
			    			patient = tbc.getString("reference");
			    			patient = patient.split("/")[1];
			    			hashmap.put(patient, patient);
			    		}
			    	}
			    	
			    }
			    
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public HashMap<String, String> getPatientList() {
		return this.hashmap;
	}

	public void setValue(HashMap<String, String> hashmap) {
		this.hashmap = hashmap;
	}
}
	