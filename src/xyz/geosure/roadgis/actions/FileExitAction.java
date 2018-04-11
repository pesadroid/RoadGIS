package xyz.geosure.roadgis.actions;

import java.awt.Button;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;

import xyz.geosure.roadgis.RoadGISApplication;
import xyz.geosure.roadgis.views.RoadGISPopup;
import xyz.geosure.roadgis.views.myIcon;


@SuppressWarnings("serial")
public class FileExitAction extends AbstractAction implements ActionListener{ 
	private RoadGISApplication app = null;

	public FileExitAction(RoadGISApplication app) {
		this.app = app;
	}

	@Override
	public void actionPerformed(ActionEvent aev) {

		RoadGISPopup frame_saveDesign = new RoadGISPopup() ;
		// open a frame
		myIcon iconQ = new myIcon("question_mark") ;
		
		try
		{
			// open a frame
			frame_saveDesign = new RoadGISPopup("Save Horizontal Design File") ;
			//frame_saveDesign.setLocation(350,150) ;
			frame_saveDesign.setSize(350,120) ;
			frame_saveDesign.setCenter() ;
			frame_saveDesign.validate() ;
			frame_saveDesign.setVisible(true) ;

			FileSaveAction controller = new FileSaveAction(app).setExit(true);


			frame_saveDesign.setLayout(new GridBagLayout()) ;
			// Create a constrains object, and specify default values
			GridBagConstraints c = new GridBagConstraints() ;
			c.fill = GridBagConstraints.BOTH ; // component grows in both directions
			c.weightx = 1.0 ; c.weighty = 1.0 ;

			c.gridx = 0 ; c.gridy = 0; c.gridwidth = 2 ; c.gridheight = 1 ;
			c.insets = new Insets(5,5,5,5) ; // 5-pixel margins on all sides
			frame_saveDesign.add(iconQ,c) ;
			c.gridx = 2 ; c.gridy = 0; c.gridwidth = 4 ; c.gridheight = 1 ;
			Label myMsg = new Label("Do you want to save current horizontal design?") ;
			//myMsg.setFont(new Font("SansSerif", Font.PLAIN , 12)) ;
			//myMsg.setForeground(new Color(0,0,218)) ;
			frame_saveDesign.setBackground(new Color(200, 200, 200)) ;
			frame_saveDesign.add(myMsg,c) ;

			c.gridx = 0 ; c.gridy = 1; c.gridwidth = 1 ;
			frame_saveDesign.add(new Label(" "),c) ;
			c.gridx = 1 ; c.gridy = 1; c.gridwidth = 1 ;
			frame_saveDesign.add(new Label(" "),c) ;
			c.gridx = 2 ; c.gridy = 1; c.gridwidth = 1 ;
			frame_saveDesign.add(new Label(" "),c) ; 
			c.gridx = 3 ; c.gridy = 1; c.gridwidth = 1 ;
			Button btn_ok = new Button(" Yes ") ;
			frame_saveDesign.add(btn_ok, c) ;
			btn_ok.addActionListener(controller.getYesListener()) ;
			c.gridx = 4 ; c.gridy = 1;
			Button btn_no = new Button(" No ") ;
			frame_saveDesign.add(btn_no, c) ;
			btn_no.addActionListener(controller.getNoListener()) ;
			c.gridx = 5 ; c.gridy = 1; c.gridwidth = 1 ;
			Button btn_cancel = new Button("Cancel") ;
			frame_saveDesign.add(btn_cancel,c) ;
			btn_cancel.addActionListener(controller.getCancelListener()) ;
			frame_saveDesign.invalidate();
			frame_saveDesign.show() ;
			frame_saveDesign.toFront() ;
			frame_saveDesign.setResizable(false) ;
		}
		catch (Exception e){
			System.out.println("file-import: "+e.toString()) ;
			e.printStackTrace();
			//do nothing
		} // try
	} // actionPerformed
} // ActionListener
