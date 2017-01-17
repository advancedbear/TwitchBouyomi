package main;

import java.io.IOException;

import org.jibble.pircbot.*;
import org.snowink.bouyomichan.BouyomiChan4J;


public class MyBot extends PircBot {
	BouyomiChan4J talker = new BouyomiChan4J();
    boolean readName = false;
	public MyBot(String UserName, String Password, String URL) throws Exception{
		 
	        this.setName(UserName);
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
		if(!readName) talker.talk(message);
		else talker.talk(message +" "+ sender);
    }


	public void readingName(boolean check) {
		// TODO Auto-generated method stub
		if(check) readName = true;
		else readName = false;
	}

}
