/*
Copyright (C) 2009  Shlomy Reinstein

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package ocr;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;


public class OCRPlugin extends EditPlugin
{
	private static final String SRC_IMAGE_FORMAT = "png";
	private static final String DST_IMAGE_FORMAT = "ppm";

	static public void readPicture(View view)
	{
		JFileChooser dlg = new JFileChooser();
		FileFilter filter = new FileNameExtensionFilter("Picture file", "pbm");
		dlg.setFileFilter(filter);
		if (dlg.showOpenDialog(view) != JFileChooser.APPROVE_OPTION)
			return;
		importPictureFile(view, dlg.getSelectedFile());
	}

	static private Buffer importPictureFile(View v, File f)
	{
		String text = readPictureFile(f);
		Buffer b = getBufferForImportedText(v);
		if (b == null)
			return null;
		b.insert(0, text);
		return b;
	}

	static public void readScreenRect(final View view)
	{
		final Rectangle rect = new Rectangle(
			Toolkit.getDefaultToolkit().getScreenSize());
		try {
			Thread.sleep(jEdit.getIntegerProperty(
				OptionPane.OPTIONS_OCR_SCREEN_CAPTURE_DELAY) * 1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		Robot robot;
		try {
			robot = new Robot();
			final BufferedImage image = robot.createScreenCapture(rect);
			image.flush();
			final JFrame f = new JFrame("Capture - select rectangle to import");
			f.setBounds(rect);
			final JPanel imagePanel = new ImagePanel(image, rect.getSize(),
				new ImagePanel.SelectionListener() {
					public void rectSelected(Point [] p) {
						final BufferedImage subImage = image.getSubimage(
							p[0].x, p[0].y, p[1].x - p[0].x, p[1].y - p[0].y);
						File imageFile = writeImage(subImage);
						if (imageFile != null)
						{
							importPictureFile(view, imageFile);
							deleteTempFile(imageFile);
						}
						f.dispose();
					}
				});
			f.add(imagePanel);
			f.setVisible(true);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("serial")
	static private class ImagePanel extends JPanel
	{
		interface SelectionListener
		{
			void rectSelected(Point [] p);
		}
		BufferedImage image;
		Dimension d;
		Point [] p = new Point[2];
		Point drag = null;
		SelectionListener l;
		public ImagePanel(BufferedImage image, Dimension d, SelectionListener l)
		{
			this.image = image;
			this.d = d;
			this.l = l;
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					p[1] = e.getPoint();
					ImagePanel.this.l.rectSelected(p);
				}
				@Override
				public void mousePressed(MouseEvent e) {
					p[0] = e.getPoint();
				}
			});
			addMouseMotionListener(new MouseAdapter() {
				@Override
				public void mouseDragged(MouseEvent e) {
					drag(e.getPoint());
				}
			});
		}
		public void drag(Point newDrag)
		{
			if (newDrag != null) {
				Graphics g = getGraphics();
				g.setXORMode(getBackground());
				g.setColor(Color.BLUE);
				if (drag != null)
					g.drawRect(p[0].x, p[0].y, drag.x - p[0].x, drag.y - p[0].y);
				drag = newDrag;
				g.drawRect(p[0].x, p[0].y, drag.x - p[0].x, drag.y - p[0].y);
			}
		}
		public void paintComponent(Graphics g)
		{
			g.drawImage(image, 0, 0, d.width, d.height, null);
		}
	}

	static private void deleteTempFile(File f)
	{
		f.delete();
	}

	static private File writeImage(BufferedImage image)
	{
		File srcImage = null, dstImage = null;
		try {
			srcImage = File.createTempFile("ocr", "." + SRC_IMAGE_FORMAT);
			if (srcImage == null)
				return null;
			ImageIO.write(image, SRC_IMAGE_FORMAT, srcImage);
			dstImage = convertImage(srcImage);
			deleteTempFile(srcImage);
		} catch (IOException e) {
			return null;
		}
		return dstImage;
	}

	static private File convertImage(File srcImage)
	{
		File dstImage = null;
		try {
			dstImage = File.createTempFile("ocr", "." + DST_IMAGE_FORMAT);
			String exe = jEdit.getProperty(OptionPane.OPTIONS_OCR_CONVERTER_PATH);
			String [] cmd = { exe, srcImage.getAbsolutePath(), dstImage.getAbsolutePath() };
			Process p = Runtime.getRuntime().exec(cmd);
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return dstImage;
	}

	static private Buffer getBufferForImportedText(View view)
	{
		return jEdit.newFile(view);
	}

	static private String readPictureFile(File f)
	{
		String path = f.getAbsolutePath();
		String exe = jEdit.getProperty(OptionPane.OPTIONS_OCR_GOCR_PATH);
		String [] cmd = { exe, path };
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			InputStream is = p.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuilder sb = new StringBuilder();
			while ((line = reader.readLine()) != null)
				sb.append(line + "\n");
			p.waitFor();
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
