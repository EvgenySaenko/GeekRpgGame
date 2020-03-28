package game.rpg.logic;

import com.badlogic.gdx.math.MathUtils;
import game.rpg.logic.utils.ObjectPool;

public class LootsController extends ObjectPool<Loot> {
    private GameController gc;

    @Override
    protected Loot newObject() {
        return new Loot(gc);
    }

    public LootsController(GameController gc) {
        this.gc = gc;
    }

    public void setup(float x, float y) {
        Loot loot = getActiveElement();//создается иконка оружия
        Loot.Type type = Loot.Type.GOLD;
        if (MathUtils.random(100) < 50) {
            type = Loot.Type.POTION;
        }
        loot.setup(type);
        loot.setPosition(x, y);
    }

    public void update(float dt) {
        for (int i = 0; i < getActiveList().size(); i++) {
            getActiveList().get(i).update(dt);
        }
        checkPool();
    }
}

