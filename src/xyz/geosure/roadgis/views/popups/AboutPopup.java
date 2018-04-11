package xyz.geosure.roadgis.views.popups;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;

import xyz.geosure.roadgis.RoadGISApplication;
import xyz.geosure.roadgis.views.RoadGISPopup;
import xyz.geosure.roadgis.views.aboutTextbox;

public class AboutPopup extends PopupWindow{
	private RoadGISApplication app = null;

	public AboutPopup(RoadGISApplication app) {
		this.app = app;
	}
	
	public void build() {
		popup = new RoadGISPopup("About ROAD") ;
		popup.setSize(300, 140) ;
		popup.setResizable(false);
		//frame.setLocation(100,100) ;
		popup.setCenter() ;

		popup.setLayout(new BorderLayout(0,0));
		JPanel textboxp = new JPanel();
		textboxp.setLayout(new BorderLayout(0,0));
		textboxp.add("Center",new aboutTextbox()); 

		JPanel about = new JPanel();
		about.setBackground(Color.white);
		about.setLayout(new BorderLayout(1,1));
		about.add("Center",textboxp);
		popup.add(about);
		
	}
}
