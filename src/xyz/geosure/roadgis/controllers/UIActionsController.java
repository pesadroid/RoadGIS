package xyz.geosure.roadgis.controllers;
/*
 * toolbar.java
 * Horizontal curve design toolbar.
 *
 * Created on March 16, 2006, 8:25 PM
 */

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JToolTip;

import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.CursorTool;

import xyz.geosure.roadgis.RoadGISApplication;
import xyz.geosure.roadgis.actions.AddLayerAction;
import xyz.geosure.roadgis.actions.ElevationMarkerInsertAction;
import xyz.geosure.roadgis.actions.HorizontalCurveCreateAction;
import xyz.geosure.roadgis.actions.HorizontalLineCreateAction;
import xyz.geosure.roadgis.actions.HorizontalMarkerAction;
import xyz.geosure.roadgis.actions.HorizontalSegmentAlignAction;
import xyz.geosure.roadgis.actions.InterpolatePointsAction;
import xyz.geosure.roadgis.views.RoadGISMenu;

public class UIActionsController implements MouseListener,  MouseMotionListener, KeyListener{
	public final static String LAYER_IMPORT = "layer_import";
	public final static String INTERPOLATE = "interpolate"; 
	public final static String LINE = "Line";
	public final static String CURVE = "Curve"; 
	public final static String MODIFY = "modify";
	public final static String HORIZONTAL_ALIGN =  "hAlign1";
	public final static String MARKER_HORIZONTAL= "markerH";
	public final static String MARKER_INSERT ="markerInsert";
	public final static String VERTICAL_ALIGN ="vertical_align1";

	private static RoadGISApplication app = null;

	int status = -1;        //. toolbar selecction
	private String currentCommand = "";
	JToolTip myToolTip = new JToolTip();

	private RoadGISMenu appMenu = null;
	// 4/21/06 modified
	private JMenuItem edit_undo  ;               // menu item handled by hDesign methods
	private JMenuItem edit_redo  ;
	private JMenuItem edit_delete  ;

	private final static ArrayList<String> alignmentDesignCommands = new ArrayList<String>(
			Arrays.asList(LAYER_IMPORT, INTERPOLATE, LINE, CURVE, HORIZONTAL_ALIGN, MARKER_HORIZONTAL, MARKER_INSERT, VERTICAL_ALIGN));

	// class construction
	public UIActionsController(RoadGISApplication gisApp)    {
		this.app = gisApp;
	}

