package game.rpg.logic;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import game.rpg.screens.utils.Assets;
import game.rpg.logic.utils.ObjectPool;


public class SpecialEffectsController extends ObjectPool<SpecialEffect> {
    private TextureRegion[][] texturesSwordSwing;//берет двумерный массив текстур

    @Override
    protected SpecialEffect newObject() {
        return new SpecialEffect();
    }

    public SpecialEffectsController() {//при создании себя подгружает
        this.texturesSwordSwing = new TextureRegion(Assets.getInstance().getAtlas().findRegion("swinganim502")).split(50, 50);
    }

    public void setupSwordSwing(float x, float y, float angle) {//в создаваемые объекты прокидывает ссылку на себя
        getActiveElement().setup(texturesSwordSwing[MathUtils.random(0, 5)], x, y, angle);
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < getActiveList().size(); i++) {
            getActiveList().get(i).render(batch, null);
        }
    }

    public void update(float dt) {
        for (int i = 0; i < getActiveList().size(); i++) {
            getActiveList().get(i).update(dt);
        }
        checkPool();
    }
}
