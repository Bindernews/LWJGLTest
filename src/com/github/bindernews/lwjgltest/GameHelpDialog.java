package com.github.bindernews.lwjgltest;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextPane;

public class GameHelpDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	public static final String GAMEPLAY_HELP = "The goal of the game is reach the awesome spinning trianglular shape.";
	public static final String[] CONTROLS_HELP = {
		"Controls:",
		"W = forward",
		"A = left",
		"S = backwards",
		"D = right",
		"Spacebar = jump",
		"Ctrl = toggle fast mode",
		"M = toggle music",
		"P = Pause / Unpause",
		"",
		"Up Arrow = look up",
		"Down Arrow = look down",
		"Left Arrow = look left",
		"Right Arrow = look right",
	};

	public GameHelpDialog(JFrame owner) {
		super(owner, "Controls", true);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setResizable(false);
		StringBuffer sb = new StringBuffer(GAMEPLAY_HELP);
		for(String s : CONTROLS_HELP) {
			sb.append(s);
			sb.append('\n');
		}
		JTextPane jta = new JTextPane();
		jta.setText(sb.toString());
		jta.setEditable(false);
		add(jta);
		pack();
	}

}
