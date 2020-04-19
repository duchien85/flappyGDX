package com.zereb.flappy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;


public class Bird {
    public float x = 0, y = 0;
    public float rotation = 0f;
    public float velY = 0, velX = 0f;
    public Rectangle rectangle;
    public float G = 9.807f * 2f;
    public int width, height;
    public State state;

    private static final float COOLDOWN = 0.4f;
    private float cooldown = COOLDOWN;
    private Array<TextureRegion> textures = new Array<>();
    private int frame = 0;
    private float lastFrameTime = 0;
    private float graceTime = 0;

    public Sound hit;
    public Sound wing;
    public Sound point;


    public enum State {
        DEAD, GRACE, PLAYING
    }


    public Bird() {
        textures.add(new TextureRegion(Flappy.resourseManager.loadSkinTexture("bird_1.png")));
        textures.add(new TextureRegion(Flappy.resourseManager.loadSkinTexture("bird_2.png")));
        textures.add(new TextureRegion(Flappy.resourseManager.loadSkinTexture("bird_3.png")));
        hit = Flappy.resourseManager.loadSound("hit.ogg");
        wing = Flappy.resourseManager.loadSound("wing.ogg");
        point = Flappy.resourseManager.loadSound("point.ogg");

        width = textures.get(frame).getRegionWidth();
        height = textures.get(frame).getRegionHeight();
        rectangle = new Rectangle(x, y, width, height);
        x = 20;
        y = Flappy.HEIGHT / 2f;
        state = State.GRACE;
    }


    public void update(float delta) {
        cooldown -= delta;
        lastFrameTime -= delta;
        if (lastFrameTime < 0 && state != State.DEAD) {
            lastFrameTime = 1f / 12f;
            frame = (frame < 2) ? frame + 1 : 0;
        }

        if (state == State.PLAYING){
            velX = 80f;
            G = 9.807f * 2f;
        }

        if (state == State.GRACE) {
            graceTime -= delta;
            G = 2f;
            if (cooldown < 0 && velY < 0) {
                cooldown = COOLDOWN;
                velY= 0;
                velY += 0.5f;
                velX = 80f;
            }
        }

        if (state != State.DEAD && y < 20){
            state = State.DEAD;
            velX = 0f;
            hit.play();
        }

        velY -= delta * G;
        rectangle.setPosition(x, y);

        rotation = rotation * -delta * 3;
        for (int i = 0; i < 9; i++)
            if (velY < -i * 1.5f) rotation = (i * -10f);
        y = MathUtils.clamp(y + velY, 0, Flappy.HEIGHT);

    }

    public void input() {
        if (state == State.DEAD){
            return;
        }

        if (state == State.GRACE && Gdx.input.justTouched() && graceTime < 0){
            G = 9.807f * 2f;
            state = State.PLAYING;
            return;
        }

        if (state == State.PLAYING && Gdx.input.justTouched() && cooldown < 0) {
            velY = 0;
            velY += 8f;
            cooldown = COOLDOWN;
            rotation = 25f;
            wing.play();
        }
    }

    public void draw(SpriteBatch batch) {
        batch.draw(textures.get(frame), x, y, width / 2f, height / 2f, width, height, 1f, 1f, rotation);
    }


    public void reset(){
        x = 20;
        y = Flappy.HEIGHT / 2f;
        state = State.GRACE;
        graceTime = 1f;
    }

}
