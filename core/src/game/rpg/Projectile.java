package game.rpg;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Projectile {
    private TextureRegion textureRegion;
    static Vector2 position;
    private Vector2 velocity;
    private boolean active;
    private Vector2 tmp;

    public Projectile(TextureAtlas atlas) {
        this.textureRegion = atlas.findRegion("arrow");
        this.position = new Vector2(0, 0);
        this.velocity = new Vector2(0, 0);
        this.active = false;
        this.tmp = new Vector2(0, 0);
    }

    public static Vector2 getPosition() {
        return position;
    }

    public boolean isActive() {
        return active;
    }

    //задаем из какой точки вылетает и под каким углом
    public void setup(float x, float y, float targetX, float targetY) {
        if (!active) {
            position.set(x, y);//если выпускаем стрелу -  позиция устанаваливается в начальную точку х и у
            velocity.set(targetX, targetY).sub(x, y).nor().scl(800.0f);//и ее скорость
            active = true;
        }
    }

    public void deactivate() {
        active = false;
    }

    public void render(SpriteBatch batch) {
        if (active) {
            batch.draw(textureRegion, position.x - 30, position.y - 30, 30, 30, 60, 60, 1, 1, velocity.angle());
        }
    }

    public void update(float dt) {
        if (active) {
            position.mulAdd(velocity, dt);
            if (position.x < 0 || position.x > 1280 || position.y > 720 || position.y < 0) {
                deactivate();
            }

        }
    }
}
