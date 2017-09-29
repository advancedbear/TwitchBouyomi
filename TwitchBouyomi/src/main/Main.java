package main;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

@SuppressWarnings("serial")
public class Main extends JFrame implements ActionListener {

	private JPanel contentPane;
	private JPasswordField textAuthPassword;
	private JTextField textUserName;
	private JButton btnSAPI;
	private JButton btnConnect = new JButton("Connect");
	private JLabel lblStatus_1 = new JLabel("Disconnected");
	private JCheckBox chkReadName = new JCheckBox("発言者の名前を読み上げる");
	private JCheckBox chkUseEnglish = new JCheckBox("英文は専用音声を使う");
	private JCheckBox chkPopup = new JCheckBox("コメント通知機能を使う");
	private JCheckBox chkReadEmote = new JCheckBox("エモートを読み上げる");

	private MyBot bot;
	private Config cfg;
	private boolean connection = false;
	
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
					JOptionPane.showMessageDialog(null, "32bit版をご利用下さい");
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
		lblAuthPassword.setBounds(12, 10, 86, 13);
		contentPane.add(lblAuthPassword);

		textAuthPassword = new JPasswordField();
		textAuthPassword.setBounds(110, 7, 172, 19);
		contentPane.add(textAuthPassword);
		textAuthPassword.setEchoChar('*');
		textAuthPassword.setColumns(10);
		textAuthPassword.setEditable(false);

		JLabel lblUserName = new JLabel("Channel Name");
		lblUserName.setBounds(12, 33, 86, 13);
		contentPane.add(lblUserName);

		textUserName = new JTextField();
		textUserName.setBounds(110, 30, 172, 19);
		contentPane.add(textUserName);
		textUserName.setColumns(10);
		textUserName.setToolTipText("https://www.twitch.tv/~~~の~~~部分をそのまま入力して下さい。");

		btnConnect.setBounds(8, 148, 276, 38);
		contentPane.add(btnConnect);
		btnConnect.addActionListener(this);
		btnConnect.setActionCommand("btnConnect");
		btnConnect.setToolTipText("Twitchコメントに接続します。");

		JLabel lblStatus = new JLabel("Status:");
		lblStatus.setBounds(12, 196, 43, 13);
		contentPane.add(lblStatus);

		lblStatus_1.setBounds(67, 196, 215, 13);
		contentPane.add(lblStatus_1);

		chkReadName.setBounds(8, 52, 264, 21);
		contentPane.add(chkReadName);
		chkReadName.addActionListener(this);
		chkReadName.setActionCommand("chkReadName");
		chkReadName.setToolTipText("コメント投稿者のユーザー名を読み上げます。");

		chkUseEnglish.setBounds(8, 75, 137, 21);
		contentPane.add(chkUseEnglish);
		chkUseEnglish.addActionListener(this);
		chkUseEnglish.setActionCommand("chkUseEnglish");
		chkUseEnglish.setToolTipText("英文が投稿された時、SAPI5で読み上げをします。");


		btnSAPI = new JButton(vapi.getVoice());
		btnSAPI.setBounds(153, 76, 129, 19);
		contentPane.add(btnSAPI);
		btnSAPI.addActionListener(this);
		btnSAPI.setActionCommand("btnSAPI");
		
		chkPopup.setBounds(8, 121, 137, 21);
		contentPane.add(chkPopup);
		chkPopup.addActionListener(this);
		chkPopup.setActionCommand("chkPopup");
		chkPopup.setToolTipText("コメント受信時に通知ポップアップを表示します。");
		
		chkReadEmote.setBounds(8, 98, 137, 21);
		contentPane.add(chkReadEmote);
		chkReadEmote.addActionListener(this);
		chkReadEmote.setActionCommand("chkReadEmote");
		chkReadEmote.setToolTipText("エモート語句を読み上げします。");

		loadConfigFile();
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("btnConnect")) {
			if (!connection) {
				System.out.println("Now Here");
				lblStatus_1.setText("Connected");
				lblStatus_1.setForeground(Color.GREEN);
				btnConnect.setText("Disconnect");
				textUserName.setEditable(false);
				connection = true;
				try {
					bot = new MyBot(textUserName.getText(), new String(textAuthPassword.getPassword()), "irc.chat.twitch.tv");
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
				bot.setReadName(chkReadName.isSelected());
				bot.setUseEnglish(chkUseEnglish.isSelected());
				bot.setUsePopup(chkPopup.isSelected());
				bot.setReadEmote(chkReadEmote.isSelected());
			} else {
				bot.disconnect();
				connection = false;
				textUserName.setEditable(true);
				lblStatus_1.setText("Disconnected");
				lblStatus_1.setForeground(Color.BLACK);
				btnConnect.setText("Connect");
			}
		}
		if(e.getActionCommand().equals("chkReadName")){
			if(connection) bot.setReadName(chkReadName.isSelected());
		}
		if(e.getActionCommand().equals("chkUseEnglish")){
			if(connection) bot.setUseEnglish(chkUseEnglish.isSelected());
		}
		if(e.getActionCommand().equals("chkPopup")){
			if(connection) bot.setUsePopup(chkPopup.isSelected());
		}
		if(e.getActionCommand().equals("chkReadEmote")){
			if(connection) bot.setReadEmote(chkReadEmote.isSelected());
		}
		if(e.getActionCommand().equals("btnSAPI")){
			ProcessBuilder pb = new ProcessBuilder("control", System.getenv("windir")+"\\System32\\Speech\\SpeechUX\\sapi.cpl");
			try {
				pb.start();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, "音声合成プロパティが開けません。");
			}
		}
		storeConfigFile();
	}

	private void loadConfigFile() {
		try {
			FileInputStream fis = new FileInputStream(config);
		    ObjectInputStream ois = new ObjectInputStream(fis);
		    cfg = (Config) ois.readObject();
		    ois.close();
			
			textAuthPassword.setText(cfg.getPassword());
			textUserName.setText(cfg.getUsername());
			chkReadName.setSelected(cfg.getReadName());
			chkUseEnglish.setSelected(cfg.getUseEnglish());
			chkPopup.setSelected(cfg.getUseNotify());
			chkReadEmote.setSelected(cfg.getReadEmote());
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
			cfg.setPassword(new String(textAuthPassword.getPassword()));
			cfg.setUsername(textUserName.getText());
			cfg.setReadEmote(chkReadEmote.isSelected());
			cfg.setReadName(chkReadName.isSelected());
			cfg.setUseEnglish(chkUseEnglish.isSelected());
			cfg.setUseNotify(chkPopup.isSelected());
			
			FileOutputStream fos = new FileOutputStream(config);
		    ObjectOutputStream oos = new ObjectOutputStream(fos);
		    oos.writeObject(cfg);
		    oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
