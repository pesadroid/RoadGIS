package xyz.geosure.roadgis.actions;

import xyz.geosure.roadgis.RoadGISApplication;

public class SikioAction {
	private RoadGISApplication app = null;

	public SikioAction(RoadGISApplication app) {
		this.app = app;
	}

	/*


	// paint method to draw the panel area
	public void paint(Graphics gr)     {
		Graphics2D g = (Graphics2D)gr ;
		int w=contourImage.getWidth();
		int h=contourImage.getHeight();

		if(null != contourImage){
			if (getDraw_scale() == 1) {
				g.drawImage(contourImage, translate.X, translate.Y, w, h, this);
			} else {
				// scaled repaint
				g.drawImage(contourImage, scaledxlate.X, scaledxlate.Y, ConversionUtils.CInt(w * getDraw_scale()), ConversionUtils.CInt(h * getDraw_scale()), this);
			}

			// line / curve construction
			switch (toolbarIndex) {
			case 3:  // move
				if (mouseHoldDown==true) {
					//g.Clear(PictureBox1.BackColor)
					if (getDraw_scale() == 1 ) {
						g.drawImage(contourImage, translate.X + translate_delta.X, translate.Y + translate_delta.Y, w, h, this);
					} else {
						g.drawImage(contourImage, scaledxlate.X + scaledxlate_delta.X, scaledxlate.Y + scaledxlate_delta.Y, 
								ConversionUtils.CInt(w * getDraw_scale()), ConversionUtils.CInt(h * getDraw_scale()), this);
					}
				}
				break;
			case 4: // // line
				if (line_started && e0!=null && e1!=null) { 
					g.setColor(Color.red) ;
					g.setStroke(new BasicStroke(2));
					g.draw( new Rectangle( e0.X - 2, e0.Y - 2, 4, 4));
					g.draw(new Rectangle( e1.X - 2, e1.Y - 2, 4, 4));
					//currentPen = New Pen(Color.FromArgb(myAlpha, myPenColor), myRoadLaneSizes) //Set up the pen
					g.setColor(roadDesign.getPreferredPenColor()) ;
					g.setStroke(new BasicStroke(ConversionUtils.CInt(roadDesign.getPreferredRoadLaneSizes())));
					g.drawLine(e0.X, e0.Y, e1.X, e1.Y);
					/*System.out.println("e0.x="+e0.X);
                        System.out.println("e0.y="+e0.Y);
                        System.out.println("e1.x="+e1.X);
                        System.out.println("e1.y="+e1.Y);
					 * /
				} 
				break;
			case 5: // // curve
				if (curve_started==true && e1!=null ){
					//currentPen = New Pen(Color.FromArgb(myAlpha, myPenColor), myRoadLaneSizes) //Set up the pen
					g.setColor(roadDesign.getPreferredPenColor()) ;
					g.setStroke(new BasicStroke(ConversionUtils.CInt(roadDesign.getPreferredRoadLaneSizes())));
					int pixelRadius ;
					pixelRadius = ConversionUtils.CInt(roadDesign.getCurveRadius() * roadDesign.getImageScale() * getDraw_scale()) ; // trandform to draw scale
					g.drawOval( e1.X - pixelRadius, e1.Y - pixelRadius, pixelRadius * 2, pixelRadius * 2);
					//System.out.println("e0.x="+e0.X);
					//System.out.println("e0.y="+e0.Y);
					//System.out.println("r="+pixelRadius);

				}
				break;
			}   // end switch
			// ==============================
			int i ;
			mPoint p1, p2 ;
			// 11/16/06 modified
			if (viewRoadOnly_flag) {
				// view road only
				if (roadDesign.getElevationMarkCount() >= 2 ) {
					// 2 or more elevation data exists
					for (i=1; i<roadDesign.getElevationMarkCount(); i++) {
						p1 = drawTransform(roadDesign.getElevationMarks()[i-1].getLocation());//   // start point
						p2 = drawTransform(roadDesign.getElevationMarks()[i].getLocation());//   // end point
						int curveID, pixelRadius ;
						float myRadius ;
						mPoint pc ;
						double start_angle, end_angle, angle_len ;
						//g.setColor(myDB.gethRoadData()[0].getPenColor()) ;
						g.setColor(roadDesign.getPreferredPenColor()) ;
						g.setStroke(new BasicStroke(ConversionUtils.CInt(roadDesign.getPreferredRoadLaneSizes()*1.5))); // 2/9/07
						//System.out.println("type="+myDB.getElevationMarks()[i-1].getSegmentType()) ;
						switch (roadDesign.getElevationMarks()[i-1].getSegmentType()) {
						case 1: // line
							g.drawLine( p1.X, p1.Y, p2.X, p2.Y);
							break ;
						case 2: // Curve
							curveID = roadDesign.getElevationMarks()[i-1].getParentIndex() ;
							myRadius = roadDesign.gethRoadData()[curveID].getRadius() * getDraw_scale();

							pixelRadius = ConversionUtils.CInt(myRadius * roadDesign.getImageScale());
							pc = drawTransform(roadDesign.gethRoadData()[curveID].getPoint1()) ;   // curve center
							start_angle = VectorUtils.vectorAngle(pc, p1) ;
							end_angle = VectorUtils.vectorAngle(pc, p2) ;
							angle_len = end_angle - start_angle ;
							//System.out.println("start, end, len b4="+start_angle+","+end_angle+","+angle_len) ;
							angle_len = processAngle(angle_len) ; 
							//System.out.println("len after="+angle_len) ;
							//System.out.println("ID, radius="+curveID + ","+pixelRadius) ;
							//System.out.println("vec1 len="+VectorUtils.vectorLen(VectorUtils.vector(pc, p1))) ;
							//System.out.println("vec2 len="+VectorUtils.vectorLen(VectorUtils.vector(pc, p2))) ;

							g.drawArc(pc.X - pixelRadius, pc.Y - pixelRadius, pixelRadius * 2, pixelRadius * 2, 
									ConversionUtils.CInt(start_angle), ConversionUtils.CInt(angle_len));

							break ;
						case 3: // tangent
							if (roadDesign.getElevationMarks()[i].getSegmentType()==1) {
								// line
								g.drawLine( p1.X, p1.Y, p2.X, p2.Y);
							} else {
								curveID = roadDesign.getElevationMarks()[i-1].getParentIndex() ;
								//System.out.println("landmark index="+(i+1)) ;
								//System.out.println("curve ID="+curveID) ;
								myRadius = roadDesign.gethRoadData()[curveID].getRadius() * getDraw_scale();
								pixelRadius = ConversionUtils.CInt(myRadius * roadDesign.getImageScale());
								pc = drawTransform(roadDesign.gethRoadData()[curveID].getPoint1()) ;
								start_angle = VectorUtils.vectorAngle(pc, p1) ;
								end_angle = VectorUtils.vectorAngle(pc, p2) ;
								angle_len = end_angle - start_angle ;
								angle_len = processAngle(angle_len) ;
								g.drawArc(pc.X - pixelRadius, pc.Y - pixelRadius, pixelRadius * 2, pixelRadius * 2, 
										ConversionUtils.CInt(start_angle), ConversionUtils.CInt(angle_len));
								// curve
							}   // if
							break ;
						}   // switch
					}   // for i
					// end view road only
				} else {
					popMessageBox("View Road Only","Please place at least 2 elevation landmarks!");
					viewRoadOnly_flag = false ;
				}
			} else {
				// view design including construct line/curve
				if (hRoadDataCount > 0) {
					for (i=0;i<hRoadDataCount;i++){
						if (!roadDesign.gethRoadData()[i].isDeleted()) {
							// segment is not deleted
							// repaint data
							if (roadDesign.gethRoadData()[i].getRadius() > 0f) {
								// curve
								float myRadius ;
								p1 = drawTransform(roadDesign.gethRoadData()[i].getPoint1());
								g.setColor(Color.red) ;
								g.setStroke(new BasicStroke(2));
								g.draw(new Rectangle( ConversionUtils.CInt(p1.X - getEndMarkSize() ),
										ConversionUtils.CInt(p1.Y - getEndMarkSize() ), 
										ConversionUtils.CInt(2 * getEndMarkSize() ), 
										ConversionUtils.CInt(2 * getEndMarkSize() ))) ;// // center
								myRadius = roadDesign.gethRoadData()[i].getRadius() * getDraw_scale();
								//myCurPen = New Pen(Color.FromArgb(myAlpha, hRoadData(i).getPenColor()), hRoadData(i).getPenWidth) //Set up the pen
								g.setColor(roadDesign.gethRoadData()[i].getPenColor()) ;
								g.setStroke(new BasicStroke(ConversionUtils.CInt(roadDesign.getPreferredRoadLaneSizes())));
								int pixelRadius;
								pixelRadius = ConversionUtils.CInt(myRadius * roadDesign.getImageScale());
								g.drawOval(p1.X - pixelRadius, p1.Y - pixelRadius, pixelRadius * 2, pixelRadius * 2);
							} else {
								// line
								p1 = drawTransform(roadDesign.gethRoadData()[i].getPoint1());//   // start point
								p2 = drawTransform(roadDesign.gethRoadData()[i].getPoint2());//   // end point
								g.setColor(Color.red) ;
								g.setStroke(new BasicStroke(2));
								g.draw(new Rectangle( ConversionUtils.CInt(p1.X - getEndMarkSize() ), 
										ConversionUtils.CInt(p1.Y - getEndMarkSize() ), 
										ConversionUtils.CInt(2 * getEndMarkSize() ), 
										ConversionUtils.CInt(2 * getEndMarkSize() )));
								g.draw(new Rectangle( ConversionUtils.CInt(p2.X - getEndMarkSize() ), 
										ConversionUtils.CInt(p2.Y - getEndMarkSize() ), 
										ConversionUtils.CInt(2 * getEndMarkSize() ), 
										ConversionUtils.CInt(2 * getEndMarkSize() )));
								//myCurPen = New Pen(Color.FromArgb(myAlpha, hRoadData(i).getPenColor()), hRoadData(i).getPenWidth); ////Set up the pen
								g.setColor(roadDesign.gethRoadData()[i].getPenColor()) ;
								g.setStroke(new BasicStroke(ConversionUtils.CInt(roadDesign.getPreferredRoadLaneSizes())));
								g.drawLine( p1.X, p1.Y, p2.X, p2.Y);
							}   // if line or curve
						}   // if not deleted
					}   // for
				}   //hRoadDataCount
			}   // if viewRoadOnly_flag

			// hAlignMarks
			if (roadDesign.gethAlignMarkCount() > 0) { 
				for (i=0;i<roadDesign.gethAlignMarkCount();i++){
					p1 = drawTransform(roadDesign.gethAlignMarks()[i].getLocation());  //   // tangent point
					g.setColor(Color.red) ;
					g.setStroke(new BasicStroke(2));
					g.drawOval( p1.X - 2, p1.Y - 2, 4, 4);
				}
			}

			// elevation markers
			//elevationMarkerPen = New Pen(Color.FromArgb(myAlpha, elevationMarkerColor), elevationMarkerSize) //Set up elevation marker pen
			g.setColor(roadDesign.getElevationMarkerColor()) ;
			g.setStroke(new BasicStroke(roadDesign.getElevationMarkerSize()));
			if (roadDesign.getElevationMarkCount() > 0 ) {
				for (i=0;i<roadDesign.getElevationMarkCount();i++){
					p1 = drawTransform(roadDesign.getElevationMarks()[i].getLocation()) ;//  // marker point
					g.drawOval( p1.X - 2, p1.Y - 2, 4, 4);
				}
			}

			// animation
			/*
            If animationFlag Then
                // animation ON
                // draw a starting mark only
                If getPreferredUnit() = 1 Then
                    // US
                    p1 = drawTransform(New PointF(animatedVehPos.X / FT2M * imageScale, animatedVehPos.Z / FT2M * imageScale))
                ElseIf getPreferredUnit() = 2 Then
                    p1 = drawTransform(New PointF(animatedVehPos.X * imageScale, animatedVehPos.Z * imageScale))
                End If
                Dim pur_pen4 As Pen = New Pen(Color.Purple, 4)
                g.DrawEllipse(pur_pen4, p1.X - 5, p1.Y - 5, 10, 10)

            End If
			 //* /        

		}   // null != image?
		else {
			Rectangle r = bounds();
			if(grid>0)
			{
				g.setColor(new Color(224,224,224)); // sub grid lines

				for(int i=grid;i<r.height;i+=grid)
					g.drawLine(0,i,r.width,i);
				for(int i=grid;i<r.width;i+=grid)
					g.drawLine(i,0,i,r.height);

				g.setColor(new Color(184,184,184)); // major grid lines

				for(int i=grid*10;i<r.height;i+=grid*10)
					g.drawLine(0,i,r.width,i);
				for(int i=grid*10;i<r.width;i+=grid*10)
					g.drawLine(i,0,i,r.height);
			}   // draw grid
		} // image=null?
	}   // end of paint
	

	public void imageResize() {
		Rectangle r = bounds();
		scaledxlate.X = ConversionUtils.CInt(0.5f * r.width * (1 - getDraw_scale()) + getDraw_scale() * translate.X);
		scaledxlate.Y = ConversionUtils.CInt(0.5f * r.height * (1 - getDraw_scale()) + getDraw_scale() * translate.Y);
		repaint();
		//PictureBox1.Invalidate()
	}
	

	public void changeDrawScale(float scale) {
		setDraw_scale(getDraw_scale() + scale) ;
		if (getDraw_scale() > 5.0f) {
			setDraw_scale(5.0f);
		}
		else if (getDraw_scale() < 0.1f) { 
			setDraw_scale(0.1f) ;
		}
		getHorizontalStatusBar().setStatusBarText(3, new Float(Math.round(getDraw_scale()*10f)/10f).toString()) ;
		imageResize() ;
	}
	

	// transform mouse click position on the screen to pixel location on the digital map
	public mPointF transform(mPoint input) {
		mPointF ptf = new mPointF(0f,0f);
		if (getDraw_scale() == 1f) {
			ptf.X = (input.X - translate.X);
			ptf.Y = (input.Y - translate.Y);
		} else {
			ptf.X = (input.X - scaledxlate.X) / getDraw_scale();
			ptf.Y = (input.Y - scaledxlate.Y) / getDraw_scale();
		}
		return ptf;
	}
	
	
	//public boolean mouseDown(Event e, int x, int y)
	public boolean mouseDown(int x, int y)    {
		getHorizontalStatusBar().setStatusBarText(2, Integer.toString(x)+","+Integer.toString(y)) ;

		System.out.println("Selected a Segment");
		switch (toolbarIndex) {
		case 3:  // move
			e0 = new mPoint(x, y) ;
			mouseHoldDown = true ;
			break ;
		case 4: //   line
			line_started = true;
			e0 = new mPoint(x, y) ;
			//System.out.println("line-down");
			break ;
		case 5: //  // curve
			// construct curve
			// if e.M MouseButtons.Left {
			curve_started = true;
			e0 = new mPoint(x, y) ;
			// }
			break ;
		case 6: //  // modify line/curve
			// check if click a point or a line
			modificationInfo = searchSegmentDB(transform(new mPoint(x, y))) ;
			//System.out.println("X,Y = " + modificationInfo.X + "," + modificationInfo.Y) ;

			if (modificationInfo.X >= 0 && modificationInfo.Y >= 0) {
				// closest segment terminal found
				modification_started = true;
			}
			break ;
		}   // end switch
		return(true);
	}


	//public boolean mouseDrag(Event e, int x, int y)
	public boolean mouseLeftDrag(int x, int y)    {
		getHorizontalStatusBar().setStatusBarText(2, Integer.toString(x)+","+Integer.toString(y)) ;
		//System.out.println("toolbar index=" + toolbarIndex) ;
		switch (toolbarIndex){
		case 3: // move
			if (null != contourImage ){
				if (e0!=null) { // Is Nothing And mouseHoldDown Then
					if (draw_scale == 1) {
						translate_delta.X = x - e0.X;
						translate_delta.Y = y - e0.Y;
					} else {
						translate_delta.X = ConversionUtils.CInt((x - e0.X) / draw_scale);
						translate_delta.Y = ConversionUtils.CInt((y - e0.Y) / draw_scale);
						scaledxlate_delta.X = (x - e0.X);
						scaledxlate_delta.Y = (y - e0.Y);
					}

					e1 = new mPoint(x,y);
					repaint();
				}
			}  // if image
			else {    // g is not defined
				popMessageBox("No Contour Map", NO_MAP_MSG);
				toolbarIndex = 0;
			} // else
			break;
		case 4 :     // line 
			if (null != contourImage ){
				e1 = new mPoint(x,y);
				repaint();
			}
			else {    // g is not defined
				popMessageBox("No Contour Map", NO_MAP_MSG);
				toolbarIndex = 0;
			} // else
			break;
		case 5 :     //  curve
			if (null != contourImage ){
				e1 = new mPoint(x,y);
				repaint();
			}
			else {    // g is not defined
				popMessageBox("No Contour Map", NO_MAP_MSG);
				toolbarIndex = 0;
			} // else
			break;
		case 6:     // modify

			if (null != contourImage ){
				//System.out.println("modification_started=" + modification_started) ;
				if (modification_started) { // modify end control point 

					int dataIndex = modificationInfo.X;
					int pointId = modificationInfo.Y;
					//System.out.println("x, y = " + dataIndex + ", " + pointId) ;

					roadDesign.gethRoadData()[dataIndex].modifyPoint(pointId, transform(new mPoint(x,y)));
					//System.out.println("idx="+dataIndex+", id="+pointId);
					repaint();
				}
				e1 = new mPoint(x,y);
			}
			else {    // g is not defined
				popMessageBox("No Contour Map", NO_MAP_MSG);
				toolbarIndex = 0;
			} // else

			break;

		} // end switch
		return (true);
	}



	public void mouseClicked(java.awt.event.MouseEvent mouseEvent) {
		if ((mouseEvent.getModifiers() & InputEvent.BUTTON3_MASK)
				== InputEvent.BUTTON3_MASK) {
			mPoint e = new mPoint(mouseEvent.getX(),mouseEvent.getY());
			mPointF marker ;

			// right Mouse clicked
			switch (toolbarIndex){
			case 5:	// curve
				idSegment = -1 ;
				mPoint radMod = searchSegmentDB(transform(e));
				if ((radMod.X >= 0) && (radMod.Y >= 0)) { 
					// found a segment
					if (roadDesign.gethRoadData()[radMod.X].getRadius() > 0f) { 
						// curve center selected
						// modify curve radius
						if (curveSettingsEditPopup != null) { 
							if (curveSettingsEditPopup.isShowing()) {
								curveSettingsEditPopup.dispose();
							}
						}
						idSegment = radMod.X;
						//frame_curveSetting.dispose();
						popEditCurveSettings();
						e0 = null;
						e1 = null;
						//frmEditCurveSetting.segIndex.Text = radMod.X.ToString()
						//frmEditCurveSetting.txtRadius.Text = hRoadData(radMod.X).getRadius().ToString
						//frmEditCurveSetting.Show()
					}
				}
				break;
			case 6: // modify tangent point
				if (roadDesign.gethAlignMarkCount()>1) {
					dataSelIndex = -1;
					marker = transform(e);
					dataSelIndex = checkhAlignTangent(marker);
					//System.out.println("click tanget index="+dataSelIndex) ;
					if (dataSelIndex >= 0 ) {
						// pop window to delete tangent pair ?
						deleteTangent_flag = true ;
						//popDeleteTangent("Delete Tangent Data","Do you want to delete tangent data pair?");
					}
				}
				break;
			case 8: case 9: // edit station marker
				int dataIndex = -1;
				//mPointF marker ;
				marker = transform(e);
				dataIndex = checkElevationLandmarks(marker);
				if (dataIndex >= 0) {
					// comment out 3/4/06, use existing ele landmark point instead of clicked point
					//currentElevationMarker = marker

					// pop screen to enter evelation & save marker data
					if (elevationMarkerEditPopup != null) { 
						if (elevationMarkerEditPopup.isShowing()) {
							elevationMarkerEditPopup.dispose();
						}
					}
					int dVal = dataIndex+1 ;
					sInfo = new StationInfo();
					sInfo.title = "Edit Station (" + dVal + ")";
					sInfo.elevation = roadDesign.getElevationMarks()[dataIndex].getElevation() ;
					sInfo.CheckBox_edit = true;
					sInfo.dataIndex = dataIndex ;
					sInfo.location = new mPointF(mouseEvent.getX(),mouseEvent.getY());
					sInfo.parentId  = roadDesign.getElevationMarks()[dataIndex].getParentIndex();
					sInfo.optionInit(); 
					switch(roadDesign.getElevationMarks()[dataIndex].getSegmentType() ) {
					case 1:
						sInfo.line_option = true;
						break;
					case 2:
						sInfo.curve_option = true;
						break;
					case 3:
						sInfo.tangent_option=true;
						break;
					} 
					popEditElevationMarker(); // display elevation landmark edit screen
				}   // if dataIndex>0
				break;
			default:   // all else
				// added 12/21/2007, chenfu
				checkItemSelect(transform(e));
				//System.out.println("right_clicked str = "+item_clicked_str) ;
				if (item_clicked_str.length()>1) {
					parseClickedStr() ;
					popDeleteSegment("Edit - Delete","Are you sure to delete selected segment(s)?");
				}
				break ;
			}   // switch
		}   // if right mouse
	}   // end function

	//public boolean mouseUp(Event e, int x, int y)
	public boolean mouseLeftUp(int x, int y)    {

		//popMessageBox(x + " , " + y, NO_MAP_MSG);    	
		int dataIndex  = -1;
		int tangentIndex = -1 ;
		mPointF marker = transform(new mPoint(x,y));

		getHorizontalStatusBar().setStatusBarText(2, Integer.toString(x)+","+Integer.toString(y)) ;
		switch (toolbarIndex){
		case 0: // // pointer, select
			checkItemSelect(transform(new mPoint(x,y)));

			repaint();
			break;

		case 3: //  // move
			translate_delta = new mPoint(0, 0);
			scaledxlate_delta = new mPoint(0, 0);
			if  (e1 != null ) {
				mouseHoldDown = false;
				if (draw_scale == 1) { 
					translate.X += e1.X - e0.X;
					translate.Y += e1.Y - e0.Y;
				} else {
					translate.X += ConversionUtils.CInt((e1.X - e0.X) / draw_scale);
					translate.Y += ConversionUtils.CInt((e1.Y - e0.Y) / draw_scale);
					scaledxlate.X += (e1.X - e0.X);
					scaledxlate.Y += (e1.Y - e0.Y);
				}

				e0 = null;
				e1 = null;
				repaint();
			}
			break;
		case 4: //  // line
			if (e1 !=null && e0 != null) { 
				line_started = false;

				roadDesign.gethRoadData()[hRoadDataCount] = new Data2D() ;
				// debug
				//debugWindow.Text &= "P1=" & hRoadData(hRoadDataCount).getPoint1.X & ", " & hRoadData(hRoadDataCount).getPoint1.Y
				//debugWindow.Text &= "P2=" & hRoadData(hRoadDataCount).getPoint2.X & ", " & hRoadData(hRoadDataCount).getPoint2.Y & vbCrLf
				hRoadDataCount += 1;

				// save # of data in log buffer
				push2SegLogBuffer(hRoadDataCount);


				e0 = null;
				e1 = null;
				repaint();
			}
			break;
		case 5: //  // curve
			if ( e1 !=null )  {
				curve_started = false;

				roadDesign.gethRoadData()[hRoadDataCount] = new Data2D();
				roadDesign.gethRoadData()[hRoadDataCount].saveData(transform(e1), roadDesign.getCurveRadius(), roadDesign.getPreferredPenColor(), roadDesign.getPreferredRoadLaneSizes());
				hRoadDataCount += 1;
				// save # of data in log buffer
				push2SegLogBuffer(hRoadDataCount);

				e0 = null;
				e1 = null;
				repaint();
			}
			break;
		case 6:	//  // Modify
			modification_started = false;
			break;
		case 8: //  Set elevation marker
			dataIndex  = -1;
			tangentIndex = -1 ;
			marker = transform(new mPoint(x,y));
			tangentIndex = checkTangentLandmarks(marker);
			if (tangentIndex < 0) { 
				dataIndex = checkMarkLocation(marker);
			} else {
				dataIndex = tangentIndex;
			}
			//System.out.println("dataIndex="+dataIndex+", tangentIndex="+tangentIndex);

			if (dataIndex < 0) { 
				popMessageBox( "Elevation Marker", "Please place marker on line/curve segments");
			} else {
				// comment out 3/4/06, using database point in checkMarkLocation()
				//currentElevationMarker = marker

				// pop screen to enter evelation & save marker data
				if (elevationMarkerPopup != null) {
					if (elevationMarkerPopup.isShowing()) {
						elevationMarkerPopup.dispose();
					}
				}
				sInfo = new StationInfo();
				sInfo.title="Station (" + (roadDesign.getElevationMarkCount() + 1) + ")" ;
				sInfo.CheckBox_edit = false;
				sInfo.parentId = dataIndex;
				sInfo.location = roadDesign.getCurrentElevationMarker() ;
				sInfo.optionInit();
				if (tangentIndex >= 0){
					sInfo.tangent_option = true ;
					sInfo.initial_state = 3 ;   // tangent
				} else if (roadDesign.gethRoadData()[dataIndex].getRadius() > 0) {
					sInfo.curve_option = true ;
					sInfo.initial_state = 2 ;   // curve
				} else {
					sInfo.line_option = true ;
					sInfo.initial_state = 1 ;   // line
				}
				popElevationMarkerForm();
			}
			break;
			// ======================== added 2/28/07 ===============================
		case 9: //  Insert elevation marker
			dataIndex  = -1;
			tangentIndex = -1 ;
			marker = transform(new mPoint(x,y));
			tangentIndex = checkTangentLandmarks(marker);
			if (tangentIndex < 0) { 
				dataIndex = checkMarkLocation(marker);
			} else {
				dataIndex = tangentIndex;
			}
			//System.out.println("dataIndex="+dataIndex+", tangentIndex="+tangentIndex);

			if (dataIndex < 0) { 
				popMessageBox( "Insert Elevation Marker", "Please place marker on line/curve segments");
			} else {
				// comment out 3/4/06, using database point in checkMarkLocation()
				//currentElevationMarker = marker

				// pop screen to enter evelation & save marker data
				if (frmInsertElevationMarker != null) {
					if (frmInsertElevationMarker.isShowing()) {
						frmInsertElevationMarker.dispose();
					}
				}
				sInfo = new StationInfo();

				sInfo.CheckBox_edit = false;
				sInfo.parentId = dataIndex;
				sInfo.location = roadDesign.getCurrentElevationMarker() ;
				sInfo.optionInit();
				if (tangentIndex >= 0){
					sInfo.tangent_option = true ;
					sInfo.initial_state = 3 ;   // tangent
				} else if (roadDesign.gethRoadData()[dataIndex].getRadius() > 0) {
					sInfo.curve_option = true ;
					sInfo.initial_state = 2 ;   // curve
				} else {
					sInfo.line_option = true ;
					sInfo.initial_state = 1 ;   // line
				}
				sInfo.insert = findMarkerInsertIndex() ; 
				sInfo.title="Insert Station (" + (sInfo.insert + 1) + ")" ; 

				popInsertElevationMarker();
			}
			break;
		}   // switch
		return (true) ;
	}
	
	
	// transform location saved onthe DB to relative position on screen
	public mPoint drawTransform(mPointF input) {
		mPoint ptf = new mPoint(0,0);
		if (getDraw_scale() == 1) {
			ptf.X = ConversionUtils.CInt(input.X + translate.X + translate_delta.X);
			ptf.Y = ConversionUtils.CInt(input.Y + translate.Y + translate_delta.Y);
		} else {
			ptf.X = ConversionUtils.CInt(input.X * getDraw_scale() + scaledxlate.X + scaledxlate_delta.X);
			ptf.Y = ConversionUtils.CInt(input.Y * getDraw_scale() + scaledxlate.Y + scaledxlate_delta.Y);
		}

		return ptf;
	}
	
	
	// mouse key down method
	public boolean keyDown(Event e,int k)    {
		return(true);
	}

	// update toolbar index
	public void newstatus(int index, String str)    {
		System.out.println("New Status: " + str);
		getHorizontalStatusBar().setStatusBarText(0, str) ;
		toolbarIndex = index ;
		if (toolbarIndex==0) {
			ptr_edit_delete.setEnabled(true) ;
		} else {
			ptr_edit_delete.setEnabled(false) ;
		}
		//for(grobj j = glist.ghead;j!=null;j=j.next)
		//{    j.select = 0;
		//}
		if (toolbarIndex==4 | toolbarIndex==5 | toolbarIndex==8) {
			// line curve & marker
			ptr_edit_undo.setEnabled(true) ;
			ptr_edit_redo.setEnabled(true) ;
		} else {
			ptr_edit_undo.setEnabled(false) ;
			ptr_edit_redo.setEnabled(false) ;           
		}
		System.out.println("Selected a Segment"+toolbarIndex);
		if (null == contourImage && ! designLinesLayer.isSelected()){
			popMessageBox("No Contour Map", "Image Dimensions");
			//frame_msgbox.toFront() ;
		}

		switch (toolbarUtil.getCurrentCommand()) {
		case ToolbarUtil.LAYER_IMPORT: // Import Layer
			break;//No Action
		case ToolbarUtil.LINE: // line
			if (null == contourImage && ! designLinesLayer.isSelected()){
				popMessageBox("No Contour Map", NO_MAP_MSG);
				//frame_msgbox.toFront() ;
			}
			viewRoadOnly_flag = false ; // 11/16/06
			break ;
		case ToolbarUtil.CURVE: // curve
			if (curveSettingsPopup==null){
				//popCurveSettings();
				popCurveSettings_flag = true ;
			} else {    // not null
				if (curveSettingsPopup.isShowing()==false){
					//popCurveSettings();
					popCurveSettings_flag = true ;
				} else {
					curveSettingsPopup.show();
				}
				//frame_curveSetting.toFront();
			}
			viewRoadOnly_flag = false ; // 11/16/06
			repaint();

			break ;
		case ToolbarUtil.MODIFY: // edit end point
			if (null == contourImage){
				popMessageBox("No Contour Map", NO_MAP_MSG);
				//frame_msgbox.toFront() ;
			}
			viewRoadOnly_flag = false ; // 11/16/06
			break ;
		case ToolbarUtil.HORIZONTAL_ALIGN: // horizontal curve alignment
			if (null == contourImage){
				popMessageBox("No Contour Map", NO_MAP_MSG);
				//frame_msgbox.toFront() ;
			} else {
				tool_curvehAlignMarks();
			}
			viewRoadOnly_flag = false ; // 11/16/06
			break ;
		case ToolbarUtil.MARKER_HORIZONTAL: // place station, marker
			if (null == contourImage){
				popMessageBox("No Contour Map", NO_MAP_MSG);
				//frame_msgbox.toFront() ;
			}
			viewRoadOnly_flag = false ; // 11/16/06
			break ;
		case ToolbarUtil.MARKER_INSERT: // refresh, marker insert
			//repaint();
			// insert landmark
			if (null == contourImage){ 
				popMessageBox("No Contour Map", NO_MAP_MSG);
				//frame_msgbox.toFront() ;
			} 
			viewRoadOnly_flag = false ; 

			break ;
		case ToolbarUtil.VERTICAL_ALIGN: // vertical curve design
			if (null == contourImage){
				popMessageBox("No Contour Map", NO_MAP_MSG);
				//frame_msgbox.toFront() ;
			} else if (roadDesign.getElevationMarkCount() < 2 ) {
				popMessageBox("Vertical Curve Design","Please place at least 2 elevation landmarks first!");
				//frame_msgbox.toFront() ;

				//} else if (design_filename.Length <= 0) { 
				// design filename does not exist

				//result = MessageBox.Show("Save horizontal geometry design?", "Save Design", MessageBoxButtons.OKCancel, MessageBoxIcon.Question, MessageBoxDefaultButton.Button1)
				//If result = DialogResult.OK Then
				//    file_save_Click(Nothing, Nothing)
				//End If

				//startVerticalDesign();
			} else {

				//startVerticalDesign();
				String status = checkLandmarks() ;  // added 3/1/07
				if (status.length()>0) { 
					popMessageBox("Landmark Data Error", "Error at landmark station "+status+
							".\nPlease include tangent point when making \ntransition between line and curve segments.\n"+
							"Use View->Station Landmarks to review data.");
				} else {

					if (frmVerticalAlign==null){
						//    javax.swing.SwingUtilities.invokeLater(new Runnable() {
						//        public void run() {
						popVerticalAlign("Vertical Curve Design"); 
						//        }
						//    });
					} else {    // not null
						if (frmVerticalAlign.isShowing()==false){
							popVerticalAlign("Vertical Curve Design");
						} else {
							frmVerticalAlign.show();
						}
					}   // if (frmVerticalAlign==null)

					//javax.swing.SwingUtilities.invokeLater(new Runnable() {
					//    public void run() {
					//try{
					//    Thread.sleep(500);
					//} catch(InterruptedException e){
					//System.out.println("Sleep Interrupted");
					//}
					frmVerticalAlign.toFront();

					//    }
					//});
				}   // if
			} // if getElevationMarkCount() < 2
			break ;
		}
		//repaint();
	}
	
	designMarksLayer.addMapLayerListener(new MapLayerListener() {

				@Override
				public void layerChanged(MapLayerEvent arg0) {
					System.out.println("designMarksLayer Changed");
				}

				@Override
				public void layerDeselected(MapLayerEvent arg0) {
					System.out.println("designMarksLayer Deselected");					
				}

				@Override
				public void layerHidden(MapLayerEvent arg0) {
					System.out.println("designMarksLayer Hidden");	

				}

				@Override
				public void layerPreDispose(MapLayerEvent arg0) {
					System.out.println("designMarksLayer Pre disposed");	

				}

				@Override
				public void layerSelected(MapLayerEvent arg0) {
					System.out.println("designMarksLayer selected");	

				}

				@Override
				public void layerShown(MapLayerEvent arg0) {
					System.out.println("designMarksLayer Shown");					
				}

			});
			
designLinesLayer.addMapLayerListener(new MapLayerListener() {

				@Override
				public void layerChanged(MapLayerEvent arg0) {
					System.out.println("geometricDesignLayer Changed");
				}

				@Override
				public void layerDeselected(MapLayerEvent arg0) {
					System.out.println("geometricDesignLayer Deselected");					
				}

				@Override
				public void layerHidden(MapLayerEvent arg0) {
					System.out.println("geometricDesignLayer Hidden");	

				}

				@Override
				public void layerPreDispose(MapLayerEvent arg0) {
					System.out.println("geometricDesignLayer Pre disposed");	

				}

				@Override
				public void layerSelected(MapLayerEvent arg0) {
					System.out.println("geometricDesignLayer selected");	

				}

				@Override
				public void layerShown(MapLayerEvent arg0) {
					System.out.println("geometricDesignLayer Shown");					
				}

			});


			radialLinesLayer.addMapLayerListener(new MapLayerListener() {

				@Override
				public void layerChanged(MapLayerEvent arg0) {
					System.out.println("radialLinesLayer Changed");
				}

				@Override
				public void layerDeselected(MapLayerEvent arg0) {
					System.out.println("radialLinesLayer Deselected");					
				}

				@Override
				public void layerHidden(MapLayerEvent arg0) {
					System.out.println("radialLinesLayer Hidden");	

				}

				@Override
				public void layerPreDispose(MapLayerEvent arg0) {
					System.out.println("radialLinesLayer Pre disposed");	

				}

				@Override
				public void layerSelected(MapLayerEvent arg0) {
					System.out.println("radialLinesLayer selected");	

				}

				@Override
				public void layerShown(MapLayerEvent arg0) {
					System.out.println("radialLinesLayer Shown");					
				}

			});

	//*/
}
