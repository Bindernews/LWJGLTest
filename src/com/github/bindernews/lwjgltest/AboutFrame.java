package com.github.bindernews.lwjgltest;

import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextPane;

@SuppressWarnings("serial")
public class AboutFrame extends JDialog {
	
	private static String ABOUT_TEXT = null;
	static {
		try {
			ABOUT_TEXT = Res.getText("res/about.html");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public AboutFrame(JFrame owner) {
		super(owner, "About", true);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setResizable(false);
		JTextPane textPane = new JTextPane();
		textPane.setContentType("text/html");
		textPane.setText(ABOUT_TEXT);
		textPane.setEditable(false);
		add(textPane);
		pack();
	}

}
