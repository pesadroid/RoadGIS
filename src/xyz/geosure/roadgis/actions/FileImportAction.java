package xyz.geosure.roadgis.actions;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;

import xyz.geosure.roadgis.RoadGISApplication;


@SuppressWarnings("serial")
public class FileImportAction extends AbstractAction implements ActionListener{ 
	private RoadGISApplication app = null;

	public FileImportAction(RoadGISApplication app) {
		this.app = app;
	}

	@Override
	public void actionPerformed(ActionEvent aev) {

		try
		{
			FileDialog fd=new FileDialog(new Frame(),"Import Contour Image", FileDialog.LOAD);
			fd.show();
			String dir = fd.getDirectory() ;
			String filename = fd.getFile() ;
			String fullpath = dir + filename;
			fd.dispose();

			//System.out.println("file-import path: "+fullpath) ;
			if(dir != null && filename != null) {
				System.out.println("Getting Image from : "+fullpath) ;

				app.getHorizontalDesign().contourImageFilepath =fullpath;
				app.getHorizontalDesign().setContourImage(app.getToolkit().getImage(fullpath));
				app.getHorizontalDesign().init(0);
				app.getUIActionsHandler().view_RESET();
			}
			//app.getHorizontalDesign().repaint();
		}
		catch (Exception e){
			System.out.println("file-import: "+e.toString()) ;
			e.printStackTrace();
			//do nothing
		} // try
	} // actionPerformed
} // ActionListener
