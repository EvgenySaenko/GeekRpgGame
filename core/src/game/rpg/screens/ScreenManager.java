package game.rpg.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    private Screen targetScreen;
//    private Viewport viewport;
//    private Camera camera;

    //глобальный объект для всего приложения - синглтон
    private static ScreenManager ourInstance = new ScreenManager();

    public static ScreenManager getInstance() {
        return ourInstance;
    }

//    public Viewport getViewport() {
//        return viewport;
//    }
//
//    public Camera getCamera() {
//        return camera;
//    }

    private ScreenManager() {//приватный конструктор
    }
    //инициализируется и создает все экраны в нашем приложении
    public void init(GeekRpgGame game, SpriteBatch batch) {
        this.game = game;
        this.batch = batch;
//        this.camera = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT);
//        this.viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        this.gameScreen = new GameScreen(batch);
        this.loadingScreen = new LoadingScreen(batch);
    }

//    public void resize(int width, int height) {
//        viewport.update(width, height);
//        viewport.apply();
//    }
//
//    public void resetCamera() {
//        camera.position.set(HALF_WORLD_WIDTH, HALF_WORLD_HEIGHT, 0);
//        camera.update();
//        batch.setProjectionMatrix(camera.combined);
//    }

    //сменить экран => указав тип экрана
    public void changeScreen(ScreenType type) {
        Screen screen = game.getScreen();//смотрим на каком экране находились
        Assets.getInstance().clear();//счищаем текущие ресурсы, т.к переходим на другой экран
        if (screen != null) {//если мы находились не на пустом экране
            screen.dispose();//чистим объекты текущего экрана
        }
//        resetCamera();
        game.setScreen(loadingScreen);//переходим на экран с полоской загрузки(при смене экрана)
        switch (type) {//просим сменить экран на
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
