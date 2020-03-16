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


public class Weapon implements MapElement, Poolable, Consumable {

    public enum Type {
        BAREHANDED, MELEE, RANGED
    }

//    public enum Type {
//        SWORD, BOW, CROSSBOW, AXE, STAFF
//    }
    private TextureRegion texture;
    private Type type;
    private String title;
    private Vector2 position;

    private int minDamage;
    private int maxDamage;
    private float speedAttack;
    private float attackRadius;
    private boolean active;

    @Override
    public boolean isActive() {
        return active;
    }

    public Vector2 getPosition() {
        return position;
    }

    @Override
    public void consume(GameCharacter gameCharacter) {
        gameCharacter.setWeapon(this);
        active = false;
    }

    @Override
    public void render(SpriteBatch batch, BitmapFont font) {
        batch.draw(texture, position.x - 32, position.y - 32);
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

    public String getTitle() {
        return title;
    }

    public int getMinDamage() {
        return minDamage;
    }

    public int getMaxDamage() {
        return maxDamage;
    }

    public int generateDamage() {
        return MathUtils.random(minDamage, maxDamage);
    }

    public float getSpeedAttack() {
        return speedAttack;
    }

    public float getAttackRadius() {
        return attackRadius;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    public Weapon() {
        this.position = new Vector2(0, 0);
    }


    public Weapon(Type type, String title, int minDamage, int maxDamage, float speedAttack, float attackRadius) {
        this.type = type;
        this.title = title;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.speedAttack = speedAttack;
        this.attackRadius = attackRadius;
    }

    public void setup(Type type, String title, int minDamage, int maxDamage, float speedAttack, float attackRadius) {
        this.type = type;
        if (type == Type.MELEE) {
            this.title = "Sword";
            texture = Assets.getInstance().getAtlas().findRegion("sword32");
        } else {
            this.title = "Bow";
            texture = Assets.getInstance().getAtlas().findRegion("bow32");
        }
        this.title = title;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.speedAttack = speedAttack;
        this.attackRadius= attackRadius;
        this.active = true;
    }

    public static Weapon createSimpleBarehandedWeapon(){
        return new Weapon(Type.BAREHANDED,"Hand", MathUtils.random(10,15),MathUtils.random(20,25),0.6f,60.0f);
    }
    public static Weapon createSimpleRangedWeapon(){
        return new Weapon(Type.RANGED,"Bow", MathUtils.random(5,10),MathUtils.random(10,15),0.4f,180.0f);
    }
    public static Weapon createSimpleMeleeWeapon(){
        return new Weapon(Type.MELEE,"Sword", MathUtils.random(15,20),MathUtils.random(25,30),0.6f,60.0f);
    }
}