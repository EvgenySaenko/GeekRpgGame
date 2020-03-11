package game.rpg.logic;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import game.rpg.logic.utils.Poolable;

public class Monster extends GameCharacter {
    private TextureRegion textureRegion;
    private float attackTime;
    private float waitingTime;
    private boolean active;


    public Monster(GameController gc) {
        super(gc,0,0);
        this.textureRegion = null;
        this.changePosition(0.0f, 0.0f);
        this.active = false;
    }

    public void setup(TextureRegion textureRegion, float x, float y) {
        this.textureRegion = textureRegion;
        this.changePosition(x, y);
        this.dst.set(position);
        this.active = true;
    }

    @Override
    public void isDeath() {
        this.position.set(MathUtils.random(0, 1280), MathUtils.random(0, 720));
        this.hp = this.hpMax;
    }



    @Override
    public void render(SpriteBatch batch, BitmapFont font) {
        batch.setColor(0.5f, 0.5f, 0.5f, 0.7f);
        batch.draw(texture, position.x - 30, position.y - 30, 30, 30, 60, 60, 1, 1, 0);
        batch.setColor(1, 1, 1, 1);
        batch.draw(textureHp, position.x - 30, position.y + 30, 60 * ((float) hp / hpMax), 12);
    }

    public void update(float dt) {
        super.update(dt);
        if (this.position.dst(gc.getHero().getPosition()) <= 300) {
            dst.set(gc.getHero().getPosition());
        }
        //если он добежал и остановился, поискал героя а он далеко => то
        if (dst.dst(position) < 2 & position.dst(gc.getHero().getPosition()) >= 300) {
            waitingTime += dt;
            if (waitingTime >= 3.5f) {//через 3.5 секунды он идет в рандомную точку
                dst.set(MathUtils.random(0, 1280), MathUtils.random(0, 720));
                waitingTime = 0.0f;
            }
        }

        if (this.position.dst(gc.getHero().getPosition()) < 40) {
            attackTime += dt;
            if (attackTime > 0.3f) {
                attackTime = 0.0f;
                gc.getHero().takeDamage(1);
            }
        }
    }

}

