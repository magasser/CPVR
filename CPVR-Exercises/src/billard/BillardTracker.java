package billard;

import java.awt.*;
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

        ImagePlus imgGray = NewImage.createByteImage("GrayDeBayered", w1, h1, 1, NewImage.FILL_BLACK);
        ImageProcessor ipGray = imgGray.getProcessor();
        byte[] pixGray = (byte[]) ipGray.getPixels();

        ImagePlus imgRGB = NewImage.createRGBImage("RGBDeBayered", w1, h1, 1, NewImage.FILL_BLACK);
        ImageProcessor ipRGB = imgRGB.getProcessor();
        int[] pixRGB = (int[]) ipRGB.getPixels();

        long msStart = System.currentTimeMillis();

        ImagePlus imgHue = NewImage.createByteImage("Hue", w1, h1, 1, NewImage.FILL_BLACK);
        ImageProcessor ipHue = imgHue.getProcessor();
        byte[] pixHue = (byte[]) ipHue.getPixels();

        ImagePlus imgBayer = NewImage.createRGBImage("Bayer", w1, h1, 1, NewImage.FILL_BLACK);
        ImageProcessor ipBayer = imgBayer.getProcessor();
        int[] pixBayer = (int[]) ipBayer.getPixels();

        ImagePlus imgSat = NewImage.createByteImage("Sat", w1, h1, 1, NewImage.FILL_BLACK);
        ImageProcessor ipSat = imgSat.getProcessor();
        byte[] pixSat = (byte[]) ipSat.getPixels();

        ImagePlus imgRGB2 = NewImage.createRGBImage("RGBDeBayered2", w1-4, h1-4, 1, NewImage.FILL_BLACK);
        ImageProcessor ipRGB2 = imgRGB2.getProcessor();
        int[] pixRGB2 = (int[]) ipRGB2.getPixels();

        long startRGB = System.currentTimeMillis();
        int i1 = 0;
        /*for (int y = 0; y < h2; y+=2)
        {
            for (int x = 0; x < w2; x+=2)
            {
                int b1 = pix1[i1+w1];
                int g1 = pix1[i1];
                int g2 = pix1[i1+w1+1];
                int r1 = pix1[i1+1];
                int pixel1 = ((b1 & 0xff)<<16)+((g1 & 0xff)<<8) + (r1 & 0xff);
                int pixel2 = ((b1 & 0xff)<<16)+((g2 & 0xff)<<8) + (r1 & 0xff);

                // RGB image
                pixRGB[i1] = pixel1;
                pixRGB[i1+1] = pixel1;
                pixRGB[i1+w1] = pixel2;
                pixRGB[i1+w1+1] = pixel2;

                // Bayer image
                pixBayer[i1] = ((b1 & 0xff)<<16);
                pixBayer[i1+1] = ((g1 & 0xff)<<8);
                pixBayer[i1+w1] = ((g2 & 0xff)<<8);
                pixBayer[i1+w1+1] = (r1 & 0xff);

                float[] hsb = Color.RGBtoHSB(b1,g1,b1,null);
                float[] hsb2 = Color.RGBtoHSB(b1,g2,b1,null);

                // Brightness
                pixGray[i1] = (byte)(255 * hsb[2]);
                pixGray[i1+1] = pixGray[i1];
                pixGray[i1+w1] = (byte)(255 * hsb2[2]);
                pixGray[i1+w1+1] = pixGray[i1+w1];

                // Hue
                pixHue[i1] = (byte)(255 * hsb[0]);
                pixHue[i1+1] = pixHue[i1];
                pixHue[i1+w1] = (byte)(255 * hsb2[0]);
                pixHue[i1+w1+1] = pixHue[i1+w1];

                // Saturation
                pixSat[i1] = (byte)(255 * hsb[1]);
                pixSat[i1+1] = pixHue[i1];
                pixSat[i1+w1] = (byte)(255 * hsb2[1]);
                pixSat[i1+w1+1] = pixHue[i1+w1];

                i1+=2;
            }
            i1+=w1;
        }*/

        long timeRGB = System.currentTimeMillis() - startRGB;

        i1 = 2*w1+2;
        int w2 = ipRGB2.getWidth();
        int h2 = ipRGB2.getHeight();
        int i2 = 0;
        for (int y = 0; y < h2; y += 2) {
            for (int x = 0; x < w2; x += 2) {

                int p1 = pix1[i1 - w1 - 1];
                int p2 = pix1[i1 - w1];
                int p3 = pix1[i1 - w1 + 1];
                int p4 = pix1[i1 - w1 + 2];
                int p5 = pix1[i1 - 1];
                int p6 = pix1[i1];
                int p7 = pix1[i1 + 1];
                int p8 = pix1[i1 + 2];
                int p9 = pix1[i1 + w1 - 1];
                int p10 = pix1[i1 + w1];
                int p11 = pix1[i1 + w1 + 1];
                int p12 = pix1[i1 + w1 + 2];
                int p13 = pix1[i1 + w1 + w1 - 1];
                int p14 = pix1[i1 + w1 + w1];
                int p15 = pix1[i1 + w1 + w1 + 1];
                int p16 = pix1[i1 + w1 + w1 + 2];

                int pixel1 = ((((p2 + p10) / 2) & 0xff) << 16) + ((((p1 + p3 + p6 + p9 + p11) / 5) & 0xff) << 8) + (((p5 + p7) / 2) & 0xff);
                int pixel2 = ((((p2 + p4 + p10 + p12) / 4) & 0xff) << 16) + ((((p3 + p6 + p8 + p11) / 4) & 0xff) << 8) + (p7 & 0xff);
                int pixel3 = ((p10 & 0xff) << 16) + ((((p6 + p9 + p11 + p14) / 4) & 0xff) << 8) + (((p5 + p7 + p13 + p15) / 4) & 0xff);
                int pixel4 = ((((p10 + p12) / 2) & 0xff) << 16) + ((((p6 + p8 + p11 + p14 + p16) / 5) & 0xff) << 8) + (((p7 + p15) / 2) & 0xff);

                pixRGB2[i2] = pixel1;
                pixRGB2[i2+1] = pixel2;
                pixRGB2[i2+w2] = pixel3;
                pixRGB2[i2+w2+1] = pixel4;

                i1+=2;
                i2+=2;
            }
            i2+=w2;
            i1+=w1;
        }


        long ms = System.currentTimeMillis() - msStart;
        System.out.println(ms);
        ImageStatistics stats = ipGray.getStatistics();
        System.out.println("Mean:" + stats.mean);

        PNG_Writer png = new PNG_Writer();
        try
        {   //png.writeImage(imgRGB , "generated/Billard2048x1088x3.png",  0);
            png.writeImage(imgRGB2 , "generated/Billard2048x1088_2x3.png",  0);
            //png.writeImage(imgHue,  "generated/Billard2048x1088x1H.png", 0);
            //png.writeImage(imgGray, "generated/Billard2048x1088x1B.png", 0);
            //png.writeImage(imgBayer, "generated/Billard2048x1088x1Ba.png", 0);
            //png.writeImage(imgBayer, "generated/Billard2048x1088x1S.png", 0);

        } catch (Exception e)
        {   e.printStackTrace();
        }

        //imgGray.show();
        //imgGray.updateAndDraw();
        //imgRGB.show();
        //imgRGB.updateAndDraw();
        //imgHue.show();
        //imgHue.updateAndDraw();
        //imgBayer.show();
        //imgBayer.updateAndDraw();
        //imgSat.show();
        //imgSat.updateAndDraw();
        imgRGB2.show();
        imgRGB2.updateAndDraw();

        System.out.println("Time RGB: " + timeRGB);
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
