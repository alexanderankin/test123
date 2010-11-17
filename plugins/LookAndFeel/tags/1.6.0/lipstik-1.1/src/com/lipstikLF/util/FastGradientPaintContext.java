/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  FastGradientPaintContext.java                                               *
*   Original Author: Sebastian Ferreyra (sebastianf@citycolor.net)             *
*   Contributor(s): Taoufik Romdhane                                           *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.lipstikLF.util;

import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.WeakHashMap;

public class FastGradientPaintContext implements PaintContext
{
    private static WeakHashMap gradientCache = new WeakHashMap();
    private static LinkedList recentInfos = new LinkedList();
    private GradientInfo info;
    private int parallelDevicePos;
    private Gradient gradient;

    public FastGradientPaintContext(ColorModel cm, Rectangle r, int sc, int ec, boolean ver)
    {
        info = new GradientInfo();
        info.model = cm;
        info.startColor = sc;
        info.endColor = ec;
        if (info.isVertical = ver)
        {
            parallelDevicePos = r.y;
            info.parallelLength = r.height;
        }
        else
        {
            parallelDevicePos = r.x;
            info.parallelLength = r.width;
        }

        recentInfos.remove(info);
        recentInfos.add(0, info);
        if (recentInfos.size() > 16)
        {
            recentInfos.removeLast();
//            System.out.println("Removing from cache");
        }


        Object o = gradientCache.get(info);
        if (o != null)
            o = ((WeakReference) o).get();
        if (o != null)
        {
            gradient = (Gradient) o;
//            System.out.println("Got gradient from cache");
        }
        else
        {
        	gradient = new Gradient(info);
            gradientCache.put(info, new WeakReference(gradient));
//            System.out.println( "Storing gradient in cache. Info: " + info.toString() );
//            System.out.println("cache size: "+gradientCache.size());
        }

    }

    public void dispose()
    {
        gradient.dispose();
    }

    public ColorModel getColorModel()
    {
        return info.model;
    }

    public synchronized Raster getRaster(int x, int y, int w, int h)
    {
        if (info.isVertical)
            return gradient.getRaster(y - parallelDevicePos, w);
        else
            return gradient.getRaster(x - parallelDevicePos, h);
    }
}


class Gradient
{
    private GradientInfo info;
    private int perpendicularLength = 0;
    private WritableRaster raster;
    private HashMap childRasterCache;

    Gradient(GradientInfo i)
    {
        info = i;
    }

    Raster getRaster(int parallelPos, int perpendicularLength)
    {
        if (raster == null || (this.perpendicularLength < perpendicularLength))
            createRaster(perpendicularLength);

        Integer key = new Integer(parallelPos);
        Object o = childRasterCache.get(key);
        if (o != null)
            return (Raster) o;
        else
        {
            Raster r;
            if (info.isVertical)
                r = raster.createChild(0, parallelPos, this.perpendicularLength, info.parallelLength - parallelPos, 0, 0, null);
            else
                r = raster.createChild(parallelPos, 0, info.parallelLength - parallelPos, this.perpendicularLength, 0, 0, null);
            childRasterCache.put(key, r);
            //System.out.println( "Storing child raster in cache. Position: " + Integer.toString(parallelPos) );
            return r;
        }

    }

    public void dispose()
    {
		raster = null;
    }

    private void createRaster(int perpendicularLength)
    {
        int gradientWidth, gradientHeight;
        if (info.isVertical)
        {
            gradientHeight = info.parallelLength;
            gradientWidth = this.perpendicularLength = perpendicularLength;
        } else
        {
            gradientWidth = info.parallelLength;
            gradientHeight = this.perpendicularLength = perpendicularLength;
        }

        int sr = ((info.startColor >> 16) & 0xFF);
        int sg = ((info.startColor >> 8) & 0xFF);
        int sb = (info.startColor & 0xFF);
        int dr;
        int dg;
        int db;

        if (info.endColor == 0)
        {
            sr -= 5;
            sg -= 5;
            sb -= 5;

            if (sr < 0) sr = 0;
            if (sg < 0) sg = 0;
            if (sb < 0) sb = 0;

            dr = (sr + 26) & 0xFF;
            dg = (sg + 26) & 0xFF;
            db = (sb + 26) & 0xFF;
        }
        else
        {
            dr = ((info.endColor >> 16) & 0xFF);
            dg = ((info.endColor >> 8) & 0xFF);
            db = (info.endColor & 0xFF);
        }

        dr -= sr;
        dg -= sg;
        db -= sb;

        raster = info.model.createCompatibleWritableRaster(gradientWidth, gradientHeight);

        Object c = null;
        int pl = info.parallelLength;
        for (int i = 0; i < pl; i++)
        {
            c = info.model.getDataElements((sr + (i * dr / pl)) << 16 | (sg + (i * dg / pl)) << 8 | (sb + (i * db / pl)), c);
            for (int j = 0; j < perpendicularLength; j++)
            {
                if (info.isVertical)
                    raster.setDataElements(j, i, c);
                else
                    raster.setDataElements(i, j, c);
            }
        }
        childRasterCache = new HashMap();
    }
}

class GradientInfo
{
    ColorModel model;
    int parallelLength, startColor, endColor;
    boolean isVertical;

    public boolean equals(Object o)
    {
        if (o == null) return false;

        GradientInfo info = (GradientInfo) o;
        return (info.model.equals(model) && info.parallelLength == parallelLength && info.startColor == startColor && info.isVertical == isVertical);
    }

    public int hashCode()
    {
        return parallelLength;
    }

    public String toString()
    {
        return String.valueOf(parallelLength);
    }
}