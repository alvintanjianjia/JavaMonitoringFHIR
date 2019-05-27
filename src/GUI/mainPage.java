package GUI;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Patient;

public class mainPage extends JFrame{
	JPanel panel;
	JLabel user_label;
	JButton patient, practitioner;
	JFrame mainFrame;
	
	//Constructor for Main Page (Choosing Patient or Practitioner before login)
	mainPage() {
		
		patient = new JButton("Patient");
		patient.putClientProperty("User", "Patient");
		practitioner = new JButton("Practitioner");
		practitioner.putClientProperty("User", "Practitioner");
		
		patient.addActionListener(new ActionListener(){	
			@Override
			public void actionPerformed(ActionEvent ae) {
				panel.setVisible(false);
				dispose();
				JFrame loginFrame = new login((String) patient.getClientProperty("User"));
			}
		});
		
		practitioner.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent ae) {
				panel.setVisible(false);
				dispose();
				JFrame loginFrame = new login((String) practitioner.getClientProperty("User"));
			}
		});
		
		panel = new JPanel(new GridLayout(3, 1));
		panel.add(patient);
		panel.add(practitioner);
		add(panel);
        setTitle("Please Login Here !");
        setSize(400, 100);
        setVisible(true);
	}
}
