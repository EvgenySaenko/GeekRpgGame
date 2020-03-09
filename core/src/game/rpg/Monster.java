package game.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Monster {
    private GameScreen gameScreen;//делаем связки между игровыми объектами через него
    private TextureRegion texture;
    private TextureRegion textureHp;
    private Vector2 position;
    private boolean active;
    private Vector2 dst;
    private Vector2 tmp;
    private float lifetime;
    private float attackTime;
    private float speed;
    private int hp;
    private int hpMax;

    public Vector2 getPosition() {
        return position;
    }

    public Monster(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.texture = Assets.getInstance().getAtlas().findRegion("knight");
        this.textureHp = Assets.getInstance().getAtlas().findRegion("hp");
        this.position =  new Vector2(MathUtils.random(gameScreen.getHero().getPosition().x + 100, 1280 - 60),
                                      MathUtils.random(gameScreen.getHero().getPosition().y + 100, 720 - 60));
        this.dst = new Vector2(position);
        this.tmp = new Vector2(0, 0);
        this.speed = 100.0f;
        this.hpMax = 30;
        this.hp = 30;
        this.active = false;
    }

    public void takeDamage(int amount) {
        if (hp > 0) {
            hp -= amount;
        }
        if (hp <= 0){
            this.gameScreen.getHero().addGold();
            deactivate();
            recreate();
            this.hp = 30;
        }
    }
    //пересоздаем монстра
    public void recreate() {
        if (!active) {
            position.x = MathUtils.random(gameScreen.getHero().getPosition().x + 100, 1280 - 60);
            position.y = MathUtils.random(gameScreen.getHero().getPosition().y + 100, 720 - 60);
            active = true;
        }
    }

    public void deactivate() {
        active = false;
    }

    public void render(SpriteBatch batch) {
        batch.setColor(0.5f, 0.5f, 0.5f, 0.7f);
        batch.draw(texture, position.x - 30, position.y - 30, 30, 30, 60, 60, 1, 1, 0);
        batch.setColor(1, 1, 1, 1);
        batch.draw(textureHp, position.x - 30, position.y + 30, 60 * ((float) hp / hpMax), 12);
    }

    public void update(float dt) {
        lifetime += dt;
        attackTime +=dt;
        if (attackTime >= 0.5f){

        }
        //указываем направление монстра -
        tmp.set(gameScreen.getHero().getPosition()).sub(position).nor().scl(speed);
        if (position.dst(gameScreen.getHero().getPosition()) > (speed * dt)*30){
            position.mulAdd(tmp, dt);
        }else {
            attackTime +=dt;
            if (attackTime >= 3.0f){
                attackTime = 0;
                gameScreen.getHero().subHP();
            }
        }

    }
}
