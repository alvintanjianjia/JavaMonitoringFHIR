package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;
import org.json.JSONException;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

public class practitioner_chartPanel extends JPanel implements chartMonitor{
	
	
	private boolean chartStateChange = false;
    private JFreeChart chart;
    private XYDataset data;
	private ChartPanel panel;
	TimeSeriesCollection datasetMain = new TimeSeriesCollection();
	CheckList checkList = null;
	private XYPlot Xyplot;
	private TextTitle pastTobaccoUsage = null;
	private Integer smoking_data = 1;
	private tobaccoModel tobaccoModel;
	private cholestrolModel cholesterolModel;
	private bloodPressureModel bpModel;
	private boolean stateChange;
	private String userName;
	private Double threshold;
	

    //Constructor and initialise all of the observers(i.e. charts)
    public practitioner_chartPanel(String userName, CheckList checkList, Double threshold, Map<String, MonitorModel> modelList) throws JSONException {
    	Timer timer1 = new Timer();
    	Timer timer2 = new Timer();
    	Timer timer3 = new Timer();
       
    	TimeSeriesCollection datasetMain = new TimeSeriesCollection();
    	this.checkList = checkList;
    	this.userName = userName;
    	this.threshold = threshold;
    	
    	this.data = null;
    	this.cholesterolModel = (cholestrolModel) modelList.get("cholestrol");
    	this.bpModel = (bloodPressureModel) modelList.get("bloodpressure");
    	this.tobaccoModel = (tobaccoModel) modelList.get("tobacco");
    	
		chart = ChartFactory.createTimeSeriesChart("Patient Data", "yyyy-mm-dd hh:mm:ss", "Values", data, true, true, false);
		
        XYPlot plot = chart.getXYPlot();
        ValueAxis domain = plot.getDomainAxis();
        domain.setAutoRange(true);
        ValueAxis range = plot.getRangeAxis();
        setLayout(new BorderLayout());
        TextTitle tobaccoUsage = new TextTitle(((tobaccoModel) modelList.get("tobacco")).getPatientTobaccoUsage(userName));
        this.pastTobaccoUsage = tobaccoUsage;
        chart.addSubtitle(tobaccoUsage);
        panel = new ChartPanel(chart);
        
        add(panel);
        
    }
    
    
    
   
    //Function for creating a new dataset based on available data.
    public XYDataset createDataset(String userName, String type, List[] raw_data, TimeSeriesCollection currentData, Double threshold) throws JSONException {
    	
    	if (raw_data == null) {
    		return currentData;
    	}
        else{
        	List<String> timestamp = raw_data[0];
            List<Double> values = raw_data[1];
            
            //Creating a TimeSeries by second
            TimeSeries s1 = new TimeSeries(type, Second.class);
            for (int i=0;i<values.size();i++) {
            	String[] parts = timestamp.get(i).split("T");
            	
            	//Regex for date
            	String[] date_parts = parts[0].split("-");
            	int year = Integer.parseInt(date_parts[0]);
            	int month = Integer.parseInt(date_parts[1]);
            	int day = Integer.parseInt(date_parts[2]);
            	
            	//Regex for time
            	String[] time = parts[1].split("\\+");
            	String[] time_parts = time[0].split("\\:");
            	int hour = Integer.parseInt(time_parts[0]);
            	int minute = Integer.parseInt(time_parts[1]);
            	int second = Integer.parseInt(time_parts[2]);
            	s1.add(new Second(second, minute, hour, day, month, year), values.get(i));
            }
            currentData.addSeries(s1);
            if (values.get(values.size()-1) > threshold) {
            	systemAlert("Emergency for " + userName + " " + "for " + type + " at value "  + values.get(values.size()-1));
            }
        }
       
        return currentData;
    }
    
    @Override
    //Updates the data for every refresh called
    public XYDataset updateData(String userName, Double threshold) throws JSONException {
    	if (this.chartStateChange) {
        	this.data = null;
        	TimeSeriesCollection datasetMain = new TimeSeriesCollection();
        	if (checkList.getModelListCholestrol() == true) {
        		System.out.println("updateData called3");
        		List[] cholesterol_data = cholesterolModel.getPatientCholestrolData();
        		if (cholesterol_data.length != 0) { 
        			this.data = createDataset(userName, "cholesterol", cholesterol_data, datasetMain, cholesterolModel.getCholestrolThreshold());
        		}
        	}
        	
        	if (checkList.getModelListDiastolicBP() == true) {
        		List[] bp_data_diastolic = bpModel.getPatientDiastolicData();
        		if (bp_data_diastolic.length != 0) {
        			this.data = createDataset(userName, "bp_diastolic", bp_data_diastolic, datasetMain, bpModel.getDiastolicThreshold());
        		}
        	}
        	
        	if (checkList.getModelListSystolicBP() == true) {
        		List[] bp_data_systolic = bpModel.getPatientSystolicData();
        		if (bp_data_systolic.length != 0) {
        			this.data = createDataset(userName, "bp_systolic", bp_data_systolic, datasetMain, bpModel.getSystolicThreshold());
        		}
        		
        	}
        	
        	if (checkList.getModelListTobacco()) {
        		List[] tobacco_usage = tobaccoModel.getPatientTobaccoData();
        		if (tobacco_usage.length != 0) {
        			this.data = createDataset(userName, "tobacco_usage", tobacco_usage, datasetMain, 10000.00);
        		}
        		
        	}
        	
        	this.chart.getXYPlot().setDataset(data);
        	TextTitle tobaccoUsage = new TextTitle(tobaccoModel.getPatientTobaccoUsageString());
        	this.chart.removeSubtitle(this.pastTobaccoUsage);
            this.chart.addSubtitle(tobaccoUsage);
            this.chart.fireChartChanged();
            this.Xyplot = chart.getXYPlot();
            ValueAxis domain = Xyplot.getDomainAxis();
            domain.setAutoRange(true);
            ValueAxis range = Xyplot.getRangeAxis();
            setLayout(new BorderLayout());
            panel = new ChartPanel(chart);
            add(panel);
            this.chartStateChange = false;
    	}
    	
    	return data;
    	
    	
    }
    
    @Override
    public cholestrolModel getModel() {
        return cholesterolModel;
    }
    
    
	@Override
	//Throws practitioner System Alert
	public void systemAlert(String alertMsg) { 
		practitioner_systemAlert alert = new practitioner_systemAlert();
		alert.systemAlertFrame(alertMsg);
	}

	
	

	public boolean getState() {
    	return this.chartStateChange;
    }
    
	
	public void setStateChange(boolean stateChange) {
		this.chartStateChange = stateChange;
	}
	
	@Override
    public void update() {
		//This function calls the update function for the observer
		//This notifies the observer that there is a change in the data and that it should pull the new data
		System.out.println(this.cholesterolModel);
    	if (this.cholesterolModel.getState() == true) {
			setStateChange(true);
		} else if (this.bpModel.getState() == true) {
			setStateChange(true);
		} else if (this.tobaccoModel.getState() == true) {
			setStateChange(true);
		}
    	try {
			this.updateData(userName,  threshold);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }









	

	

	
}