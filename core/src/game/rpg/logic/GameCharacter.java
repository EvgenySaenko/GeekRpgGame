package game.rpg.logic;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import game.rpg.logic.utils.MapElement;
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

    protected State state;
    protected float stateTimer;

    protected GameCharacter lastAttacker;
    protected GameCharacter target;

    protected Vector2 position;
    protected Vector2 dst;
    protected Vector2 tmp;
    protected Vector2 tmp2;

    protected Circle area;
    protected StringBuilder strBuilder;

    protected float lifetime;
    protected float attackTime;
    protected float walkTime;
    protected float timePerFrame;
    protected float damageTimer;//время с момента последнего удара по нам

    protected float visionRadius;
    protected float speed;
    protected int hp, hpMax, whatDamage;
    protected boolean alive;
    protected int coins;
    protected BitmapFont font14;



    public int getCellX() {
        return (int) (position.x / Map.CELL_WIDTH);
    }

    public int getCellY() {
        return (int) (position.y /Map.CELL_HEIGHT);
    }

    @Override
    public float getY() {
        return position.y;
    }


    public Weapon getWeapon() {
        return weapon;
    }

    public int getWhatDamage() {
        return whatDamage;
    }

    public void changePosition(float x, float y) {
        position.set(x, y);
        if (position.x < 0.1f){
            position.x = 0.1f;
        }
        if (position.y < 0.1f){
            position.y = 0.1f;
        }
        if (position.x > Map.MAP_CELLS_WIDTH * 80 - 1){
            position.x = Map.MAP_CELLS_WIDTH * 80 - 1;
        }
        if (position.y > Map.MAP_CELLS_HEIGHT* 80 - 1){
            position.y = Map.MAP_CELLS_HEIGHT * 80 - 1;
        }
        area.setPosition(position.x, position.y);
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
        this.coins = 0;
        this.font14 = Assets.getInstance().getAssetManager().get("fonts/font14.ttf");
    }

    public void addCoins(int amount) {//начисляем монеты
        coins += amount;
        gc.getTextController().setupAddCoins(position.x,position.y,amount);//при хиле показывает сколько здоровья восстановил
    }

    public void addHp(float percent) {//начисляем здоровье
        int amount = (int) (hpMax * percent);
        if(hp + amount >= hpMax){// добавим правильное отображение хп если банка восполняет больше чем нам надо
            amount = hpMax - hp;// то восполняет ровно на недостающее количество хп
        }
        hp += amount;
        gc.getTextController().setupHealing(position.x,position.y,amount);//при хиле показывает сколько здоровья восстановил
        if (hp > hpMax) {
            hp = hpMax;
        }
    }


    @Override
    public void render(SpriteBatch batch, BitmapFont font) {
            TextureRegion currentRegion = textures[0][getCurrentFrameIndex()];
            if (dst.x > position.x) {
                if (currentRegion.isFlipX()) {
                    currentRegion.flip(true, false);
                }
            } else {
                if (!currentRegion.isFlipX()) {
                    currentRegion.flip(true, false);
                }
            }
            batch.setColor(1.0f, 1.0f - damageTimer, 1.0f - damageTimer, 1.0f);//когда будут сильно бить зеленый и синий каналы будут обнулятся
            batch.draw(currentRegion, position.x - 32, position.y - 16, 32, 32, 64, 64, 1.2f, 1.2f, 0);
            batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);

            batch.setColor(0.2f, 0.2f, 0.2f, 1.0f);//черная обводка
            batch.draw(textureHp, position.x - 32, position.y + 48, 64, 14);//черная полоска

            float n = (float) hp / hpMax;
            float shock = damageTimer * 4.0f;
            batch.setColor(1.0f - n, n, 0.0f, 1.0f);
            batch.draw(textureHp, position.x - 30 + MathUtils.random(-shock, shock), position.y + 50 + MathUtils.random(-shock, shock), 60 * ((float) hp / hpMax), 14);
            batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
            //количество хп осталось
            font14.draw(batch, String.valueOf(hp), position.x - 30 + MathUtils.random(-shock, shock), position.y + 63 + MathUtils.random(-shock, shock), 60, 1, false);
    }

    //метод возвращает индекс текстуры
    public int getCurrentFrameIndex(){
        return(int) (walkTime / timePerFrame) % textures[0].length;
    }

    //общее поведение персонажей
    public void update(float dt) {
        lifetime += dt;
        damageTimer -= dt;
        if (damageTimer < 0.0f){
            damageTimer = 0.0f;
        }
        if (state == State.ATTACK) {//если мы в состоянии атаки
            dst.set(target.getPosition());//дст равна координатам нашей мишени
        }
        //если состояние двигаться или убегать или атаковать или от нас до цели расстояние на 5 пх меньше нашего радиуса атаки
        if (state == State.MOVE || state == State.RETREAT || (state == State.ATTACK && this.position.dst(target.getPosition()) > weapon.getRange() - 5)) {
            moveToDst(dt);//то двигаемся к дст
        }
        //если состояние атаки и позиция до цели меньше радиуса атаки
        if (state == State.ATTACK && this.position.dst(target.getPosition()) < weapon.getRange()) {
            walkTime += dt;//если aтакуем персонаж шевелит ногами
            attackTime += dt;//то атактайм накапливается
            if (attackTime > weapon.getSpeed()) {//раз в 0... секунды мы атакуем
                attackTime = 0.0f;
                if (weapon.getType() == Weapon.Type.MELEE ) {//если мы находимся в мили
                    tmp.set(target.position).sub(position);//построили вектор от нас в сторону мишени
                    gc.getSpecialEffectsController().setupSwordSwing(position.x,position.y,tmp.angle());//вконце у вектора запросили его угол
                    target.takeDamage(this,weapon.generateDamage());//и цель получает урон
                }
                if (weapon.getType() == Weapon.Type.RANGED && target != null) {//если в рэндж то кидает снаряд
                    gc.getProjectilesController().setup(this, position.x, position.y, target.getPosition().x, target.getPosition().y,weapon.generateDamage());

                }
            }
        }
        slideFromWall(dt);//если отрисуется персонаж в непроходимой зоне - постепенно метод его вытолкнет
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
        hp -= damage;
        gc.getTextController().setupDamage(position.x,position.y,damage);
        damageTimer += 0.4;
        if (damageTimer > 1.0f){
            damageTimer = 1.0f;
        }
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

    public void slideFromWall(float dt) {
        if (!gc.getMap().isGroundPassable(position)) {//если мы отрисовывыаемся в камне или в дереве- в непроходимых местах
            //то мы находим вектор направленый из центра стены в сторону персонажа и потихоньку начинаем выталкивать наружу объекта
            tmp.set(position).sub(getCellX() * Map.CELL_WIDTH + Map.CELL_WIDTH / 2, getCellY() * Map.CELL_HEIGHT + Map.CELL_HEIGHT / 2).nor().scl(60.0f);
            changePosition(position.x + tmp.x * dt, position.y + tmp.y * dt);
        }
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
