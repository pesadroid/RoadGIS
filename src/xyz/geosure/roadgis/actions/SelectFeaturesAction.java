package xyz.geosure.roadgis.actions;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;

import org.geotools.data.DataStore;
import org.geotools.data.DataUtilities;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.SchemaException;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.Mark;
import org.geotools.styling.Rule;
import org.geotools.styling.SLD;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.geotools.swing.event.MapMouseEvent;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import xyz.geosure.roadgis.RoadGISApplication;
import xyz.geosure.roadgis.utils.csv.CSVDataStoreFactory;


@SuppressWarnings("serial")
public class SelectFeaturesAction extends AbstractAction implements ActionListener{ 
	private RoadGISApplication app = null;

	/*
	 * Factories that we will use to create style and filter objects
	 */
	private StyleFactory sf = CommonFactoryFinder.getStyleFactory();
	private FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();

	private String sourceDataFileFilepath = "";


	/*
	 * Some default style variables
	 */
	private static final Color LINE_COLOUR = Color.BLUE;
	private static final Color FILL_COLOUR = Color.CYAN;
	private static final Color SELECTED_COLOUR = Color.YELLOW;
	private static final float OPACITY = 1.0f;
	private static final float LINE_WIDTH = 1.0f;
	private static final float POINT_SIZE = 10.0f;
	private enum GeomType { POINT, LINE, POLYGON };

	private GeomType geometryType;

	private SimpleFeatureSource featureSource;
	private String geometryAttributeName;

	DataStore store = null;

	public SelectFeaturesAction(RoadGISApplication app) {
		this.app = app;
		/*
		ImageIcon theIcon = new ImageIcon("icons/About16.gif");
		putValue(NAME, "GetFeature");
		putValue(SMALL_ICON, theIcon);
		putValue(SHORT_DESCRIPTION, "Get A Feature");
		putValue(ACTION_COMMAND_KEY, "GETFEATURE");//*/
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
				}else {
					store = FileDataStoreFinder.getDataStore(file);
					featureSource = store.getFeatureSource(file_minus_extension);
					setGeometryType();
				}

				SimpleFeatureType type = store.getSchema(file_minus_extension);
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

				//store.updateSchema("SPOT", SPOT);

				/*/
	 			SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(SPOT);

	 			//First line of the data file is the header 
	 			String line = reader.readLine();
	 			System.out.println("Header: " + line);
	 			for (line = reader.readLine(); line != null; line = reader.readLine()) {
	 				if (line.trim().length() > 0) { // skip blank lines
	 					String tokens[] = line.split("\\,");

	 					double latitude = Double.parseDouble(tokens[1]);
	 					double longitude = Double.parseDouble(tokens[2]);
	 					String name = tokens[3].trim();
	 					int number = Integer.parseInt(tokens[0].trim());

	 					// Longitude (= x coord) first !
	 					Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));

	 					featureBuilder.add(point);
	 					featureBuilder.add(name);
	 					featureBuilder.add(number);
	 					SimpleFeature feature = featureBuilder.buildFeature(null);
	 					features.add(feature);
	 				}
	 			}

	 		    SimpleFeatureCollection collection = new ListFeatureCollection(SPOT, features);
	 			//*/


				Layer layer = new FeatureLayer(featureSource, style);
				app.getMapFrame().getMapContent().addLayer(layer);

				app.getMapFrame().getMapContent().setTitle(file_minus_extension);




			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
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



