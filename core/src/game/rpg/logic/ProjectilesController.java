package game.rpg.logic;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import game.rpg.logic.utils.ObjectPool;
import game.rpg.screens.utils.Assets;

public class ProjectilesController extends ObjectPool<Projectile> {
    private TextureRegion projectileTextureRegion;

    @Override
    protected Projectile newObject() {
        return new Projectile();
    }

    public ProjectilesController() {
        this.projectileTextureRegion = Assets.getInstance().getAtlas().findRegion("arrow");
    }

    public void setup(GameCharacter owner, float x, float y, float targetX, float targetY) {
        getActiveElement().setup(owner, projectileTextureRegion, x, y, targetX, targetY);
    }

    public void update(float dt) {
        for (int i = 0; i < getActiveList().size(); i++) {
            getActiveList().get(i).update(dt);
        }
        checkPool();
    }
}