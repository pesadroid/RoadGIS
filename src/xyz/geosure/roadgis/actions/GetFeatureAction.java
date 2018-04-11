package xyz.geosure.roadgis.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;

import xyz.geosure.roadgis.RoadGISApplication;


@SuppressWarnings("serial")
public class GetFeatureAction extends AbstractAction implements ActionListener{ 
	private RoadGISApplication app = null;

	public GetFeatureAction(RoadGISApplication app) {
		this.app = app;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		//setToolInEffect(GETFEATURE);	
	}
}