package main;

import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.String;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.jibble.pircbot.PircBot;
import org.snowink.bouyomichan.BouyomiChan4J;


public class MyBot extends PircBot {
	BouyomiChan4J talker = new BouyomiChan4J();
    boolean readName = false;
    boolean usePopup = false;
    int useEnglish = -1;
    
    private TrayIcon icon;
    private SystemTray tray;
    
	Map<String, String> R6Sdic = new HashMap<String, String>();
    
	public MyBot(String UserName, String Password, String URL) throws Exception{

	        this.setName(UserName);
	        this.setEncoding("UTF-8");
            this.connect(URL, 6667, Password);
            this.joinChannel("#"+UserName);
            
            tray  = SystemTray.getSystemTray();
    		icon = new TrayIcon(ImageIO.read(new File("icon.png")), "Twitch Bouyomi");
    		icon.setImageAutoSize(true);
    		tray.add(icon);
    		
    		BufferedReader reader = new BufferedReader(new FileReader("R6S.dic"));
    		String rl;
    		while((rl = reader.readLine()) != null) {
    			R6Sdic.put(rl.split(",")[0],rl.split(",")[1]);
    		}
    		reader.close();
	}


	public void onMessage(String channel, String sender,
                       String login, String hostname, String message) {
		System.out.println(sender+": "+message+", English:"+useEnglish);
		if(!readName) {
			if(useEnglish!=-1 && isEnglish(message)) talker.talk(-1, -1, -1, useEnglish, message);
			else talker.talk(rainbowReplace(message));
		}
		else {
			if(useEnglish!=-1 && isEnglish(message)) {
				talker.talk(-1, -1, -1, useEnglish, message +", "+ sender);
			} else {
				talker.talk(rainbowReplace(message) +" "+ sender);
			}
		}
		
		if(usePopup){
			icon.displayMessage(sender, message, MessageType.NONE);
		}
    }


	public void readingName(boolean check) {
		// TODO Auto-generated method stub
		if(check) readName = true;
		else readName = false;
	}

	public void usingEnglish(int num) {
		useEnglish = num;
	}
	
	public void usingPopup(boolean check) {
		if(check) usePopup = true;
		else usePopup = false;
	}

	public boolean isEnglish(String text){
		if(text.matches("^(.*[｡-ﾟぁ-ん亜-黑].*)*$")) return false;
		//byte[] Bytes = text.getBytes();
		//if(text.length() != Bytes.length) return false;
		return true;
	}
	
	public String rainbowReplace(String word){
		String result = word;
		for(String key: R6Sdic.keySet()){
			String data = R6Sdic.get(key);
			Pattern p = Pattern.compile(key, Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(result);
			result = m.replaceAll(data);
		}
		
		return result;
	}

}
