package game.rpg.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import game.rpg.GeekRpgGame;
import game.rpg.screens.utils.Assets;

public class ScreenManager {//управляет всеми экранами приложения

    public enum ScreenType {//на какие экраны мы можем перейти
        MENU, GAME
    }

    public static final int WORLD_WIDTH = 1280;
    public static final int HALF_WORLD_WIDTH = WORLD_WIDTH / 2;
    public static final int WORLD_HEIGHT = 720;
    public static final int HALF_WORLD_HEIGHT = WORLD_HEIGHT / 2;

    private GeekRpgGame game;//даем доступ к корневой игре => этой игре он задает экран
    private SpriteBatch batch;
    private LoadingScreen loadingScreen;
    private GameScreen gameScreen;//ссылка на все возможные экраны
    private MenuScreen menuScreen;
    private Screen targetScreen;
    private Viewport viewport;
    private Camera camera;

    //глобальный объект для всего приложения - синглтон
    private static ScreenManager ourInstance = new ScreenManager();

    public static ScreenManager getInstance() {
        return ourInstance;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public Camera getCamera() {
        return camera;
    }

    private ScreenManager() {//приватный конструктор
    }
    //инициализируется и создает все экраны в нашем приложении
    public void init(GeekRpgGame game, SpriteBatch batch) {
        this.game = game;
        this.batch = batch;
        this.camera = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT); //ортографик камера - камера у которой нет координаты z (2D)
        this.viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);//вписывает мир с сохранением пропорций
        this.gameScreen = new GameScreen(batch);
        this.menuScreen = new MenuScreen(batch);
        this.loadingScreen = new LoadingScreen(batch);
    }

    //нужен если размер окна игры меняется - сохраняет пропорции
    public void resize(int width, int height) {
        viewport.update(width, height);//вьюпорт получает новые размеры окна
        viewport.apply();//и решает как выводит картинку с соблюдением пропорций
    }

    public void resetCamera() {//метод который сбрасывает камеру
        camera.position.set(HALF_WORLD_WIDTH, HALF_WORLD_HEIGHT, 0);
        camera.update();
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
    }
    //метод выставляет нашу камеру в указанную нами точку
    public void pointCameraTo(Vector2 position){
        camera.position.set(position,0);
        camera.update();
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
    }

    //сменить экран => указав тип экрана
    public void changeScreen(ScreenType type) {
        Screen screen = game.getScreen();//смотрим на каком экране находились
        Assets.getInstance().clear();//счищаем текущие ресурсы, т.к переходим на другой экран
        Gdx.input.setInputProcessor(null);//сбрасываем чтобы не обрабатываеть нажатия на кнопки меню
        if (screen != null) {//если мы находились не на пустом экране
            screen.dispose();//чистим объекты текущего экрана
        }
        resetCamera();//при смене экрана мы сбрасываем камеру - нам не нужно чтобы она смотрела в какую-то точку
        game.setScreen(loadingScreen);//переходим на экран с полоской загрузки(при смене экрана)
        switch (type) {//просим сменить экран на
            case MENU://если нам надо перейти на МЕНЮ экран
                targetScreen = menuScreen;//то запоминаем что хотели перейти на меню экран
                Assets.getInstance().loadAssets(ScreenType.MENU);//и начинаем грузить ресурсы для меню
                break;
            case GAME://если нам надо перейти на ИГРОВОЙ экран
//                game.setScreen(gameScreen);
                targetScreen = gameScreen;//то запоминаем что хотели перейти на ИГРОВОЙ экран
                Assets.getInstance().loadAssets(ScreenType.GAME);//и начинаем грузить ресурсы для ИГРОВОГО
                break;
        }
    }
    //переключает игру в целевой экран(куда мы хотели)
    public void goToTarget() {
        game.setScreen(targetScreen);
    }
}
