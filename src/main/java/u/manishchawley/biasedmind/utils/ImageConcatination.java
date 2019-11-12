/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package u.manishchawley.biasedmind.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.ImageIO;

/**
 *
 * @author Manish Chawley
 */



public class ImageConcatination {
    
    private static final int OFFSET = 10;
    private static final int IMAGEWIDTH = 403;
    private static final int IMAGEHEIGHT = 356;
    
    
    public String[] getImageNames(){
        String[] imageNames = new String[1024];
        
        for(int i=0;i<1024;i++){
            String temp = "0000000000" + Integer.toBinaryString(1023-i);
            temp = temp.substring(temp.length()-10);
            StringBuffer sb = new StringBuffer();
            
            for(int j=0;j<10;j++)
                if(temp.substring(j, j+1).equals("1"))
                    sb.append("100");
                else
                    sb.append("10");
            
            sb.append(".png");
            imageNames[i] = sb.toString();
//            System.out.println(i + ": " + temp + ": " + imageNames[i]);
        }
        return imageNames;
    }
    
    public boolean checkIfFileExists(String fileName){
        return (new File(Constants.IMAGE_PATH + fileName)).exists();
    }
    
    public BufferedImage concatinateImages(String[] imageNames) throws IOException{
        int width = (IMAGEWIDTH + OFFSET)*32 + OFFSET;
        int height = (IMAGEHEIGHT + OFFSET)*32 + OFFSET;
        
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        Color brushColor = g2d.getColor();
        
        g2d.setPaint(Color.GRAY);
        g2d.fillRect(0, 0, width, height);
        
        g2d.setColor(Color.BLACK);
        g2d.setFont(g2d.getFont().deriveFont(Font.TRUETYPE_FONT, 11));
        
        int rowCount, colCount;
        rowCount = 0;
        colCount = 0;
        for(String imageName : imageNames){
            int x = colCount * (IMAGEWIDTH + OFFSET) + OFFSET;
            int y = rowCount * (IMAGEHEIGHT + OFFSET) + OFFSET;
            File file = new File(Constants.IMAGE_PATH + imageName);
            File defaultFile = new File(Constants.IMAGE_PATH + "default.png");
            if(file.exists()){
                BufferedImage tempImage = ImageIO.read(file);
                g2d.drawImage(tempImage, null, x, y);
                System.out.println(imageName + ": file composited");
            }else{
                BufferedImage tempImage = ImageIO.read(defaultFile);
                g2d.drawImage(tempImage, null, x, y);
                System.out.println(imageName + ": file not found");
            }
            g2d.drawString(imageName.split(".p")[0], x+OFFSET, y+OFFSET);
            colCount++;
            if(colCount==32){
                colCount=0;
                rowCount++;
            }
        }
        g2d.dispose();
        return image;
    }
    
    public static void main(String[] args) throws IOException {
        ImageConcatination ic = new ImageConcatination();
        String[] images = ic.getImageNames();
        BufferedImage finalImage = ic.concatinateImages(images);
        ImageIO.write(finalImage, "png", new File(Constants.IMAGE_PATH + "composite.png"));
    }
}
