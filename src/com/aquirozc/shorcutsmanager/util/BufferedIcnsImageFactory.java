package com.aquirozc.shorcutsmanager.util;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.BufferedImageFactory;
import org.apache.commons.imaging.formats.icns.IcnsImageParser;
import org.apache.commons.imaging.formats.icns.IcnsImagingParameters;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Transparency;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.ImageIcon;

public class BufferedIcnsImageFactory implements BufferedImageFactory {

    @Override
    public BufferedImage getColorBufferedImage(int width, int height, boolean hasAlpha) {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice gd = ge.getDefaultScreenDevice();
        final GraphicsConfiguration gc = gd.getDefaultConfiguration();
        return gc.createCompatibleImage(width, height,Transparency.TRANSLUCENT);
    }

    @Override
    public BufferedImage getGrayscaleBufferedImage(int width, int height, boolean hasAlpha) {
        return getColorBufferedImage(width, height, hasAlpha);
    }

    public ImageIcon [] getIconPack(File file){

        ImageIcon [] iconPack = new ImageIcon[5];

        IcnsImageParser icnsParser = new IcnsImageParser();
        IcnsImagingParameters icnsParams = icnsParser.getDefaultParameters();
        BufferedImage maxRes = new BufferedImage(1,1,BufferedImage.TYPE_4BYTE_ABGR);

        try{

            icnsParams.setBufferedImageFactory(this);
            List<BufferedImage> bufferedImageList= icnsParser.getAllBufferedImages(file);

            for(BufferedImage image : bufferedImageList){

                if(image.getHeight() >= maxRes.getHeight()){
                    if(!isBlank(image)){
                        maxRes = image;
                    }
                }

            }


            iconPack[0] = new ImageIcon(getResizedBufferedImage(maxRes,64,64));
            iconPack[1] = new ImageIcon(getResizedBufferedImage(maxRes,72,72));
            iconPack[2] = new ImageIcon(getResizedBufferedImage(maxRes,88,88));
            iconPack[3] = new ImageIcon(getResizedBufferedImage(maxRes,111,111));
            iconPack[4] = new ImageIcon(getResizedBufferedImage(maxRes,135,135));

        }catch (ImageReadException | IOException | NullPointerException e){

        }

        return  iconPack;
    }

    public static BufferedImage getResizedBufferedImage(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    public Boolean isBlank(BufferedImage bufferedImage){
        boolean isBlank = true;

        for (int i = 0; i < bufferedImage.getWidth() ;i++){

            for (int j = 0; j < bufferedImage.getHeight() ;j++){
                if (bufferedImage.getRGB(i,j) != 0){
                    isBlank = false;
                    break;
                }
            }
        }

        return isBlank;
    }

}
