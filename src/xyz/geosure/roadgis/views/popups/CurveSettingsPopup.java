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

public class CurveSettingsPopup extends PopupWindow{
	private RoadGISApplication app = null;
	

	// Java GUI
	// design dettings
	JTextField txtRadius;    // curve radius setting
	float calcMinRadius ;       // calculated minimum radius (Rv)
	

	public CurveSettingsPopup(RoadGISApplication app, float radius) {
		this.app = app;
		this.calcMinRadius = radius;
	}
	public void build() {
		// open a frame
					popup = new RoadGISPopup("Curve Settings") ;
					popup.setSize(200,120) ;
					//frame.setCenter() ;
					popup.validate() ;
					popup.setVisible(true) ;
					popup.setResizable(false);
					popup.setLocation(250,2) ;

					KeyAdapter frame_radius_listener = new KeyAdapter() {
						public void keyTyped(KeyEvent ke) {
						}
						public void keyPressed(KeyEvent ke) {
						}        
						public void keyReleased(KeyEvent ke) {
							String str = txtRadius.getText();
							float val = new Float(str).floatValue();
							app.getHorizontalDesign().getRoadDesign().setCurveRadius(val);
							//System.out.println(str + ", val=" + new Float(val).toString());
						}        
					} ;
					// check min radius
					ActionListener frame_chk_listener = new ActionListener() {
						public void actionPerformed(ActionEvent aev) {
							float Rv, spd, val ;
							if (app.getHorizontalDesign().getRoadDesign().getPreferredUnit()==1){
								spd = (app.getHorizontalDesign().getRoadDesign().getSpeedLimit() * 1.467f);
								calcMinRadius = spd*spd / 32.2f / (app.getHorizontalDesign().getRoadDesign().getSideFrictionCoef() + app.getHorizontalDesign().getRoadDesign().getMaxSuperelevation());
								// or using MPH speed, AASHTO 2004, pp.146 Eq 3-10
								//calcMinRadius = (speedLimit ^ 2) / (15 * (sideFrictionCoef + maxSuperelevation))
							} else if (app.getHorizontalDesign().getRoadDesign().getPreferredUnit()==2) {
								spd = app.getHorizontalDesign().getRoadDesign().getSpeedLimit();
								// or using MPH speed, AASHTO 2004, pp.146 Eq 3-10
								calcMinRadius = (spd*spd) / (127f * (app.getHorizontalDesign().getRoadDesign().getSideFrictionCoef() + app.getHorizontalDesign().getRoadDesign().getMaxSuperelevation()));
							}
							val = new Float(txtRadius.getText()).floatValue();
							String str_Rv = new Float(calcMinRadius).toString();
							if (calcMinRadius > val) { 
								app.getHorizontalDesign().popUpdateCurveRadius();
							} else {
								app.getHorizontalDesign().popMessageBox("Check Minimum Radius","Design radius greater than \nminimum radius " + str_Rv + ". OK!");
							}

							//frame.dispose() ;
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
					txtRadius= new JTextField(new Float(app.getHorizontalDesign().getRoadDesign().getMinHCurveRadius()).toString()) ;
					//txtRadius.setFont(new Font("SansSerif", Font.PLAIN , 12)) ;
					//txtRadius.setForeground(new Color(0,0,218)) ;
					//frame.setBackground(new Color(200, 200, 200)) ;
					popup.add(lblRadius, c) ;
					c.gridx = 0 ; c.gridy = 1; c.gridwidth = 1 ;
					c.insets = new Insets(1,5,0,5) ;
					popup.add(txtRadius,c) ;
					c.gridx = 1 ; c.gridy = 1; c.gridwidth = 1 ;
					popup.add(new JLabel(unitStr),c) ;
					c.gridx = 0 ; c.gridy = 2; c.gridwidth = 2 ;
					c.insets = new Insets(5,5,5,5) ;
					JButton btn_chk = new JButton("Check Minimum Radius") ;
					popup.add(btn_chk,c) ;

					txtRadius.addKeyListener(frame_radius_listener);
					btn_chk.addActionListener(frame_chk_listener) ;

	}

}
