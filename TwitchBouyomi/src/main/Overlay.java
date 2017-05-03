package main;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;

public class Overlay extends JFrame{
	

	private int count = 0;

	private JLabel Comment1 = new JLabel("");
	private JLabel Comment2 = new JLabel("");
	private JLabel Comment3 = new JLabel("");
	private JLabel Comment4 = new JLabel("");
	private JLabel Comment5 = new JLabel("");
	
	private String comments[] = {"", "", "", "", ""};
	
	public Overlay() {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		
		this.setTitle("Twitch Comment Overlay");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setUndecorated(true);
		this.setPreferredSize(new Dimension(400, 400));
		this.setLocation(d.width-400, 0);
		//this.setBounds(100, 100, 300, 220);
		this.setAlwaysOnTop(true);
		JPanel contentPane = new JPanel();
		contentPane.setBackground(new Color(0,0,0,0));
		this.setContentPane(contentPane);
		contentPane.setLayout(null);
		Comment1.setFont(new Font("MS UI Gothic", Font.PLAIN, 16));
		
		Comment1.setBackground(new Color(0,0,0,0));
		Comment1.setVerticalAlignment(SwingConstants.TOP);
		Comment1.setForeground(Color.WHITE);
		Comment1.setBounds(12, 10, 376, 63);
		contentPane.add(Comment1);
		
		Comment2.setVerticalAlignment(SwingConstants.TOP);
		Comment2.setForeground(Color.WHITE);
		Comment2.setFont(new Font("MS UI Gothic", Font.PLAIN, 16));
		Comment2.setBackground(new Color(0, 0, 0, 0));
		Comment2.setBounds(12, 83, 376, 63);
		contentPane.add(Comment2);
		
		Comment3.setVerticalAlignment(SwingConstants.TOP);
		Comment3.setForeground(Color.WHITE);
		Comment3.setFont(new Font("MS UI Gothic", Font.PLAIN, 16));
		Comment3.setBackground(new Color(0, 0, 0, 0));
		Comment3.setBounds(12, 156, 376, 63);
		contentPane.add(Comment3);
		
		Comment4.setVerticalAlignment(SwingConstants.TOP);
		Comment4.setForeground(Color.WHITE);
		Comment4.setFont(new Font("MS UI Gothic", Font.PLAIN, 16));
		Comment4.setBackground(new Color(0, 0, 0, 0));
		Comment4.setBounds(12, 229, 376, 63);
		contentPane.add(Comment4);
		
		Comment5.setVerticalAlignment(SwingConstants.TOP);
		Comment5.setForeground(Color.WHITE);
		Comment5.setFont(new Font("MS UI Gothic", Font.PLAIN, 16));
		Comment5.setBackground(new Color(0, 0, 0, 0));
		Comment5.setBounds(12, 302, 376, 63);
		contentPane.add(Comment5);
		
		//this.getRootPane().setBorder(new LineBorder(Color.black, 2));
		this.setBackground(new Color(0,0,0,0));

        this.pack();
        this.setVisible(true);
		
		System.out.println("Open new overlay");
	}
	
	public void SetComment(String name, String text){
		if(count < 5) comments[count] = "<html><B>" +name + "</B><br>" + text;
		else {
			for(int i=1;i<5;i++) comments[i-1]=comments[i];
			comments[4] = "<html><B>" +name + "</B><br>" + text;
		}
		Comment1.setText(comments[0]);
		Comment2.setText(comments[1]);
		Comment3.setText(comments[2]);
		Comment4.setText(comments[3]);
		Comment5.setText(comments[4]);
		count++;
	}
}
