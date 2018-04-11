package xyz.geosure.roadgis.actions;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;

import xyz.geosure.roadgis.RoadGISApplication;
import xyz.geosure.roadgis.views.RoadGISPopup;
import xyz.geosure.roadgis.views.aboutTextbox;


@SuppressWarnings("serial")
public class HelpAboutAction extends AbstractAction implements ActionListener{ 
	private RoadGISApplication app = null;

	public HelpAboutAction(RoadGISApplication app) {
		this.app = app;
	}

	@Override
	public void actionPerformed(ActionEvent aev) {
		RoadGISPopup frmAbout = new RoadGISPopup() ; ;                 // help about screen

		if (frmAbout.isShowing()==false) {
			frmAbout = new RoadGISPopup("About ROAD") ;
			frmAbout.setSize(300, 140) ;
			frmAbout.setResizable(false);
			//frmAbout.setLocation(100,100) ;
			frmAbout.show() ;
			frmAbout.setCenter() ;

			frmAbout.setLayout(new BorderLayout(0,0));
			Panel textboxp = new Panel();
			textboxp.setLayout(new BorderLayout(0,0));
			textboxp.add("Center",new aboutTextbox()); 

			Panel about = new Panel();
			about.setBackground(Color.white);
			about.setLayout(new BorderLayout(1,1));
			about.add("Center",textboxp);
			frmAbout.add(about);
			frmAbout.invalidate() ;
			frmAbout.setVisible(true) ;
		}
		else {
			frmAbout.show();
		}
	} // actionPerformed
} // ActionListener
