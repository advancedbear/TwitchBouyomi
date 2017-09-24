package main;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.jibble.pircbot.NickAlreadyInUseException;

import WinSAPI.WinSAPI;

import java.awt.Font;

@SuppressWarnings("serial")
public class Main extends JFrame implements ActionListener {

	private JPanel contentPane;
	private JPasswordField textAuthPassword;
	private JTextField textUserName;
	private JButton textEnglish;
	private JButton btnConnect = new JButton("Connect");
	private JLabel lblStatus_1 = new JLabel("Disconnected");
	JCheckBox checkBox = new JCheckBox("発言者の名前を読み上げる");
	JCheckBox checkBox2 = new JCheckBox("英文は専用音声を使う");
	JCheckBox notifiCheck = new JCheckBox("コメント通知機能を使う");
	JCheckBox readEmote = new JCheckBox("エモートを読み上げる");

	private MyBot bot;
	private boolean connection = false;
	private boolean nameReading = false;
	private boolean emoteReading = false;
	private boolean notifiUsing = false;
	private int English = -1;
	
	private WinSAPI vapi = new WinSAPI("");
	
	File config = new File("config.cfg");

	public static void main(String[] args) {
		System.setProperty("sun.java2d.noddraw", "true");
	    System.setProperty("swing.defaultlaf", "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				} catch (java.lang.UnsatisfiedLinkError e2){
					e2.printStackTrace();
					JOptionPane.showMessageDialog(null, "64bit版をご利用下さい");
					System.exit(-1);
				}
			}
		});
	}

	public Main() {
		setTitle("Twitch Comment Talker");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ImageIcon icon = new ImageIcon("./icon.png");
	    setIconImage(icon.getImage());
		setBounds(100, 100, 300, 248);
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblAuthPassword = new JLabel("OAuth Pass");
		//lblAuthPassword.setFont(new Font("Meiryo UI", Font.PLAIN, 12));
		lblAuthPassword.setBounds(12, 10, 86, 13);
		contentPane.add(lblAuthPassword);

		textAuthPassword = new JPasswordField();
		textAuthPassword.setBounds(110, 7, 172, 19);
		contentPane.add(textAuthPassword);
		textAuthPassword.setEchoChar('*');
		textAuthPassword.setColumns(10);
		textAuthPassword.setEditable(false);

		JLabel lblUserName = new JLabel("Channel Name");
		//lblUserName.setFont(new Font("Meiryo UI", Font.PLAIN, 12));
		lblUserName.setBounds(12, 33, 86, 13);
		contentPane.add(lblUserName);

		textUserName = new JTextField();
		textUserName.setBounds(110, 30, 172, 19);
		contentPane.add(textUserName);
		textUserName.setColumns(10);
		//btnConnect.setFont(new Font("Meiryo UI", Font.PLAIN, 12));

		btnConnect.setBounds(8, 148, 276, 38);
		contentPane.add(btnConnect);
		btnConnect.addActionListener(this);
		btnConnect.setActionCommand("auth");

		JLabel lblStatus = new JLabel("Status:");
		//lblStatus.setFont(new Font("Meiryo UI", Font.PLAIN, 12));
		lblStatus.setBounds(12, 196, 43, 13);
		contentPane.add(lblStatus);
		//lblStatus_1.setFont(new Font("Meiryo UI", Font.PLAIN, 12));

		lblStatus_1.setBounds(67, 196, 215, 13);
		contentPane.add(lblStatus_1);
		//checkBox.setFont(new Font("Meiryo UI", Font.PLAIN, 12));

		checkBox.setBounds(8, 52, 264, 21);
		contentPane.add(checkBox);
		checkBox.addActionListener(this);
		checkBox.setActionCommand("Check");
		//checkBox2.setFont(new Font("Meiryo UI", Font.PLAIN, 12));

		checkBox2.setBounds(8, 75, 137, 21);
		contentPane.add(checkBox2);
		checkBox2.addActionListener(this);
		checkBox2.setActionCommand("Check2");


		textEnglish = new JButton(vapi.getVoice());
		//textEnglish.setFont(new Font("Meiryo UI", Font.PLAIN, 12));
		textEnglish.setBounds(153, 76, 129, 19);
		contentPane.add(textEnglish);
		textEnglish.addActionListener(this);
		textEnglish.setActionCommand("openSAPI");
		
		//notifiCheck.setFont(new Font("Meiryo UI", Font.PLAIN, 12));
		notifiCheck.setBounds(8, 121, 137, 21);
		contentPane.add(notifiCheck);
		notifiCheck.addActionListener(this);
		notifiCheck.setActionCommand("notifi");
		
		readEmote.setBounds(8, 98, 137, 21);
		contentPane.add(readEmote);
		readEmote.addActionListener(this);
		readEmote.setActionCommand("emote");

		loadConfigFile();
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("auth")) {
			if (!connection) {
				System.out.println("Now Here");
				lblStatus_1.setText("Connected");
				lblStatus_1.setForeground(Color.GREEN);
				btnConnect.setText("Disconnect");
				textUserName.setEditable(false);
				connection = true;
				try {
					bot = new MyBot(textUserName.getText(), new String(textAuthPassword.getPassword()), "irc.chat.twitch.tv");
					bot.readingName(checkBox.isSelected());
				} catch (NickAlreadyInUseException e1) {
					lblStatus_1.setText("ユーザー名が違います。");
					lblStatus_1.setForeground(Color.RED);
					textUserName.setEditable(true);
					connection = false;
					bot.disconnect();
				} catch (Exception e2) {
					lblStatus_1.setText(e2.toString());
					lblStatus_1.setForeground(Color.RED);
					textUserName.setEditable(true);
					connection = false;
					bot.disconnect();
				}
				if(checkBox2.isSelected()) bot.usingEnglish(English);
				if(notifiCheck.isSelected()) bot.usingPopup(true);
				if(readEmote.isSelected()) bot.readingEmote(true);
				storeConfigFile();
			} else {
				bot.disconnect();
				connection = false;
				textUserName.setEditable(true);
				lblStatus_1.setText("Disconnected");
				lblStatus_1.setForeground(Color.BLACK);
				btnConnect.setText("Connect");
				storeConfigFile();
			}
		}
		if(e.getActionCommand().equals("Check")){
			nameReading = checkBox.isSelected();
			if(connection) bot.readingName(nameReading);
			storeConfigFile();
		}
		if(e.getActionCommand().equals("Check2")){
			try {
				if(checkBox2.isSelected()) English = 1;
				else English = -1;
			} catch (NumberFormatException nfe){
				checkBox2.setSelected(false);;
				JOptionPane.showMessageDialog(this, "英語音声のIDを入力してください");
			}
			
			if(connection) bot.usingEnglish(English);
			storeConfigFile();
		}
		if(e.getActionCommand().equals("notifi")){
			if(connection){
				notifiUsing = notifiCheck.isSelected();
				bot.usingPopup(notifiUsing);
				storeConfigFile();
			}
		}
		if(e.getActionCommand().equals("emote")){
			if(connection){
				emoteReading = readEmote.isSelected();
				bot.readingEmote(emoteReading);
				storeConfigFile();
			}
		}
		if(e.getActionCommand().equals("openSAPI")){
			ProcessBuilder pb = new ProcessBuilder("control", System.getenv("windir")+"\\System32\\Speech\\SpeechUX\\sapi.cpl");
			try {
				pb.start();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, "音声合成プロパティが開けません。");
			}
		}
	}

	private void loadConfigFile() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(config));
			textAuthPassword.setText(br.readLine());
			textUserName.setText(br.readLine());
			String readName = br.readLine();
			if(readName.equals("1")){
				checkBox.setSelected(true);
				nameReading = true;
			}
			else {
				checkBox.setSelected(false);
				nameReading = false;
			}
			if(br.readLine().equals("1")){
				checkBox2.setSelected(true);
				English = 1;
			}
			else {
				checkBox2.setSelected(false);
				English = -1;
			}
			if(br.readLine().equals("1")){
				notifiCheck.setSelected(true);
				notifiUsing = true;
			}
			else {
				notifiCheck.setSelected(false);
				notifiUsing = false;
			}
			if(br.readLine().equals("1")){
				readEmote.setSelected(true);
				emoteReading = true;
			}
			else {
				readEmote.setSelected(false);
				emoteReading = false;
			}
			br.close();
		} catch (Exception e) {
			Desktop desktop = Desktop.getDesktop();
			URI uri;
			try {
				uri = new URI("http://www.twitchapps.com/tmi/");
				desktop.browse(uri);
			} catch (Exception error) {
				lblStatus_1.setText(error.toString());
				lblStatus_1.setForeground(Color.RED);
				connection = false;
			}
			String value = JOptionPane.showInputDialog(this, "OAuthPasswordを入力して下さい。");
			if (value == null) {
				JOptionPane.showMessageDialog(this, "OAuthPasswordを入力して下さい。");
				System.exit(-1);
			} else {
				textAuthPassword.setText(value);
			}
		}
	}

	private void storeConfigFile() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(config));
			bw.write(new String(textAuthPassword.getPassword()));
			bw.newLine();
			bw.write(textUserName.getText());
			bw.newLine();
			if(checkBox.isSelected()) bw.write("1");
			else bw.write("0");
			bw.newLine();
			if(checkBox2.isSelected()) bw.write("1");
			else bw.write("0");
			bw.newLine();
			if(notifiCheck.isSelected()) bw.write("1");
			else bw.write("0");
			bw.newLine();
			if(readEmote.isSelected()) bw.write("1");
			else bw.write("0");
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
