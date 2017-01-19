package main;

import java.io.IOException;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;
import org.snowink.bouyomichan.BouyomiChan4J;


public class MyBot extends PircBot {
	BouyomiChan4J talker = new BouyomiChan4J();
    boolean readName = false;
    int useEnglish = -1;
	public MyBot(String UserName, String Password, String URL) throws Exception{

	        this.setName(UserName);
	        this.setEncoding("UTF-8");
	        try {
	            this.connect(URL, 6667, Password);
	            this.joinChannel("#"+UserName);
	        } catch (NickAlreadyInUseException e) {
	            System.err.println("既に使われているnickです.");
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (IrcException e) {
	            e.printStackTrace();
	        }
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
		if(text.length() != text.getBytes().length) return false;
		return true;
	}

}
