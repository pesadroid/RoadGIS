package xyz.geosure.roadgis.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;

import xyz.geosure.roadgis.RoadGISApplication;


@SuppressWarnings("serial")
public class EditUndoAction extends AbstractAction implements ActionListener{ 
	private RoadGISApplication app = null;

	public EditUndoAction(RoadGISApplication app) {
		this.app = app;
	}

	@Override
	public void actionPerformed(ActionEvent aev) {

		try
		{
			app.getHorizontalDesign().edit_undo();
		}
		catch (Exception e){
			System.out.println("file-import: "+e.toString()) ;
			e.printStackTrace();
			//do nothing
		} // try
	} // actionPerformed
} // ActionListener
