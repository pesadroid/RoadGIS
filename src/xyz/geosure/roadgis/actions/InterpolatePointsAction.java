package xyz.geosure.roadgis.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.NumberFormatter;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.NameImpl;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.GridCoverageLayer;
import org.geotools.map.Layer;
import org.geotools.process.ProcessExecutor;
import org.geotools.process.Processors;
import org.geotools.util.KVP;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;

import xyz.geosure.roadgis.RoadGISApplication;
import xyz.geosure.roadgis.model.RoadDesign;
import xyz.geosure.roadgis.utils.GeoUtils;


@SuppressWarnings("serial")
public class InterpolatePointsAction extends AbstractAction implements ActionListener{ 
	private RoadGISApplication app = null;

	JComboBox jLayersList;
	JOptionPane jOptionsPane;
	JFormattedTextField jResolution ;
	JDialog diag;

	File selectedFile;
	String selectedLayer;
	String selectedResolution;

	public InterpolatePointsAction(RoadGISApplication app) {
		this.app = app;
	}

	@Override
	public void actionPerformed(ActionEvent event) {

		try		
		{


			ArrayList<String> layersList = new ArrayList<String>();

			List<Layer> layers =  app.getMapContent().layers();
			for(Layer layer:layers) {
				String geomType = layer.getFeatureSource().getSchema().getGeometryDescriptor().getType().getBinding().getName();
				System.out.println(geomType);
				if(geomType.endsWith("Point") || geomType.endsWith("point") && !layer.getTitle().startsWith("Construction Marks")) {
					//System.out.println("Layer:" + layer.getTitle());

					Collection<PropertyDescriptor> descriptors = layer.getFeatureSource().getSchema().getDescriptors();


					for(PropertyDescriptor descriptor:descriptors) {
						String binding = descriptor.getType().getBinding().getName();
						//System.out.println("Layer:" + layer.getTitle()+ "." + descriptor.getName() + " type = " + binding);
						if(binding.endsWith("Double") || binding.endsWith("Long") || binding.endsWith("Integer") || binding.endsWith("Byte")|| binding.endsWith("Float")) {
							layersList.add(layer.getTitle() + "." + descriptor.getName());
						}
					}
				}
			}

			String[] layerNames = layersList.toArray(new String[0]);
			jLayersList = new JComboBox(layerNames);
			jLayersList.setEditable(false);
			jLayersList.setSelectedItem(0);

			//create a JOptionPane
			Object[] options = new Object[] {};
			jOptionsPane = new JOptionPane("",
					JOptionPane.QUESTION_MESSAGE,
					JOptionPane.DEFAULT_OPTION,
					null,options, null);

			jOptionsPane.add(new JLabel("Which Attribute should be Interpolated?"));
			jOptionsPane.add(jLayersList);

			jOptionsPane.add(new JLabel("Please Enter the Resolution of the Interpolation (in feet or metres depending on settings)"));
			NumberFormat format = NumberFormat.getInstance();
			NumberFormatter formatter = new NumberFormatter(format);
			formatter.setValueClass(Double.class);
			formatter.setMinimum(0.000001);
			formatter.setMaximum(Double.MAX_VALUE);
			formatter.setAllowsInvalid(false);
			// If you want the value to be committed on each keystroke instead of focus lost
			//formatter.setCommitsOnValidEdit(true);
			jResolution = new JFormattedTextField(formatter);
			jResolution.setText("1.0");
			jOptionsPane.add(jResolution);

			JButton interpolateButton = new JButton("Interpolate");
			interpolateButton.addActionListener (new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					//selectedFile = jChooser.getSelectedFile();
					selectedLayer = (String) jLayersList.getSelectedItem();
					selectedResolution = jResolution.getText();

					GridCoverage2D grid = interpolate();
					if(null != grid) {

						System.out.println("Yay! interpolate returned something: ") ;
						GridCoverageLayer interpolatedLayer = new GridCoverageLayer(grid, GeoUtils.getRasterStyle());
						interpolatedLayer.setTitle("Interpolation of " + selectedLayer);

						app.getMapContent().addLayer(interpolatedLayer);

						//Now we save the interpolated file

						try {
							JFileChooser fileChooser = new JFileChooser();

					        FileNameExtensionFilter xmlFilter = new FileNameExtensionFilter("GeoTIFF files (*.tif)", "tif");
					        // add filters
					        fileChooser.addChoosableFileFilter(xmlFilter);
					        fileChooser.setFileFilter(xmlFilter);
					        
							if (fileChooser.showSaveDialog(app) == JFileChooser.APPROVE_OPTION) {
								File file = fileChooser.getSelectedFile();
								// save to file
								GeoTiffWriter writer = new GeoTiffWriter(file);
								
								writer.write(grid, null);
								writer.dispose();
							}
						} catch (IllegalArgumentException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IndexOutOfBoundsException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}



					}
					diag.dispose();
				}
			});
			jOptionsPane.add(interpolateButton);



