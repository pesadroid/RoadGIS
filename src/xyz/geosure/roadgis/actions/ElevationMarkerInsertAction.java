package xyz.geosure.roadgis.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.CursorTool;

import xyz.geosure.roadgis.RoadGISApplication;

public class ElevationMarkerInsertAction extends AbstractAction implements ActionListener{ 
	private RoadGISApplication app = null;

	public ElevationMarkerInsertAction(RoadGISApplication app) {
		this.app = app;
	}

	@Override
	public void actionPerformed(ActionEvent aev) {
		app.getMapFrame().getMapPane().setCursorTool(
				new CursorTool() {

					@Override
					public void onMouseClicked(MapMouseEvent ev) {
						//selectFeatures(ev);
						//setCurrentCommand(CURVE);

						//selectFeatures(ev);
						DirectPosition2D position = ev.getWorldPos();

						if(!app.getHorizontalDesign().lineHasStarted()) {
							app.getHorizontalDesign().setStartPoint(position);
							app.getHorizontalDesign().setLineStarted(true);
						}else {
							app.getHorizontalDesign().setEndPoint(position);
							app.getHorizontalDesign().drawLine();
							app.getHorizontalDesign().setLineStarted(false);
						}
						app.getHorizontalDesign().drawMyPoint(position.getX(), position.getY());
					}
				});
	} // actionPerformed
} // ActionListener
