package com.zereb.flappy;

import com.badlogic.gdx.utils.Array;

public class Skin {
    private Array<String> skins = new Array<>();
    private int current = 0;
    private long cooldown = 1000;
    private long lastTime = System.currentTimeMillis();

    public Skin(){
        skins.add("classic");
        skins.add("beans");
        skins.add("space");
    }

    public boolean next(){
        if (System.currentTimeMillis() - lastTime  < cooldown)
            return false;
        lastTime = System.currentTimeMillis();
        current = (current < skins.size - 1) ? current + 1 : 0;
        Flappy.resourseManager.setSkin(skins.get(current));
        return true;
    }

    public boolean prev(){
        if (System.currentTimeMillis() - lastTime  < cooldown)
            return false;
        lastTime = System.currentTimeMillis();
        current = (current > 0) ? current - 1 : skins.size - 1;
        Flappy.resourseManager.setSkin(skins.get(current));
        return true;
    }

    public String current(){
        return skins.get(current);
    }

}
