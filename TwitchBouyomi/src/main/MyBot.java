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
    boolean readEmote = false;
    boolean useEnglish = false;
    

	WinSAPI  sapi = new WinSAPI("");
    
    private TrayIcon icon;
    private SystemTray tray;
    
	Map<String, String> repDicJP = new HashMap<String, String>();
	Map<String, String> repDicEN = new HashMap<String, String>();
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
    		
    		BufferedReader reader = new BufferedReader(new FileReader("Replacement_JP.dic"));
    		String rl;
    		while((rl = reader.readLine()) != null) {
    			if(!(rl.startsWith("//") || rl.equals(""))){
    				repDicJP.put(rl.split(",")[0],rl.split(",")[1]);
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
    		
    		BufferedReader reader3 = new BufferedReader(new FileReader("Replacement_EN.dic"));
    		String rl3;
    		while((rl3 = reader3.readLine()) != null) {
    			if(!(rl3.startsWith("//") || rl3.equals(""))){
    				repDicEN.put(rl3.split(",")[0],rl3.split(",")[1]);
    			}
    		}
    		reader3.close();
	}


	public void onMessage(String channel, String sender,
                       String login, String hostname, String message) {
		String tmpMessage = message;
		if(!isBlockedUser(sender)){
			tmpMessage = readEmote ? urlReplace(tmpMessage) : urlReplace(emoteReplace(tmpMessage));
			tmpMessage = readName ? tmpMessage+", "+sender : tmpMessage;
			if(useEnglish && isEnglish(message)) sapi.speakAsyMsg(wordReplaceEN(tmpMessage));
			else talker.talk(wordReplaceJP(tmpMessage));
			
			if(usePopup){
				icon.displayMessage(sender, message, MessageType.NONE);
			}
		}
    }


	public void setReadName (boolean check) {
		readName = check;
	}

	public void setUseEnglish(boolean check) {
		useEnglish = check;
	}
	
	public void setUsePopup(boolean check) {
		usePopup = check;
	}
	
	public void setReadEmote(boolean check){
		readEmote = check;
	}

	public boolean isEnglish(String text){
		if(text.matches("^(.*[｡-ﾟ０-９ａ-ｚＡ-Ｚぁ-んァ-ヶ亜-黑一-龠々ー].*)*$")) {
			System.out.println("これは日本語です。");
			return false;
		}
		return true;
	}
	
	public boolean isBlockedUser (String username){
		for(int i=0;i < 1000; i++){
			if(username.equals(blockUsers[i])){
				return true;
			}
		}
		return false;
	}
	
	public String wordReplaceJP(String word){
		String result = word;
		for(String key: repDicJP.keySet()){
			String data = repDicJP.get(key);
			Pattern p = Pattern.compile(key, Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(result);
			result = m.replaceAll(data);
		}
		
		return result;
	}
	
	public String wordReplaceEN(String word){
		String result = word;
		for(String key: repDicEN.keySet()){
			String data = repDicEN.get(key);
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
	
	public String emoteReplace(String word){
		String result = word;
		String[] emotes = {"4Head","AMPTropPunch","ANELE","ArgieB8","ArigatoNas","ArsonNoSexy","AsianGlow","BCWarrior","BJBlazkowicz","BabyRage","BatChest","BegWan","BibleThump","BigBrother","BigPhish","BlargNaut","BlessRNG","BloodTrail","BrainSlug","BrokeBack","BudStar","BuddhaBar","CarlSmile","ChefFrank","CoolCat","CoolStoryBob","CorgiDerp","CrreamAwk","CurseLit","DAESuppy","DBstyle","DansGame","DatSheffy","DendiFace","DogFace","DoritosChip","DxCat","EleGiggle","EntropyWins","FUNgineer","FailFish","FrankerZ","FreakinStinkin","FunRun","FutureMan","GOWSkull","GingerPower","GivePLZ","GrammarKing","HassaanChop","HassanChop","HeyGuys","HotPokket","HumbleLife","InuyoFace","ItsBoshyTime","JKanStyle","Jebaited","JonCarnage","KAPOW","Kappa","KappaClaus","KappaPride","KappaRoss","KappaWealth","Kappu","Keepo","KevinTurtle","Kippa","KonCha","Kreygasm","MVGame","Mau5","MikeHogu","MingLee","MorphinTime","MrDestructoid","NinjaGrumpy","NomNom","NotATK","NotLikeThis","OSblob","OSfrog","OSkomodo","OSsloth","OhMyDog","OneHand","OpieOP","OptimizePrime","PJSalt","PJSugar","PMSTwin","PRChase","PanicVis","PartyTime","PeoplesChamp","PermaSmug","PicoMause","PipeHype","PogChamp","Poooound","PraiseIt","PrimeMe","PunOko","PunchTrees","QuadDamage","RaccAttack","RalpherZ","RedCoat","ResidentSleeper","RitzMitz","RlyTho","RuleFive","SMOrc","SSSsss","SabaPing","SeemsGood","ShadyLulu","ShazBotstix","SmoocherZ","SoBayed","SoonerLater","Squid1","Squid2","Squid3","Squid4","StinkyCheese","StoneLightning","StrawBeary","SuperVinlin","SwiftRage","TBAngel","TBCrunchy","TBTacoBag","TBTacoProps","TF2John","TPcrunchyroll","TTours","TakeNRG","TearGlove","TehePelo","ThankEgg","TheIlluminati","TheRinger","TheTarFu","TheThing","ThunBeast","TinyFace","TooSpicy","TriHard","TwitchLit","TwitchRPG","TwitchUnity","UWot","UnSane","UncleNox","VaultBoy","VoHiYo","VoteNay","VoteYea","WTRuck","WholeWheat","WutFace","YouDontSay","YouWHY","bleedPurple","cmonBruh","copyThis","duDudu","imGlitch","mcaT","panicBasket","pastaThat","riPepperonis","twitchRaid"};
		for(int i=0; i<emotes.length; i++){
			Pattern p = Pattern.compile(emotes[i]);
			Matcher m = p.matcher(result);
			result = m.replaceAll("");
		}
		return result;
	}
}
