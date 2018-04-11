package xyz.geosure.roadgis.model;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.PixelGrabber;

public class ContourImage extends Image{
	private Image image = null ;
	private int imageW = 0, imageH=0 ;  

	public int getWidth() {
		return imageW;
	}

	public void setImageWidth(int imageW) {
		this.imageW = imageW;
	}

	public int getHeight() {
		return imageH;
	}

	public void setImageHeight(int imageH) {
		this.imageH = imageH;
	}

	// process image file
	public void process() {
		PixelGrabber pg=new PixelGrabber(image,0,0,-1,-1,true);
		try{
			if(pg.grabPixels()){
				imageW=pg.getWidth();
				imageH=pg.getHeight();
				//                    int[] op=(int[]) pg.getPixels();
				//                    int[] np=(int[]) new int[w*w];
				//                    g.drawImage(image,0,0,this);
				System.out.println("Image Dimensions (" + imageW + "x" + imageH + ")");
			}
		} catch(InterruptedException ie){
			//horizontalStatusBar.setStatusBarText(1, "Error: "+ie.toString()) ;
			ie.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public Graphics getGraphics() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Object getProperty(String name, ImageObserver observer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageProducer getSource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getHeight(ImageObserver observer) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getWidth(ImageObserver observer) {
		// TODO Auto-generated method stub
		return 0;
	}

}
