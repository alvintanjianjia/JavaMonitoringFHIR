package GUI;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.client.IGenericClient;



import org.hl7.fhir.dstu3.model.*;

public class login extends JFrame {
	JPanel panel;
    JLabel user_label, password_label, message;
    JTextField userName_text;
    JPasswordField password_text;
    JButton submit, cancel;

    login(String userType) {
        
        // User Label
        user_label = new JLabel();
        user_label.setText("User Name :");
        userName_text = new JTextField();
        
        // Password

        password_label = new JLabel();
        password_label.setText("Password :");
        password_text = new JPasswordField();

        // Submit

        submit = new JButton("Login");
        submit.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent ae) {
				
				String userName = userName_text.getText();
				String password = password_text.getText();
				int status = 0;
				try {
					URL url = new URL("http://hapi-fhir.erc.monash.edu:8080/baseDstu3/" + userType + "/" + userName);
					System.out.println("http://hapi-fhir.erc.monash.edu:8080/baseDstu3/" + userType + "/" + userName);
					HttpURLConnection con = (HttpURLConnection) url.openConnection();
					con.setRequestMethod("GET");
					
					status = con.getResponseCode();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// Create a client (only needed once)
				
				/*
				FhirContext ctx = FhirContext.forDstu3();
				IGenericClient client = ctx.newRestfulGenericClient("http://hapi-fhir.erc.monash.edu:8080/baseDstu3/" + userType + "/" + userName);
				ctx.getRestfulClientFactory().setConnectTimeout(60 * 1000);
		        ctx.getRestfulClientFactory().setSocketTimeout(60 * 1000);
		        try {
				// Invoke the client
				Bundle response = client.search()
						.forResource(Patient.class)
						.where(Patient.FAMILY.matches().values(userName))
						.where(Patient..matches().values(userName))
						.returnBundle(Bundle.class)
						.execute();
		        } catch (Exception e) {
		        	e.printStackTrace();
		        }
		        */
				
				
				// Check for account credentials here
		        if (status == 200) {
		            message.setText(" Hello " + userName + "");
		            dispose();
		            if (userType == "Patient") {
		            	JFrame dashboardFrame = new patient_dashboard((String) userName);
		            } else if (userType == "Practitioner") {
		            	JFrame dashboardFrame = new practitioner_dashboard((String) userName);
		            }
		         
		        } else {
		            message.setText(" Wrong username or password.. ");
		        }
				
				
			}
		});
        cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent ae) {
				panel.setVisible(false);
				dispose();
				JFrame mainFrame = new mainPage();
			}
		});
        
        panel = new JPanel(new GridLayout(4, 1));
        panel.add(user_label);
        panel.add(userName_text);
        panel.add(password_label);
        panel.add(password_text);

        message = new JLabel();
        
        panel.add(submit);
        panel.add(message);
        panel.add(cancel);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Adding the listeners to components..
        
        add(panel);
        setTitle("Please Login Here !");
        setSize(400, 100);
        setVisible(true);

    }

    


}
