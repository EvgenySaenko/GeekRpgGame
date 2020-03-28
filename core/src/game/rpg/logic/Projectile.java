package game.rpg.logic;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import game.rpg.logic.utils.MapElement;
import game.rpg.logic.utils.Poolable;

public class Projectile implements Poolable, MapElement {
    private  GameController gc;
    private TextureRegion textureRegion;
    private GameCharacter owner;
    private Vector2 position;
    private Vector2 velocity;
    private boolean active;
    private int damage;

    public int getDamage() {
        return damage;
    }

    public GameCharacter getOwner() {
        return owner;
    }

    @Override
    public int getCellX() {
        return (int) position.x / Map.CELL_WIDTH;
    }

    @Override
    public int getCellY() {
        return (int) position.y / Map.CELL_HEIGHT;
    }

    @Override
    public float getY() {
        return position.y;
    }

    public Vector2 getPosition() {
        return position;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public Projectile(GameController gc) {
        this.gc = gc;
        this.textureRegion = null;
        this.position = new Vector2(0, 0);
        this.velocity = new Vector2(0, 0);
        this.active = false;
    }

    public void setup(GameCharacter owner, TextureRegion textureRegion, float x, float y, float targetX, float targetY,int damage) {
        this.owner = owner;
        this.textureRegion = textureRegion;
        this.position.set(x, y);
        this.velocity.set(targetX, targetY).sub(x, y).nor().scl(800.0f);
        this.damage = damage;
        this.active = true;

    }

    public void deactivate() {
        active = false;
    }

    @Override
    public void render(SpriteBatch batch, BitmapFont font) {
        batch.draw(textureRegion, position.x - 30, position.y - 30, 30, 30, 60, 60, 1, 1, velocity.angle());
    }

    public void update(float dt) {
        position.mulAdd(velocity, dt);
        if (position.x < 0 || position.x > gc.getMap().getWidthLimit() || position.y < 0 || position.y > gc.getMap().getHeightLimit()) {
            deactivate();
        }
    }
}
