package GUI;

import java.util.List;
import org.json.JSONException;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
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

//Interface for monitors
public interface chartMonitor {
	
	//Getting the model for the type of monitoring. (
	public cholestrolModel getModel();
	
	//Update data for the model
	//@parameter username is the patient username / id 
	//@parameter threshold is the amount to set before the alert message is sent
	public XYDataset updateData(String userName, Double threshold) throws JSONException;
	
	//Throw system alert
	public default void systemAlert(String alertMsg) { }

	
	//Update function for observers
	public default void update() {} 
	
}
