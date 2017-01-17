package main;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.jibble.pircbot.NickAlreadyInUseException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JCheckBox;

public class Main extends JFrame implements ActionListener {

	private JPanel contentPane;
	private JTextField textAuthPassword;
	private JTextField textUserName;
	private JButton btnConnect = new JButton("Connect");
	private JLabel lblStatus_1 = new JLabel("Disconnected");
	JCheckBox checkBox = new JCheckBox("発言者の名前を読み上げる");
	
	private MyBot bot;
	private boolean connection = false;
	private boolean nameReading = false;
	
	File config = new File("config.cfg");

	/**
	 * Launch the application.
	 */

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

	/**
	 * Create the frame.
	 */
	public Main() {
		setTitle("Twitch Comment Talker");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 300, 190);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblAuthPassword = new JLabel("OAuth Password");
		lblAuthPassword.setBounds(12, 10, 86, 13);
		contentPane.add(lblAuthPassword);

		textAuthPassword = new JTextField();
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

		btnConnect.setBounds(12, 80, 260, 38);
		contentPane.add(btnConnect);
		btnConnect.addActionListener(this);
		btnConnect.setActionCommand("auth");

		JLabel lblStatus = new JLabel("Status:");
		lblStatus.setBounds(12, 128, 35, 13);
		contentPane.add(lblStatus);

		lblStatus_1.setBounds(59, 128, 213, 13);
		contentPane.add(lblStatus_1);
		
		checkBox.setBounds(8, 53, 264, 21);
		contentPane.add(checkBox);
		checkBox.addActionListener(this);
		checkBox.setActionCommand("Check");

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
				storeConfigFile(textAuthPassword.getText(), textUserName.getText());
			} else {
				bot.disconnect();
				lblStatus_1.setText("Disconnected");
				lblStatus_1.setForeground(Color.BLACK);
				btnConnect.setText("Connect");
				storeConfigFile(textAuthPassword.getText(), textUserName.getText());
			}
		}
		if(e.getActionCommand().equals("Check")){
			nameReading = checkBox.isSelected();
			if(connection) bot.readingName(nameReading);
		}
	}

	private void loadConfigFile() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(config));
			textAuthPassword.setText(br.readLine());
			textUserName.setText(br.readLine());
			String readName = br.readLine();
			if(readName.equals(1)){
				checkBox.setSelected(true);
				nameReading = true;
			}
			else {
				checkBox.setSelected(false);
				nameReading = false;
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
			String value = JOptionPane.showInputDialog(this, "OAuthPasswordを取得して下さい。");
			if (value == null) {
				JOptionPane.showMessageDialog(this, "OAuthPasswordを入力して下さい。");
			} else {
				textAuthPassword.setText(value);
			}
		}
	}

	private void storeConfigFile(String password, String username) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(config));
			bw.write(textAuthPassword.getText());
			bw.newLine();
			bw.write(textUserName.getText());
			bw.newLine();
			if(checkBox.isSelected()) bw.write(1);
			else bw.write(0);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
