package game.rpg.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import game.rpg.screens.utils.Assets;

public class MenuScreen extends AbstractScreen {
    private Stage stage;
    private BitmapFont font72;


    public MenuScreen(SpriteBatch batch) {
        super(batch);
    }

    @Override
    public void show() {
        this.font72 = Assets.getInstance().getAssetManager().get("fonts/font72.ttf");
        createGUI();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.0f, 1);//очищаем экран
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();//начинаем отрисовку
        font72.draw(batch,"Geek Rpg Game 2020",0,500,1280, Align.center,false);
        batch.end();
        stage.draw();
    }

    public void createGUI() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);//обработка ввода для стейджа - чтобы граф интерфейс реагировал на мышку и кнопки
        Skin skin = new Skin();//то как выглядит наш интерфейс, кнопки поля итд
        skin.addRegions(Assets.getInstance().getAtlas());//скин мы хотим использовать текстуру которая в атласе
        BitmapFont font14 = Assets.getInstance().getAssetManager().get("fonts/font52.ttf");//берем шрифт

        TextButton.TextButtonStyle menuBtnStyle = new TextButton.TextButtonStyle(//подготавливаем стиль заранее
                skin.getDrawable("button"), null, null, font14);

        TextButton btnNewGame = new TextButton("New Game", menuBtnStyle);//создаем кнопку с текстом -кладем стиль для кнопки
        btnNewGame.setPosition(400, 250);
        TextButton btnExitGame = new TextButton("Exit Game", menuBtnStyle);
        btnExitGame.setPosition(400, 80);

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
        stage.addActor(btnExitGame);
        skin.dispose();
    }

    public void update(float dt) {
        stage.act(dt);//стейдж проверяет нажатие кнопки смену ее изображений
    }
}
