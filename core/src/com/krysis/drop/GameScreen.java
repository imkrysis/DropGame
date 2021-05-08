package com.krysis.drop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import org.w3c.dom.css.Rect;

import java.util.Iterator;

public class GameScreen implements Screen {

    final DropGame game;

    private Texture dropTexture;
    private Texture bucketTexture;
    private Texture backgroundTexture;
    private Sound dropSound;
    private Sound gameOverSound;
    private Music rainMusic;

    private OrthographicCamera camera;

    private Rectangle bucket;
    private Array<Rectangle> rainDrops;

    private long lastDropTime;
    int dropsGathered;
    private boolean gameOver;

    Texture gameOverImage;
    Texture gameOverBackground;

    GlyphLayout gameOverTextL;
    Rectangle gameOverButton;


    public GameScreen(final DropGame game) {

        this.game = game;

        game.setGameScreen(this);

        dropTexture = new Texture(Gdx.files.internal("drop.png"));
        bucketTexture = new Texture(Gdx.files.internal("bucket.png"));
        backgroundTexture = new Texture(Gdx.files.internal("background.png"));
        gameOverBackground = new Texture(Gdx.files.internal("gameOverWallpaper.jpg"));

        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        rainMusic.setLooping(true);
        gameOverSound = Gdx.audio.newSound(Gdx.files.internal("gameOver.wav"));

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        gameOverTextL = new GlyphLayout(game.font, "");

        loadGame();

    }

    private void spawnRaindrop() {

        Rectangle rainDrop = new Rectangle();
        rainDrop.x = MathUtils.random(0, 800-64);
        rainDrop.y = 480;
        rainDrop.width = 64;
        rainDrop.height = 64;
        rainDrops.add(rainDrop);

        lastDropTime = TimeUtils.nanoTime();

    }

    @Override
    public void render (float delta) {

        if (gameOver) {

            Gdx.gl.glClearColor(0, 0, 0.2f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            game.batch.begin();
            game.batch.draw(gameOverBackground,0,0,camera.viewportWidth,camera.viewportHeight);
            game.font.draw(game.batch, gameOverTextL, (camera.viewportWidth - gameOverTextL.width) / 2, 150);
            game.batch.draw(gameOverImage, gameOverButton.x, gameOverButton.y, 50, 50);
            game.batch.end();

            if (Gdx.input.isTouched()) {

                Vector3 touchPos = new Vector3();
                touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(touchPos);

                if (touchPos.x >= gameOverButton.x && touchPos.x <= (gameOverButton.x + gameOverButton.getWidth())) {

                    if (touchPos.y >= gameOverButton.y && touchPos.y <= (gameOverButton.y + gameOverButton.getHeight())) {

                        if (gameOver) {

                            loadGame();

                        }

                    }

                }

            }

        } else {

            Gdx.gl.glClearColor(0, 0, 0.2f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            camera.update();

            game.batch.setProjectionMatrix(camera.combined);
            game.batch.begin();
            game.batch.draw(backgroundTexture,0,0,camera.viewportWidth,camera.viewportHeight);
            game.font.draw(game.batch, "Drops Collected: " + dropsGathered, 0, 480);
            game.batch.draw(bucketTexture, bucket.x, bucket.y, bucket.width, bucket.height);

            for (Rectangle rainDrop : rainDrops) {
                game.batch.draw(dropTexture, rainDrop.x, rainDrop.y);
            }

            game.batch.end();

            if (Gdx.input.isTouched()) {

                Vector3 touchPos = new Vector3();
                touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(touchPos);
                bucket.x = touchPos.x - 64 / 2;

            }

            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {

                bucket.x -= 800 * Gdx.graphics.getDeltaTime();

            }

            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {

                bucket.x += 800 * Gdx.graphics.getDeltaTime();
            }

            if (bucket.x < 0) {

                bucket.x = 0;

            }

            if(bucket.x > 800 - 64) {

                bucket.x = 800 - 64;

            }

            if(TimeUtils.nanoTime() - lastDropTime > (1000000000 - dropsGathered * 10000000) ) {

                spawnRaindrop();

            }

            Iterator<Rectangle> iter = rainDrops.iterator();

            while (iter.hasNext()) {

                Rectangle rainDrop = iter.next();
                rainDrop.y -= 200 * Gdx.graphics.getDeltaTime();

                if(rainDrop.y + 64 < 0) {

                    iter.remove();
                    gameOver();

                }

                if(rainDrop.overlaps(bucket)) {

                    dropsGathered++;
                    dropSound.play();
                    iter.remove();

                }

            }

        }

    }

    private void loadGame() {

        gameOver = false;
        dropsGathered = 0;

        bucket = new Rectangle();

        bucket.x = 800 / 2 - 64 / 2;
        bucket.y = 20;
        bucket.width = 64;
        bucket.height = 64;

        rainDrops = new Array<Rectangle>();
        spawnRaindrop();


        bucket.x = 800 / 2 - 64 / 2;
        bucket.y = 20;

        rainMusic.play();
        rainMusic.setLooping(true);

        rainDrops = new Array<Rectangle>();

        spawnRaindrop();

    }

    private void gameOver() {

        rainMusic.stop();
        gameOverSound.play();
        gameOver = true;

        if (gameOverButton == null) {

            gameOverButton = new Rectangle();
            gameOverButton.y = 20;

        }

        if (gameOverImage == null) {

            gameOverImage = new Texture(Gdx.files.internal("restart.png"));
            gameOverButton.width =  100;
            gameOverButton.height = 100;

            gameOverButton.x = 0;

        }

        gameOverTextL.setText(game.font, "Score: " + dropsGathered);

    }


    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {

        rainMusic.play();

    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose () {
        dropTexture.dispose();
        bucketTexture.dispose();
        dropSound.dispose();
        rainMusic.dispose();

        gameOverImage.dispose();
        backgroundTexture.dispose();
        gameOverBackground.dispose();
        gameOverSound.dispose();
    }

}