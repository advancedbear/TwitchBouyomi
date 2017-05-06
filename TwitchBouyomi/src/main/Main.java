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

public class Main extends JFrame implements ActionListener {

	private JPanel contentPane;
	private JTextField textAuthPassword;
	private JTextField textUserName;
	private JTextField textEnglish;
	private JButton btnConnect = new JButton("Connect");
	private JLabel lblStatus_1 = new JLabel("Disconnected");
	JCheckBox checkBox = new JCheckBox("発言者の名前を読み上げる");
	JCheckBox checkBox2 = new JCheckBox("英文は専用音声を使う");
	JCheckBox notifiCheck = new JCheckBox("通知");

	private MyBot bot;
	private boolean connection = false;
	private boolean nameReading = false;
	private boolean notifiUsing = false;
	private int English = -1;
	
	File config = new File("config.cfg");

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Main() {
		setTitle("Twitch Comment Talker");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 300, 220);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblAuthPassword = new JLabel("OAuth Pass");
		lblAuthPassword.setBounds(12, 10, 86, 13);
		contentPane.add(lblAuthPassword);

		textAuthPassword = new JPasswordField();
		textAuthPassword.setBounds(110, 7, 162, 19);
		contentPane.add(textAuthPassword);
		textAuthPassword.setColumns(10);
		textAuthPassword.setEditable(false);

		JLabel lblUserName = new JLabel("User Name");
		lblUserName.setBounds(12, 33, 86, 13);
		contentPane.add(lblUserName);

		textUserName = new JTextField();
		textUserName.setBounds(110, 30, 162, 19);
		contentPane.add(textUserName);
		textUserName.setColumns(10);

		btnConnect.setBounds(12, 110, 260, 38);
		contentPane.add(btnConnect);
		btnConnect.addActionListener(this);
		btnConnect.setActionCommand("auth");

		JLabel lblStatus = new JLabel("Status:");
		lblStatus.setBounds(12, 158, 40, 13);
		contentPane.add(lblStatus);

		lblStatus_1.setBounds(59, 158, 145, 13);
		contentPane.add(lblStatus_1);

		checkBox.setBounds(8, 53, 264, 21);
		contentPane.add(checkBox);
		checkBox.addActionListener(this);
		checkBox.setActionCommand("Check");

		checkBox2.setBounds(8, 80, 160, 21);
		contentPane.add(checkBox2);
		checkBox2.addActionListener(this);
		checkBox2.setActionCommand("Check2");


		textEnglish = new JTextField();
		textEnglish.setBounds(170, 82, 100, 19);
		contentPane.add(textEnglish);
		textEnglish.setColumns(10);
		
		notifiCheck.setBounds(212, 154, 60, 21);
		contentPane.add(notifiCheck);
		notifiCheck.addActionListener(this);
		notifiCheck.setActionCommand("notifi");

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
				connection = true;
				try {
					bot = new MyBot(textUserName.getText(), textAuthPassword.getText(), "irc.chat.twitch.tv");
					bot.readingName(checkBox.isSelected());
				} catch (NickAlreadyInUseException e1) {
					lblStatus_1.setText("ユーザー名が違います。");
					lblStatus_1.setForeground(Color.RED);
					connection = false;
					bot.disconnect();
				} catch (Exception e2) {
					lblStatus_1.setText(e2.toString());
					lblStatus_1.setForeground(Color.RED);
					connection = false;
					bot.disconnect();
				}
				if(checkBox2.isSelected()) bot.usingEnglish(English);
				if(notifiCheck.isSelected()) bot.usingPopup(true);
				storeConfigFile();
			} else {
				bot.disconnect();
				connection = false;
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
				if(checkBox2.isSelected()) English = Integer.parseInt(textEnglish.getText());
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
			String useEnglish = br.readLine();
			textEnglish.setText(useEnglish);
			English = Integer.parseInt(useEnglish);
			if(br.readLine().equals("1")){
				checkBox2.setSelected(true);
			}
			else {
				checkBox2.setSelected(false);
			}
			if(br.readLine().equals("1")){
				notifiCheck.setSelected(true);
				notifiUsing = true;
			}
			else {
				notifiCheck.setSelected(false);
				notifiUsing = false;
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
			} else {
				textAuthPassword.setText(value);
			}
		}
	}

	private void storeConfigFile() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(config));
			bw.write(textAuthPassword.getText());
			bw.newLine();
			bw.write(textUserName.getText());
			bw.newLine();
			if(checkBox.isSelected()) bw.write("1");
			else bw.write("0");
			bw.newLine();
			bw.write(textEnglish.getText());
			bw.newLine();
			if(checkBox2.isSelected()) bw.write("1");
			else bw.write("0");
			bw.newLine();
			if(notifiCheck.isSelected()) bw.write("1");
			else bw.write("0");
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
