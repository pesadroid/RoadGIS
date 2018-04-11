package xyz.geosure.roadgis.actions;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;

import javax.swing.AbstractAction;

import xyz.geosure.roadgis.RoadGISApplication;
import xyz.geosure.roadgis.views.RoadGISPopup;

public class FileSaveAction  extends AbstractAction implements ActionListener{ 
	private RoadGISApplication app = null;
	RoadGISPopup frame_saveDesign;

	ActionListener frame_msgbox_yes_listener, frame_msgbox_no_listener, frame_msgbox_cancel_listener;
	private boolean exitFlag = false;

	public FileSaveAction(RoadGISApplication app) {
		this.app = app;	

	}

	@Override
	public void actionPerformed(ActionEvent aev) {

		frame_saveDesign = new RoadGISPopup() ;
		frame_msgbox_yes_listener = new ActionListener() {
			public void actionPerformed(ActionEvent aev) {
				saveDesignFile() ;

				frame_saveDesign.dispose() ;
				app.getHorizontalDesign().view_RESET();
				//repaint();
			}
		} ;
		frame_msgbox_no_listener = new ActionListener() {
			public void actionPerformed(ActionEvent aev) {
				frame_saveDesign.dispose() ;
				app.getHorizontalDesign().view_RESET();
				if(exitFlag) {
					System.exit(0);
				}
			}
		} ;
		frame_msgbox_cancel_listener = new ActionListener() {
			public void actionPerformed(ActionEvent aev) {
				frame_saveDesign.dispose() ;
			}
		} ;
	} // ActionListener
	
	
	public ActionListener getYesListener() {
		return frame_msgbox_yes_listener;
	}
	public ActionListener getNoListener() {
		return frame_msgbox_no_listener;
	}
	public ActionListener getCancelListener() {
		return frame_msgbox_cancel_listener;
	}


