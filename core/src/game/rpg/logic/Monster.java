package game.rpg.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import game.rpg.logic.utils.Poolable;
import game.rpg.screens.utils.Assets;

public class Monster extends GameCharacter implements Poolable {
    private StringBuilder strBuilder;
    private Sound sound;
    @Override
    public boolean isActive() {//если здоровье больше он активный
        return hp > 0;
    }

    public Monster(GameController gc) {
        super(gc, 80, 80.0f);
        this.textures = new TextureRegion(Assets.getInstance().getAtlas().findRegion("dwarf64")).split(64,64);
        this.sound = Gdx.audio.newSound(Gdx.files.internal("audio/swordStrike.mp3"));
        this.changePosition(800.0f, 300.0f);
        this.dst.set(this.position);
        this.strBuilder = new StringBuilder();
        this.visionRadius = 160.0f;
        this.weapon = gc.getWeaponController().getOneFromAnyPrototype();//получает оружие
    }


    public void generateMe() {
        do {
            changePosition(MathUtils.random(0, 1280), MathUtils.random(0, 720));
        } while (!gc.getMap().isGroundPassable(position));
        hpMax = 80;
        hp = hpMax;
    }

    @Override
    public void onDeath() {
        super.onDeath();
        gc.getWeaponController().setup(position.x + MathUtils.random(-50,50), position.y + MathUtils.random(-50,50));
        gc.getLootsController().setup(position.x,position.y);
    }

    @Override
    public boolean takeDamage(GameCharacter attacker, int damage) {
        gc.getInfoController().setupAnyAmount(position.x,position.y, Color.WHITE,"-",damage);//отрисовка урона по монстру
        sound.play();
        return super.takeDamage(attacker, damage);
    }

    public void update(float dt) {
        super.update(dt);
        stateTimer -= dt;
        //смена состояния монстра
        if (stateTimer < 0.0f) {
            if (state == State.ATTACK) {//если монстр хотел атаковать а время его вышло
                target = null;//то мишень должна быть сброшена
            }
            state = State.values()[MathUtils.random(0, 1)];//состояния может быть либо IDLE либо MOVE,
            if (state == State.MOVE) {
                dst.set(MathUtils.random(1280), MathUtils.random(720));
            }
            stateTimer = MathUtils.random(2.0f, 5.0f);//через рандом секунд меняет состояние
        }


        //если монстр здоровый и герой в его радиусе видимости
        if (state != State.RETREAT && this.position.dst(gc.getHero().getPosition()) < visionRadius) {
            state = State.ATTACK;//то монстр переходит в состояние атаки
            target = gc.getHero();//его мишень - герой
            stateTimer = 10.0f;//и в течении 10 сек пытается его преследовать, если не удасться, то позже он перейдет в  обычное состояние
        }

        //монстр переходит в состояние побега
        if (hp < hpMax * 0.2 && state != State.RETREAT) {
            state = State.RETREAT;
            stateTimer = 1.0f;
            //монстр отбежит на 100-200 пикселей если атакующий слева от нас получим + и сместимся на +100-200 пикс(правее) если отрицат то левее
            dst.set(position.x + MathUtils.random(100, 200) * Math.signum(position.x - lastAttacker.position.x),
                    position.y + MathUtils.random(100, 200) * Math.signum(position.y - lastAttacker.position.y));//то же и по вертикали
        }
    }

    public void dispose(){
        sound.dispose();
    }
}
