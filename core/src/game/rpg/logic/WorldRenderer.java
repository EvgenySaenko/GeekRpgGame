package game.rpg.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import game.rpg.screens.ScreenManager;
import game.rpg.screens.utils.Assets;
import game.rpg.logic.utils.MapElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WorldRenderer {//отрисовщик
    private GameController gc;
    private SpriteBatch batch;
    private BitmapFont font32;
    private BitmapFont font20;
    private BitmapFont font14;
    private List<MapElement>[] drawables;//на какой полосе что находится
    private Vector2 pov;

    private Comparator<MapElement> yComparator;

    private FrameBuffer frameBuffer;//разовый кадр
    private TextureRegion frameBufferRegion;
    private ShaderProgram shaderProgram;//сам шейдер который может быть построен из файлов в папке шейдер

    public WorldRenderer(GameController gameController, SpriteBatch batch) {
        this.gc = gameController;
        this.font32 = Assets.getInstance().getAssetManager().get("fonts/font32.ttf");
        this.font20 = Assets.getInstance().getAssetManager().get("fonts/font20.ttf");
        this.font14 = Assets.getInstance().getAssetManager().get("fonts/font14.ttf");
        this.batch = batch;
        this.pov = new Vector2(0,0);
        this.drawables = new ArrayList[Map.MAP_CELLS_HEIGHT];
        for (int i = 0; i < drawables.length; i++) {//инициализируем лист
            drawables[i] = new ArrayList<>();
        }
        this.yComparator = new Comparator<MapElement>() {
            @Override
            public int compare(MapElement o1, MapElement o2) {
                return (int) (o2.getY() - o1.getY());
            }
        };


        //форматРГБ888 означает у каждого пикселя есть 3 канала,задаем размеры
        this.frameBuffer = new FrameBuffer(Pixmap.Format.RGB888, ScreenManager.WORLD_WIDTH, ScreenManager.WORLD_HEIGHT, false);
        this.frameBufferRegion = new TextureRegion(frameBuffer.getColorBufferTexture());//создаем для него регион
        this.frameBufferRegion.flip(false, true);//и делаем флип по Y (изначально он перевернут)
        this.shaderProgram = new ShaderProgram(Gdx.files.internal("shaders/vertex.glsl").readString(),//создаем шейдерную программу
                Gdx.files.internal("shaders/fragment.glsl").readString());
        if (!shaderProgram.isCompiled()) {//выполняем компиляцию
            throw new IllegalArgumentException("Error compiling shader: " + shaderProgram.getLog());
        }
    }

    public void render() {
        pov.set(gc.getHero().getPosition());
        if (pov.x < ScreenManager.HALF_WORLD_WIDTH) {//если вектор смотрит чуть левее чем половина экрана - то увидим черный фон
            pov.x = ScreenManager.HALF_WORLD_WIDTH;//вектор равен половине экрана -  то есть мы не будем видеть черный фон за краницами половины экрана
        }
        if (pov.y < ScreenManager.HALF_WORLD_HEIGHT) {//аналогично
            pov.y = ScreenManager.HALF_WORLD_HEIGHT;
        }
        if (pov.x > gc.getMap().getWidthLimit() - ScreenManager.HALF_WORLD_WIDTH) {
            pov.x = gc.getMap().getWidthLimit() - ScreenManager.HALF_WORLD_WIDTH;
        }
        if (pov.y > gc.getMap().getHeightLimit() - ScreenManager.HALF_WORLD_HEIGHT) {
            pov.y = gc.getMap().getHeightLimit() - ScreenManager.HALF_WORLD_HEIGHT;
        }

        ScreenManager.getInstance().pointCameraTo(pov);

        //ставим в каком порядке и что рисовать
        for (int i = 0; i < drawables.length; i++) {
            drawables[i].clear();//очищаем список объектов
        }

        for (int i = 0; i < gc.getWeaponController().getActiveList().size(); i++) {//добавляем оружие
            Weapon w = gc.getWeaponController().getActiveList().get(i);
            drawables[w.getCellY()].add(w);
        }

        for (int i = 0; i < gc.getLootsController().getActiveList().size(); i++) {//добавляем лут
            Loot loot = gc.getLootsController().getActiveList().get(i);
            drawables[loot.getCellY()].add(loot);
        }
        drawables[gc.getHero().getCellY()].add(gc.getHero());//смотрим где находится герой и в эту линию добавляем его

        for (int i = 0; i < gc.getMonstersController().getActiveList().size(); i++) {//монстров
            Monster m = gc.getMonstersController().getActiveList().get(i);
            drawables[m.getCellY()].add(m);//смотрим где монтср и добавляем монстра
        }

        for (int i = 0; i < gc.getProjectilesController().getActiveList().size(); i++) {//стрелы
            Projectile p = gc.getProjectilesController().getActiveList().get(i);
            drawables[p.getCellY()].add(p);
        }

        for (int i = 0; i < drawables.length; i++) {
            Collections.sort(drawables[i], yComparator);
        }

        //рисуем все в буфер  вначале
        frameBuffer.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);//почистили буфер
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);//почистили буфер
        batch.begin();//начали отрисовку

        for (int y = Map.MAP_CELLS_HEIGHT - 1; y >= 0; y--) {//земля
            for (int x = 0; x < Map.MAP_CELLS_WIDTH; x++) {
                gc.getMap().renderGround(batch, x, y);
            }
        }
        for (int y = Map.MAP_CELLS_HEIGHT - 1; y >= 0; y--) {//рисуем сверху вниз слоями
            for (int i = 0; i < drawables[y].size(); i++) {
                drawables[y].get(i).render(batch, null);//рисуем персонажей(живые объекты) - урон над головой тоже рисуется
            }
            for (int x = 0; x < Map.MAP_CELLS_WIDTH; x++) {
                gc.getMap().renderUpper(batch, x, y);//рисуем саму карту
            }
        }
        for (int i = 0; i < gc.getMonstersController().getActiveList().size(); i++) {//монстров
            Monster m = gc.getMonstersController().getActiveList().get(i);
        }

        gc.getSpecialEffectsController().render(batch);//рисуем спецэффекты
        gc.getInfoController().render(batch,font20);
        //заканчиваем отрисовку в буфер
        batch.end();
        frameBuffer.end();

        ScreenManager.getInstance().resetCamera();//сбрасываем камеру

        //начинаем выводить на экран при помощи шейдера
        batch.begin();
        batch.setShader(shaderProgram);//устанавливаем шейденую программу(активируем)
        shaderProgram.setUniformf(shaderProgram.getUniformLocation("time"), gc.getWorldTimer());//прокидываем значения - на место тайм - мировое время
        shaderProgram.setUniformf(shaderProgram.getUniformLocation("px"), pov.x / ScreenManager.WORLD_WIDTH);//отдаем координату х
        shaderProgram.setUniformf(shaderProgram.getUniformLocation("py"), pov.y / ScreenManager.WORLD_HEIGHT);
        Gdx.gl.glClearColor(0, 0, 0, 1);//чистим экран
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.draw(frameBufferRegion, 0, 0);//рисуем разом наш кадр из буфера
        batch.end();
        batch.setShader(null);//если шейдер не нужен- сбрасываем его(выключаем)


        //после уже рисуем гуи персонажа
        batch.begin();
        gc.getHero().renderGUI(batch, font14);
        batch.end();
        //после всей отрисовки возвращаем камеру на место относительно героя
        ScreenManager.getInstance().pointCameraTo(pov);
    }
}