	// save horizonatl design file
	private void saveDesignFile() {
		FileOutputStream fos=null;
		DataOutputStream w=null;

		try
		{
			FileDialog fd=new FileDialog(new Frame(),"Save Design", FileDialog.SAVE);
			fd.setFile("*.rdw");
			/*            fd.setFilenameFilter(new FilenameFilter(){
	                public boolean accept(File dir, String name){
	                  return (name.endsWith(".rdp")) ;  // || name.endsWith(".gif"));
	                  }
	            });
			 */
			fd.show();
			String fullpath=fd.getDirectory()+fd.getFile();
			fd.dispose();
			//System.out.println("filepath="+fullpath);
			if(fullpath!=null) {
				if (fullpath.indexOf(".rdw")<0) {
					fullpath += ".rdw" ;
				}
				fos = new FileOutputStream(fullpath);
				w = new DataOutputStream( new BufferedOutputStream(fos,512)); 
				// 1 - save settings
				w.writeInt(app.getHorizontalDesign().getRoadDesign().getPreferredPenColor().getRed());
				w.writeInt(app.getHorizontalDesign().getRoadDesign().getPreferredPenColor().getGreen());
				w.writeInt(app.getHorizontalDesign().getRoadDesign().getPreferredPenColor().getBlue());
				w.writeDouble(app.getHorizontalDesign().getRoadDesign().getPreferredRoadLaneSizes());
				w.writeInt(app.getHorizontalDesign().getRoadDesign().getElevationMarkerColor().getRed());
				w.writeInt(app.getHorizontalDesign().getRoadDesign().getElevationMarkerColor().getGreen());
				w.writeInt(app.getHorizontalDesign().getRoadDesign().getElevationMarkerColor().getBlue());
				w.writeDouble(app.getHorizontalDesign().getRoadDesign().getElevationMarkerSize());
				w.flush();
				// 2 - save image file path/name
				w.writeUTF(app.getHorizontalDesign().contourImageFilepath) ;
				// 3 - save image resolution & scale
				w.writeDouble(app.getHorizontalDesign().getRoadDesign().getContourImageResolution());
				w.writeDouble(app.getHorizontalDesign().getRoadDesign().getContourScale());
				// 4 - save hRoadData DB
				int i, actualDataSize ;
				// 11/8/06
				// save only undeleted items
				actualDataSize = app.getHorizontalDesign().gethRoadDataCount() ;
				for (i=0;i<app.getHorizontalDesign().gethRoadDataCount();i++) {
					if (app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).isDeleted()) {
						actualDataSize-- ;
					}   // if item deleted
				}   // i
				w.writeInt(actualDataSize);
				w.flush();
				int saved_count = 0 ;
				int[] lookupRef = new int[actualDataSize] ;
				for (i=0;i<app.getHorizontalDesign().gethRoadDataCount();i++) {
					if (!app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).isDeleted()) {   // 11/8/06
						// item not deleted
						w.writeDouble(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getStartPoint().getX());
						w.writeDouble(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getStartPoint().getY());
						w.writeDouble(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getEndPoint().getX());
						w.writeDouble(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getEndPoint().getY());
						w.writeDouble(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getRadius());
						w.writeBoolean(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).isSelected());
						w.writeDouble(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getPenWidth());
						w.writeInt(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getPenColor().getRed());
						w.writeInt(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getPenColor().getGreen());
						w.writeInt(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentSegments().get(i).getPenColor().getBlue());
						w.flush();
						lookupRef[saved_count] = i ;    // lookup reference table after remove deleted segment
						saved_count++ ;
					}
				}
				// 5 - save landmarks DB
				w.writeInt(app.getHorizontalDesign().getRoadDesign().getElevationMarkCount());
				int oldParentID ;
				for (i=0;i<app.getHorizontalDesign().getRoadDesign().getElevationMarkCount();i++){
					w.writeDouble(app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i).getLocation().getX());
					w.writeDouble(app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i).getLocation().getY());
					w.writeDouble(app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i).getElevation());
					oldParentID = app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i).getParentIndex() ; // 3/1/07
					w.writeByte(lookupParentIndex(oldParentID, lookupRef));         // 3/1/07
					w.writeByte(app.getHorizontalDesign().getRoadDesign().getElevationMarks().get(i).getSegmentType().ordinal());
					w.flush();
				}
				// 6 - save horizontal alignment landmarks
				w.writeInt(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentMarkCount());
				for (i=0; i<app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentMarkCount();i++) {
					w.writeDouble(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentMarks().get(i).getLocation().getX());
					w.writeDouble(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentMarks().get(i).getLocation().getY());
					w.writeDouble(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentMarks().get(i).getElevation());
					oldParentID = app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentMarks().get(i).getParentIndex() ;    // 3/1/07
					w.writeByte(lookupParentIndex(oldParentID, lookupRef));         // 3/1/07
					w.writeByte(app.getHorizontalDesign().getRoadDesign().getHorizontalAlignmentMarks().get(i).getSegmentType().ordinal());
					w.flush();
				}
				// 7 - save control parameters
				w.writeInt(app.getHorizontalDesign().getRoadDesign().getPreferredUnit());                           // unit 1-US, 2-Metric
				w.writeDouble(app.getHorizontalDesign().getRoadDesign().getGradeLimit()) ;                      // grade limit default 6%
				w.writeDouble(app.getHorizontalDesign().getRoadDesign().getMinGrade()) ;                      // min grade limit default 0.5%
				w.writeDouble(app.getHorizontalDesign().getRoadDesign().getSpeedLimit()) ;                      // speed limit MPH default 40MPH
				w.writeDouble(app.getHorizontalDesign().getRoadDesign().getMaxCut()) ;                          // maximum cut, default 10 ft
				w.writeDouble(app.getHorizontalDesign().getRoadDesign().getMaxFill()) ;                         // maximum fill, default 10 ft
				w.writeDouble(app.getHorizontalDesign().getRoadDesign().getVehDecel()) ;                        // vehicle deceleration rate, 11.2 ft/s/s
				w.writeDouble(app.getHorizontalDesign().getRoadDesign().getReactionTime()) ;                    // drive reaction time 2.5 sec
				w.writeDouble(app.getHorizontalDesign().getRoadDesign().getFrictionCoef())  ;                   // friction coefficient
				w.writeDouble(app.getHorizontalDesign().getRoadDesign().getSideFrictionCoef());
				w.writeDouble(app.getHorizontalDesign().getRoadDesign().getMinVCurveLen())   ;                  // min vertical curve length
				w.writeDouble(app.getHorizontalDesign().getRoadDesign().getMinHCurveRadius()) ;                 // min horizontal curve radius
				w.writeDouble(app.getHorizontalDesign().getRoadDesign().getMaxSuperelevation()) ;               // max superelevation
				w.writeDouble(app.getHorizontalDesign().getRoadDesign().getPreferredLaneWidth())   ;                   // lane width 12 ft
				w.writeDouble(app.getHorizontalDesign().getRoadDesign().getPreferredShoulderWidth()) ;                 // shoulder width 6 ft
				w.flush();
				w.close();
			}
			fos.close();
		}
		catch (Exception e){
			//do nothing
			System.out.println("Save Design File:"+e.toString());
		} // try

	}

	

	// 3/1/07 added
	private byte lookupParentIndex(int oldParentID, int[] lookupRef) {
		int i ;
		byte value=(byte)255 ;
		for (i=0; i<lookupRef.length; i++) {
			if (lookupRef[i]==oldParentID) {
				value = (byte) i ;
				break ;
			}
		}
		if (value==255) {
			System.out.println("Error in looupParentIndex: parentIndex not found!") ;
		}
		return value ;
	}
	public boolean isExit() {
		return exitFlag;
	}

	public FileSaveAction setExit(boolean exit) {
		this.exitFlag = exit;
		return this;
	}
}
