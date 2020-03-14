package game.rpg.logic;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import game.rpg.logic.utils.MapElement;
import game.rpg.logic.utils.Poolable;
import game.rpg.screens.utils.Assets;

import java.util.HashMap;
import java.util.Map;

public class Weapon implements MapElement,Poolable {
    private GameController gc;
    private GameCharacter owner;
    private TextureRegion sword;
    private TextureRegion bow;
    private Vector2 position;
    private boolean active;

    private float attackRadius;
    private int damage;
    private float speedAttack;
    Map<Integer, TextureRegion> listweapon;
    private  TextureRegion slot;


    public GameCharacter getOwner() {
        return owner;
    }

    @Override
    public int getCellX() {
        return (int) position.x / 80;
    }

    @Override
    public int getCellY() {
        return (int) position.y / 80;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Weapon(GameController gc) {
        this.listweapon = new HashMap<>();
        this.sword = Assets.getInstance().getAtlas().findRegion("sword32");
        this.bow = Assets.getInstance().getAtlas().findRegion("bow32");
        listweapon.put(1,sword);
        listweapon.put(2,bow);
        this.attackRadius = attackRadius;
        this.damage = damage;
        this.speedAttack = speedAttack;
        this.position = new Vector2(0.0f, 0.0f);
        this.active = false;
    }


    public void create(float x, float y, int random) {
        this.slot = listweapon.get(random);//достаем оружие по ключу
        this.position.set(x,y);
        this.active = true;
    }

    public void deactivate() {
        active = false;
    }

    @Override
    public void render(SpriteBatch batch, BitmapFont font){
        batch.draw(slot,position.x - 16,position.y -16);//рисуем
    }

    public void update(float dt) {
        if (!active){//если не активна то деактивируем
            deactivate();
        }
    }

    @Override
    public boolean isActive() {
        return active;
    }
}
