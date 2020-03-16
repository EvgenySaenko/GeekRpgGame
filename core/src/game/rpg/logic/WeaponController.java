package game.rpg.logic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import game.rpg.logic.utils.ObjectPool;
import game.rpg.screens.utils.Assets;

public class WeaponController extends ObjectPool<Weapon> {
    private GameController gc;

    @Override
    public Weapon newObject() {
        return new Weapon(gc);
    }

    public WeaponController(GameController gc) {
        this.gc = gc;
    }

    public void update(float dt) {
        for (int i = 0; i < getActiveList().size(); i++) {
            getActiveList().get(i).update(dt);
        }
        checkPool();
    }

}
