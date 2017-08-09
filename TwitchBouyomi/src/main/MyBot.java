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

import WinSAPI.WinSAPI;

import org.jibble.pircbot.PircBot;
import org.snowink.bouyomichan.BouyomiChan4J;


public class MyBot extends PircBot {
	BouyomiChan4J talker = new BouyomiChan4J();
    boolean readName = false;
    boolean usePopup = false;
    int useEnglish = -1;
    

	WinSAPI  sapi = new WinSAPI("");
    
    private TrayIcon icon;
    private SystemTray tray;
	private ProcessBuilder pb = new ProcessBuilder();
    
	Map<String, String> repDic = new HashMap<String, String>();
	String[] blockUsers = new String[1000];
    
	public MyBot(String UserName, String Password, String URL) throws Exception{

	        this.setName(UserName);
	        this.setEncoding("UTF-8");
            this.connect(URL, 6667, Password);
            this.joinChannel("#"+UserName);

			sapi.setVoice("Zira");
			sapi.setSpeed(0);
            
            tray  = SystemTray.getSystemTray();
    		icon = new TrayIcon(ImageIO.read(new File("./icon.png")), "Twitch Bouyomi");
    		icon.setImageAutoSize(true);
    		tray.add(icon);
    		
    		BufferedReader reader = new BufferedReader(new FileReader("Replacement.dic"));
    		String rl;
    		while((rl = reader.readLine()) != null) {
    			if(!(rl.startsWith("//") || rl.equals(""))){
    				repDic.put(rl.split(",")[0],rl.split(",")[1]);
    			}
    		}
    		reader.close();
    		
    		BufferedReader reader2 = new BufferedReader(new FileReader("BlockUsers.txt"));
    		String rl2;
    		for(int i=0;(rl2 = reader2.readLine()) != null; i++) {
    			if(i==1000) break;
    			blockUsers[i] = rl2;
    		}
    		reader2.close();
	}


	public void onMessage(String channel, String sender,
                       String login, String hostname, String message) {
		System.out.println(sender+": "+message+", English:"+useEnglish);
		if(!checkBlockUser(sender)){
			message = urlReplace(message);
			System.out.println("Replaced URL: " +message);
			if(!readName) {
				if(useEnglish!=-1 && isEnglish(message)) sapi.speakAsyMsg(message);
				else talker.talk(wordReplace(message));
			}
			else {
				if(useEnglish!=-1 && isEnglish(message)) {
					sapi.speakAsyMsg(message +", "+ sender);
				} else {
					talker.talk(wordReplace(message) +" "+ sender);
				}
			}
			
			if(usePopup){
				icon.displayMessage(sender, message, MessageType.NONE);
			}
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
		if(text.matches("^(.*[｡-ﾟ０-９ａ-ｚＡ-Ｚぁ-んァ-ヶ亜-黑一-龠々ー].*)*$")) {
			System.out.println("これは日本語です。");
			return false;
		}
		return true;
	}
	
	public boolean checkBlockUser (String username){
		for(int i=0;i < 1000; i++){
			if(username.equals(blockUsers[i])){
				return true;
			}
		}
		return false;
	}
	
	public String wordReplace(String word){
		String result = word;
		for(String key: repDic.keySet()){
			String data = repDic.get(key);
			Pattern p = Pattern.compile(key, Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(result);
			result = m.replaceAll(data);
		}
		
		return result;
	}

	public String urlReplace(String word){
		String result = word;
		Pattern p = Pattern.compile("(http://|https://){1}[\\w\\.\\-/:\\#\\?\\=\\&\\;\\%\\~\\+]+",Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(result);
		result = m.replaceAll(";WebURL;");
		return result;
	}
}
