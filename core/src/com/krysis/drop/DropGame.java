package com.krysis.drop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class DropGame extends Game {

	public SpriteBatch batch;
	public BitmapFont font;
	GameScreen gameScreen;

	public void create() {

		batch = new SpriteBatch();

		font = new BitmapFont();
		this.setScreen(new MainMenuScreen(this));

	}

	public void render() {
		super.render(); //important!
	}

	public void dispose() {
		batch.dispose();
		font.dispose();

		if (gameScreen != null) {

			gameScreen.dispose();

		}

	}

	public  void setGameScreen(GameScreen gameScreen) {

		this.gameScreen = gameScreen;

	}

}