	public void initializeToolbar()    {

		//Add a Toolbar to separate from the GIS Tools
		app.getToolBar().addSeparator();

		for (String command: alignmentDesignCommands) {
			URL url  = getImageResource(command);

			System.out.println(command + " gives :" + url);

			JButton button = new JButton();
			button.setActionCommand(command);
			try {
				Image img = app.getToolkit().getImage(url);
				if(null != img) {
					button.setIcon(new ImageIcon(img));
				}
			}catch(Exception e) {
				//No iCON
				e.printStackTrace();
			}

			if(command.equalsIgnoreCase(LAYER_IMPORT)){
				button.addActionListener(new AddLayerAction(app));

			}else if(command.equalsIgnoreCase(INTERPOLATE)){
				button.addActionListener(new InterpolatePointsAction(app));

			}else if(command.equalsIgnoreCase(LINE)) {//Line Tool
				/*
				 * When the user clicks the button we want to enable
				 * our custom feature selection tool. Since the only
				 * mouse action we are intersted in is 'clicked', and
				 * we are not creating control icons or cursors here,
				 * we can just create our tool as an anonymous sub-class
				 * of CursorTool.
				 */
				button.addActionListener(new HorizontalLineCreateAction(app));

			}else if(command.equalsIgnoreCase(CURVE)) {//Curve Tool

				//setCurrentCommand(CURVE);
				button.addActionListener(new HorizontalCurveCreateAction(app) );

			}else if(command.equalsIgnoreCase(HORIZONTAL_ALIGN)) {//H Align Tool

				//setCurrentCommand(CURVE);
				button.addActionListener(new HorizontalSegmentAlignAction(app) );

			}else if(command.equalsIgnoreCase(MARKER_HORIZONTAL)) {//H Marker Tool

				//setCurrentCommand(CURVE);
				button.addActionListener(new HorizontalMarkerAction(app) );
				
			}else if(command.equalsIgnoreCase(MARKER_INSERT)) {//H Marker Tool

				//setCurrentCommand(CURVE);
				button.addActionListener(new ElevationMarkerInsertAction(app) );
				
				
			}else if(command.equalsIgnoreCase(VERTICAL_ALIGN)) {//Curve Tool

				button.addActionListener(e -> app.getMapFrame().getMapPane().setCursorTool(
						new CursorTool() {

							@Override
							public void onMouseClicked(MapMouseEvent ev) {
								//selectFeatures(ev);
								setCurrentCommand(VERTICAL_ALIGN);
								app.getHorizontalDesign().setVerticalAlignmentStatus( true );//Launch Vertical Alignment Mode
							}
						}));
			}else {
				button.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) { 
						//System.exit(0);
						int index = getActionIndex(e.getActionCommand());
						//app.getHorizontalDesign().newstatus(index, e.getActionCommand());

					}
				});

			}

			app.getToolBar().add(button);
		}
		//addSeparator();
	}


	public void initializeMenu() {
		// file menu


		appMenu = new RoadGISMenu(app);

		edit_undo = appMenu.getUndoMenu();
		edit_redo = appMenu.getRedoMenu();
		edit_delete = appMenu.getDeleteMenu();

		app.setJMenuBar(appMenu.getMenuBar());
	}

	public String getActionString(int index)     {
		return alignmentDesignCommands.get(index);
	}

	public static int getActionIndex(String action)     {		
		return alignmentDesignCommands.indexOf(action);
	}

	/*
	public void paint(Graphics g)     {
		Rectangle r = bounds();


		for(int i=0;i<NUM_ICONS;i++)	{
			if(i==status)	    {
				g.setColor(new Color(192,192,192));
				g.fillRect(i*33, 0,32,32);
			}
			//g.setColor(Color.black);
			//g.drawLine(i*33+32, 0,i*33+32, 32);

			g.drawImage(img[i], i*33+4,4,this) ;

			if (i!=status) {
				// button boundary depressed
				g.setColor(Color.white);
				g.drawLine(i*33+1, 1,(i+1)*33-2, 1);
				g.drawLine(i*33+1, 1,i*33+1, 30);
				g.setColor(Color.black);
				g.drawLine(i*33+2, 30,(i+1)*33-2, 30);
				g.drawLine((i+1)*33-2, 2,(i+1)*33-2, 30);
			} else {
				// button pressed
				// button boundary depressed
				g.setColor(Color.black);
				g.drawLine(i*33+1, 1,(i+1)*33-2, 1);
				g.drawLine(i*33+1, 1,i*33+1, 30);
				g.setColor(Color.white);
				g.drawLine(i*33+2, 30,(i+1)*33-2, 30);
				g.drawLine((i+1)*33-2, 2,(i+1)*33-2, 30);
			}
		}   // i
		g.setColor(Color.black);
		g.drawString("MTO/CE/ITS Institute, University of Minnesota", 383, 20) ;
	}   // paint
	//*/

	public static URL getImageResource(String img) {
		URL url = null;
		//try {
		url = app.getResource("/resources/" + img + ".png");
		if (null == url) {
			url = app.getResource("/resources/" + img+".PNG");
		}

		//} catch (IOException ioe) {
		//    System.out.println(url.toString());
		//}
		return url ;
	}   //getImageResource

	public Dimension preferredSize()    {
		return(new Dimension(32,32));
	}

	public String getCurrentCommand() {
		return currentCommand;
	}
	public void setCurrentCommand(String command) {
		this.currentCommand = command;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (null != app.getHorizontalDesign()){
			if (e.isShiftDown() && (e.getKeyChar() == 'z' || e.getKeyChar() == 'Z')) {
				//System.out.println("Zoom Out"); 
				//changeDrawScale(-0.1f);
				//repaint();
			}else if(!e.isShiftDown() && (e.getKeyChar() == 'z' || e.getKeyChar() == 'Z')) {
				//System.out.println("Zoom In");
				//changeDrawScale(0.1f);
				//repaint();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (null != app.getHorizontalDesign()){
			int deltaX = 0, deltaY = 0;
			if(e.getKeyCode()== KeyEvent.VK_RIGHT) {
				System.out.println("draw.moveRight()");
				//deltaX = delta;
			}else if(e.getKeyCode()== KeyEvent.VK_LEFT) {
				System.out.println("draw.moveLeft()");
				//deltaX = - delta;
			}else if(e.getKeyCode()== KeyEvent.VK_DOWN) {
				System.out.println("draw.moveDown()");
				//deltaY = delta;
			}else if(e.getKeyCode()== KeyEvent.VK_UP) {
				System.out.println("draw.moveUp()");
				//deltaY = -delta;
			}
			/*
            if (draw_scale == 1) {
                translate_delta.X = deltaX;
                translate_delta.Y = deltaY;
            } else {
                translate_delta.X = CInt(deltaX/ draw_scale);
                translate_delta.Y = CInt(deltaY / draw_scale);
                scaledxlate_delta.X = deltaX;
                scaledxlate_delta.Y = deltaY;
            }

            repaint();
            //*/
		}

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}


	@Override
	public void mouseEntered(java.awt.event.MouseEvent mouseEvent) {
	}

	@Override
	public void mouseExited(java.awt.event.MouseEvent mouseEvent) {
	}

	@Override
	public void mousePressed(java.awt.event.MouseEvent mouseEvent) {
		int x = mouseEvent.getX();
		int y = mouseEvent.getY();
		//hDesign.mouseDown(x,y);
	}

	@Override
	public void mouseReleased(java.awt.event.MouseEvent mouseEvent) {
		int x = mouseEvent.getX();
		int y = mouseEvent.getY();
		if ((mouseEvent.getModifiers() & InputEvent.BUTTON1_MASK)
				== InputEvent.BUTTON1_MASK) {
			//hDesign.mouseLeftUp(x,y);    
		}
	}

	@Override
	public void mouseDragged(java.awt.event.MouseEvent mouseEvent) {
		int x = mouseEvent.getX();
		int y = mouseEvent.getY();
		if ((mouseEvent.getModifiers() & InputEvent.BUTTON1_MASK)
				== InputEvent.BUTTON1_MASK) {
			//hDesign.mouseLeftDrag(x,y);
		}
	}
	@Override
	public void mouseMoved(java.awt.event.MouseEvent mouseEvent) {
	}


	@Override
	public void mouseClicked(MouseEvent mouseEvent) {
		//hDesign.mouseClicked(mouseEvent);
	}



}
