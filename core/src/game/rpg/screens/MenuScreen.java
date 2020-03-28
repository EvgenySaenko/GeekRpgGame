package game.rpg.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import game.rpg.screens.utils.Assets;

public class MenuScreen extends AbstractScreen {
    private Stage stage;
    private BitmapFont font72;
    Texture menuImages;
    Music music;


    public MenuScreen(SpriteBatch batch) {
        super(batch);
        this.music = Gdx.audio.newMusic(Gdx.files.internal("audio/mysticalTheme.mp3"));
        this.music.setLooping(true);//зациклим музыку
        this.music.play();//запустим музыку на экране меню
    }

    @Override
    public void show() {
        this.menuImages = new Texture("menu.png");
        this.font72 = Assets.getInstance().getAssetManager().get("fonts/font72.ttf");
        createGUI();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.0f, 1);//очищаем экран
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();//начинаем отрисовку
        batch.draw(menuImages,0,0);
        font72.draw(batch,"KNIGHT",860,490);
        batch.end();
        stage.draw();
    }

    public void createGUI() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);//обработка ввода для стейджа - чтобы граф интерфейс реагировал на мышку и кнопки
        Skin skin = new Skin();//то как выглядит наш интерфейс, кнопки поля итд
        skin.addRegions(Assets.getInstance().getAtlas());//скин мы хотим использовать текстуру которая в атласе
        BitmapFont font14 = Assets.getInstance().getAssetManager().get("fonts/font32.ttf");//берем шрифт

        TextButton.TextButtonStyle menuBtnStyle = new TextButton.TextButtonStyle(//подготавливаем стиль заранее
                skin.getDrawable("button284x86"), null, null, font14);

        TextButton btnNewGame = new TextButton("New Game", menuBtnStyle);//создаем кнопку с текстом -кладем стиль для кнопки
        btnNewGame.setPosition(850, 340);
        TextButton btnOptions = new TextButton("Options", menuBtnStyle);//создаем кнопку с текстом -кладем стиль для кнопки
        btnOptions.setPosition(850, 250);
        TextButton btnExitGame = new TextButton("Exit Game", menuBtnStyle);
        btnExitGame.setPosition(850, 160);

        btnNewGame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAME);
            }
        });

        btnExitGame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        stage.addActor(btnNewGame);//добавим кнопки на сцену
        stage.addActor(btnOptions);
        stage.addActor(btnExitGame);
        skin.dispose();
    }

    public void update(float dt) {
        stage.act(dt);//стейдж проверяет нажатие кнопки смену ее изображений
    }

    @Override
    public void dispose() {
        music.dispose();
    }
}
