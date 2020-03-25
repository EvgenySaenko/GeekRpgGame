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

    protected TextureRegion[][] textures;

    protected float timePerFrame;
    protected float walkTime;

    private Type type;
    private String title;
    private Vector2 position;
    private boolean active;
    private int index;

    public enum Type {
        POTION(0), GOLD(1);
        int index;
        Type(int index){
            this.index = index;
        }
    }



    @Override
    public boolean isActive() {
        return active;
    }

    public Vector2 getPosition() {
        return position;
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
        this.textures = new TextureRegion(Assets.getInstance().getAtlas().findRegion("powerUPS")).split(64,64);
    }

    public void setup(Type type) {
        this.type = type;
        if (type == Type.POTION) {
            this.title = "Gold";
            this.index = 0;
        } else {
            this.title = "Live";
            this.index = 1;
        }
        this.title = title;
        this.active = true;
    }
    @Override
    public void consume(GameCharacter gameCharacter) {
        switch (type){
            case POTION:
                if (gameCharacter.hp != gameCharacter.hpMax) {//сделал пока что если здоровье фул лут не поднимается - валяется на земле
                    gameCharacter.addHp(0.4f);
                    gameCharacter.setLoot(this);
                    active = false;
                    break;
                }else {
                    break;
                }

            case GOLD:
                gameCharacter.addCoins(MathUtils.random(3,10));
                gameCharacter.setLoot(this);
                active = false;
                break;
        }
    }
    @Override
    public void render(SpriteBatch batch, BitmapFont font) {
            batch.draw(textures[index][getCurrentFrameIndex(type)], position.x - 32, position.y - 32, 32, 32, 64, 64, 0.6f, 0.6f, 0);
    }

    //метод возвращает индекс текстуры
    public int getCurrentFrameIndex(Type type){
        if (type == Type.POTION) {
            return (int) (walkTime / timePerFrame) % textures[index].length;
        }else {
            return (int) (walkTime / timePerFrame) % textures[index].length;
        }
    }

    public void update(float dt){
        walkTime += dt;
    }

}