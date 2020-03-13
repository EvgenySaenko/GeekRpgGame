package game.rpg.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import game.rpg.screens.utils.Assets;
import game.rpg.logic.utils.MapElement;

import java.util.ArrayList;
import java.util.List;

public class WorldRenderer {//отрисовщик
    private GameController gc;
    private SpriteBatch batch;
    private BitmapFont font32;
    private List<MapElement>[] drawables;//на какой полосе что находится

    public WorldRenderer(GameController gameController, SpriteBatch batch) {
        this.gc = gameController;
        this.font32 = Assets.getInstance().getAssetManager().get("fonts/font32.ttf");
        this.batch = batch;
        this.drawables = new ArrayList[Map.MAP_CELLS_HEIGHT];
        for (int i = 0; i < drawables.length; i++) {//инициализируем лист
            drawables[i] = new ArrayList<>();
        }
    }

    public void render() {
        for (int i = 0; i < drawables.length; i++) {
            drawables[i].clear();//очищаем список объектов
        }
        drawables[gc.getHero().getCellY()].add(gc.getHero());//смотрим где находится герой и в эту линию добавляем его
        //drawables[gc.getHero().getCellY()].add(gc.getMonster());//аналогично с монстром

        for (int i = 0; i < gc.getMonstersController().getActiveList().size(); i++) {
            GameCharacter p = gc.getMonstersController().getActiveList().get(i);
            drawables[p.getCellY()].add(p);//так же со снарядами
        }

        for (int i = 0; i < gc.getProjectilesController().getActiveList().size(); i++) {
            Projectile p = gc.getProjectilesController().getActiveList().get(i);
            drawables[p.getCellY()].add(p);//так же со снарядами
        }
        //после как мы расположили все объекты можем рисовать
        Gdx.gl.glClearColor(0, 0, 0, 1);//очищаем экран
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();//начинаем отрисовку
        for (int y = Map.MAP_CELLS_HEIGHT - 1; y >= 0; y--) {//земля
            for (int x = 0; x < Map.MAP_CELLS_WIDTH; x++) {
                gc.getMap().renderGround(batch, x, y);
            }
        }
        for (int y = Map.MAP_CELLS_HEIGHT - 1; y >= 0; y--) {//рисуем сверху вниз слоями
            for (int i = 0; i < drawables[y].size(); i++) {
                drawables[y].get(i).render(batch, null);//рисуем персонажей(живые объекты)
            }
            for (int x = 0; x < Map.MAP_CELLS_WIDTH; x++) {
                gc.getMap().renderUpper(batch, x, y);//рисуем саму карту
            }
        }
        gc.getHero().renderGUI(batch, font32);
        batch.end();//заканчиваем отрисовку
    }
}
