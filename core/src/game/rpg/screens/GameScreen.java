package game.rpg.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import game.rpg.logic.GameController;
import game.rpg.logic.WorldRenderer;


public class GameScreen extends AbstractScreen {
    private GameController gc;
    private WorldRenderer worldRenderer;

    public GameScreen(SpriteBatch batch) {
        super(batch);
    }

    @Override
    public void show() {
        gc = new GameController();
        worldRenderer = new WorldRenderer(gc, batch);
    }

    @Override
    public void render(float delta) {
        gc.update(delta);
        worldRenderer.render();
    }
}