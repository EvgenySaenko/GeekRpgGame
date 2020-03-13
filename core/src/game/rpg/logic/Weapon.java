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
    private int slot;

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
        this.attackRadius = attackRadius;
        this.damage = damage;
        this.speedAttack = speedAttack;
        this.position = new Vector2(0.0f, 0.0f);
        this.active = false;
    }

    public void changeType(GameCharacter.Type type){
        switch (type) {
            //BAREHANDED, MELEE, RANGED
            case BAREHANDED:
                this.damage = MathUtils.random(6, 10);
                this.speedAttack = 1.0f;
                this.attackRadius = 30.0f;
                break;
            case MELEE:
                this.damage = MathUtils.random(15, 20);
                this.speedAttack = 0.8f;
                this.attackRadius = 30.0f;
                break;
            case  RANGED:
                this.damage = MathUtils.random(5, 8);
                this.speedAttack = 0.3f;
                this.attackRadius = 100.0f;
                break;
        }
    }

    public void create(float x, float y) {
        this.position.set(x,y);
        this.active = true;
    }

    public void deactivate() {
        active = false;
    }

    @Override
    public void render(SpriteBatch batch, BitmapFont font){
        batch.draw(sword,position.x - 16,position.y -16);
    }

    public void update(float dt) {
        if (gc.isUp()){
            deactivate();
        }
    }

    @Override
    public boolean isActive() {
        return active;
    }
}
