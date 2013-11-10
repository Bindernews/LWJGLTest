package com.github.bindernews.lwjgltest;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

public class GUtils {
	
	public static final Color ERROR_COLOR_1 = new Color(255, 58, 0);
	public static final Color ERROR_COLOR_2 = new Color(255, 210, 210);

	public static void setWinVisible(final JFrame win, final boolean vis) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				win.setVisible(vis);
			}
		});
	}

	public static void setWinVisible(final JDialog win, final boolean vis) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				win.setVisible(vis);
			}
		});
	}

	public static void addListenerToItems(JMenu menu, ActionListener listener) {
		for (int i = 0; i < menu.getItemCount(); i++) {
			JMenuItem jmi = menu.getItem(i);
			if (jmi != null)
				jmi.addActionListener(listener);
		}
	}

	public static JFrame makePanicFrame(Throwable t) {
		JFrame frame = new JFrame("FATAL ERROR");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(makeErrorPane(t));
		frame.pack();
		setWinVisible(frame, true);
		return frame;
	}

	public static JTextPane makeErrorPane(Throwable t) {
		String message = Util.stackTraceToString(t);
		System.err.println(message);
		JTextPane jta = new JTextPane();
		jta.setBackground(ERROR_COLOR_2);
		jta.setText(message);
		jta.setEditable(false);
		return jta;
	}

	public static WindowListener windowListenerDispose = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			e.getWindow().dispose();
		}
	};

	public static WindowListener windowHideListener = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			e.getWindow().setVisible(false);
		}
	};

	public static WindowListener windowListenerPanic = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	};

}
