package com.zereb.flappy;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;


public class Flappy extends Game {
	public SpriteBatch batch;
	public ShapeRenderer debug;
	public BitmapFont font;
	public Audio audio;
	public int maxScore = 0;
	public Skin skin;
	public static ResourseManager resourseManager;
	public static int WIDTH = 320, HEIGHT = 560;
	private Preferences preferences;


	@Override
	public void create () {
		batch = new SpriteBatch();
		debug = new ShapeRenderer();
		audio = Gdx.audio;
		debug.setAutoShapeType(true);
		font = new BitmapFont(Gdx.files.internal("pixel.fnt"));
		skin = new Skin();
		resourseManager = new ResourseManager(skin.current());
		preferences = Gdx.app.getPreferences("score");
		maxScore = preferences.getInteger("maxScore");
		this.setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		Gdx.app.log("Flappy: ", "Disposing");
		batch.dispose();
		font.dispose();
		debug.dispose();
		resourseManager.dispose();
	}

	public void save(int score){
		maxScore = score;
		preferences.putInteger("maxScore", score);
		preferences.flush();

	}
}
