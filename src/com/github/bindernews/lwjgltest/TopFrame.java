package com.github.bindernews.lwjgltest;

import java.awt.Canvas;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.openal.SoundStore;

public class TopFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	private static final String WINDOW_TITLE = "LWJGL Test";
	private Canvas canvas;
	private JFileChooser fileChooser;
	private JDialog controlsDialog;
	private JDialog aboutDialog;
	final MainLoop mainLoop;

	public TopFrame(MainLoop loop) {
		super(WINDOW_TITLE);

		mainLoop = loop;
		fileChooser = new JFileChooser();

		controlsDialog = new GameHelpDialog(this);
		controlsDialog.addWindowListener(windowListenerGamePause);

		aboutDialog = new AboutFrame(this);
		aboutDialog.addWindowListener(windowListenerGamePause);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				mainLoop.requestClose();
			}
		});
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				mainLoop.needsResize = true;
			}
		});

		JMenu fileMenu = new JMenu("File");
		fileMenu.add("Open");
		fileMenu.addSeparator();
		fileMenu.add("Level 1");
		fileMenu.add("Level 2");
		fileMenu.add("Fallback Level");
		fileMenu.addSeparator();
		fileMenu.add("Quit");
		GUtils.addListenerToItems(fileMenu, this);

		JMenu helpMenu = new JMenu("Help");
		helpMenu.add("Game Help");
		helpMenu.add("About");
		GUtils.addListenerToItems(helpMenu, this);

		JMenuBar menubar = new JMenuBar();
		menubar.add(fileMenu);
		menubar.add(helpMenu);
		setJMenuBar(menubar);

		canvas = new Canvas();
		add(canvas);
		canvas.setSize(640, 480);
		pack();
	}

	@Override
	public void dispose() {
		deinitLWJGL();
		super.dispose();
	}

	public void initLWJGL() throws LWJGLException, InvocationTargetException, InterruptedException {
		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				setVisible(true);
			}
		});
		Display.setParent(canvas);
		Display.setResizable(true);
		Display.create();
		Mouse.create();
		Keyboard.create();
		canvas.requestFocus();
	}

	public void deinitLWJGL() {
		Display.destroy();
		Mouse.destroy();
		Keyboard.destroy();
		SoundStore.get().clear();
		AL.destroy();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JMenuItem) {
			if ("About".equals(e.getActionCommand())) {
				aboutDialog.setVisible(true);
			}
			if ("Game Help".equals(e.getActionCommand())) {
				controlsDialog.setVisible(true);
			}
			if ("Open".equals(e.getActionCommand())) {
				fileChooser.showOpenDialog(this);
				final File lfile = fileChooser.getSelectedFile();
				if (lfile == null || !lfile.exists())
					return;
				mainLoop.exec.submit(new Runnable() {
					public void run() {
						try {
							mainLoop.loadLevelFile(lfile.getAbsolutePath());
						}
						catch (Exception ex) {
							mainLoop.loadFallbackLevel();
							makeErrorDialog(ex).setVisible(true);
						}
					}
				});

			}
			if ("Quit".equals(e.getActionCommand())) {
				mainLoop.requestClose();
			}
			if (e.getActionCommand().equals("Level 1")) {
				mainLoop.exec.submit(new Runnable() {
					public void run() {
						try {
							loadLevelResource("res/Level1.xml");
						} catch (Exception ex) {
							handleLoadException(ex);
						}
					}
				});
			}
			if (e.getActionCommand().equals("Level 2")) {
				mainLoop.exec.submit(new Runnable() {
					public void run() {
						try {
							loadLevelResource("res/Level2.xml");
						} catch (Exception ex) {
							handleLoadException(ex);
						}
					}
				});
			}
			if ("Fallback Level".equals(e.getActionCommand())) {
				mainLoop.exec.submit(new Runnable() {
					public void run() {
						mainLoop.loadFallbackLevel();
					}
				});
			}
			canvas.requestFocus();
		}

	}
	
	private void handleLoadException(Exception e) {
		mainLoop.exec.submit(new Runnable() {
			public void run() {
				mainLoop.loadFallbackLevel();
			}
		});
		makeErrorDialog(e);
	}

	public void loadLevelResource(String resname) throws LevelLoaderException, IOException {
		mainLoop.loadLevelStream(Res.getResourceAsStream(resname));
	}

	public JDialog makeErrorDialog(Throwable t) {
		JDialog dlg = new JDialog(this, "Error", true);
		dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dlg.addWindowListener(windowListenerGamePause);
		dlg.add(GUtils.makeErrorPane(t));
		dlg.pack();
		GUtils.setWinVisible(dlg, true);
		return dlg;
	}

	public WindowListener windowListenerQuit = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			mainLoop.requestClose();
		}
	};

	public WindowListener windowListenerGamePause = new WindowAdapter() {
		@Override
		public void windowOpened(WindowEvent e) {
			mainLoop.setPaused(true);
		}

		@Override
		public void windowClosing(WindowEvent e) {
			mainLoop.setPaused(false);
		}
	};

}
