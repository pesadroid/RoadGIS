package xyz.geosure.roadgis.views.popups;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import xyz.geosure.roadgis.RoadGISApplication;
import xyz.geosure.roadgis.views.RoadGISPopup;

public class CurveRadiusPopup extends PopupWindow{
	private RoadGISApplication app = null;
	

	// Java GUI
	// design dettings
	JTextField txtEditRadius;    // edit curve radius setting
	JTextField txtRadius;    // curve radius setting
	float calcMinRadius ;       // calculated minimum radius (Rv)
	

	public CurveRadiusPopup(RoadGISApplication app, float radius) {
		this.app = app;
		this.calcMinRadius = radius;
	}
	public void build() {
		// open a frame
					String str_Rv = new Float(calcMinRadius).toString();
		String spdStr = new Float(app.getHorizontalDesign().getRoadDesign().getSpeedLimit()).toString();
		String superEleStr = new Float(app.getHorizontalDesign().getRoadDesign().getMaxSuperelevation() * 100f).toString();
		String unitStr = "";
		if (app.getHorizontalDesign().getRoadDesign().getPreferredUnit()==1) {
			unitStr = " (MPH) ";
		} else if (app.getHorizontalDesign().getRoadDesign().getPreferredUnit()==2) {
			unitStr = " (Km/h) ";
		} 
		String message = "Minimum Radius " + str_Rv + " required for maximum \nspeed " + 
				spdStr + unitStr + "and superelevation " + superEleStr + "%. \nUse minimum radius?" ;
		// open a frame
		popup = new RoadGISPopup("Check Minimum Radius") ;
		popup.setLocation(300,10) ;
		popup.setSize(350,150) ;
		popup.validate() ;
		popup.setVisible(true) ;

		ActionListener frame_yes_listener = new ActionListener() {
			public void actionPerformed(ActionEvent aev) {
				app.getHorizontalDesign().getRoadDesign().setCurveRadius(calcMinRadius);
				String str_Rv = new Float(calcMinRadius).toString();
				txtRadius.setText(str_Rv);

				popup.dispose() ;
				//repaint();
			}
		} ;
		ActionListener frame_no_listener = new ActionListener() {
			public void actionPerformed(ActionEvent aev) {
				popup.dispose() ;
			}
		} ;

		popup.setLayout(new GridBagLayout()) ;
		// Create a constrains object, and specify default values
		GridBagConstraints c = new GridBagConstraints() ;
		c.fill = GridBagConstraints.BOTH ; // component grows in both directions
		c.weightx = 1.0 ; c.weighty = 1.0 ;

		c.gridx = 0 ; c.gridy = 0; c.gridwidth = 2 ; c.gridheight = 1 ;
		c.insets = new Insets(5,5,5,5) ; // 5-pixel margins on all sides
		JTextArea myMsg = new JTextArea(message,4,40) ;
		//myMsg.setFont(new Font("SansSerif", Font.PLAIN , 12)) ;
		//myMsg.setForeground(new Color(0,0,218)) ;
		//frame.setBackground(new Color(200, 200, 200)) ;
		popup.add(myMsg,c) ;
		c.insets = new Insets(0,5,5,5) ;
		c.gridx = 0 ; c.gridy = 1; c.gridwidth = 1 ;
		JButton btn_ok = new JButton(" Yes ") ;
		popup.add(btn_ok, c) ;
		btn_ok.addActionListener(frame_yes_listener) ;
		c.gridx = 1 ; c.gridy = 1;
		JButton btn_no = new JButton(" No ") ;
		popup.add(btn_no, c) ;
		btn_no.addActionListener(frame_no_listener) ;

	}

}
