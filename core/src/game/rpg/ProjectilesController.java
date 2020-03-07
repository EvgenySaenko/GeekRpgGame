package game.rpg;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import game.rpg.Projectile;

public class ProjectilesController extends ObjectPool<Projectile> {
    private TextureRegion projectileTextureRegion;

    @Override
    protected Projectile newObject() {
        return new Projectile();
    }

    public ProjectilesController() {
        this.projectileTextureRegion = Assets.getInstance().getAtlas().findRegion("arrow");
    }

    public void setup(float x, float y, float targetX, float targetY) {
        //достали объект из свободного списка => перенесли в список активных и настраиваем
        getActiveElement().setup(projectileTextureRegion, x, y, targetX, targetY);
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < getActiveList().size(); i++) {
            getActiveList().get(i).render(batch);//отрисовали активные элементы
        }
    }

    public void update(float dt) {
        for (int i = 0; i < getActiveList().size(); i++) {
            getActiveList().get(i).update(dt);//обновляем активные элементы
        }
        checkPool();
    }
}
