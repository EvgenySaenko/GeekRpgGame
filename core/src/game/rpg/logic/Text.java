package game.rpg.logic;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import game.rpg.logic.utils.MapElement;
import game.rpg.logic.utils.Poolable;
import game.rpg.screens.utils.Assets;


public class Text implements Poolable, MapElement {
    GameController gc;
    private Vector2 position;
    private Vector2 velocity;
    private float time;
    private boolean active;
    private StringBuilder strBuilder;
    private int damage,amount, coins;
    private Type type;
    private BitmapFont font20;
    private BitmapFont font24;

    public enum Type{
        DAMAGE, HEALING, COINS
    }

    public Text(GameController gc) {
        this.gc = gc;
        this.position = new Vector2(0,0);
        this.velocity = new Vector2(0,70);
        this.strBuilder = new StringBuilder();
        this.font20 = Assets.getInstance().getAssetManager().get("fonts/font20.ttf");
        this.font24 = Assets.getInstance().getAssetManager().get("fonts/font24.ttf");
    }
    //текстовое при получении урона
    public void setupDamage(float x, float y, int damage){
        this.type = Type.DAMAGE;
        time = 0.0f;
        position.set(MathUtils.random(x - 5,x + 5),y + 80);
        this.damage = damage;
        active = true;
    }
    //текстовое при отхиле
    public void setupHealing(float x, float y, int amount){
        this.type = Type.HEALING;
        time = 0.0f;
        position.set(x,y + 80);
        this.amount = amount;
        active = true;
    }
    //текстовое при отхиле
    public void setupAddCoins(float x, float y, int amount){
        this.type = Type.COINS;
        time = 0.0f;
        position.set(x,y + 80);
        this.coins = amount;
        active = true;
    }



    @Override
    public void render(SpriteBatch batch, BitmapFont font) {
        font20.getColor().set(1.0f,0.2f,0.2f,1.0f);
        font24.getColor().set(0.2f,1.0f,0.2f,1.0f);
        switch (type){
            case DAMAGE:
                strBuilder.setLength(0);
                strBuilder.append("-").append(damage);
                font20.draw(batch, strBuilder, position.x,position.y);
                break;
            case HEALING:
                strBuilder.setLength(0);
                strBuilder.append("+").append(amount);
                font24.draw(batch, strBuilder, position.x,position.y);
                break;
            case COINS:
                font24.setColor(1.0f,1.0f,0.0f,1.0f);
                strBuilder.setLength(0);
                strBuilder.append("+").append(coins).append(" coins");
                font24.draw(batch, strBuilder, position.x - 10,position.y);
                break;
        }

    }

    public void update(float dt){
        this.time += dt;
        position.mulAdd(velocity, dt);
        if (time > 0.5f){
            active = false;
            time = 0.0f;
        }
    }

    @Override
    public int getCellX() {
        return (int) (position.x / Map.CELL_WIDTH);
    }

    @Override
    public int getCellY() {
        return (int) (position.y / Map.CELL_HEIGHT);
    }

    @Override
    public float getY() {
        return position.y;
    }

    @Override
    public boolean isActive() {
        return active;
    }
}