	/**
	 * This method is called by our feature selection tool when
	 * the user has clicked on the map.
	 *
	 * @param ev the mouse event being handled
	 */
	void selectFeatures(MapMouseEvent ev) {

		System.out.println("Mouse click at: " + ev.getMapPosition());

		/*
		 * Construct a 5x5 pixel rectangle centred on the mouse click position
		 */
		java.awt.Point screenPos = ev.getPoint();
		Rectangle screenRect = new Rectangle(screenPos.x-2, screenPos.y-2, 5, 5);

		/*
		 * Transform the screen rectangle into bounding box in the coordinate
		 * reference system of our map context. Note: we are using a naive method
		 * here but GeoTools also offers other, more accurate methods.
		 */
		AffineTransform screenToWorld = app.getMapFrame().getMapPane().getScreenToWorldTransform();
		Rectangle2D worldRect = screenToWorld.createTransformedShape(screenRect).getBounds2D();
		ReferencedEnvelope bbox = new ReferencedEnvelope(
				worldRect,
				app.getMapFrame().getMapContent().getCoordinateReferenceSystem());

		/*
		 * Create a Filter to select features that intersect with
		 * the bounding box
		 */
		Filter filter = ff.intersects(ff.property(geometryAttributeName), ff.literal(bbox));

		/*
		 * Use the filter to identify the selected features
		 */
		try {
			SimpleFeatureCollection selectedFeatures = featureSource.getFeatures(filter);

			Set<FeatureId> IDs = new HashSet<>();
			try (SimpleFeatureIterator iter = selectedFeatures.features()) {
				while (iter.hasNext()) {
					SimpleFeature feature = iter.next();
					IDs.add(feature.getIdentifier());

					System.out.println("   " + feature.getIdentifier());
				}

			}

			if (IDs.isEmpty()) {
				System.out.println("   no feature selected");
			}

			displaySelectedFeatures(IDs);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Create a default Style for feature display
	 */
	private Style createDefaultStyle() {
		Rule rule = createRule(LINE_COLOUR, FILL_COLOUR);

		FeatureTypeStyle fts = sf.createFeatureTypeStyle();
		fts.rules().add(rule);

		Style style = sf.createStyle();
		style.featureTypeStyles().add(fts);
		return style;
	}

	/**
	 * Sets the display to paint selected features yellow and
	 * unselected features in the default style.
	 *
	 * @param IDs identifiers of currently selected features
	 */
	public void displaySelectedFeatures(Set<FeatureId> IDs) {
		Style style;

		if (IDs.isEmpty()) {
			style = createDefaultStyle();

		} else {
			style = createSelectedStyle(IDs);
		}

		Layer layer = app.getMapFrame().getMapContent().layers().get(0);
		((FeatureLayer) layer).setStyle(style);
		app.getMapFrame().getMapPane().repaint();
	}

	/**
	 * Create a Style where features with given IDs are painted
	 * yellow, while others are painted with the default colors.
	 */
	private Style createSelectedStyle(Set<FeatureId> IDs) {
		Rule selectedRule = createRule(SELECTED_COLOUR, SELECTED_COLOUR);
		selectedRule.setFilter(ff.id(IDs));

		Rule otherRule = createRule(LINE_COLOUR, FILL_COLOUR);
		otherRule.setElseFilter(true);

		FeatureTypeStyle fts = sf.createFeatureTypeStyle();
		fts.rules().add(selectedRule);
		fts.rules().add(otherRule);

		Style style = sf.createStyle();
		style.featureTypeStyles().add(fts);
		return style;
	}

	/**
	 * Helper for createXXXStyle methods. Creates a new Rule containing
	 * a Symbolizer tailored to the geometry type of the features that
	 * we are displaying.
	 */
	private Rule createRule(Color outlineColor, Color fillColor) {
		Symbolizer symbolizer = null;
		Fill fill = null;
		Stroke stroke = sf.createStroke(ff.literal(outlineColor), ff.literal(LINE_WIDTH));

		switch (geometryType) {
		case POLYGON:
			fill = sf.createFill(ff.literal(fillColor), ff.literal(OPACITY));
			symbolizer = sf.createPolygonSymbolizer(stroke, fill, geometryAttributeName);
			break;

		case LINE:
			symbolizer = sf.createLineSymbolizer(stroke, geometryAttributeName);
			break;

		case POINT:
			fill = sf.createFill(ff.literal(fillColor), ff.literal(OPACITY));

			Mark mark = sf.getCircleMark();
			mark.setFill(fill);
			mark.setStroke(stroke);

			Graphic graphic = sf.createDefaultGraphic();
			graphic.graphicalSymbols().clear();
			graphic.graphicalSymbols().add(mark);
			graphic.setSize(ff.literal(POINT_SIZE));

			symbolizer = sf.createPointSymbolizer(graphic, geometryAttributeName);
		}

		Rule rule = sf.createRule();
		rule.symbolizers().add(symbolizer);
		return rule;
	}

	/**
	 * Retrieve information about the feature geometry
	 */
	private GeomType setGeometryType() {
		GeometryDescriptor geomDesc = featureSource.getSchema().getGeometryDescriptor();
		geometryAttributeName = geomDesc.getLocalName();

		Class<?> clazz = geomDesc.getType().getBinding();

		if (Polygon.class.isAssignableFrom(clazz) ||
				MultiPolygon.class.isAssignableFrom(clazz)) {
			geometryType = GeomType.POLYGON;

		} else if (LineString.class.isAssignableFrom(clazz) ||
				MultiLineString.class.isAssignableFrom(clazz)) {

			geometryType = GeomType.LINE;

		} else {
			geometryType = GeomType.POINT;
		}
		return geometryType;

	}

	public SelectFeaturesAction setSourceDataPath(String path) {
		this.sourceDataFileFilepath = path;
		addLayer();
		return this;
	}
	public String getSourceDataPath() {
		return sourceDataFileFilepath;
	}

}
