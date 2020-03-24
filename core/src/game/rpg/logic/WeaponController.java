package game.rpg.logic;

import com.badlogic.gdx.math.MathUtils;
import game.rpg.logic.utils.ObjectPool;

public class WeaponController extends ObjectPool<Weapon> {
    private GameController gc;

    @Override
    protected Weapon newObject() {
        return new Weapon();
    }

    public WeaponController(GameController gc) {
        this.gc = gc;
    }

    public void setup(float x, float y) {
        Weapon w = getActiveElement();//создается иконка оружия
        int maxDamage = MathUtils.random(18,23);
        for (int i = 0; i < 10; i++) {
            if (MathUtils.random(100) < 50 - i * 5) {
                maxDamage++;
            }
        }
        Weapon.Type type = Weapon.Type.MELEE;
        String title = "Sword";
        float attackRadius = 60.0f;
        float attackSpeed = 0.4f;
        if (MathUtils.random(100) < 40) {
            title = "Bow";
            type = Weapon.Type.RANGED;
            attackRadius = 160.0f;
            attackSpeed = 0.6f;
        }
        w.setup(type, title, MathUtils.random(10, 15), maxDamage, attackSpeed, attackRadius);
        w.setPosition(x, y);
    }

    public void update(float dt) {
        checkPool();
    }
}

