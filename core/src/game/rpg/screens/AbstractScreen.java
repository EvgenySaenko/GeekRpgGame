package game.rpg.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//создаем базовый экран
public abstract class AbstractScreen implements Screen {
    protected SpriteBatch batch;

    public AbstractScreen(SpriteBatch batch) {
        this.batch = batch;
    }

    @Override
    public void resize(int width, int height) {
        ScreenManager.getInstance().resize(width, height);//вызов метода resize(ScreenManager) пересчет размеров экрана
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
}
