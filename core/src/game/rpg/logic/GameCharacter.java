package game.rpg.logic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
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

    static final int WIDTH = 60;
    static final int HEIGHT = 60;
    protected Weapon weapon;
    protected Loot loot;

    protected GameController gc;

    protected TextureRegion[][] textures;
    protected TextureRegion textureHp;
    protected Texture textureHitPoint;

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
    protected float attackTime;
    protected float walkTime;
    protected float timePerFrame;

    protected float visionRadius;
    //protected float attackRadius;
    //protected int damage;

    //protected float speedAttack;
    protected float speed;
    protected int hp, hpMax, whatDamage;
    protected boolean alive;


    protected Color color;

    public int getCellX() {
        return (int) position.x / 80;
    }

    public int getCellY() {
        return (int) (position.y - 20) / 80;
    }

    public void changePosition(float x, float y) {
        position.set(x, y);
        if (position.x < 0.1f){
            position.x = 0.1f;
        }
        if (position.y - 20 < 0.1f){
            position.y = 20.1f;
        }
        if (position.x > Map.MAP_CELLS_WIDTH * 80 - 1){
            position.x = Map.MAP_CELLS_WIDTH * 80 - 1;
        }
        if (position.y - 20 > Map.MAP_CELLS_HEIGHT* 80 - 1){
            position.y = Map.MAP_CELLS_HEIGHT * 80 - 1 + 20;
        }
        area.setPosition(position.x, position.y - 20);
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

    public boolean isAlive() {
        return hp > 0;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public void setLoot(Loot loot) {
        this.loot = loot;
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
        this.state = State.IDLE;
        this.stateTimer = 1.0f;
        this.timePerFrame = 0.2f;
        this.target = null;
        this.alive = true;
        this.whatDamage = 0;
        this.color = new Color(0.0f,1.0f,0.0f,50);

        Pixmap pixmap = new Pixmap(60, 20, Pixmap.Format.RGB888);
        pixmap.setColor(color);
        pixmap.fill();
        this.textureHitPoint = new Texture(pixmap);
    }

    public void changeColor(){//изменение цвета полоски здоровья
        float red = (float) (this.hpMax - this.hp) / hpMax;
        float green = (float) hp /this.hpMax;
        float blue = 0;
        color.set(red,green,blue,50);
        System.out.println( hp  +" " +hpMax + "red ="+ hp/hpMax);
        System.out.println("color change"+"\n"+ "red "+ red+ "\n "+"gren " + green+"\n "+ "blue "+" "+ blue);
    }


    public void renderDamage(SpriteBatch batch, BitmapFont font){//заготовка к отлетающему хп
        if (hp < hpMax) {
            strBuilder.setLength(0);
            strBuilder.append(hp).append("\n");
            font.draw(batch, strBuilder, position.x - 16, position.y + 60);
        }
    }
    //метод возвращает индекс текстуры
    public int getCurrentFrameIndex(){
        return(int) (walkTime / timePerFrame) % textures[0].length;
    }

    //общее поведение персонажей
    public void update(float dt) {
        lifetime += dt;
        if (state == State.ATTACK) {//если мы в состоянии атаки
            dst.set(target.getPosition());//дст равна координатам нашей мишени
        }
        //если состояние двигаться или убегать или атаковать или от нас до цели расстояние на 5 пх меньше нашего радиуса атаки
        if (state == State.MOVE || state == State.RETREAT || (state == State.ATTACK && this.position.dst(target.getPosition()) > weapon.getAttackRadius() - 5)) {
            moveToDst(dt);//то двигаемся к дст
        }
        //если состояние атаки и позиция до цели меньше радиуса атаки
        if (state == State.ATTACK && this.position.dst(target.getPosition()) < weapon.getAttackRadius()) {
            walkTime += dt;//если aтакуем персонаж шевелит ногами
            attackTime += dt;//то атактайм накапливается
            if (attackTime > weapon.getSpeedAttack()) {//раз в 0... секунды мы атакуем
                attackTime = 0.0f;
                if (weapon.getType() == Weapon.Type.MELEE ) {//если мы находимся в мили
                    target.takeDamage(this, this.whatDamage = weapon.generateDamage());//и цель получает урон
                }
                if (weapon.getType() == Weapon.Type.RANGED && target != null) {//если в рэндж то кидает снаряд
                    gc.getProjectilesController().setup(this, position.x, position.y, target.getPosition().x, target.getPosition().y,this.whatDamage = weapon.generateDamage());
                }
            }
        }
    }

    public void moveToDst(float dt) {
        tmp.set(dst).sub(position).nor().scl(speed);
        tmp2.set(position);
        walkTime += dt;//если перемещаем персонаж шевелит ногами
        if (position.dst(dst) > speed * dt) {
            changePosition(position.x + tmp.x * dt,position.y + tmp.y * dt);
        } else {
            changePosition(dst);//если добрались до точки то состояние меняем в IDLE и стоим на месте
            state = State.IDLE;
        }
        if (!gc.getMap().isGroundPassable(getCellX(), getCellY())) {
            changePosition(tmp2.x + tmp.x * dt,tmp2.y);
            if (!gc.getMap().isGroundPassable(getCellX(), getCellY())) {
                changePosition(tmp2.x, tmp2.y + tmp.y * dt);
                if (!gc.getMap().isGroundPassable(getCellX(), getCellY())) {
                    changePosition(tmp2);
                }
            }
        }
    }

    public boolean takeDamage(GameCharacter attacker, int damage) {
        lastAttacker = attacker;//запоминаем последнего атакующего
        hp -= this.weapon.generateDamage();
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
        for (int i = 0; i < gc.getAllCharacters().size(); i++) {//проходимя повсем персонажам
            GameCharacter gameCharacter = gc.getAllCharacters().get(i);
            if (gameCharacter.target == this) {//если кто то охотился но его убили
                gameCharacter.resetAttackState();//другие теряют его мишень
            }
        }
    }
}
