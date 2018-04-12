package xyz.geosure.roadgis.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;

import xyz.geosure.roadgis.RoadGISApplication;


@SuppressWarnings("serial")
public class ExportShapefileAction extends AbstractAction implements ActionListener{ 
	private RoadGISApplication app = null;

	ExportShapefileAction(RoadGISApplication app) {
		this.app = app;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		exportToShapefile();
	}

	private void exportToShapefile() {// throws Exception {

		/*
        SimpleFeatureType schema = featureSource.getSchema();
        JFileDataStoreChooser chooser = new JFileDataStoreChooser("shp");
        chooser.setDialogTitle("Save reprojected shapefile");
        chooser.setSaveFile(sourceFile);
        int returnVal = chooser.showSaveDialog(null);
        if (returnVal != JFileDataStoreChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        if (file.equals(sourceFile)) {
            JOptionPane.showMessageDialog(null, "Cannot replace " + file);
            return;
        }
        //*/
	}
}
