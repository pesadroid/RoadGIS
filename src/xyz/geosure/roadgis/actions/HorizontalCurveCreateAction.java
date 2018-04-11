package xyz.geosure.roadgis.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.CursorTool;

import xyz.geosure.roadgis.RoadGISApplication;


@SuppressWarnings("serial")
public class HorizontalCurveCreateAction extends AbstractAction implements ActionListener{ 
	private RoadGISApplication app = null;

	public HorizontalCurveCreateAction(RoadGISApplication app) {
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

						if(!app.getHorizontalDesign().curveHasStarted()) {
							app.getHorizontalDesign().setMidpointFlag(false);
							app.getHorizontalDesign().setStartPoint(ev.getWorldPos());
							app.getHorizontalDesign().setCurveStarted(true);
						}else if(!app.getHorizontalDesign().midpointFlagIsHigh()) {
							app.getHorizontalDesign().setMidPoint(ev.getWorldPos());
							app.getHorizontalDesign().setMidpointFlag(true);
						}else {
							app.getHorizontalDesign().setEndPoint(ev.getWorldPos());
							app.getHorizontalDesign().drawCurve();
							app.getHorizontalDesign().setCurveStarted(false);
						}
						DirectPosition2D p = ev.getWorldPos();
						app.getHorizontalDesign().drawMyPoint(p.getX(), p.getY());
					}
				});
	} // actionPerformed
} // ActionListener
