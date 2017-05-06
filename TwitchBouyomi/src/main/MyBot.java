package main;

import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.io.IOException;
import java.lang.String;

import javax.imageio.ImageIO;

import org.jibble.pircbot.PircBot;
import org.snowink.bouyomichan.BouyomiChan4J;


public class MyBot extends PircBot {
	BouyomiChan4J talker = new BouyomiChan4J();
    boolean readName = false;
    int useEnglish = -1;
    
    private TrayIcon icon;
    
	public MyBot(String UserName, String Password, String URL) throws Exception{

	        this.setName(UserName);
	        this.setEncoding("UTF-8");
            this.connect(URL, 6667, Password);
            this.joinChannel("#"+UserName);
            

    		Image image;
    		try {
    			image = ImageIO.read(
    			        getClass().getResourceAsStream("icon.png"));
    			icon = new TrayIcon(image);
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} 
    		SystemTray.getSystemTray().add(icon);
	}


	public void onMessage(String channel, String sender,
                       String login, String hostname, String message) {
		System.out.println(sender+": "+message+", English:"+useEnglish);
		if(!readName) {
			if(useEnglish!=-1 && isEnglish(message)) talker.talk(-1, -1, -1, useEnglish, message);
			else talker.talk(message);
		}
		else {
			if(useEnglish!=-1 && isEnglish(message)) {
				talker.talk(-1, -1, -1, useEnglish, message +", "+ sender);
			} else {
				talker.talk(message +" "+ sender);
			}
		}
		
		icon.displayMessage(sender, message, MessageType.NONE);
		
    }


	public void readingName(boolean check) {
		// TODO Auto-generated method stub
		if(check) readName = true;
		else readName = false;
	}

	public void usingEnglish(int num) {
		useEnglish = num;
	}

	public boolean isEnglish(String text){
		//if(text.matches("^[a-zA-Z0-9 -/:-@[-`{-~]+$")) return true;
		byte[] Bytes = text.getBytes();
		if(text.length() != Bytes.length) return false;
		return true;
	}

}
