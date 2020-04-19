package com.zereb.flappy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import static com.zereb.flappy.Flappy.resourseManager;


public class GameScreen implements Screen {

    private final Flappy game;
    private OrthographicCamera camera;
    private Bird bird;
    private Score score;
    private Texture bg;
    private Texture base;
    private Texture gameOver;
    private Texture menu;
    private float TUBE_DIST_X = 200;
    private float TUBE_DIST_Y = 200;

    private float[] basesX = new float[3];
    private Array<Rectangle> tubes = new Array<>();
    private Texture tubeTexture;
    private Rectangle bufferRect;
    private Rectangle menuRect;
    private Vector3 touchPoint;


    private GlyphLayout layout;

    public GameScreen(Flappy game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Flappy.WIDTH, Flappy.HEIGHT);

        bird = new Bird();
        score = new Score();
        bg = resourseManager.loadSkinTexture("background.png");
        base = resourseManager.loadSkinTexture("base.png");
        tubeTexture = resourseManager.loadSkinTexture("tube.png");
        gameOver = resourseManager.loadSkinTexture("gameover.png");
        menu = resourseManager.loadTexture("menu.png");
        menuRect = new Rectangle(10, Flappy.HEIGHT - 10 - menu.getHeight() , menu.getWidth(), menu.getHeight());
        touchPoint = new Vector3();
        layout = new GlyphLayout();

        System.out.println("tube dist y: " + TUBE_DIST_Y);
        for (int i = 0; i < basesX.length; i++)
            basesX[i] = i * base.getWidth();

        Rectangle rectangle = new Rectangle(10, Flappy.HEIGHT + 10 , tubeTexture.getWidth(), tubeTexture.getHeight());

        bufferRect = new Rectangle();
        tubes.add(new Rectangle(rectangle));
        tubes.add(new Rectangle(rectangle));
        tubes.add(new Rectangle(rectangle));
        tubes.add(new Rectangle(rectangle));

        for (int i = 0; i < tubes.size; i++) {
            tubes.get(i).x = 500 + TUBE_DIST_X * i;
            tubes.get(i).y = -MathUtils.random(100, 250);
        }


    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        if (bird.state == Bird.State.DEAD) {
            if (score.getScore() > game.maxScore) game.save(score.getScore());
            if (Gdx.input.justTouched()) reset();
        }

        if (Gdx.input.isTouched()) {
            camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            if (menuRect.contains(touchPoint.x, touchPoint.y)){
                game.setScreen(new MainMenuScreen(game));
            }
        }

        bird.input();
        bird.update(delta);


        float mostRight = 0f;

        //tubes
        for (Rectangle tube : tubes)
            mostRight = Math.max(tube.x, mostRight);

        for (Rectangle tube : tubes) {
            if (tube.x <= -tubeTexture.getWidth()) {
                tube.x = mostRight + TUBE_DIST_X;
                score.setScore(score.getScore() + 1);
                bird.point.play();
            }
            if (bird.state == Bird.State.PLAYING)
                tube.x -= bird.velX * delta;
            bufferRect.set(tube.x, tube.y + tube.height + TUBE_DIST_Y, tube.width, tube.height);

            if ((tube.overlaps(bird.rectangle) || bufferRect.overlaps(bird.rectangle)) && bird.state != Bird.State.DEAD) {
                bird.state = Bird.State.DEAD;
                bird.hit.play();
            }
        }
        //end tubes

        //ground movement
        mostRight = 0f;
        for (float x : basesX)
            mostRight = Math.max(x, mostRight);

        for (int i = 0; i < basesX.length; i++) {
            if (basesX[i] <= -base.getWidth())
                basesX[i] = mostRight + base.getWidth();
            if (bird.state != Bird.State.DEAD)
                basesX[i] -= bird.velX * delta;
        }
        //ground movement end

        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(bg, 0, 0, Flappy.WIDTH, Flappy.HEIGHT);

        for (int i = 0; i < tubes.size; i++) {
            game.batch.draw(tubeTexture, tubes.get(i).x, tubes.get(i).y);
            game.batch.draw(tubeTexture, tubes.get(i).x, tubes.get(i).y + tubeTexture.getHeight() + TUBE_DIST_Y,  //this monstrosity just to flip texture on y
                    tubeTexture.getWidth(), tubeTexture.getHeight(), 0, 0,
                    tubeTexture.getWidth(), tubeTexture.getHeight(), false, true
            );
        }

        for (float x : basesX)
            game.batch.draw(base, x, -70f);

        if (bird.state == Bird.State.DEAD) {
            layout.setText(game.font, "Max score: " + game.maxScore);
            game.batch.draw(gameOver, Flappy.WIDTH / 2f - gameOver.getWidth() / 2f, Flappy.HEIGHT / 2f - gameOver.getHeight() / 2f);
            game.font.draw(game.batch, layout, Flappy.WIDTH / 2f - layout.width / 2, Flappy.HEIGHT / 2f - layout.height / 2f - gameOver.getHeight());
        }

        score.draw(game.batch, Flappy.WIDTH / 2f - score.width / 2f, Flappy.HEIGHT - 100);
        game.batch.draw(menu, menuRect.x, menuRect.y);

        bird.draw(game.batch);
        game.batch.end();
    }


    private void reset() {
        score.setScore(0);
        tubes.clear();
        bird.reset();
        Rectangle rectangle = new Rectangle(0, 0, tubeTexture.getWidth(), tubeTexture.getHeight());

        tubes.add(new Rectangle(rectangle));
        tubes.add(new Rectangle(rectangle));
        tubes.add(new Rectangle(rectangle));
        tubes.add(new Rectangle(rectangle));

        for (int i = 0; i < tubes.size; i++) {
            tubes.get(i).x = 500 + TUBE_DIST_X * i;
            tubes.get(i).y = -MathUtils.random(100, 250);
        }

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
