package xyz.geosure.roadgis;

import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.JFrame;

import org.geotools.map.MapContent;
import org.geotools.swing.JMapFrame;

import xyz.geosure.roadgis.controllers.HorizontalDesignController;
import xyz.geosure.roadgis.controllers.UIActionsController;
import xyz.geosure.roadgis.controllers.VerticalDesignController;

public class RoadGISApplication extends JMapFrame {

	private static final long serialVersionUID = 1L;
	private static RoadGISApplication app = null;
	static MapContent map = new MapContent();

	private static UIActionsController uiActionsHandler = null;

	private HorizontalDesignController hDesign;
	private VerticalDesignController vDesign;

	//RoadDesignGIS roadMaker = new RoadDesignGIS();
	// class initialization
	public  RoadGISApplication()    {
		super(map);
		setTitle("Road Design GIS" );
		
		// Initialize the JMapFrame 
		enableLayerTable( true );
		enableToolBar(true);
		enableStatusBar(true);

		setSize(1024, 724) ;
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  

		//Initialize the Design API Controller
		hDesign = new HorizontalDesignController(this); 

	}


	public static void main(String[] args)    {
		if(null == app) {
			app = new RoadGISApplication ();
		}

		//Create the Handler that shall respond to UI actions
		uiActionsHandler = new UIActionsController(app);		
		uiActionsHandler.initializeToolbar();		
		uiActionsHandler.initializeMenu();

		if (app.getMouseListeners().length==0) {
			// add mouse listener if not already included
			app.addMouseListener(uiActionsHandler);
			app.addMouseMotionListener(uiActionsHandler);
		}
	}
	
	// rest horizontal design database & settings
	public void resetHDesign() {
		int i=0 ;
		hDesign.setContourImage(null);
		//hDesign.toolbarIndex=0 ;
		// clear all DBs
		for (i=0; i<hDesign.getRoadDesign().getHorizontalAlignmentMarkCount(); i++) {
			hDesign.getRoadDesign().getHorizontalAlignmentMarks().get(i).reset();
		}
		for (i=0; i<hDesign.getRoadDesign().getElevationMarkCount(); i++) {
			hDesign.getRoadDesign().getElevationMarks().get(i).reset();
		}
		for (i=0; i<hDesign.gethRoadDataCount();i++) {
			hDesign.getRoadDesign().getHorizontalAlignmentSegments().get(i).reset();
		}

		//hDesign.getRoadDesign().setHorizontalAlignmentMarkCount( 0);
		//hDesign.getRoadDesign().setElevationMarkCount(0);
		//hDesign.sethRoadDataCount(0);
		//hDesign.repaint();        
	}   // reset hDesign

	/**
	 * find the helpset file and create a HelpSet object
	 */
	/*
    public HelpSet getHelpSet(String helpsetfile) {
          HelpSet hs = null;
          ClassLoader cl = this.getClass().getClassLoader();
          try {
            URL hsURL = HelpSet.findHelpSet(cl, helpsetfile);
            hs = new HelpSet(null, hsURL);
          } catch(Exception ee) {
            System.out.println("HelpSet: "+ee.getMessage());
            System.out.println("HelpSet: "+ helpsetfile + " not found");
          }
          return hs;
    }
	 */

	public void windowClosing(WindowEvent e)    {
		//dispose();
		System.exit(0);
	}

	public void windowOpened(WindowEvent e)    { }
	public void windowIconified(WindowEvent e){ }
	public void windowClosed(WindowEvent e)    { 

	}
	public void windowDeiconified(WindowEvent e)    { }
	public void windowActivated(WindowEvent e)    { }
	public void windowDeactivated(WindowEvent e)    { }

	/*
	public void addShapefile(String shpFile) {
	 FileDataStore dataStore = FileDataStoreFinder.getDataStore(shpFile);
	    SimpleFeatureSource shapefileSource = dataStore.getFeatureSource();       
	    Style shpStyle = SLD.createPolygonStyle(Color.RED, null, 0.0f);
	    Layer shpLayer = new FeatureLayer(shapefileSource, shpStyle);       
	    map.addLayer(shpLayer);
	    show();    
	 }
	 public void addraster(File rasterFile) throws Exception {         
	    AbstractGridFormat format = GridFormatFinder.findFormat( rasterFile );
	    reader = format.getReader(rasterFile);     
	    Style rasterStyle = createGreyscaleStyle(1);      
	    Layer rasterLayer = new GridReaderLayer(reader, rasterStyle);
	    map.addLayer(rasterLayer);        
	    show();
	    }
//*/

	public JMapFrame getMapFrame() {
		return this;
	}

	public HorizontalDesignController getHorizontalDesign() {
		return hDesign;
	}
	public VerticalDesignController getVerticalDesign() {
		return vDesign;
	}
	public RoadGISApplication setVerticalDesign(VerticalDesignController design) {
		this.vDesign = design;
		return this;
	}
	
	public URL getResource(String resource){
		return getClass().getResource(resource);
	}

	public static UIActionsController getUiActionsHandler() {
		return uiActionsHandler;
	}


	public static void setUiActionsHandler(UIActionsController uiActionsHandler) {
		RoadGISApplication.uiActionsHandler = uiActionsHandler;
	}

}   // RoadDesignGISApplication
