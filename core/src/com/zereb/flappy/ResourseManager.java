package com.zereb.flappy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;
import java.util.Set;

public class ResourseManager {


    private HashMap<String, Texture> textures = new HashMap<>();
    private HashMap<String, Sound> sounds = new HashMap<>();
    private String skin;

    public ResourseManager(String skin){
        this.skin = skin;
        textures.put("fail", new Texture(Gdx.files.internal("_fail_.png")));
    }


    public Texture loadSkinTexture(String path) {
        path = skin+ "/" + path;
        return loadTexture(path);
    }

    public Texture loadTexture(String path) {
        if (textures.containsKey(path))
            return textures.get(path);

        FileHandle fileHandle = Gdx.files.internal(path);
        if (!fileHandle.exists()) return textures.get("fail");

        Texture texture = new Texture(fileHandle);
        textures.put(path, texture);
        Gdx.app.debug(getClass().getName(), "loaded texture: " + path);
        return texture;
    }

    public Sound loadSound(String path){
        path = "sfx/" + path;
        if (sounds.containsKey(path))
            return sounds.get(path);

        FileHandle fileHandle = Gdx.files.internal(path);
        if (!fileHandle.exists()) return sounds.get("fail");

        Sound sound = Gdx.audio.newSound(fileHandle);
        sounds.put(path, sound);
        Gdx.app.debug(getClass().getName(), "loaded sound: " + path);
        return sound;
    }

    public void dispose(){
        for (Texture value : textures.values())
            value.dispose();
        for (Sound value : sounds.values())
            value.dispose();
        sounds.clear();
        textures.clear();
    }

    public void setSkin(String skin) {
        System.out.println("Set skin: "+  skin);
        this.skin = skin;
        Set<String> paths = textures.keySet();
        dispose();
        for (String path : paths) {
            loadSkinTexture(path);
        }
    }

    private String skin(){
        return skin;
    }
}
