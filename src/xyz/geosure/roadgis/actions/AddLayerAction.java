package xyz.geosure.roadgis.actions;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.DataSourceException;
import org.geotools.data.DataStore;
import org.geotools.data.DataUtilities;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.Hints;
import org.geotools.feature.SchemaException;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.GridCoverageLayer;
import org.geotools.referencing.CRS;
import org.geotools.referencing.CRS.AxisOrder;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.FilterFactory2;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.GeometryFactory;

import xyz.geosure.roadgis.RoadGISApplication;
import xyz.geosure.roadgis.utils.GeoUtils;
import xyz.geosure.roadgis.utils.GeoUtils.GeomType;
import xyz.geosure.roadgis.utils.csv.CSVDataStoreFactory;


@SuppressWarnings("serial")
public class AddLayerAction extends AbstractAction implements ActionListener{ 
	private RoadGISApplication app = null;

	/*
	 * Factories that we will use to create style and filter objects
	 */
	private StyleFactory sf = CommonFactoryFinder.getStyleFactory();
	private FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();

	/*
	 * Some default style variables
	 */
	private static final Color LINE_COLOUR = Color.BLUE;
	private static final Color FILL_COLOUR = Color.CYAN;
	private static final Color SELECTED_COLOUR = Color.YELLOW;
	private static final float OPACITY = 1.0f;
	private static final float LINE_WIDTH = 1.0f;
	private static final float POINT_SIZE = 10.0f;

	private String sourceDataFileFilepath = "";
	private SimpleFeatureSource featureSource;
	private String geometryAttributeName;
	private DataStore store = null;

	public AddLayerAction(RoadGISApplication app) {
		this.app = app;
	}

	@Override
	public void actionPerformed(ActionEvent event) {

		try
		{
			FileDialog fd=new FileDialog(new Frame(),"Import CSV Coordinates", FileDialog.LOAD);
			//fd.setFilenameFilter(new FilenameFilter("csv"));
			fd.show();
			String dir = fd.getDirectory() ;
			String filename = fd.getFile() ;
			String fullpath = dir + filename;
			fd.dispose();

			//System.out.println("file-import path: "+fullpath) ;
			if(dir != null && filename != null) {
				System.out.println("Getting GIS Data from : " + fullpath) ;
				sourceDataFileFilepath = fullpath;
				addLayer();
			}
			//app.getHorizontalDesign().repaint();
		}catch (Exception e){
			System.out.println("file-import: "+ e.toString()) ;
			e.printStackTrace();
			//do nothing
		} // try
	} // actionPerformed


