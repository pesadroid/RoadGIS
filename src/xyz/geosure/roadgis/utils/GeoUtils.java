package xyz.geosure.roadgis.utils;

import java.awt.Color;

import org.geotools.coverage.GridSampleDimension;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.FeatureSource;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.grid.GridFeatureBuilder;
import org.geotools.grid.Grids;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.referencing.CRS;
import org.geotools.styling.ChannelSelection;
import org.geotools.styling.ContrastEnhancement;
import org.geotools.styling.ContrastEnhancementImpl;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Font;
import org.geotools.styling.Graphic;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.SLD;
import org.geotools.styling.SelectedChannelType;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactoryImpl;
import org.geotools.styling.Symbolizer;
import org.geotools.styling.TextSymbolizer;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.FilterFactory2;
import org.opengis.geometry.Geometry;
import org.opengis.style.ContrastMethod;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class GeoUtils {

	public enum GeomType { POINT, LINE, POLYGON };

	private static SimpleFeatureType ROAD_SEGMENT_TYPE = null;
	private static SimpleFeatureType ROAD_MARKER_TYPE = null;
	private static SimpleFeatureType ROAD_RADIAL_TYPE = null;
	private static SimpleFeatureType GRID_TYPE = null;


	final static StyleFactoryImpl sf = new StyleFactoryImpl();
	private final static FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();

	/**
	 * Here is how you can use a SimpleFeatureType builder to create the schema for your shapefile
	 * dynamically.
	 * <p>
	 * This method is an improvement on the code used in the main method above (where we used
	 * DataUtilities.createFeatureType) because we can set a Coordinate Reference System for the
	 * FeatureType and a a maximum field length for the 'name' field dddd
	 */
	public static SimpleFeatureType getRoadMarkerType(String epsg) {

		if(null == ROAD_MARKER_TYPE) {
			try {
				SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
				builder.setName("RoadMarker");
				builder.setCRS(CRS.decode(epsg));
				builder.add("location", Point.class);
				builder.add("index", Integer.class);
				//builder.length(15).add("name", String.class); // <- 15 chars width for name field

				ROAD_MARKER_TYPE = builder.buildFeatureType();

			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return ROAD_MARKER_TYPE;
	}

	public static SimpleFeatureType getRoadSegmentFeatureType(String epsg) {

		if(null == ROAD_SEGMENT_TYPE) {
			try {
				SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
				builder.setName("RoadSegment");
				//builder.setCRS(DefaultGeographicCRS.WGS84); // <- Coordinate reference system
				//builder.setCRS(CRS.decode("EPSG:21096"));
				builder.setCRS(CRS.decode(epsg));
				builder.add("route", LineString.class);
				builder.add("index", Integer.class);
				builder.add("type", String.class);
				builder.add("length", Double.class);
				builder.add("radius", Double.class);
				builder.add("center", Coordinate.class);

				ROAD_SEGMENT_TYPE = builder.buildFeatureType();

			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return ROAD_SEGMENT_TYPE;
	}

	public static SimpleFeatureType getGridType (ReferencedEnvelope  gridBounds) {

		if(null == GRID_TYPE) {
			SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
			tb.setName("grid");
			tb.add(GridFeatureBuilder.DEFAULT_GEOMETRY_ATTRIBUTE_NAME, Polygon.class, gridBounds.getCoordinateReferenceSystem());
			tb.add("id", Integer.class);

			GRID_TYPE = tb.buildFeatureType();

		}
		return GRID_TYPE;
	}

	public static SimpleFeatureType getRoadRadialFeatureType(String epsg) {

		if(null == ROAD_RADIAL_TYPE) {
			try {
				SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
				builder.setName("RoadRadial");
				//builder.setCRS(DefaultGeographicCRS.WGS84); // <- Coordinate reference system
				//builder.setCRS(CRS.decode("EPSG:21096"));
				builder.setCRS(CRS.decode(epsg));
				builder.add("line", LineString.class);
				builder.add("index", Integer.class);
				builder.add("feature", Integer.class);

				ROAD_RADIAL_TYPE = builder.buildFeatureType();

			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return ROAD_RADIAL_TYPE;
	}


	public static SimpleFeatureSource makeGridSource(ReferencedEnvelope envelope, FeatureLayer layer,  double vertexSpacing ) {	 

		SimpleFeatureSource grid = Grids.createSquareGrid(envelope, vertexSpacing);
		return grid;
	}
	public static void refreshLayer(FeatureLayer layer) {
		layer.setVisible(false);
		layer.setVisible(true);
	}



	public static void  addFeature(MapContent map, ListFeatureCollection collection, FeatureLayer layer,  Geometry geometry, String epsg) {
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(getRoadSegmentFeatureType(epsg));
		featureBuilder.add(geometry);
		SimpleFeature feature = featureBuilder.buildFeature(null);
		// Add feature 2, 3, etc

		collection.add(feature); //Add feature 1

		map.removeLayer(layer);
		Style lineStyle = SLD.createLineStyle(Color.BLUE, 1);
		layer = new FeatureLayer(collection, lineStyle);
		layer.setTitle("Construction Lines");
		map.addLayer(layer);
	}

	public void snap(SimpleFeatureSource source, DirectPosition2D position){
		/*
		try (SimpleFeatureIterator itr = source.getFeatures().features()) { 
			while (itr.hasNext()) {
				SimpleFeature f = itr.next();
				Geometry snapee = (Geometry) f.getDefaultGeometry();
				WKTWriter writer = new WKTWriter();
				Filter filter = ECQL.toFilter("DWITHIN(\"the_geom\",'" + writer.write(snapee) + "'," + MAX_SEARCH_DISTANCE + "," + "kilometers" + ")");
				SimpleFeatureCollection possibles = indexed.subCollection(filter);

				double minDist = Double.POSITIVE_INFINITY;
				SimpleFeature bestFit = null;
				Coordinate bestPoint = null;
				try (SimpleFeatureIterator pItr = possibles.features()) {
					while (pItr.hasNext()) {
						SimpleFeature p = pItr.next();
						Geometry line = (Geometry) p.getDefaultGeometry();

						double dist = snapee.distance(line);
						if (dist < minDist) {
							minDist = dist;
							bestPoint = DistanceOp.nearestPoints(snapee, line)[1]; // google DistanceOp
							bestFit  = p;
						}
						longiOut = bestPoint.x;
						latiOut = bestPoint.y;
						rowOut[0] = bestFit.getID();
						rowOut[1] = Double.toString(minDist);
						rowOut[2] = Double.toString(longiOut);
						rowOut[3] = Double.toString(latiOut);

						//rowOut = {bestFit.getID(), Double.toString(minDist), Double.toString(longiOut), Double.toString(latiOut)};

					}
					csvWriter.writeNext(rowOut);
					progress ++;
					remn = progress % 1000000;
					if(remn == 0){
						System.out.println("Just snapped line" + progress);
					}

				}

			}
		}//*/
	}



	/**
	 * Retrieve information about the feature geometry
	 */
	public static GeomType getGeometryType(FeatureSource source) {
		GeometryDescriptor geomDesc = source.getSchema().getGeometryDescriptor();
		String geometryAttributeName = geomDesc.getLocalName();

		Class<?> clazz = geomDesc.getType().getBinding();
		GeomType geometryType = null;
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

	public static Coordinate  toCoordinate(DirectPosition2D point) {
		return new Coordinate(point.getX(), point.getY());
	}

	public static Point  toJTSPoint(DirectPosition2D point) {
		return new GeometryFactory().createPoint(new Coordinate(point.getX(), point.getY()));
	}

	public static Point  makePoint(double x, double y) {
		return new GeometryFactory().createPoint(new Coordinate(x, y));
	}

	public static Point  makePoint(Coordinate coord) {
		return new GeometryFactory().createPoint(coord);
	}

	public static FeatureTypeStyle getLabeledMarkerStyle(String field) {

		StyleBuilder sb = new StyleBuilder();
		TextSymbolizer textSymbolizer = sb.createTextSymbolizer(sb.createFill(Color.BLACK),
				new Font[] { sb.createFont("Lucida Sans", 10), sb.createFont("Arial", 10) },
				sb.createHalo(), sb.attributeExpression(field), null, null);


		Mark circle = sb.createMark(StyleBuilder.MARK_SQUARE, Color.GREEN);
		
		Graphic graph2 = sb.createGraphic(null, circle, null, 1, 4, 0);
		PointSymbolizer pointSymbolizer = sb.createPointSymbolizer(graph2);

		FeatureTypeStyle fts = sb.createFeatureTypeStyle("labelPointFeature", new Symbolizer[] { textSymbolizer, pointSymbolizer });
		
		//Style style = builder.createStyle(pointSymbolizer);
		//style.addFeatureTypeStyle(fts);
		return fts;
	}

	public static Style getMarkerStyle() {
		return SLD.createPointStyle("Circle", Color.RED, Color.ORANGE, 0.5f, 10);
	}
	public static Style getLineStyle() {
		return SLD.createLineStyle(Color.BLUE, 1);
	}

	public static Style getRasterStyle() {
		final RasterSymbolizer symbolizer = sf.getDefaultRasterSymbolizer();
		return SLD.wrapSymbolizers(symbolizer);
	}
	/**
	 * This method examines the names of the sample dimensions in the provided
	 * coverage looking for "red...", "green..." and "blue..." (case insensitive
	 * match). If these names are not found it uses bands 1, 2, and 3 for the red,
	 * green and blue channels. It then sets up a raster symbolizer and returns
	 * this wrapped in a Style.
	 *
	 * @return a new Style object containing a raster symbolizer set up for RGB
	 *         image
	 */
	public static  Style createRGBStyle(GridCoverage2D cov) {

		// We need at least three bands to create an RGB style
		int numBands = cov.getNumSampleDimensions();
		if (numBands < 3) {
			return createGreyscaleStyle(1);
		}
		// Get the names of the bands
		String[] sampleDimensionNames = new String[numBands];
		for (int i = 0; i < numBands; i++) {
			GridSampleDimension dim = cov.getSampleDimension(i);
			sampleDimensionNames[i] = dim.getDescription().toString();
		}
		final int RED = 0, GREEN = 1, BLUE = 2;
		int[] channelNum = { -1, -1, -1 };
		// We examine the band names looking for "red...", "green...", "blue...".
		// Note that the channel numbers we record are indexed from 1, not 0.
		for (int i = 0; i < numBands; i++) {
			String name = sampleDimensionNames[i].toLowerCase();
			if (name != null) {
				if (name.matches("red.*")) {
					channelNum[RED] = i + 1;
				} else if (name.matches("green.*")) {
					channelNum[GREEN] = i + 1;
				} else if (name.matches("blue.*")) {
					channelNum[BLUE] = i + 1;
				}
			}
		}
		// If we didn't find named bands "red...", "green...", "blue..."
		// we fall back to using the first three bands in order
		if (channelNum[RED] < 0 || channelNum[GREEN] < 0 || channelNum[BLUE] < 0) {
			channelNum[RED] = 1;
			channelNum[GREEN] = 2;
			channelNum[BLUE] = 3;
		}
		// Now we create a RasterSymbolizer using the selected channels
		SelectedChannelType[] sct = new SelectedChannelType[cov.getNumSampleDimensions()];
		ContrastEnhancement ce = sf.contrastEnhancement((ff).literal(1.0), ContrastMethod.NORMALIZE);
		for (int i = 0; i < 3; i++) {
			sct[i] = sf.createSelectedChannelType(String.valueOf(channelNum[i]), ce);
		}
		RasterSymbolizer sym = sf.getDefaultRasterSymbolizer();
		ChannelSelection sel = sf.channelSelection(sct[RED], sct[GREEN], sct[BLUE]);
		sym.setChannelSelection(sel);

		return SLD.wrapSymbolizers(sym);
	}

	public static  Style createGreyscaleStyle(int band) {
		ContrastEnhancement ce = new ContrastEnhancementImpl();// sf.contrastEnhancement(ff.literal(1.0),
		// new Normalize());
		SelectedChannelType sct = sf.createSelectedChannelType(String.valueOf(band), ce);

		RasterSymbolizer sym = sf.getDefaultRasterSymbolizer();
		ChannelSelection sel = sf.channelSelection(sct);
		sym.setChannelSelection(sel);

		return SLD.wrapSymbolizers(sym);
	}

	public static Style getGridLineStyle() {
		//Style style = SLD.createLineStyle(Color.GRAY, 1);
		StyleBuilder builder = new StyleBuilder();
		LineSymbolizer lineSymbolizer = builder.createLineSymbolizer(Color.GRAY);
		float[] dashArray = {1,4,1,4};
		Stroke stroke = builder.createStroke(Color.GRAY, 1, dashArray);
		lineSymbolizer.setStroke(stroke);
		Style style = builder.createStyle(lineSymbolizer);
		return style;
	}
}
