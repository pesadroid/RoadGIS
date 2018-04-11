package xyz.geosure.roadgis.views.popups;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;

import xyz.geosure.roadgis.RoadGISApplication;
import xyz.geosure.roadgis.views.RoadGISPopup;
import xyz.geosure.roadgis.views.myIcon;

public class ClearAllPopup extends PopupWindow{
	private RoadGISApplication app = null;
	private myIcon iconQ = new myIcon("question_mark") ;



	public ClearAllPopup(RoadGISApplication app) {
		this.app = app;
	}
	public void build() {
		// open a frame
		popup = new RoadGISPopup("Clear All") ;
		//frame.setLocation(400,200) ;
		popup.setSize(300,120) ;
		popup.setCenter() ;
		popup.validate() ;
		popup.setVisible(true) ;

		ActionListener frame_msgbox_yes_listener = new ActionListener() {
			public void actionPerformed(ActionEvent aev) {
				/*
		                     int i=0 ;
		                    toolbarIndex=0 ;                    
		                    // clear all DBs
		                    for (i=0; i<myDB.hAlignMarkCount; i++) {
		                        myDB.gethAlignMarks()[i].RESET();
		                    }
		                    for (i=0; i<myDB.getElevationMarkCount(); i++) {
		                        myDB.getElevationMarks()[i].RESET();
		                    }
		                    for (i=0; i<hRoadDataCount;i++) {
		                        myDB.gethRoadData()[i].RESET();
		                    }
				 */
				app.getHorizontalDesign().sethRoadDataCount(0);
				//app.getHorizontalDesign().getRoadDesign().setHorizontalAlignmentMarkCount(0);
				//app.getHorizontalDesign().getRoadDesign().setElevationMarkCount(0);
				app.getHorizontalDesign().push2SegLogBuffer(app.getHorizontalDesign().gethRoadDataCount());
				app.getHorizontalDesign().push2MarkLogBuffer(app.getHorizontalDesign().getRoadDesign().getElevationMarkCount());
				popup.dispose() ;
				//repaint();
			}
		} ;
		ActionListener frame_msgbox_no_listener = new ActionListener() {
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
		popup.add(iconQ, c) ;
		c.gridx = 2 ; c.gridy = 0; c.gridwidth = 4 ; c.gridheight = 1 ;
		JLabel myMsg = new JLabel("Clear All Design Elements") ;
		//myMsg.setFont(new Font("SansSerif", Font.PLAIN , 12)) ;
		//myMsg.setForeground(new Color(0,0,218)) ;
		popup.setBackground(new Color(200, 200, 200)) ;
		popup.add(myMsg,c) ;

		c.gridx = 0 ; c.gridy = 1; c.gridwidth = 1 ;
		popup.add(new JLabel(" "), c) ;
		c.gridx = 1 ; c.gridy = 1; c.gridwidth = 1 ;
		popup.add(new JLabel(" "), c) ;
		c.gridx = 2 ; c.gridy = 1; c.gridwidth = 1 ;
		popup.add(new JLabel(" "), c) ;
		c.gridx = 3 ; c.gridy = 1; c.gridwidth = 1 ;
		popup.add(new JLabel(" "), c) ;
		c.gridx = 4 ; c.gridy = 1; c.gridwidth = 1 ;
		JButton btn_ok = new JButton(" OK ") ;
		popup.add(btn_ok, c) ;
		btn_ok.addActionListener(frame_msgbox_yes_listener) ;
		c.gridx = 5 ; c.gridy = 1;
		JButton btn_no = new JButton(" No ") ;
		popup.add(btn_no, c) ;
		btn_no.addActionListener(frame_msgbox_no_listener) ;
	}

}
