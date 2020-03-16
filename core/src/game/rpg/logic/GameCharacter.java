package game.rpg.logic;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import game.rpg.logic.utils.MapElement;
import game.rpg.logic.utils.Poolable;
import game.rpg.screens.utils.Assets;


public abstract class GameCharacter implements MapElement {
    public enum State {
        IDLE, MOVE, ATTACK, PURSUIT, RETREAT
    }

    public enum Type {
        BAREHANDED, MELEE, RANGED
    }
    protected Weapon weapon;

    protected GameController gc;

    protected TextureRegion texture;
    protected TextureRegion textureHp;

    protected Type typeWeapon;
    protected State state;
    protected float stateTimer;

    protected GameCharacter lastAttacker;
    protected GameCharacter target;

    protected Vector2 position;
    protected Vector2 dst;
    protected Vector2 tmp;
    protected Vector2 tmp2;

    protected Circle area;
    private StringBuilder strBuilder;

    protected float lifetime;
    protected float visionRadius;
    protected float attackRadius;
    protected int damage;
    protected float attackTime;
    protected float speedAttack;
    protected float speed;
    protected int hp, hpMax,whatDamage;
    protected boolean alive;

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

    public Type getTypeWeapon() {
        return typeWeapon;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public GameCharacter(GameController gc, int hpMax, float speed) {
        this.gc = gc;
        this.textureHp = Assets.getInstance().getAtlas().findRegion("hp");
        this.strBuilder = new StringBuilder();
        this.tmp = new Vector2(0.0f, 0.0f);
        this.tmp2 = new Vector2(0.0f, 0.0f);
        this.dst = new Vector2(0.0f, 0.0f);
        this.position = new Vector2(0.0f, 0.0f);
        this.area = new Circle(0.0f, 0.0f, 15);
        this.hpMax = hpMax;
        this.hp = this.hpMax;
        this.speed = speed;//когда персонаж создается он получает скорость
        this.damage = MathUtils.random(6,10);
        this.speedAttack = 1.0f;
        this.attackRadius = 30.0f;
        this.state = State.IDLE;
        this.stateTimer = 1.0f;
        this.target = null;
        this.alive = true;
        this.whatDamage = 0;
        this.typeWeapon = Type.BAREHANDED;//кога любой перс создается (он безоружный)
    }

    public void changeTypeSpecifications(Type type){
        switch (type) {
            //BAREHANDED, MELEE, RANGED
            case BAREHANDED://статы безоружного
                this.typeWeapon = Type.BAREHANDED;
                this.damage = MathUtils.random(6, 10);
                this.speedAttack = 1.0f;
                this.attackRadius = 30.0f;
                break;
            case MELEE://статы с мечом
                this.typeWeapon = Type.MELEE;
                this.damage = MathUtils.random(15, 20);
                this.speedAttack = 0.8f;
                this.attackRadius = 30.0f;
                break;
            case RANGED://статы с луком
                this.typeWeapon = Type.RANGED;
                this.damage = MathUtils.random(3, 6);
                this.speedAttack = 0.2f;
                this.attackRadius = 150.0f;
                this.speed = 150.0f;
                break;
        }
    }

    //общее поведение персонажей
    public void update(float dt) {
        lifetime += dt;
        if (state == State.ATTACK) {//если мы в состоянии атаки
            dst.set(target.getPosition());//дст равна координатам нашей мишени
        }
        //если состояние двигаться или убегать или атаковать или от нас до цели расстояние на 5 пх меньше нашего радиуса атаки
        if (state == State.MOVE || state == State.RETREAT || (state == State.ATTACK && this.position.dst(target.getPosition()) > attackRadius - 5)) {
            moveToDst(dt);//то двигаемся к дст
        }
        //если состояние атаки и позиция до цели меньше радиуса атаки
        if (state == State.ATTACK && this.position.dst(target.getPosition()) < attackRadius) {
            attackTime += dt;//то атактайм накапливается
            if (typeWeapon == Type.MELEE||typeWeapon == Type.BAREHANDED) {//если мы находимся в мили варианте
                if (attackTime > speedAttack) {//раз в 0... секунды мы атакуем
                    attackTime = 0.0f;
                    target.takeDamage(this, this.damage);//и цель получает урон
                    this.whatDamage = this.damage;
                }
            }
            if (typeWeapon == Type.RANGED) {//если в рэндж то кидает снаряд
                if (attackTime > speedAttack) {//раз в 0.... секунды мы атакуем
                    attackTime = 0.0f;
                    gc.getProjectilesController().setup(this, position.x, position.y, target.getPosition().x, target.getPosition().y);
                }
            }
        }
        area.setPosition(position.x, position.y - 20);

    }

    public void moveToDst(float dt) {
        tmp.set(dst).sub(position).nor().scl(speed);
        tmp2.set(position);
        if (position.dst(dst) > speed * dt) {
            position.mulAdd(tmp, dt);
        } else {
            position.set(dst);//если добрались до точки то состояние меняем в IDLE и стоим на месте
            state = State.IDLE;
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
    }

    public boolean takeDamage(GameCharacter attacker, int damage) {
        lastAttacker = attacker;//запоминаем последнего атакующего
        hp -= this.damage;
        if (hp <= 0) {
            onDeath();
            return true;
        }
        return false;
    }
    //сброс состояние атаки
    public void resetAttackState() {
        dst.set(position);
        state = State.IDLE;
        target = null;
    }

    public void onDeath() {
        gc.getWeaponController().getActiveElement().create(position.x, position.y,MathUtils.random(1,2));//выпадает оружие
        for (int i = 0; i < gc.getAllCharacters().size(); i++) {//проходимя повсем персонажам
            GameCharacter gameCharacter = gc.getAllCharacters().get(i);
            if (gameCharacter.target == this) {//если кто то охотился но его убили
                gameCharacter.resetAttackState();//другие теряют его мишень
            }
        }
    }
}
