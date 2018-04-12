package xyz.geosure.roadgis.actions;
/*
 * LayerAddOSMAction.java
 * Adds an Opern Street Map Layer to the map
 * 
 * Created on March 21, 2018, Felix Kiptum
 * 
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;

import org.geotools.tile.TileService;
import org.geotools.tile.impl.osm.OSMService;
import org.geotools.tile.util.TileLayer;

import xyz.geosure.roadgis.RoadGISApplication;

@SuppressWarnings("serial")
public class LayerAddOSMAction extends AbstractAction implements ActionListener{ 
	private RoadGISApplication app = null;

	public LayerAddOSMAction(RoadGISApplication app) {
		this.app = app;
	}

	@Override
	public void actionPerformed(ActionEvent event) {

		try
		{
			String baseURL = "http://tile.openstreetmap.org/";
			TileService service = new OSMService("OSM", baseURL);
			TileLayer osm = new TileLayer(service);
			osm.setTitle("Open Street Map");
			
			// you may add to a map:
			app.getMapContent().addLayer(osm);
			int currentPosition = app.getMapContent().layers().indexOf(osm);
			app.getMapContent().moveLayer(currentPosition, 0);//Moves to the Lowest Position to prevent Oclusion
			
			//app.getHorizontalDesign().repaint();
		}catch (Exception e){
			System.out.println("file-import: "+ e.toString()) ;
			e.printStackTrace();
			//do nothing
		} // try
	} // actionPerformed


} // ActionListener
