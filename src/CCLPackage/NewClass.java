/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CCLPackage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author KD
 */

public class NewClass {
    
    
    private int[][] labeling;
    private static int newLabel = 1;
    private int minLabel;
    private static BufferedImage input;
    private boolean flag = true;
    
    public void processing(){
        
        int input_width = input.getWidth();
        int input_height = input.getHeight();
        labeling = new int [input.getHeight()][input.getWidth()];
        
        //first pass
        for(int i=0; i<input_height; i++) {
            for(int j=0; j<input_width; j++) {
                minLabel = newLabel;
                if(j==5){ 
                    int x = 1;
                }
                if(i==0 && j==0){
                    newLabel++;
                }
                
                if(j-1>=0 && (input.getRGB(j, i) == input.getRGB(j-1,i))){
                        minLabel = labeling[i][j-1];
                        flag = true;
                }
                if(i-1>=0 && (input.getRGB( j,i) == input.getRGB( j,i-1))){
                        minLabel = labeling[i-1][j];
                        flag = true;
                }
                if(i-1>=0 && j-1>=0 && (input.getRGB(j,i) == input.getRGB(j-1,i-1))){
                        minLabel = labeling[i-1][j-1];
                        flag = true;
                }
                if(i-1>=0 && j+1<input.getWidth() && (input.getRGB(j,i) == input.getRGB(j+1,i-1))){
                        minLabel = labeling[i-1][j+1];
                        flag = true;
                }
                
                if(flag == false){
                    newLabel++;
                }
                labeling[i][j] = minLabel;               
            }
        }
        
        //second pass
        for(int i=0; i<input.getHeight(); i++) {
            for(int j=0; j<input.getWidth(); j++) {
                
                minLabel = labeling[i][j];
                
                if(j-1>=0 && input.getRGB(j,i) == input.getRGB(j-1,i)){
                    if(minLabel > labeling[i][j-1])
                        minLabel = labeling[i][j-1];
                }
                if(i-1>=0 && j-1>=0 && input.getRGB(j,i) == input.getRGB(j-1,i-1)){
                    if(minLabel > labeling[i-1][j-1])
                        minLabel = labeling[i-1][j-1];
                }
                if(i-1>=0 && input.getRGB(j,i) == input.getRGB(j,i-1)){
                    if(minLabel > labeling[i-1][j])
                        minLabel = labeling[i-1][j];
                }
                if(i-1>=0 && j+1<input.getWidth() && input.getRGB(j,i) == input.getRGB(j+1,i-1)){
                    if(minLabel > labeling[i-1][j+1])
                        minLabel = labeling[i-1][j+1];
                }
                if(j+1<input.getWidth() && input.getRGB(j,i) == input.getRGB(j+1,i)){
                    if(minLabel > labeling[i][j+1])
                        minLabel = labeling[i][j+1];
                }
                if(i+1<input.getHeight() && j+1<input.getWidth() && input.getRGB(j,i) == input.getRGB(j+1,i+1)){
                    if(minLabel > labeling[i+1][j+1])
                        minLabel = labeling[i+1][j+1];
                }
                if(i+1<input.getHeight() && input.getRGB(j,i) == input.getRGB(j,i+1)){
                    if(minLabel > labeling[i+1][j])
                        minLabel = labeling[i+1][j];
                }
                if(i+1<input.getHeight() && j-1>=0 && input.getRGB(j,i) == input.getRGB(j-1,i+1)){
                    if(minLabel > labeling[i+1][j-1])
                        minLabel = labeling[i+1][j-1];
                }
                labeling[i][j] = minLabel;
            }
        }
    }
    
    public void output() throws IOException{
        for(int value=1; value<=newLabel; value++){
            BufferedImage OutputImage = input;
            for(int i=0; i<input.getHeight(); i++){
                for(int j=0; j<input.getWidth(); j++){
                    if(labeling[i][j] != value){
                        OutputImage.setRGB(j, i, 0);
                    }
                }
            }
            
            //
            ImageIO.write(OutputImage, "png", new File("one" + value + "." + "png"));
        }
    }
    
    
    public static void main(String[] args) throws IOException {
    	CCL ccl = new CCL();
    	try {
                input = ImageIO.read(new File(CCL.class.getResource("four.png").toURI()));
                //System.out.println(input.getRGB(9, 9));
                
                ccl.processing();
                System.out.println(newLabel);
                ccl.output();
                
                //ImageIO.write(input, "png", new File("two" + "-" + "." + "png"));
                
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
    }
}
