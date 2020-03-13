package game.rpg.logic;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import game.rpg.logic.utils.ObjectPool;
import game.rpg.screens.utils.Assets;

public class MonstersController extends ObjectPool<GameCharacter> {
    GameController gc;
    private TextureRegion monstersTextureRegion;


    @Override
    protected GameCharacter newObject() {
        return new Monster(gc);
    }

    public MonstersController (){
        this.monstersTextureRegion = Assets.getInstance().getAtlas().findRegion("knight");
    }
    //создать и настроить поведение монстра
    public void setup(float x, float y) {
        getActiveElement().setup(monstersTextureRegion, 800, 300);
    }
    //проходим по списку активных элементов и рисуем
    public void render(SpriteBatch batch) {
        for (int i = 0; i < getActiveList().size(); i++) {
            getActiveList().get(i).render(batch, null);
        }
    }
    //обновляем только активных монстров
    public void update(float dt) {
        for (int i = 0; i < getActiveList().size(); i++) {
            getActiveList().get(i).update(dt);
        }
        checkPool();
    }
}
