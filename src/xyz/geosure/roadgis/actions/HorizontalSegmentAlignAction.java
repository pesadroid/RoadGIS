package xyz.geosure.roadgis.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.CursorTool;

import com.vividsolutions.jts.geom.Point;

import xyz.geosure.roadgis.RoadGISApplication;
import xyz.geosure.roadgis.model.AlignmentMarker;
import xyz.geosure.roadgis.model.AlignmentMarker.MarkerType;
import xyz.geosure.roadgis.utils.GeoUtils;
import xyz.geosure.roadgis.utils.VectorUtils;


@SuppressWarnings("serial")
public class HorizontalSegmentAlignAction extends AbstractAction implements ActionListener{ 
	private RoadGISApplication app = null;

	public HorizontalSegmentAlignAction(RoadGISApplication app) {
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
						alignMarks();
					}
				});
	} // actionPerformed

	private void alignMarks() {
		int i, selectedCurve=-1 ;
		int[] selectedLines = new int[2];
		int selLineIdx = 0;
		int selCurveIdx = 0;
		for (i=0; i<app.getHorizontalDesign().getHorizontalAlignmentMarkCount(); i++) {
			if (app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).isSelected()) {
				// item selected
				if (app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getRadius() < 0) { 
					// line
					selectedLines[selLineIdx] = i;
					selLineIdx += 1;
				} else {
					// curve
					selectedCurve = i;
					selCurveIdx += 1;
				}
			}
		} // for i

		// check if 2 lines & 1 curve are selected
		if ((selLineIdx == 2) && (selCurveIdx == 1)) { 
			Point pt1;
			Point pt2 ;  // 2 tangent points on the curve
			calculateCurveCenter(selectedLines[0], selectedLines[1], selectedCurve);

			pt1 = calculateTangentPoint(selectedLines[0], selectedCurve); 
			app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(selectedCurve).saveTangentAngle((byte)1, pt1);
			pt2 = calculateTangentPoint(selectedLines[1], selectedCurve); 
			app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(selectedCurve).saveTangentAngle((byte)2, pt2); 

			// save tangent points
			AlignmentMarker startMarker = new AlignmentMarker(pt1, 0.0, selectedCurve, MarkerType.TANGENT);
			AlignmentMarker endMarker = new AlignmentMarker(pt2, 0.0, selectedCurve, MarkerType.TANGENT);
			app.getHorizontalDesign().getRoadDesign().addHorizontalAlignmentMarker(startMarker);
			app.getHorizontalDesign().getRoadDesign().addHorizontalAlignmentMarker(endMarker);

			// unselect segments
			app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(selectedLines[0]).unSelectItem();
			app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(selectedLines[1]).unSelectItem();
			app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(selectedCurve).unSelectItem();

		} else {
			app.getUIActionsHandler().popMessageBox("Horizontal Alignment","Please select 2 linear and 1 curve segments first!");
		}
		//repaint();
		//PictureBox1.Invalidate()
	}   // tool_curvehAlignMarks

	//Calculate the Centre of the Curve
	public void calculateCurveCenter(int lineIndex1, int lineIndex2, int curveIndex) {

		Point p1, p2, p3, p4, pc ;
		double a1, a2, b1, b2 ;
		double c2;
		double c1;
		double rad;
		double L1;
		double L2;
		double den ;
		Point[] Center = new Point[4] ;
		p1 = app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(lineIndex1).getStartPoint();
		p2 = app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(lineIndex1).getEndPoint();
		p3 = app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(lineIndex2).getStartPoint();
		p4 = app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(lineIndex2).getEndPoint();
		pc = app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(curveIndex).getStartPoint();
		rad = app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(curveIndex).getRadius() * app.getHorizontalDesign().getRoadDesign().getImageScale();
		a1 = p2.getY()- p1.getY();
		b1 = p1.getX()- p2.getX();
		a2 = p4.getY()- p3.getY();
		b2 = p3.getX()- p4.getX();
		L1 = VectorUtils.distanceOf(p1, p2);
		L2 = VectorUtils.distanceOf(p3, p4);
		int i ;
		for (i=0; i<4; i++) {
			Center[i] = GeoUtils.makePoint(0,0);
		}
		den = (a1 * b2 - a2 * b1);
		c1 = p2.getX()* p1.getY()- p1.getX()* p2.getY()+ L1 * rad;
		c2 = p4.getX()* p3.getY()- p3.getX()* p4.getY()+ L2 * rad;
		// case 1
		Center[0] = GeoUtils.makePoint( (c2 * b1 - c1 * b2) / den,  -(c2 * a1 - c1 * a2) / den);

		c1 = p2.getX()* p1.getY()- p1.getX()* p2.getY()+ L1 * rad;
		c2 = p4.getX()* p3.getY()- p3.getX()* p4.getY()- L2 * rad;
		// case 2
		Center[1]= GeoUtils.makePoint((c2 * b1 - c1 * b2) / den, -(c2 * a1 - c1 * a2) / den);

		c1 = p2.getX()* p1.getY()- p1.getX()* p2.getY()- L1 * rad;
		c2 = p4.getX()* p3.getY()- p3.getX()* p4.getY()+ L2 * rad;
		// case 3
		Center[2] = GeoUtils.makePoint((c2 * b1 - c1 * b2) / den, -(c2 * a1 - c1 * a2) / den);

		c1 = p2.getX()* p1.getY()- p1.getX()* p2.getY()- L1 * rad;
		c2 = p4.getX()* p3.getY()- p3.getX()* p4.getY()- L2 * rad;
		// case 4
		Center[3] = GeoUtils.makePoint((c2 * b1 - c1 * b2) / den, -(c2 * a1 - c1 * a2) / den);

		int index = 0;
		double min_dist = 99999f;
		double dist ;
		// find the closest one
		for (i=0; i<4; i++) {
			dist = VectorUtils.distanceOf(pc, Center[i]);
			if (dist < min_dist) {
				min_dist = dist;
				index = i;
			}
			//debugWindow.Text &= i & " (Xc, Yc)=" & Center(i).getX()& ", " & Center(i).getY()& " dist=" & dist & vbCrLf
		}
		//debugWindow.Text &= i & " (PXo, PYo)=" & pc.getX()& ", " & pc.getY()& vbCrLf

		if (min_dist < 40 && min_dist >= 0) {    // change from 20 to 40 , 2/12/2007
			app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(curveIndex).updateCurveCenter(Center[index]);
		} else {
			app.getUIActionsHandler().popMessageBox("calculateCurveCenter", "Please move curve closer to line segments!");
		}

	} //calculateCurveCenter


	// calc & return tangent point of a line & a circle
	public Point calculateTangentPoint(int lineIndex, int curveIndex) {
		double dx, dy, xt, yt, xy, len2 ;
		Point p1, p2, pc ;

		p1 = app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(lineIndex).getStartPoint();
		p2 = app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(lineIndex).getEndPoint();
		pc = app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(curveIndex).getMidPoint();
		xy = p2.getY()* p1.getX()- p1.getY()* p2.getX();
		dx = p2.getX()- p1.getX();
		dy = p2.getY()- p1.getY(); 
		len2 = (dx*dx + dy*dy);
		xt = (dy * xy + (pc.getY()* dy + pc.getX()* dx) * dx) / len2;
		yt = (-dx * xy + (pc.getY()* dy + pc.getX()* dx) * dy) / len2;
		return GeoUtils.makePoint(xt, yt);
	}   // calculateTangentPoint
} // ActionListener
