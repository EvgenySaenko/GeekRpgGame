package game.rpg;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Apple {
    private TextureRegion textureRegion;
    private Vector2 position;
    private boolean active;
    private Circle circle;

    public Apple(TextureAtlas atlas) {
        this.textureRegion = atlas.findRegion("apple");
        this.position = new Vector2(MathUtils.random(0, 1280), MathUtils.random(0, 720));
        this.active = false;
        this.circle = new Circle(position.x, position.y, 32);
    }

    public Circle getCircle() {
        return circle;
    }


    public void setup() {
        if (!active) {
            position.x = MathUtils.random(0 + 32.0f, 1280 - 32.0f);
            position.y = MathUtils.random(0 + 32.0f, 720 - 32.0f);
            this.circle.set(position.x, position.y, 32);
            active = true;
        }
    }

    public Vector2 getPosition() {
        return position;
    }

    public void deactivate() {
        active = false;
    }

    public void render(SpriteBatch batch) {
        batch.draw(textureRegion, position.x - 32, position.y - 32, 64, 64);
    }

    public void update() {
    }
}
