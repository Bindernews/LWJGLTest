package com.github.bindernews.lwjgltest;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.SoundStore;

/**
 * Initialize everything.
 * 
 * @author bindernews
 * 
 */
public class MainLoop implements Runnable {

	public static final String FALLBACK_LEVEL_STRING = "<level></level>";

	private TrueTypeFont textFont;
	private byte[] keyStates = new byte[Keyboard.KEYBOARD_SIZE];

	protected TopFrame frame;
	protected ExecutorService exec = Executors.newSingleThreadExecutor();

	private boolean musicPlaying = false;
	private boolean wonLevel = false;
	private AtomicBoolean closeRequested = new AtomicBoolean(false);
	private AtomicBoolean paused = new AtomicBoolean(false);

	public LevelLoader levelLoader;
	public RenderEngine renderer;
	public CameraController camControl;
	public Audio gameMusic;
	public boolean needsResize = false;

	public void start() {
		exec.execute(new Runnable() {
			public void run() {
				startRun();
			}
		});
	}

	private void startRun() {
		try {
			Thread.currentThread().getId();
			levelLoader = new XMLLevelLoader();
			frame = new TopFrame(this);
			frame.initLWJGL();
			RenderEngine.setupOpenGL();
			needsResize = true;
			SoundStore.get().init();
			gameMusic = SoundStore.get().getOgg(Res.getResourceAsStream("res/music.ogg"));
			gameMusic.playAsMusic(-10, 5, true);
			setMusicPlaying(false);
			renderer = new RenderEngine(this);
			textFont = new TrueTypeFont(Res.getFont("res/DejaVuSans-Bold.ttf", 20f), true);
			renderer.mgl.setFont(textFont);
			camControl = new CameraController();
			camControl.setCamera(renderer.cam);
			frame.loadLevelResource("res/Level1.xml");
			setPaused(true);
			exec.execute(this);
		} catch (Exception e) {
			frame.setVisible(false);
			frame.dispose();
			GUtils.makePanicFrame(e);
		}
	}

	public void run() {
		Display.sync(40);
		renderer.render();
		Display.update();

		Arrays.fill(keyStates, (byte) 0);
		while (Keyboard.next()) {
			keyStates[Keyboard.getEventKey()] = Keyboard.getEventKeyState() ? (byte) 1
					: (byte) 2;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			requestClose();
		}
		if (keyStates[Keyboard.KEY_M] == 1) {
			setMusicPlaying(!musicPlaying);
		}
		if (keyStates[Keyboard.KEY_P] == 1) {
			setPaused(!isPaused());
			if (wonLevel) {
				refreshLevel();
			}
		}
		if (!isPaused()) {
			camControl.update();
			renderer.cam.update(this);
			camControl.postCollide();
			if (renderer.checkWin(1.5)) {
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
				}
				wonLevel = true;
				setPaused(true);
			}
			renderer.rotateGoal();
		}
		if (!closeRequested.get() && !Display.isCloseRequested()) {
			exec.execute(this);
		}
		else {
			exec.submit(new Runnable() {
				public void run() {
					frame.dispose();
				}
			});
			exec.shutdown();
		}
	}

	public void loadLevelStream(InputStream is) throws LevelLoaderException, IOException {
		Reader r = new InputStreamReader(is);
		levelLoader.loadReader("Level1.xml", r);
		refreshLevel();
		r.close();
	}

	public void loadLevelFile(String fname) throws LevelLoaderException, IOException {
		Reader r = new FileReader(fname);
		levelLoader.loadReader(fname, r);
		refreshLevel();
		r.close();
	}

	public void refreshLevel() {
		wonLevel = false;
		for (BoxObject bo : renderer.blocks) {
			bo.destroy();
		}
		renderer.blocks.clear();
		renderer.blocks.addAll(levelLoader.getBoxes());
		renderer.trianglePos.set(levelLoader.getGoalPos());
		renderer.cam.pos.set(levelLoader.getPlayerPos());
		renderer.cam.rot.y = levelLoader.getPlayerDirection();
	}

	public void loadFallbackLevel() {
		try {
			Reader r = new StringReader(FALLBACK_LEVEL_STRING);
			levelLoader.loadReader("FALLBACK_LEVEL_STRING", r);
			refreshLevel();
		} catch (LevelLoaderException e) {
			GUtils.makePanicFrame(e);
		}
	}

	public void requestClose() {
		closeRequested.set(true);
	}

	public boolean hasWon() {
		return wonLevel;
	}

	public boolean isPaused() {
		return paused.get();
	}

	public void setPaused(final boolean p) {
		paused.set(p);
		if (isPaused()) {
			SoundStore.get().pauseLoop();
		}
		else {
			setMusicPlaying(musicPlaying);
		}
	}

	public boolean isMusicPlaying() {
		return musicPlaying;
	}

	public void setMusicPlaying(final boolean m) {
		musicPlaying = m;
		if (musicPlaying) {
			SoundStore.get().restartLoop();
		}
		else {
			SoundStore.get().pauseLoop();
		}
	}
}
