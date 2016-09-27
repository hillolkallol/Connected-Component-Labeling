/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CCLPackage;

/**
 *
 * @author KD
 */

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import static sun.java2d.cmm.ColorTransform.In;

public class CCL2
{
    private int[][] _board;
    private BufferedImage _input;
    private Graphics inputGD;
    private int _width;
    private int _height;
    private int backgroundColor;

    public Map<Integer, BufferedImage> Process(BufferedImage input, int bgColor)
    {
    	backgroundColor = bgColor;
        _input = input;
        _width = input.getWidth();
        _height = input.getHeight();
        _board = new int[_width][];
        for(int i = 0;i < _width;i++)
        	_board[i] = new int[_height];

        Map<Integer, List<Pixel>> patterns = Find();
        Map<Integer, BufferedImage> images = new HashMap<Integer, BufferedImage>();

        inputGD = _input.getGraphics();
        inputGD.setColor(Color.BLUE);
        for(Integer id : patterns.keySet())
        {
            BufferedImage bmp = CreateBitmap(patterns.get(id));
            images.put(id, bmp);
        }
        inputGD.dispose();

        return images;
    }

    protected boolean CheckIsBackGround(Pixel currentPixel)
    {
    	// check if pixel color is backgroundColor (white).
        //return currentPixel.color.getAlpha() == 255 && currentPixel.color.getRed() == 255 && currentPixel.color.getGreen() == 255 && currentPixel.color.getBlue() == 255;
    	return currentPixel.color == backgroundColor;
    }

    private static int Min(List<Integer> neighboringLabels, Map<Integer, Label> allLabels) {
    	if(neighboringLabels.isEmpty())
    		return 0; // TODO: is 0 appropriate for empty list
    	
    	int ret = allLabels.get(neighboringLabels.get(0)).GetRoot().name;
    	for(Integer n : neighboringLabels) {
    		int curVal = allLabels.get(n).GetRoot().name;
    		ret = (ret < curVal ? ret : curVal);
    	}
    	return ret;
    }
    
    private static int Min(List<Pixel> pattern, boolean xOrY) {
    	if(pattern.isEmpty())
    		return 0; // TODO: is 0 appropriate for empty list
    	
    	int ret = (xOrY ? pattern.get(0).x : pattern.get(0).y);
    	for(Pixel p : pattern) {
    		int curVal = (xOrY ? p.x : p.y);
    		ret = (ret < curVal ? ret : curVal);
    	}
    	return ret;
    }

    private static int Max(List<Pixel> pattern, boolean xOrY) {
    	if(pattern.isEmpty())
    		return 0; // TODO: is 0 appropriate for empty list
    	
    	int ret = (xOrY ? pattern.get(0).x : pattern.get(0).y);
    	for(Pixel p : pattern) {
    		int curVal = (xOrY ? p.x : p.y);
    		ret = (ret > curVal ? ret : curVal);
    	}
    	return ret;
    }

    private Map<Integer, List<Pixel>> Find()
    {
        int labelCount = 1;
        Map<Integer, Label> allLabels = new HashMap<Integer, Label>();

        for (int i = 0; i < _height; i++)
        {
            for (int j = 0; j < _width; j++)
            {
                Pixel currentPixel = new Pixel(j, i, _input.getRGB(j, i));

                if (CheckIsBackGround(currentPixel))
                {
                    continue;
                }

                List<Integer> neighboringLabels = GetNeighboringLabels(currentPixel);
                int currentLabel;

                if (neighboringLabels.isEmpty())
                {
                    currentLabel = labelCount;
                    allLabels.put(currentLabel, new Label(currentLabel));
                    labelCount++;
                }
                else
                {
                    currentLabel = Min(neighboringLabels, allLabels);
                    Label root = allLabels.get(currentLabel).GetRoot();

                    for (Integer neighbor : neighboringLabels)
                    {
                        if (root.name != allLabels.get(neighbor).GetRoot().name)
                        {
                            allLabels.get(neighbor).Join(allLabels.get(currentLabel));
                        }
                    }
                }

                _board[j][i] = currentLabel;
            }
        }


        Map<Integer, List<Pixel>> patterns = AggregatePatterns(allLabels);

        return patterns;
    }

    private List<Integer> GetNeighboringLabels(Pixel pix)
    {
        List<Integer> neighboringLabels = new ArrayList<Integer>();

        for (int i = pix.y - 1; i <= pix.y + 2 && i < _height - 1; i++)
        {
            for (int j = pix.x - 1; j <= pix.x + 2 && j < _width - 1; j++)
            {
                if (i > -1 && j > -1 && _board[j][i] != 0)
                {
                    neighboringLabels.add(_board[j][i]);
                }
            }
        }

        return neighboringLabels;
    }

