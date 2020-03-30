package game.rpg.logic;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import game.rpg.logic.utils.Consumable;
import game.rpg.logic.utils.MapElement;
import game.rpg.logic.utils.Poolable;
import game.rpg.screens.utils.Assets;


public class Weapon implements MapElement, Poolable, Consumable {
    public enum WeaponClass {
        SWORD, SPEAR, AXE, MACE, BOW, CROSSBOW;

        public static WeaponClass fromString(String in) {//из файла преобразует в enum WeaponClass
            switch (in) {
                case "SWORD":
                    return SWORD;
                case "SPEAR":
                    return SPEAR;
                case "AXE":
                    return AXE;
                case "MACE":
                    return MACE;
                case "BOW":
                    return BOW;
                case "CROSSBOW":
                    return CROSSBOW;
                default:
                    throw new RuntimeException("Unknown weapon class");
            }
        }
    }

    public enum Type {
        MELEE, RANGED;

        public static Type fromString(String in) {//то же самое
            switch (in) {
                case "MELEE":
                    return MELEE;
                case "RANGED":
                    return RANGED;
                default:
                    throw new RuntimeException("Unknown weapon type");
            }
        }
    }

    private GameController gc;
    private TextureRegion texture;
    private WeaponClass weaponClass;
    private Type type;
    private String title;
    private Vector2 position;
    private int minDamage;
    private int maxDamage;
    private float speed;
    private float range;
    private boolean active;

    public void setMinDamage(int minDamage) {
        this.minDamage = minDamage;
    }

    public void setMaxDamage(int maxDamage) {
        this.maxDamage = maxDamage;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setRange(float range) {
        this.range = range;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void activate() {
        this.active = true;
    }

    public Vector2 getPosition() {
        return position;
    }

    public String getDps() {
        return String.valueOf((int) ((maxDamage + minDamage) / 2.0f / speed));
    }

    @Override
    public void consume(GameCharacter gameCharacter) {
        gameCharacter.getWeapon().copyFrom(this);//запрашиваем у этого персонажа оружие и меняем стат на новое(поднятое)
        active = false;//деактивируем что подобрали
    }

    @Override
    public int getCellX() {
        return (int) (position.x / Map.CELL_WIDTH);
    }

    @Override
    public int getCellY() {
        return (int) (position.y / Map.CELL_HEIGHT);
    }

    @Override
    public float getY() {
        return position.y;
    }

    public Type getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public int getMinDamage() {
        return minDamage;
    }

    public int getMaxDamage() {
        return maxDamage;
    }

    public int generateDamage() {
        return  MathUtils.random(minDamage, maxDamage);
    }

    public float getSpeed() {
        return speed;
    }

    public float getRange() {
        return range;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    public Weapon(GameController gc) {
        this.gc = gc;
        this.position = new Vector2(0, 0);
    }

    // CLASS ,TYPE  ,TITLE ,MIN_DAMAGE, MAX_DAMAGE, SPEED, RANGE
    public Weapon(String line) {//сплитает файл генерации оружия(в папке data)
        String[] tokens = line.split(",");
        this.weaponClass = WeaponClass.fromString(tokens[0].trim());
        this.type = Type.fromString(tokens[1].trim());
        this.title = tokens[2].trim();
        this.minDamage = Integer.parseInt(tokens[3].trim());
        this.maxDamage = Integer.parseInt(tokens[4].trim());
        this.speed = Float.parseFloat(tokens[5].trim());
        this.range = Float.parseFloat(tokens[6].trim());
        if (this.type == Type.MELEE) {
            texture = Assets.getInstance().getAtlas().findRegion("sword64");
        } else {
            texture = Assets.getInstance().getAtlas().findRegion("bow64");
        }
    }
    //сюда отдается готовое оружие и он переписывает данные в себя
    public void copyFrom(Weapon from) {//когда мы поднимаем оружие - свойства бывшего заменяются на поднятого
        this.type = from.type;
        this.weaponClass = from.weaponClass;
        this.title = from.title;
        this.range = from.range;
        this.maxDamage = from.maxDamage;
        this.minDamage = from.minDamage;
        this.speed = from.speed;
        this.texture = from.texture;
    }

    @Override
    public void render(SpriteBatch batch, BitmapFont font) {
        batch.draw(texture, position.x - 32, position.y - 32, 32, 32, 64, 64, 0.5f, 0.5f, 0.0f);
    }

    public void renderGUI(SpriteBatch batch, BitmapFont font){
        font.draw(batch,title + " [" + minDamage + "-"+ maxDamage + "]\nSpeed: " + speed + "\nDPS: " + getDps(), position.x,position.y);
    }
}
