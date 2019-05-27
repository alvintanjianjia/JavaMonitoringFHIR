package GUI;

import javax.swing.JFrame;
import javax.swing.JLabel;

public interface systemAlert {
	public default void systemAlertFrame(String alertMsg) { 
		JFrame alertFrame = new JFrame();
		JLabel label = new JLabel(alertMsg);
		alertFrame.add(label);
		alertFrame.setVisible(true);
		alertFrame.setSize(800,200);
	}
}
