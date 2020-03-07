package game.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class LoadingScreen extends AbstractScreen {
    private Texture texture;

    public LoadingScreen(SpriteBatch batch) {//формируем длинную зеленую полоску
        super(batch);
        Pixmap pixmap = new Pixmap(1280, 20, Pixmap.Format.RGB888);
        pixmap.setColor(Color.GREEN);
        pixmap.fill();
        this.texture = new Texture(pixmap);
        pixmap.dispose();
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (Assets.getInstance().getAssetManager().update()) {//если у нас загрузка завершена
            Assets.getInstance().makeLinks();//мы перейдем на след экран
            ScreenManager.getInstance().goToTarget();//переходим на целевой экран
        }
        batch.begin();//рисует текстуру зеленой полоски => маштабируемой по X,при загрузке возвращает число от 0 до 1(прогрес загрузки)
        batch.draw(texture, 0, 0, 1280 * Assets.getInstance().getAssetManager().getProgress(), 20);
        batch.end();
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
