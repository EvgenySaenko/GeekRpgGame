package game.rpg.logic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import game.rpg.logic.utils.MapElement;
import game.rpg.logic.utils.Poolable;
import game.rpg.screens.utils.Assets;


public abstract class GameCharacter implements MapElement{
    protected GameController gc;

    protected TextureRegion texture;
    protected TextureRegion textureHp;

    protected Vector2 position;
    protected Vector2 dst;
    protected Vector2 tmp;
    protected Vector2 tmp2;

    protected Circle area;

    protected float lifetime;
    protected float speed;
    protected int hp, hpMax;

    public int getCellX() {
        return (int) position.x / 80;
    }

    public int getCellY() {
        return (int) (position.y - 20) / 80;
    }

    public void changePosition(float x, float y) {
        position.set(x, y);
        area.setPosition(x, y - 20);
    }

    public void changePosition(Vector2 newPosition) {
        changePosition(newPosition.x, newPosition.y);
    }

    public Vector2 getPosition() {
        return position;
    }

    public Circle getArea() {
        return area;
    }

    public GameCharacter(GameController gc, int hpMax, float speed) {
        this.gc = gc;
        this.textureHp = Assets.getInstance().getAtlas().findRegion("hp");
        this.tmp = new Vector2(0.0f, 0.0f);
        this.tmp2 = new Vector2(0.0f, 0.0f);
        this.dst = new Vector2(0.0f, 0.0f);
        this.position = new Vector2(0.0f, 0.0f);
        this.area = new Circle(0.0f, 0.0f, 15);
        this.hpMax = hpMax;
        this.hp = this.hpMax;
        this.speed = speed;
    }

    public void update(float dt) {
        lifetime += dt;
        tmp.set(dst).sub(position).nor().scl(speed);
        tmp2.set(position);
        if (position.dst(dst) > speed * dt) {
            position.mulAdd(tmp, dt);
        } else {
            position.set(dst);
        }
        if (!gc.getMap().isGroundPassable(getCellX(), getCellY())) {
            position.set(tmp2);
            position.add(tmp.x * dt, 0);
            if (!gc.getMap().isGroundPassable(getCellX(), getCellY())) {
                position.set(tmp2);
                position.add(0, tmp.y * dt);
                if (!gc.getMap().isGroundPassable(getCellX(), getCellY())) {
                    position.set(tmp2);
                }
            }
        }
        area.setPosition(position.x, position.y - 20);
    }

    public boolean takeDamage(int amount) {
        hp -= amount;
        if (hp <= 0) {
            isDeath();
            return true;
        }
        return false;
    }

    public abstract void isDeath();


    public abstract void setup(TextureRegion monstersTextureRegion, int i, int i1);
}
