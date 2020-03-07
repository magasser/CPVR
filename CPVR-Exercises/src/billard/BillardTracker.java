package billard;

import java.awt.Color;
import java.util.Arrays;

import ij.ImagePlus;
import ij.gui.NewImage;
import ij.plugin.PNG_Writer;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;

public class BillardTracker implements PlugInFilter
{
    @Override
    public int setup(String arg, ImagePlus imp)
    {   return DOES_8G;
    }

    @Override
    public void run(ImageProcessor ip1)
    {   int w1 = ip1.getWidth();
        int h1 = ip1.getHeight();
        byte[] pix1 = (byte[]) ip1.getPixels();

        ImagePlus imgGray = NewImage.createByteImage("GrayDeBayered", w1-1, h1-1, 1, NewImage.FILL_BLACK);
        ImageProcessor ipGray = imgGray.getProcessor();
        byte[] pixGray = (byte[]) ipGray.getPixels();
        int w2 = ipGray.getWidth();
        int h2 = ipGray.getHeight();

        ImagePlus imgRGB = NewImage.createRGBImage("RGBDeBayered", w1, h1, 1, NewImage.FILL_BLACK);
        ImageProcessor ipRGB = imgRGB.getProcessor();
        int[] pixRGB = (int[]) ipRGB.getPixels();

        ImagePlus imgRGB2 = NewImage.createRGBImage("RGBDeBayered2", w1-1, h1-1, 1, NewImage.FILL_BLACK);
        ImageProcessor ipRGB2 = imgRGB2.getProcessor();
        int[] pixRGB2 = (int[]) ipRGB2.getPixels();

        long msStart = System.currentTimeMillis();

        ImagePlus imgHue = NewImage.createByteImage("Hue", w1-1, h1-1, 1, NewImage.FILL_BLACK);
        ImageProcessor ipHue = imgHue.getProcessor();
        byte[] pixHue = (byte[]) ipHue.getPixels();

        long startRGB1 = System.currentTimeMillis();
        int i1 = 0;
        for (int y = 0; y < h2; y+=2)
        {
            for (int x = 0; x < w2; x+=2)
            {
                int b1 = pix1[i1+w1];
                int g1 = pix1[i1];
                int g2 = pix1[i1+w1+1];
                int r1 = pix1[i1+1];
                int pixel1 = ((b1 & 0xff)<<16)+((g1 & 0xff)<<8) + (r1 & 0xff);
                int pixel2 = ((b1 & 0xff)<<16)+((g1 & 0xff)<<8) + (r1 & 0xff);
                int pixel3 = ((b1 & 0xff)<<16)+((g2 & 0xff)<<8) + (r1 & 0xff);
                int pixel4 = ((b1 & 0xff)<<16)+((g2 & 0xff)<<8) + (r1 & 0xff);

                ipRGB.putPixel(x,y, pixel1);
                ipRGB.putPixel(x+1,y, pixel2);
                ipRGB.putPixel(x,y+1, pixel3);
                ipRGB.putPixel(x+1,y+1, pixel4);

                i1++;
            }
            i1+=w1;
        }

        long timeRGB1 = System.currentTimeMillis() - startRGB1;

        long startRGB2 = System.currentTimeMillis();

        int i2 = 0;
        for (int y = 0; y < h2-1; y+=2)
        {
            for (int x = 0; x < w2-1; x+=2)
            {
                int b1 = pix1[i2+w1];
                int g1 = (pix1[i2] + pix1[i2+w1+1]) / 2;
                int r1 = pix1[i2+1];
                int pixel1 = ((b1 & 0xff)<<16)+((g1 & 0xff)<<8) + (r1 & 0xff);

                int b2 = pix1[i2+w1+2];
                int g2 = (pix1[i2+2] + pix1[i2+w1+1]) / 2;
                int r2 = r1;
                int pixel2 = ((b2 & 0xff)<<16)+((g2 & 0xff)<<8) + (r2 & 0xff);

                int b3 = b1;
                int g3 = (pix1[i2+w1+1] + pix1[i2+w1+w1]) / 2;
                int r3 = pix1[i2+w1+w1+1];
                int pixel3 = ((b3 & 0xff)<<16)+((g3 & 0xff)<<8) + (r3 & 0xff);

                int b4 = b2;
                int g4 = (pix1[i2+w1+1] + pix1[i2+w1+w1+2]) / 2;
                int r4 = r3;
                int pixel4 = ((b4 & 0xff)<<16)+((g4 & 0xff)<<8) + (r4 & 0xff);

                ipRGB2.putPixel(x,y, pixel1);
                ipRGB2.putPixel(x+1,y, pixel2);
                ipRGB2.putPixel(x,y+1, pixel3);
                ipRGB2.putPixel(x+1,y+1, pixel4);

                i2+=2;
            }
            i2 += w1 + 2;
        }

        long timeRGB2 = System.currentTimeMillis() - startRGB2;


        long ms = System.currentTimeMillis() - msStart;
        System.out.println(ms);
        ImageStatistics stats = ipGray.getStatistics();
        System.out.println("Mean:" + stats.mean);

        PNG_Writer png = new PNG_Writer();
        try
        {   png.writeImage(imgRGB , "resources/Billard1024x544x3.png",  0);
            png.writeImage(imgRGB2 , "resources/Billard1024x544x32.png",  0);
            //png.writeImage(imgHue,  "../../Images/Billard1024x544x1H.png", 0);
            //png.writeImage(imgGray, "../../Images/Billard1024x544x1B.png", 0);

        } catch (Exception e)
        {   e.printStackTrace();
        }

        //imgGray.show();
        //imgGray.updateAndDraw();
        imgRGB.show();
        imgRGB.updateAndDraw();
        imgRGB2.show();
        imgRGB2.updateAndDraw();
        //imgHue.show();
        //imgHue.updateAndDraw();

        System.out.println("Time RGB1: " + timeRGB1 + ", Time RGB2: " + timeRGB2);
    }

    public static void main(String[] args)
    {
        BillardTracker plugin = new BillardTracker();

        ImagePlus im = new ImagePlus("resources/Billard2048x1088x1.png");
        im.show();
        plugin.setup("", im);
        plugin.run(im.getProcessor());
    }
}
