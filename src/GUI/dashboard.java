package GUI;

import FHIR.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import javax.swing.*;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.client.IGenericClient;
import org.hl7.fhir.dstu3.model.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


//Code for dashboard
public class dashboard extends JFrame {
	
	JPanel panel;
	JPanel button_panel;
	JFrame frame;
	JButton add_patient, delete_patient;
	JTextField patientIdField;
	JTabbedPane tab;
	int numberOfTabs = 1;
	
	//LinkedHashMap to keep track of insertion order.
	LinkedHashMap<String, Integer> dashboard_collection = new LinkedHashMap<String, Integer>();
	
	//Constructor
	dashboard(String userName) {
		
		//This text field is for the manual inspection of other patients using patient ID.
		patientIdField = new JTextField("Enter patient ID");
		
		//Adding of panels to allow simultaneous viewing of different patients.
		JPanel buttonPanel = new JPanel();
	    buttonPanel.setLayout(new GridBagLayout());
	    buttonPanel.setSize(new Dimension(400, 300));

	    //Addition of scroll pane to accomodate multiple patient buttons
	    JScrollPane pane = new JScrollPane();
	    pane.setSize(new Dimension(400, 300));

	    // GridBagConstraint for patient button
	    GridBagConstraints constraint = new GridBagConstraints();
	    constraint.anchor = GridBagConstraints.CENTER;
	    constraint.fill = GridBagConstraints.NONE;
	    constraint.gridx = 0;
	    constraint.gridy = GridBagConstraints.RELATIVE;
	    constraint.weightx = 1.0f;
	    constraint.weighty = 1.0f;

	    int sizeOfButtons = 50;
	 
	    //Getting patient list from FHIR package
		fhirPatientObjectList patientList = new fhirPatientObjectList(userName);
		System.out.println(Arrays.asList(patientList.getPatientList()));
		for (String key: patientList.getPatientList().keySet()) {
			fhirCholestrolObjectList cholestrol = null;
			try {
				cholestrol = new fhirCholestrolObjectList(key);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			
			//Get the average cholestrol data from current data points
			Double avg_cholestrol = cholestrol.getAverage();
			JButton button = new JButton();
			
			//Changing of button colours depending on the average cholestrol level.
			if (avg_cholestrol > 190) {
				button.setBackground(Color.RED);
				button.setText("<html>" + key + "<br>" +" High Cholestrol" + "</html>");
			} else if (avg_cholestrol > 160 && avg_cholestrol <= 189) {
				button.setBackground(Color.ORANGE);
				button.setText("<html>" + key + "<br>" +" Medium Cholestrol" + "</html>");
			} else if (avg_cholestrol == 0.0 ) {
				button.setText("<html>" + key + "<br>" +" No Data" + "</html>");
			} else {
				button.setText(key);
			}
			
			//Add action listener for Patient Button to show graph and details for monitoring purposes.
			buttonPanel.add(button,constraint);	
			button.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent ae) {
					JPanel new_panel = new JPanel();
					String tbc = key;
					cholestrolModel model = new cholestrolModel();
					chartPanel chart;
					try {
						chart = new chartPanel(model, tbc);
						Timer timer = new Timer(10000, new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								try {
									chart.updateData(tbc);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
						timer.start();
						new_panel.add(chart);
						JButton deleteButton = new JButton();
						deleteButton.setText("Stop Monitoring");
						new_panel.add(deleteButton);
						
						//Add action listener for Stop Monitoring button, aka delete button.
						deleteButton.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent ae) {
								
								//Get index of current to be removed patient from LinkedHashMap
								//Remove patient counter from LinkedHashMap
								int counter = dashboard_collection.get(tbc);
								tab.remove(counter);
								dashboard_collection.remove(tbc);
								
								//Creating new temp LinkedHashMap and reset all the counters for the still available panels
								LinkedHashMap<String, Integer> panel_tbc = new LinkedHashMap<String, Integer>();
								int temp= 0;
								numberOfTabs--;
								for (String key: dashboard_collection.keySet()) {
									panel_tbc.put(key,temp);
									temp++;
								}
								dashboard_collection = panel_tbc;
								timer.stop();
							}
						});
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					tab.add(tbc, new_panel);
					tab.setSelectedIndex(numberOfTabs - 1);
					dashboard_collection.put(tbc, numberOfTabs-1);
					numberOfTabs++;
				}
			});
		}
		
		//Add scroll pane (which contains buttons) to main pane.
		pane.setViewportView(buttonPanel);
		add(pane, BorderLayout.LINE_START);
		
		//Set up new JPanel to accomodate the new graphs.
		button_panel = new JPanel();
		tab = new JTabbedPane();
		
		tab.setBounds(50,50,200,200);
		
		//The following code blocks are for the manual addition and deletion of patients based on patient ID.
		add_patient = new JButton("Add Patient");
		delete_patient = new JButton("Delete Patient");
		add_patient.setSize(50,50);
		add_patient.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent ae) {
				JPanel new_panel = new JPanel();
				String tbc = patientIdField.getText();
				cholestrolModel model = new cholestrolModel();
				chartPanel chart;
				try {
					chart = new chartPanel(model, tbc);
					Timer timer = new Timer(10000, new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							try {
								chart.updateData(tbc);
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					});
					timer.start();
					JButton deleteButton = new JButton();
					deleteButton.setText("Stop Monitoring");
					new_panel.add(deleteButton);
					
					//Add action listener for Stop Monitoring button, aka delete button.
					deleteButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent ae) {
							
							//Get index of current to be removed patient from LinkedHashMap
							//Remove patient counter from LinkedHashMap
							int counter = dashboard_collection.get(tbc);
							tab.remove(counter);
							dashboard_collection.remove(tbc);
							
							//Creating new temp LinkedHashMap and reset all the counters for the still available panels
							LinkedHashMap<String, Integer> panel_tbc = new LinkedHashMap<String, Integer>();
							int temp= 0;
							numberOfTabs--;
							for (String key: dashboard_collection.keySet()) {
								panel_tbc.put(key,temp);
								temp++;
							}
							dashboard_collection = panel_tbc;
							timer.stop();
						}
					});
					new_panel.add(chart);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				tab.add(tbc, new_panel);
				tab.setSelectedIndex(numberOfTabs - 1);
				dashboard_collection.put(tbc, numberOfTabs-1);
				numberOfTabs++;
			} 
		});
		
		//Add buttons to current panel
		button_panel.add(add_patient);
		button_panel.add(delete_patient);
		add(patientIdField, BorderLayout.PAGE_START);
		add(button_panel, BorderLayout.PAGE_END);
		add(tab, BorderLayout.CENTER);
		
		setVisible(true);
		setSize(400,400);
	}

}
