package game.rpg;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Hero {
    private TextureRegion texture;
    private TextureRegion texturePointer;
    private TextureRegion textureHp;
    private Vector2 position;
    private Vector2 dst;//точка назначения
    private Vector2 tmp;
    private float lifetime;
    private float speed;
    private int hp;
    private int hpMax;
    private StringBuilder strBuilder;
    private int fireCounter;


    public Hero(TextureAtlas atlas) {
        this.texture = atlas.findRegion("archer");
        this.texturePointer = atlas.findRegion("pointer32");
        this.textureHp = atlas.findRegion("hp");
        this.position = new Vector2(100, 100);
        this.dst = new Vector2(position);
        this.tmp = new Vector2(0, 0);
        this.speed = 300.0f;
        this.hpMax = 10;
        this.hp = 10;
        this.strBuilder = new StringBuilder();
        this.fireCounter = 0;
    }


    public void fire() {
        for (int i = 0; i < GeekRpgGame.projectiles.length; i++) {
            if (!GeekRpgGame.projectiles[i].isActive()) {
                GeekRpgGame.projectiles[i].setup(position.x, position.y, Gdx.input.getX(), 720.0f - Gdx.input.getY());
                break;
            }
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(texturePointer, dst.x - 16, dst.y - 16,
                16, 16, 32, 32, 1, 1, lifetime * 120);
        batch.draw(texture, position.x - 32, position.y - 32, 32, 32, 64, 64, 1.2f, 1.2f,
                0.0f);
        batch.draw(textureHp, position.x - 30, position.y + 40, 60 * ((float) hp / hpMax), 8);
        //projectile.render(batch);
    }

    public void renderGUI(SpriteBatch batch, BitmapFont font) {
        strBuilder.setLength(0);
        strBuilder.append("Class: ").append("Knight").append("\n");
        strBuilder.append("HP: ").append(hp).append(" / ").append(hpMax).append("\n");
        font.draw(batch, strBuilder, 10, 710);
    }

    public void update(float dt) {
        lifetime += dt;
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {//если нажата левая => мы идем
            dst.set(Gdx.input.getX(), 720.0f - Gdx.input.getY());
        }
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {//если нажата правая => мы выпускаем стрелу
            fire();
        }
        tmp.set(dst).sub(position).nor().scl(speed);//вектор скорости
        if (position.dst(dst) > speed * dt) { //если нам нужно пройти расстояние большее чем мы можем пройти за один кадр то мы туда бежим
            // к вектору position прибавляем вектор умноженый на dt
            position.mulAdd(tmp, dt);
        } else {
            position.set(dst);//перемещаем в точку назначения
        }
    }
}
