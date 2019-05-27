package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.json.JSONException;

import FHIR.fhirBPObjectList;
import FHIR.fhirCholestrolObjectList;
import FHIR.fhirPatientObjectList;
import FHIR.fhirTobaccoObjectList;


public class practitioner_dashboard extends JFrame implements dashboard_factory {
	JPanel panel;
	JPanel button_panel;
	JFrame frame;
	JButton add_patient, delete_patient;
	JTextField patientIdField;
	JTabbedPane tab;
	int numberOfTabs = 1;
	private tobaccoModel tobaccoModel;
	private cholestrolModel cholesterolModel;
	private bloodPressureModel bpModel;
	
	//LinkedHashMap to keep track of insertion order.
	LinkedHashMap<String, Integer> dashboard_collection = new LinkedHashMap<String, Integer>();

	//Constructor
	public practitioner_dashboard(String userName) {
		//This text field is for the manual inspection of other patients using patient ID.
		
				patientIdField = new JTextField("Enter patient ID");
				tab = new JTabbedPane();
				tab.setBounds(50,50,200,200);
				
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
				
				//Getting patient Cholestrol Object list
				for (String key: patientList.getPatientList().keySet()) {
					fhirCholestrolObjectList cholestrol = null;
					fhirBPObjectList bloodPressure = null;
					fhirTobaccoObjectList tobacco = null;
					try {
						cholestrol = new fhirCholestrolObjectList(key);
						bloodPressure = new fhirBPObjectList(key);
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					
					//Get the average cholestrol data from current data points
					Double avg_cholestrol = cholestrol.getAverage();
					JButton button = new JButton();
					
					//Changing of button colours depending on the average cholestrol level.
					if (avg_cholestrol > 190) {
						button.setBackground(Color.RED);
						button.setText("<html>" + key + "<br>" +" High Risk User" + "</html>");
					} else if (avg_cholestrol > 160 && avg_cholestrol <= 189) {
						button.setBackground(Color.ORANGE);
						button.setText("<html>" + key + "<br>" +" Medium Risk User" + "</html>");
					} else if (avg_cholestrol == 0.0 ) {
						button.setText("<html>" + key + "<br>" +" Low Risk User" + "</html>");
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
							practitioner_chartPanel chart;
							
							try {
								//Initialise the new subjects and their respective timers
								Timer timer1 = new Timer();
						    	Timer timer2 = new Timer();
						    	Timer timer3 = new Timer();
								timer1.schedule(cholesterolModel = new cholestrolModel(tbc),0,10000);
						        timer2.schedule(bpModel = new bloodPressureModel(tbc),0,10000);
						        timer3.schedule(tobaccoModel = new tobaccoModel(tbc),0,10000);
						        
						        //HashMap to store the Monitoring Models
						        HashMap<String, MonitorModel> modelList = new HashMap<String, MonitorModel>();
						        
						        CheckList checkList = new CheckList();
								checkList.setModelListCholestrol(false);
								checkList.setModelListDiastolicBP(false);
								checkList.setModelListSystolicBP(false);
								checkList.setModelListTobacco(false);
								modelList.put("cholestrol", cholesterolModel);
						        modelList.put("bloodpressure",bpModel);
						        modelList.put("tobacco", tobaccoModel);
								//Default threshold for all parameters. Detailed thresholds are implemented within each model.
								Double threshold = 110.00;
								chart = new practitioner_chartPanel(tbc, checkList, threshold, modelList);
								chart.setStateChange(true);
								cholesterolModel.attachModel(tbc, chart);
								bpModel.attachModel(tbc, chart);
								tobaccoModel.attachModel(tbc, chart);
							
								new_panel.add(chart);
								JButton deleteButton = new JButton();
								deleteButton.setText("Stop Monitoring");
								new_panel.add(deleteButton);
		
								deleteButton.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent ae) {
										cholesterolModel.detachModel(userName);
										bpModel.detachModel(userName);
										tobaccoModel.detachModel(userName);
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
									}
								});
								
								JCheckBox checkBoxCholesterol = new JCheckBox();
								checkBoxCholesterol.setText("Enable Cholesterol");
								checkBoxCholesterol.setSelected(false);
								checkBoxCholesterol.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										if(checkBoxCholesterol.isSelected() == true) {
											chart.checkList.setModelListCholestrol(true);
										} else {
											chart.checkList.setModelListCholestrol(false);
										}
										chart.update();
										chart.repaint();
									}
								});
								
								JCheckBox checkBoxDiastolicBP = new JCheckBox();
								checkBoxDiastolicBP.setText("Enable Diastolic Blood Pressure");
								checkBoxDiastolicBP.setSelected(false);
								checkBoxDiastolicBP.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										if(checkBoxDiastolicBP.isSelected() == true) {
											checkList.setModelListDiastolicBP(true);
										} else {
											checkList.setModelListDiastolicBP(false);
										}
										chart.update();
										chart.repaint();		
									}
								});
								
								JCheckBox checkBoxSystolicBP = new JCheckBox();
								checkBoxSystolicBP.setText("Enable Systolic Blood Pressure");
								checkBoxSystolicBP.setSelected(false);
								checkBoxSystolicBP.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										if(checkBoxSystolicBP.isSelected() == true) {
											checkList.setModelListSystolicBP(true);
										} else {
											checkList.setModelListSystolicBP(false);
										}
										chart.update();
										chart.repaint();	
									}
								});
								
								JCheckBox checkBoxTobaccoUsage = new JCheckBox();
								checkBoxTobaccoUsage.setText("Tobacco Usage");
								checkBoxTobaccoUsage.setSelected(false);
								checkBoxTobaccoUsage.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										if(checkBoxTobaccoUsage.isSelected() == true) {
											checkList.setModelListTobacco(true);
										} else {
											checkList.setModelListTobacco(false);
										}
										chart.update();
										chart.repaint();	
									}
								});
								
								new_panel.add(checkBoxCholesterol);
								new_panel.add(checkBoxDiastolicBP);
								new_panel.add(checkBoxSystolicBP);
								new_panel.add(checkBoxTobaccoUsage);
									
								
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
				add(patientIdField, BorderLayout.PAGE_START);
				add(button_panel, BorderLayout.PAGE_END);
				add(tab, BorderLayout.CENTER);
				setVisible(true);
				setSize(400,400);
			}
}