	public void addLayer() {
		int status =0;
		if(status == 0) {

			try {
				File file = new File(sourceDataFileFilepath);

				String[] components = sourceDataFileFilepath.split("/");
				String file_name = file.getName();
				String file_minus_extension = file_name.split("\\.")[0];

				GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();

				if(file.getName().toLowerCase().endsWith("csv")) {
					processCsv(file_minus_extension);
				}else if(file.getName().toLowerCase().endsWith("tif")) {
					processGeoTiff();
				}else {
					store = FileDataStoreFinder.getDataStore(file);
					featureSource = store.getFeatureSource(file_minus_extension);


					SimpleFeatureType type = store.getSchema(file_minus_extension);
					//Style style = SLD.createSimpleStyle(store.getSchema(file_minus_extension));
					Style style = SLD.createSimpleStyle(store.getSchema(file_minus_extension));
					
					/*
		 	        if (!(featureSource instanceof SimpleFeatureStore)) {
		 	            throw new IllegalStateException("Modification not supported");
		 	        }//*/
					//SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;

					//System.out.println("featureType  name: " + type.getName());
					//System.out.println("featureType count: " + type.getAttributeCount());
					//System.out.println("featuretype attributes list:");

					// access by list
					for (AttributeDescriptor descriptor : type.getAttributeDescriptors()) {
						System.out.print("  " + descriptor.getName());
						System.out.print(" (" + descriptor.getMinOccurs() + "," + descriptor.getMaxOccurs()  + ",");
						System.out.print((descriptor.isNillable() ? "nillable" : "mandatory") + ")");
						System.out.print(" type: " + descriptor.getType().getName());
						System.out.println(" binding: " + descriptor.getType().getBinding().getSimpleName());
						
					}

					
					FeatureLayer layer = new FeatureLayer(featureSource, style);
					layer.setTitle(file_minus_extension);
					app.getMapFrame().getMapContent().addLayer(layer);


					GeomType geoType = GeoUtils.getGeometryType(featureSource);
					if(geoType == GeomType.POINT ) {
						app.getHorizontalDesign().setPointsLayer(layer);
					}
					
					//app.getMapFrame().getMapContent().setTitle(file_minus_extension);
					ReferencedEnvelope bounds = layer.getBounds();
					app.getMapFrame().getMapContent().getViewport().setBounds(bounds);

					System.out.print("Bounds:" + bounds);
					//767075.7611 : 779389.0117, 404396.1281 : 415199.9129
					//767075.7611, 404396.1281), (779389.0117, 415199.9129

					//ReferencedEnvelope gridBounds = Envelopes.expandToInclude(bounds, app.getHorizontalDesign().getRoadDesign().getGridBuffer());
					//SimpleFeatureSource source = Grids.createSquareGrid(gridBounds, app.getHorizontalDesign().getRoadDesign().getGridVertexSpacing());
					//app.getHorizontalDesign().setGridLinesLayer(source);
				}


			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	private void processGeoTiff() {

		File file = new File(sourceDataFileFilepath);
		GeoTiffReader reader;
		try {
			Hints hint = new Hints();

			//hint.put(Hints.DEFAULT_COORDINATE_REFERENCE_SYSTEM, crs );    
			//hint.put(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);
			//hint.put(Hints.FORCE_STANDARD_AXIS_DIRECTIONS, Boolean.TRUE);
			//hint.put(Hints.FORCE_AXIS_ORDER_HONORING, Boolean.TRUE);

			reader = new GeoTiffReader(file, hint);

			GridCoverage2D coverage = (GridCoverage2D) reader.read(null);

			CoordinateReferenceSystem crs = coverage.getCoordinateReferenceSystem();
			System.out.print("Axis Order:" + CRS.getAxisOrder(crs));

			if (CRS.getAxisOrder(crs).equals(AxisOrder.EAST_NORTH)) {
				System.setProperty("org.geotools.referencing.forceXY", "true");
			}
			GridCoverageLayer interpolatedLayer = new GridCoverageLayer(coverage, GeoUtils.getRasterStyle());
			interpolatedLayer.setTitle(file.getName());

			app.getMapContent().addLayer(interpolatedLayer);

			Envelope env = coverage.getEnvelope();
			System.out.print("Envelope:" + env);

			app.getHorizontalDesign().setDEMLayer(interpolatedLayer);

		} catch (DataSourceException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// process CVS file
	public void processCsv(String file_minus_extension) {

		SimpleFeatureType SPOT;

		List<SimpleFeature> features = new ArrayList<SimpleFeature>();

		//try (BufferedReader reader = new BufferedReader(new FileReader(sourceDataFileFilepath)) ){
		try{


			//System.out.println("coordinatesFileFilepath: " + coordinatesFileFilepath);
			//System.out.println("full FILE  name: " + file_name);
			//System.out.println("FILE: " + file_minus_extension);


			SPOT = DataUtilities.createType("SPOT", "number:Integer,the_geom:Point:srid=32636,name:String");
			//Arc 60 36N = EPSG:21096

			Map<String, Serializable> params = new HashMap<>();
			params.put("file", sourceDataFileFilepath);

			//params.put("schema", SPOT);
			DataStore store = new CSVDataStoreFactory().createDataStore(params);
			SimpleFeatureType type = store.getSchema(file_minus_extension);
			featureSource = store.getFeatureSource(file_minus_extension);



		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SchemaException e1) {
			e1.printStackTrace();
		}//*/

	}
}
