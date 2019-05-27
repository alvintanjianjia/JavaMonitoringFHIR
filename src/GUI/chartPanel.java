package GUI;

import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.json.JSONException;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;


public class chartPanel extends JPanel implements chartMonitor {
	private cholestrolModel model;
    private JFreeChart chart;

    //To plot the charts
    public chartPanel(cholestrolModel model, String userName) throws JSONException {
    	this.model = model;
    	XYDataset data = createDataset(userName);
    	JFreeChart chart = ChartFactory.createTimeSeriesChart("Cholestrol Level", "yyyy-mm-dd hh:mm:ss", "Cholestrol Level", data, true, true, false);
        XYPlot plot = chart.getXYPlot();
        ValueAxis domain = plot.getDomainAxis();
        domain.setAutoRange(true);
        ValueAxis range = plot.getRangeAxis();
        setLayout(new BorderLayout());
        ChartPanel panel = new ChartPanel(chart);
        add(panel);
    }
    
    //Function to read and create Dataset
    public XYDataset createDataset(String userName) throws JSONException {
    	TimeSeriesCollection dataset = new TimeSeriesCollection();
        List[] data = model.getPatientCholestrol(userName);
        if (data.length == 0) {
        	return dataset;
        }
        List<String> timestamp = data[0];
        List<Double> values = data[1];
        System.out.println(timestamp.size());
        System.out.println(values.size());
        
        //Creating a TimeSeries by second
        TimeSeries s1 = new TimeSeries("Series 1", Second.class);
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
        	System.out.print(i);
        }
     
        dataset.addSeries(s1);
        return dataset;
    }
    

    @Override
    public cholestrolModel getModel() {
        return model;
    }
    
	@Override
	//Updates the data for every refresh called
	public void updateData(String userName) throws JSONException {
		this.model = model;
    	XYDataset data = createDataset(userName);
    	JFreeChart chart = ChartFactory.createTimeSeriesChart("Cholestrol Level", "yyyy-mm-dd hh:mm:ss", "Cholestrol Level", data, true, true, false);
        XYPlot plot = chart.getXYPlot();
        ValueAxis domain = plot.getDomainAxis();
        domain.setAutoRange(true);
        ValueAxis range = plot.getRangeAxis();
        setLayout(new BorderLayout());
        ChartPanel panel = new ChartPanel(chart);
        add(panel);
	}
    
}
