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

public class ContourSettingsPopup extends PopupWindow{
	private RoadGISApplication app = null;
	

	JTextField txtImgResol;    // edit contour image resolution setting
	JTextField txtMapScale;    // edit contour map scale setting

	public ContourSettingsPopup(RoadGISApplication app) {
		this.app = app;
	}
	public void build() {
		// open a frame
				popup = new RoadGISPopup("Contour Image Settings") ;
				//frame.setLocation(350,60) ;
				popup.setSize(200,150) ;
				popup.setCenter() ;
				popup.validate() ;
				popup.setVisible(true) ;
				popup.setResizable(false);
				KeyAdapter frame_listener = new KeyAdapter() {
					public void keyTyped(KeyEvent ke) {
					}
					public void keyPressed(KeyEvent ke) {
					}        
					public void keyReleased(KeyEvent ke) {
					}        
				} ;
				// save changes
				ActionListener frame_ok_listener = new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						float val;
						val = new Float(txtImgResol.getText()).floatValue();
						app.getHorizontalDesign().getRoadDesign().setContourImageResolution(val) ;
						val = new Float(txtMapScale.getText()).floatValue();
						app.getHorizontalDesign().getRoadDesign().setContourScale(val) ;
						app.getHorizontalDesign().getRoadDesign().setImageScale((float)app.getHorizontalDesign().getRoadDesign().getContourImageResolution() / (float)app.getHorizontalDesign().getRoadDesign().getContourScale());  //  // pixel/ft

						popup.dispose() ;
						//repaint() ;
					}
				} ;
				ActionListener frame_cancel_listener = new ActionListener() {
					public void actionPerformed(ActionEvent aev) {
						popup.dispose() ;
						//repaint() ;
					}
				} ;
				String unitStr="", unitStr1="";
				if (app.getHorizontalDesign().getRoadDesign().getPreferredUnit() == 1) {
					unitStr = "pixel/in" ;
					unitStr1 = "ft/in" ;
				} else if (app.getHorizontalDesign().getRoadDesign().getPreferredUnit() == 2) { 
					unitStr = "pixel/cm" ;
					unitStr1 = "m/cm" ;
				}

				popup.setLayout(new GridBagLayout()) ;
				// Create a constrains object, and specify default values
				GridBagConstraints c = new GridBagConstraints() ;
				c.fill = GridBagConstraints.BOTH ; // component grows in both directions
				c.weightx = 1.0 ; c.weighty = 1.0 ;

				c.gridx = 0 ; c.gridy = 0; c.gridwidth = 1 ; c.gridheight = 1 ;
				c.insets = new Insets(5,5,0,5) ; // 5-pixel margins on all sides
				JLabel lblimg = new JLabel("Image Rosolution");
				txtImgResol= new JTextField(new Float(app.getHorizontalDesign().getRoadDesign().getContourImageResolution()).toString()) ;
				//txtImgResol.setFont(new Font("SansSerif", Font.PLAIN , 12)) ;
				//txtImgResol.setForeground(new Color(0,0,218)) ;
				//frame.setBackground(new Color(200, 200, 200)) ;
				popup.add(lblimg, c) ;
				c.gridx = 0 ; c.gridy = 1; c.gridwidth = 1 ;
				c.insets = new Insets(1,5,0,5) ;
				popup.add(txtImgResol,c) ;
				c.gridx = 1 ; c.gridy = 1; c.gridwidth = 1 ;
				popup.add(new JLabel(unitStr),c) ;

				c.gridx = 0 ; c.gridy = 2; c.gridwidth = 1 ; c.gridheight = 1 ;
				c.insets = new Insets(5,5,0,5) ; // 5-pixel margins on all sides
				JLabel lblmap = new JLabel("Map Scale");
				txtMapScale= new JTextField(new Float(app.getHorizontalDesign().getRoadDesign().getContourScale()).toString()) ;
				popup.add(lblmap, c) ; 
				c.gridx = 0 ; c.gridy = 3; c.gridwidth = 1 ;
				c.insets = new Insets(1,5,0,5) ;
				popup.add(txtMapScale,c) ;
				c.gridx = 1 ; c.gridy = 3; c.gridwidth = 1 ;
				popup.add(new JLabel(unitStr1),c) ;

				c.gridx = 0 ; c.gridy = 4; c.gridwidth = 1 ;
				c.insets = new Insets(5,5,5,5) ;
				JButton btn_ok = new JButton(" OK ") ;
				popup.add(btn_ok,c) ;
				c.gridx = 1 ; c.gridy = 4; c.gridwidth = 1 ;
				JButton btn_cancel = new JButton(" Cancel ") ;
				popup.add(btn_cancel,c) ;

				btn_ok.addActionListener(frame_ok_listener) ;
				btn_cancel.addActionListener(frame_cancel_listener) ;
	}

}
