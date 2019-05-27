package GUI;
import javax.swing.JFrame;

import ca.uhn.fhir.context.FhirContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class App {
	//General driver to start the application.
	public static void main(String[] args) {
		
		
		System.out.println("Starting FIHR App");
		// Create a client (only needed once)
		FhirContext ctx = FhirContext.forDstu3();
		JFrame mainFrame = new mainPage();
		
		//Creation of the main frame
		mainFrame.setSize(500,400);
		mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainFrame.setVisible(true);
	}

}
