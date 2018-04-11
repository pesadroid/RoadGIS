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

public class TangentDeletePopup extends PopupWindow{

	private RoadGISApplication app = null;
	private myIcon iconQ = new myIcon("question_mark") ;


	String caption, message;
	int dataSelIndex = -1 ;

	public TangentDeletePopup(RoadGISApplication app, int index, String caption, String message) {
		this.app = app;
		this.dataSelIndex = index;
		this.caption = caption;
		this.message = message;
	}
	public void build() {
		// open a frame
		popup = new RoadGISPopup(caption) ;
		//frame.setLocation(300,100) ;
		popup.setSize(350,120) ;
		popup.setCenter() ;
		popup.validate() ;
		popup.setVisible(true) ;

		ActionListener frame_yes_listener = new ActionListener() {
			public void actionPerformed(ActionEvent aev) {
				// delete tangent data pair
				removeTangentPair(dataSelIndex) ;
				/*
                int j ;
                if (Math.IEEEremainder(dataSelIndex, 2) == 0) { 
                    // delete i & i+1
                    for (j = dataSelIndex + 2 ; j< myDB.gethAlignMarkCount() ; j++) {
                        myDB.gethAlignMarks()[j - 2] = myDB.gethAlignMarks()[j];
                    }
                } else {
                    // delete i & i-1 
                    for (j = dataSelIndex + 1; j< myDB.gethAlignMarkCount(); j++) {
                        myDB.gethAlignMarks()[j - 2] = myDB.gethAlignMarks()[j];
                    }
                }
                myDB.gethAlignMarkCount() -= 2 ;
				 */
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
		popup.add(iconQ, c) ;

		c.gridx = 2 ; c.gridy = 0; c.gridwidth = 4 ; c.gridheight = 1 ;
		JLabel myMsg = new JLabel(message) ;
		//myMsg.setFont(new Font("SansSerif", Font.PLAIN , 12)) ;
		//myMsg.setForeground(new Color(0,0,218)) ;
		popup.setBackground(new Color(200, 200, 200)) ;
		popup.add(myMsg,c) ;

		c.gridx = 0 ; c.gridy = 1; c.gridwidth = 1 ;
		popup.add(new JLabel(" "),c) ;
		c.gridx = 1 ; c.gridy = 1; c.gridwidth = 1 ;
		popup.add(new JLabel(" "),c) ;
		c.gridx = 2 ; c.gridy = 1; c.gridwidth = 1 ;
		popup.add(new JLabel(" "),c) ;
		c.gridx = 3 ; c.gridy = 1; c.gridwidth = 1 ;
		popup.add(new JLabel(" "),c) ;
		c.gridx = 4 ; c.gridy = 1; c.gridwidth = 1 ;
		JButton btn_ok = new JButton(" Yes ") ;
		popup.add(btn_ok, c) ;
		btn_ok.addActionListener(frame_yes_listener) ;
		c.gridx = 5 ; c.gridy = 1;
		JButton btn_no = new JButton(" No ") ;
		popup.add(btn_no, c) ;
		btn_no.addActionListener(frame_no_listener) ;
	}

	// remove Tangent pair
	public void removeTangentPair(int dataSelIndex) {
		// delete tangent data pair

		app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentMarks().remove(dataSelIndex);
		/* Need to examine if this works. Original Code Below - Felix, 10 Apr 2018 
		int j ;
		if (Math.IEEEremainder(dataSelIndex, 2) == 0) { 
			// delete i & i+1
			for (j = dataSelIndex + 2 ; j< app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentMarkCount() ; j++) {
				app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentMarks()[j - 2] = app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentMarks()[j];
			}
		} else {
			// delete i & i-1 
			for (j = dataSelIndex + 1; j< app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentMarkCount(); j++) {
				app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentMarks()[j - 2] = app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentMarks()[j];
			}
		}
		app.getHorizontalDesign().getRoadDesign().setHorizontalAlignmentMarkCount(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentMarkCount() - 2) ;
		
		//*/        
	}
}