    private Map<Integer, List<Pixel>> AggregatePatterns(Map<Integer, Label> allLabels)
    {
        Map<Integer, List<Pixel>> patterns = new HashMap<Integer, List<Pixel>>();

        for (int i = 0; i < _height; i++)
        {
            for (int j = 0; j < _width; j++)
            {
                int patternNumber = _board[j][i];

                if (patternNumber != 0)
                {
                    patternNumber = allLabels.get(patternNumber).GetRoot().name;

                    if (!patterns.containsKey(patternNumber))
                    {
                        patterns.put(patternNumber, new ArrayList<Pixel>());
                    }

                    patterns.get(patternNumber).add(new Pixel(j, i, _input.getRGB(j, i)));
                }
            }
        }

        return patterns;
    }

    private BufferedImage CreateBitmap(List<Pixel> pattern)
    {
        int minX = Min(pattern, true);
        int maxX = Max(pattern, true);

        int minY = Min(pattern, false);
        int maxY = Max(pattern, false);
        
        int width = maxX + 1 - minX;
        int height = maxY + 1 - minY;

        BufferedImage bmp = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (Pixel pix : pattern)
        {
            bmp.setRGB(pix.x - minX, pix.y - minY, pix.color); //shift position by minX and minY
        }
        
        inputGD.drawRect(minX, minY, maxX-minX, maxY-minY);

        return bmp;
    }

    public static String getBaseFileName(String fileName) {
    	return fileName.substring(0, fileName.indexOf('.'));
    }
    
    public static String getFileNameExtension(String fileName) {
    	return fileName.substring(fileName.indexOf('.') + 1);
    }
    
    public BufferedImage getProcessedImage() {
    	return _input;
    }
    
    // Sample usage:
    // java org.ml.ccl.CCL images/one.png
    // java org.ml.ccl.CCL images/two.png -5000269
    public static void main(String[] args) throws IOException, URISyntaxException {
//                CCL ccl = new CCL();
//                int bgColor = 0xFFFFFFFF;
                
                //String dirName="C:\\Users\\KD\\Desktop";
//		ByteArrayOutputStream baos=new ByteArrayOutputStream(1000);
//		BufferedImage img=ImageIO.read(new File(CCL.class.getResource("kallol.jpg").toURI()));
//		ImageIO.write(img, "png", baos);
//		baos.flush();
 
//		String base64String=Base64.encode(baos.toByteArray());
                
//                Map<Integer, BufferedImage> components = ccl.Process(img, bgColor);
//                
//                for(Integer c : components.keySet()) {
//    			ImageIO.write(components.get(c), "jpg", new File("kallol" + "-component-" + c + "."  + "jpg"));
//    		}
//                
//                ImageIO.write(ccl.getProcessedImage(), "jpg", new File("kallol" + "-processed" + "." + "jpg"));
//                
//		baos.close();
// 
//		byte[] bytearray = Base64.decode(base64String);
// 
//		BufferedImage imag=ImageIO.read(new ByteArrayInputStream(bytearray));
//		ImageIO.write(imag, "png", new File(dirName,"snap.png"));
        
        /*if(args.length == 0) {
    		System.err.println("Usage: CCL imageFile [bgColor]");
    		return;
    	}*/
    	CCL2 ccl = new CCL2();
    	try {
    		int bgColor = 0xFFFFFFFF; // white default background color
//    		if(args.length == 2) {
//    			bgColor = Integer.decode(args[1]);
//    		}
    		//BufferedImage img = ImageIO.read(new File(args[0]));
                BufferedImage img=ImageIO.read(new File(CCL2.class.getResource("two.png").toURI()));
    		// TODO: Obtain background color.
    		// An attempt to obtain bg color automatically: Center of image.
    		System.out.println("image bg color: " + img.getRGB(img.getWidth()/2, img.getHeight()/2));
    		Map<Integer, BufferedImage> components = ccl.Process(img, bgColor);
    		//String format = getFileNameExtension(args[0]);
    		for(Integer c : components.keySet()) {
    			ImageIO.write(components.get(c), "png", new File("two" + "-component-" + c + "."  + "png"));
    		}
                
                ImageIO.write(ccl.getProcessedImage(), "png", new File("two" + "-processed" + "." + "png"));
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
    }
}
