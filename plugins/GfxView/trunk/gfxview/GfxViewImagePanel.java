//{{{ imports
import java.beans.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
 //}}}

public class GfxViewImagePanel extends JPanel implements MouseListener,MouseMotionListener,PropertyChangeListener {
	//{{{ constants
  static final int ZOOM = 1;
  static final int UNZOOM = 2;
  static final int UNZOOM_FULL = 3;
	//}}}

	//{{{ variables
	private Image image;
	private int dx1,dy1,dx2,dy2,sx1,sy1,sx2,sy2;
	private int rx1,ry1,rw,rh,rx2,ry2;
	private int imageW,imageH;
	private int panelW,panelH,panelWZ,panelHZ;
	private boolean moveModeActive=false;
	private boolean zoomModeActive=false;
	private double scale;
	private double speed;
	private PropertyChangeSupport changes;
	//}}}


	//{{{ +GfxViewImagePanel() : <init>
	public GfxViewImagePanel() {
		speed = 0.25;

		addMouseListener(this);
		addMouseMotionListener(this);
		
		changes = new PropertyChangeSupport(this);
	} //}}}

	//{{{ +paintComponent(Graphics) : void
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		panelW = this.getWidth();
		panelH = this.getHeight();
		
		if (image!=null) {
			panelWZ = (int)((double)(panelW)/scale);
			panelHZ = (int)((double)(panelH)/scale);
			int blankWZ = (panelWZ>imageW ? panelWZ-imageW : 0);
			int blankHZ = (panelHZ>imageH ? panelHZ-imageH : 0);

			/*
				Different coordinates are geometricaly reduced by one
				as it should be (0,0) ... (width-1,heigth-1) but drawImage
				seems to use (0,0) ... (width,heigth) !
			*/
			sx1 = (sx1 < 0 ? 0 : sx1);
			sy1 = (sy1 < 0 ? 0 : sy1);

			sx2 = sx1 + (panelWZ-blankWZ) -1;
			if (sx2 > imageW-1) {
					sx2 = imageW-1 ;
					sx1 = sx2 - (panelWZ-blankWZ) + 1;
			} 
			sy2 = sy1 + (panelHZ-blankHZ) -1;
			if (sy2 > imageH-1) {
					sy2 = imageH-1;
					sy1 = sy2 - (panelHZ-blankHZ) + 1;
			}
			dx1 = 0;
			dy1 = 0;
			dx2 = (panelW>=(int)(imageW*scale) ? (int)(imageW*scale) : panelW) -1 ; // DONE: Much better calculation - far less round error
			dy2 = (panelH>=(int)(imageH*scale) ? (int)(imageH*scale) : panelH) -1 ; // DONE: Much better calculation - far less round error

			// Draw image
			g.drawImage(image,dx1,dy1,dx2+1,dy2+1,sx1,sy1,sx2+1,sy2+1,this); // QUESTION: Understand why right coordinates are offseted by 1 ?

			// Draw a blue square showing image limits
			g.setColor(Color.blue);
			if (sx1==0) { g.drawLine(dx1,dy1,dx1,dy2); }
			if (sy1==0) {	g.drawLine(dx1,dy1,dx2,dy1); }
			if (sx2==imageW-1) { g.drawLine(dx2,dy1,dx2,dy2); }
			if (sy2==imageH-1) { g.drawLine(dx1,dy2,dx2,dy2); }

			// Show different way of handling picture
			if (zoomModeActive) {
				g.setColor(Color.red);
				g.drawRect(Math.min(rx1,rx2),Math.min(ry1,ry2),
							Math.abs(rw),Math.abs(rh));
			}
		}
		else {
			g.setColor(Color.gray);
			g.drawLine(0,0,panelW,panelH);
			g.drawLine(panelW,0,0,panelH);
		} 
	} //}}}


	//{{{ +mousePressed(MouseEvent) : void
	public void mousePressed (MouseEvent me) {
		rx1 = me.getX();
		ry1 = me.getY();

		if (!me.isControlDown()) {
			setMoveMode(true);
		}
	} //}}}

	//{{{ -setZoomMode(boolean) : void
	private void setZoomMode(boolean value) {
		zoomModeActive=value;
		setCursor(Cursor.getPredefinedCursor(value==true ?
				Cursor.CROSSHAIR_CURSOR:Cursor.DEFAULT_CURSOR));
	}//}}}
	
	//{{{ -setMoveMode(boolean) : void
	private void setMoveMode(boolean value) {
		moveModeActive=value;
		setCursor(Cursor.getPredefinedCursor(value==true ?
				Cursor.MOVE_CURSOR:Cursor.DEFAULT_CURSOR));
	}//}}}


	//{{{ +mouseReleased(MouseEvent) : void
	public void mouseReleased (MouseEvent me) {
		if (zoomModeActive) {
			double oldscale = scale; 

			setZoomMode(false);
			if (me.isControlDown()) {
				scale *= (rw!=0 ? Math.abs((double)(panelW)/(double)rw) : 1.0);
				if (scale > 3.0) {
					scale = 3.0;
				}
				sx1 += Math.min(rx1,rx2);
				sy1 += Math.min(ry1,ry2);
				
				changes.firePropertyChange("zoomValue-label",new java.lang.Integer((int)(100.0*oldscale)),new java.lang.Integer((int)(100.0*scale)));
				repaint();
			}
		}
		else { // moveModeActive == true
			setMoveMode(false);
			repaint();
		}
	} //}}} 

	//{{{ +mouseDragged(MouseEvent) : void
	public void mouseDragged(MouseEvent me) {
		rx2 = me.getX();
		ry2 = me.getY();

		if (me.isControlDown() && !me.isShiftDown()) { // Control but not shift
			setZoomMode(true);

			// Check boundaries
			if (rx2 < 0) {
				rx2 = 0;
			}
			if (rx2 > panelW - 1) {
				rx2 = panelW-1;
			}
			if (ry2 < 0) {
				ry2 = 0;
			}
			if (ry2 > panelH - 1) {
				ry2 = panelH-1;
			}
			rw = (rx2 - rx1);
			rh = (ry2 - ry1);
		}
		else { // moveModeActive
			sx1 += (int)(((double)(rx2-rx1)/scale)*speed);
			sy1 += (int)(((double)(ry2-ry1)/scale)*speed);
		}
		repaint();
	} //}}}


	//{{{ +addPropertyChangeListener(PropertyChangeListener) : void
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
			changes.addPropertyChangeListener(listener);
	} //}}}

	//{{{ +removePropertyChangeListener(PropertyChangeListener) : void
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
			changes.removePropertyChangeListener(listener);
	} //}}}

	//{{{ +propertyChange(PropertyChangeEvent) : void
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().compareTo("zoomValueButton")==0) {
			try {
				setZoomValue(Integer.parseInt(evt.getNewValue().toString()));
			}
			catch(Exception except) {
				setZoomValue(UNZOOM_FULL);
			}
		}
	} //}}}

	//{{{ +loadImage(Image) : void
	public void loadImage(Image image) {
		this.image = image;
		if (image!=null) {
			imageW = this.image.getWidth(this);
			imageH = this.image.getHeight(this);
		}
		sx1 = 0;
		sx2 = 0;
		setZoomValue(UNZOOM_FULL);
	} //}}}


	//{{{ +setZoomValue(int) : void
	public void setZoomValue(int zoomValue) {
		double oldscale = scale; 
		switch (zoomValue) {
			case ZOOM:
				scale+=0.25;
				if (scale > 3.0) {
					scale = 3.0;
				}
				break;
			case UNZOOM:
				scale-=0.25;
				if (scale < 0.25) {
					scale = 0.25;
				}
				break;
			case UNZOOM_FULL:
				scale=1.0;
				break;
			default:
				return;
		}
		changes.firePropertyChange("zoomValue-label",new java.lang.Integer((int)(100.0*oldscale)),new java.lang.Integer((int)(100.0*scale)));
		repaint();
	} //}}}

	//{{{ +getZoomValue() : int
	public int getZoomValue() {
		return((int)(scale*100.0));
	} //}}}


	//{{{ +mouseClicked(MouseEvent) : void
	public void mouseClicked (MouseEvent me) {
		if (me.isControlDown()) {
			 setZoomValue(me.isShiftDown() ? UNZOOM : ZOOM);
		}
	} //}}}

	//{{{ +mouseEntered(MouseEvent) : void
	public void mouseEntered (MouseEvent me) {
	} //}}}

	//{{{ +mouseExited(MouseEvent) : void
	public void mouseExited (MouseEvent me) {
	} //}}}

	//{{{ +mouseMoved(MouseEvent) : void
	public void mouseMoved(MouseEvent me) {
	} //}}}
}

/* :folding=explicit:tabSize=2:indentSize=2: */