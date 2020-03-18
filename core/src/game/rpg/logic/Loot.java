package game.rpg.logic;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import game.rpg.logic.utils.Consumable;
import game.rpg.logic.utils.MapElement;
import game.rpg.logic.utils.Poolable;
import game.rpg.screens.utils.Assets;


public class Loot implements MapElement, Poolable, Consumable {

    public enum Type {
        GOLD, POTION
    }
    protected TextureRegion[][] lootGold;
    protected TextureRegion[][] lootPotion;

    protected float timePerFrame;
    protected float walkTime;

    private Type type;
    private String title;
    private Vector2 position;
    private boolean active;



    @Override
    public boolean isActive() {
        return active;
    }

    public Vector2 getPosition() {
        return position;
    }

    @Override
    public int getCellX() {
        return (int) (position.x / 80);
    }

    @Override
    public int getCellY() {
        return (int) (position.y / 80);
    }

    public Type getType() {
        return type;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }


    public Loot() {
        this.position = new Vector2(0, 0);
        this.timePerFrame = 0.15f;
        this.type = null;
    }

    public void setup(Type type) {
        this.type = type;
        if (type == Type.GOLD) {
            this.title = "Gold";
            this.lootGold = new TextureRegion(Assets.getInstance().getAtlas().findRegion("coin64")).split(64,64);
        } else {
            this.title = "Live";
            this.lootPotion = new TextureRegion(Assets.getInstance().getAtlas().findRegion("heart64")).split(64,64);
        }
        this.title = title;
        this.active = true;
    }
    @Override
    public void consume(GameCharacter gameCharacter) {
        gameCharacter.setLoot(this);
        active = false;
    }
    @Override
    public void render(SpriteBatch batch, BitmapFont font) {
        if (type == Type.GOLD) {
            TextureRegion texture = lootGold[0][getCurrentFrameIndex(type)];
            batch.draw(texture, position.x - 32, position.y - 32, 32, 32, 64, 64, 0.6f, 0.6f, 0);
        }
        if (type == Type.POTION) {
            TextureRegion texture = lootPotion[0][getCurrentFrameIndex(type)];
            batch.draw(texture, position.x - 32, position.y - 32, 32, 32, 64, 64, 0.8f, 0.8f, 0);
        }
    }
    //метод возвращает индекс текстуры
    public int getCurrentFrameIndex(Type type){
        if (type == Type.GOLD) {
            return (int) (walkTime / timePerFrame) % lootGold[0].length;
        }else {
            return (int) (walkTime / timePerFrame) % lootPotion[0].length;
        }
    }

    public void update(float dt){
        walkTime += dt;
    }

}