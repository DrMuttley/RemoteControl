package screencapture;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import javax.imageio.ImageIO;

/**
 * Class takes a screenshot, saves it to disk, removes it from the disk.
 * 
 * @author Anonymous
 */

public class ScreenCapture {
    
    /**
     * Method takes a screenshot and saves it to the disk.
     * 
     * @return name of screenshot file
     */

    public String doCapture() {
        
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        
        String fileName = new String();
        
        //preparing name for screen capture file
        fileName = timestamp.toString();
        fileName = fileName.substring(0, 19);
        fileName = fileName.replace(':', '-');
        fileName = fileName.replace(' ', '_');
        fileName = fileName + ".jpg";

        File file = new File(fileName);
        
        try {
            //capture the whole screen
            BufferedImage screenCapture = new Robot().createScreenCapture(
                    new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));

            ImageIO.write(screenCapture, "jpg", file);

        } catch (AWTException | IOException e) {
            e.getMessage();
        }
        return fileName;
    }
    
    /**
     * Method deletes screenshot file.
     * 
     * @param screenCaptureName name of screenshot file
     */

    public void deleteScreenCapture(String screenCaptureName){
        
        File screenCaptureFile = new File(screenCaptureName);
        
        if(screenCaptureFile.delete()){
            System.out.println("File deleted successfully");
        }else{
            System.out.println("Failed to delete the file");
        }
    }
}