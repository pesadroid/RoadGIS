package xyz.geosure.roadgis.views.popups;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import xyz.geosure.roadgis.RoadGISApplication;
import xyz.geosure.roadgis.views.RoadGISPopup;

public class CurveSettingsEditPopup extends PopupWindow{

	private RoadGISApplication app = null;
	private int idSegment = 0;

	// Java GUI
	// design dettings
	JTextField txtEditRadius;    // edit curve radius setting
	

	public CurveSettingsEditPopup(RoadGISApplication app, int idsegment) {
		this.app = app;
		this.idSegment = idsegment;
	}
	public void build() {
		// open a frame
		popup = new RoadGISPopup("Edit Curve") ;
		popup.setLocation(450,2) ;
		popup.setSize(200,120) ;
		//frame.setCenter() ;
		popup.validate() ;
		popup.setVisible(true) ;
		popup.setResizable(false);

		KeyAdapter frame_radius_listener = new KeyAdapter() {
			public void keyTyped(KeyEvent ke) {
			}
			public void keyPressed(KeyEvent ke) {
			}        
			public void keyReleased(KeyEvent ke) {
				// String str = txtEditRadius.getText();
				// float val = new Float(str).floatValue();
				// myDB.curveRadius = val;
				//System.out.println(str + ", val=" + new Float(val).toString());
			}        
		} ;
		// OK
		ActionListener frame_ok_listener = new ActionListener() {
			public void actionPerformed(ActionEvent aev) {
				float val ;
				val = new Float(txtEditRadius.getText()).floatValue();
				app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(idSegment).setRadius(val);
				popup.dispose() ;
				//System.out.println("Radius="+ new Float(val).toString());
				//repaint();
			}
		} ;
		// cancel
		ActionListener frame_cancel_listener = new ActionListener() {
			public void actionPerformed(ActionEvent aev) {
				popup.dispose() ;
				//System.out.println("Radius="+ new Float(val).toString());
				//repaint();
			}
		} ;
		String unitStr="";
		if (app.getHorizontalDesign().getRoadDesign().getPreferredUnit() == 1) {
			unitStr = "(ft)" ;
		} else if (app.getHorizontalDesign().getRoadDesign().getPreferredUnit() == 2) { 
			unitStr = "(m)" ;
		}
		//System.out.println("getPreferredUnit()="+myDB.getPreferredUnit());

		popup.setLayout(new GridBagLayout()) ;
		// Create a constrains object, and specify default values
		GridBagConstraints c = new GridBagConstraints() ;
		c.fill = GridBagConstraints.BOTH ; // component grows in both directions
		c.weightx = 1.0 ; c.weighty = 1.0 ;

		c.gridx = 0 ; c.gridy = 0; c.gridwidth = 1 ; c.gridheight = 1 ;
		c.insets = new Insets(5,5,0,5) ; // 5-pixel margins on all sides
		JLabel lblRadius = new JLabel("Radius ");
		txtEditRadius= new JTextField(new Float(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(idSegment).getRadius()).toString()) ;
		//txtEditRadius.setFont(new Font("SansSerif", Font.PLAIN , 12)) ;
		//txtEditRadius.setForeground(new Color(0,0,218)) ;
		//frame.setBackground(new Color(200, 200, 200)) ;
		popup.add(lblRadius, c) ;
		c.gridx = 0 ; c.gridy = 1; c.gridwidth = 1 ;
		c.insets = new Insets(1,5,0,5) ;
		popup.add(txtEditRadius,c) ;
		txtEditRadius.addKeyListener(frame_radius_listener);
		c.gridx = 1 ; c.gridy = 1; c.gridwidth = 1 ;
		popup.add(new JLabel(unitStr),c) ;
		c.gridx = 0 ; c.gridy = 2; c.gridwidth = 1 ;
		c.insets = new Insets(5,5,5,5) ;
		JButton btn_ok = new JButton("Ok") ;
		popup.add(btn_ok,c) ;
		c.gridx = 1 ; c.gridy = 2; c.gridwidth = 1 ;
		JButton btn_cancel = new JButton("Cancel") ;
		popup.add(btn_cancel,c) ;

		btn_ok.addActionListener(frame_ok_listener) ;
		btn_cancel.addActionListener(frame_cancel_listener) ;
	}

}
