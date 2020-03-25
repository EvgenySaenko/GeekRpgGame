package game.rpg.logic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import game.rpg.logic.utils.ObjectPool;
import game.rpg.screens.utils.Assets;

public class TextController extends ObjectPool<Text> {
    private GameController gc;

    @Override
    protected Text newObject() {
        return new Text(gc);
    }

    public TextController(GameController gc) {
        this.gc = gc;
    }

    //вызов метода показ урона
    public void setupDamage(float x, float y, int damage) {
        getActiveElement().setupDamage(x,y,damage);
    }
    //вызов метода показ отхила
    public void setupHealing(float x, float y, int amount){
        getActiveElement().setupHealing(x,y,amount);
    }
    //вызов метода показ подбора монет
    public void setupAddCoins(float x, float y, int amount){
        getActiveElement().setupAddCoins(x,y,amount);
    }

    public void update(float dt) {
        for (int i = 0; i < getActiveList().size(); i++) {
            getActiveList().get(i).update(dt);
        }
        checkPool();
    }
}