			//create a JDialog and add JOptionPane to it 
			diag = new JDialog();
			diag.setTitle("Create New DEM Layer by Interpolation");
			diag.getContentPane().add(jOptionsPane);
			diag.setLocationRelativeTo(app.getMapFrame());
			diag.pack();
			diag.setVisible(true);


			//app.getHorizontalDesign().repaint();
		}catch (Exception e){
			System.out.println("file-import: "+ e.toString()) ;
			e.printStackTrace();
			//do nothing
		} // try
	} // actionPerformed

	public GridCoverage2D interpolate() {
		try {
			System.out.println("selectedLayer.attribute:" + selectedLayer);

			String attributeName = selectedLayer.substring(selectedLayer.lastIndexOf(".")+1, selectedLayer.length()).trim();			
			String layerName = selectedLayer.substring(0, selectedLayer.lastIndexOf(".")).trim();

			System.out.println("layerName:" + layerName);
			System.out.println("attributeName:" + attributeName);

			System.out.println("selectedResolution " + selectedResolution + " m or feet");
			double resolution = Double.parseDouble(selectedResolution);
			if(app.getHorizontalDesign().getRoadDesign().getPreferredUnit() == 1) {
				resolution = resolution * RoadDesign.FT2M;
			}
			System.out.println("Interpolate " + layerName + " using " + attributeName + " at a resolution of " + resolution + " metres");

			SimpleFeatureCollection features = null;
			List<Layer> layers =  app.getMapContent().layers();
			for(Layer layer:layers) {
				String geomType = layer.getFeatureSource().getSchema().getGeometryDescriptor().getType().getBinding().getName();
				System.out.println(geomType);
				if(layer.getTitle().equalsIgnoreCase(layerName)) {
					System.out.println("Interpolating Layer:" + layer.getTitle());

					features = (SimpleFeatureCollection) layer.getFeatureSource().getFeatures();

					break;
				}
			}

			if(null != features) {
				Name pName = new NameImpl("vec", "BarnesSurface");
				org.geotools.process.Process process = Processors.createProcess(pName);

				System.out.println("Interpolating process:" + process);
				ProcessExecutor engine = Processors.newProcessExecutor(2);

				ReferencedEnvelope bounds = features.getBounds();
				double aspectRatio = bounds.getHeight() / bounds.getWidth();
				int width = 1000;
				int height = (int) (aspectRatio * width);

				System.out.println("Width:" + width + ", height:" + height);
				System.out.println("bounds:" + bounds);
				
				Map<String, Object> input = new KVP("data", features, "valueAttr", attributeName, "scale", 1000, "queryBuffer", 1000, "outputBBOX", bounds, "outputWidth", width, "outputHeight", height);
				org.geotools.process.Progress working = engine.submit(process, input);

				Map<String, Object> result = null;
				try {
					result = working.get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				} // get is BLOCKING

				GridCoverage2D grid= (GridCoverage2D) result.get("result");
								
				return grid;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
