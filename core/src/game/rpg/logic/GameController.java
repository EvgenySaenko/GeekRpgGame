package game.rpg.logic;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

//содержит информацию о объектах их поведение(логика)
public class GameController {
    private ProjectilesController projectilesController;
    private MonstersController monstersController;
    private List<GameCharacter> allCharacters;
    private Map map;
    private Hero hero;
    private WeaponController weaponController;
    private LootsController lootsController;
    private Weapon weapon;
    private Vector2 tmp, tmp2;
    private boolean up;


    public List<GameCharacter> getAllCharacters() {
        return allCharacters;
    }

    public Hero getHero() {
        return hero;
    }

    public MonstersController getMonstersController() {
        return monstersController;
    }

    public Map getMap() {
        return map;
    }

    public ProjectilesController getProjectilesController() {
        return projectilesController;
    }

    public WeaponController getWeaponController() {
        return weaponController;
    }

    public LootsController getLootsController() {
        return lootsController;
    }

    public boolean isUp() {
        return up;
    }


    public GameController() {
        this.allCharacters = new ArrayList<>();
        this.projectilesController = new ProjectilesController();
        this.hero = new Hero(this);
        this.weaponController = new WeaponController(this);
        this.lootsController = new LootsController(this);
        this.map = new Map();
        this.monstersController = new MonstersController(this, 5);
        this.tmp = new Vector2(0, 0);
        this.tmp2 = new Vector2(0, 0);

    }

    public void update(float dt) {
        allCharacters.clear();//очищаем всех персонажей
        allCharacters.add(hero);//добавили героя
        allCharacters.addAll(monstersController.getActiveList());//добавили всех монстров
        hero.update(dt);
        monstersController.update(dt);
        weaponController.update(dt);
        lootsController.update(dt);
        checkCollisions();
        projectilesController.update(dt);
    }

    public void collideUnits(GameCharacter u1, GameCharacter u2) {
        if (u1.getArea().overlaps(u2.getArea())) {
            tmp.set(u1.getArea().x, u1.getArea().y);
            tmp.sub(u2.getArea().x, u2.getArea().y);
            float halfInterLen = ((u1.getArea().radius + u2.getArea().radius) - tmp.len()) / 2.0f;
            tmp.nor();

            tmp2.set(u1.getPosition()).mulAdd(tmp, halfInterLen);
            if (map.isGroundPassable(tmp2)) {
                u1.changePosition(tmp2);
            }

            tmp2.set(u2.getPosition()).mulAdd(tmp, -halfInterLen);
            if (map.isGroundPassable(tmp2)) {
                u2.changePosition(tmp2);
            }
        }
    }


        public void checkCollisions () {
            for (int i = 0; i < monstersController.getActiveList().size(); i++) {//перебераем активных монстров
                Monster m = monstersController.getActiveList().get(i);
                collideUnits(hero, m);//проверяем столкновение с героем
            }
            for (int i = 0; i < monstersController.getActiveList().size() - 1; i++) {
                Monster m = monstersController.getActiveList().get(i);
                for (int j = i + 1; j < monstersController.getActiveList().size(); j++) {
                    Monster m2 = monstersController.getActiveList().get(j);
                    collideUnits(m, m2);//проверяем столкновение монстра с монстром
                }
            }

            for (int i = 0; i < weaponController.getActiveList().size(); i++) {
                Weapon w = weaponController.getActiveList().get(i);
                if (hero.getPosition().dst(w.getPosition()) < 20) {
                    w.consume(hero);
                }
            }
            for (int i = 0; i < lootsController.getActiveList().size(); i++) {//столкновения лута и героя
                Loot loot = lootsController.getActiveList().get(i);
                if (hero.getPosition().dst(loot.getPosition()) < 20) {
                    loot.consume(hero);
                    if (loot.getType() == Loot.Type.GOLD){//если тип лута золото начисляем золото
                        hero.addCoins(MathUtils.random(3,6));
                    }
                    if (loot.getType() == Loot.Type.POTION){//если зелья, то  = hp++
                        hero.addHp(hero.hpMax/3);
                    }
                }
            }


            for (int i = 0; i < projectilesController.getActiveList().size(); i++) {
                Projectile p = projectilesController.getActiveList().get(i);//получили снаряд
                if (!map.isAirPassable(p.getCellX(), p.getCellY())) {//если не проходима земля деактивация
                    p.deactivate();
                    continue;
                }
                //если позиция от стрелы до героя меньше 24 и владельцем стрелы не является сам герой
                if (p.getPosition().dst(hero.getPosition()) < 24 && p.getOwner() != hero) {
                    p.deactivate();//стрела деактивируется
                    hero.takeDamage(p.getOwner(), p.getDamage());//то владелец стрелы получает урон
                }
                for (int j = 0; j < monstersController.getActiveList().size(); j++) {
                    Monster m = monstersController.getActiveList().get(j);
                    if (p.getOwner() == m) {//если владельцем стрелы является данный монстр
                        continue;//не делаем проверок
                    }
                    if (p.getPosition().dst(m.getPosition()) < 24) {
                        p.deactivate();
                        m.takeDamage(p.getOwner(), p.getDamage());
                    }
                }
            }
        }
    }
