package main;

import java.io.Serializable;

public class Config implements Serializable {
	private static final long serialVersionUID = -2704125217030108449L;
	
	private String password;
	private String username;
	private boolean readName;
	private boolean readEmote;
	private boolean useNotify;
	private boolean useEnglish;
	
	public void setPassword(String p){
		password = p;
	}
	
	public void setUsername(String u){
		username = u;
	}
	
	public void setReadEmote(boolean s){
		readEmote = s;
	}
	
	public void setReadName(boolean s){
		readName = s;
	}
	
	public void setUseNotify(boolean s){
		useNotify = s;
	}
	
	public void setUseEnglish(boolean s){
		useEnglish = s;
	}
	
	public String getPassword(){
		return password;
	}
	
	public String getUsername(){
		return username;
	}
	
	public boolean getReadEmote(){
		return readEmote;
	}
	
	public boolean getReadName(){
		return readName;
	}
	
	public boolean getUseNotify(){
		return useNotify;
	}
	
	public boolean getUseEnglish(){
		return useEnglish;
	}
}
