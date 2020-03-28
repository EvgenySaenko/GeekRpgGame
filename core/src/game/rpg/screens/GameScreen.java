package game.rpg.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import game.rpg.logic.GameController;
import game.rpg.logic.WorldRenderer;
import game.rpg.screens.utils.Assets;


public class GameScreen extends AbstractScreen {
    private GameController gc;
    private WorldRenderer worldRenderer;
    Stage stage;
    private boolean pressed;
    private TextButton pause;

    public GameScreen(SpriteBatch batch) {
        super(batch);
    }

    @Override
    public void show() {
        gc = new GameController();
        worldRenderer = new WorldRenderer(gc, batch);
        createGUI();
    }

    @Override
    public void render(float delta) {
        update(delta);
        if (!pressed){
            gc.update(delta);
        }
        worldRenderer.render();
        stage.draw();

    }

    @Override
    public void pause() {
        if (pressed ==false){
            pressed = true;
            pause.setText("PLAY");
        }else{
            pressed = false;
            pause.setText("PAUSE");
        }
    }

    public void createGUI(){
        this.stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        Skin skin = new Skin();
        skin.addRegions(Assets.getInstance().getAtlas());

        BitmapFont font10 = Assets.getInstance().getAssetManager().get("fonts/font10.ttf");

        TextButton.TextButtonStyle menuButtonStyle = new TextButton.TextButtonStyle(skin.getDrawable("button64x32"),
                skin.getDrawable("buttonPressed64x32"),null,font10);
        TextButton.TextButtonStyle pauseButtonStyle = new TextButton.TextButtonStyle(skin.getDrawable("button64x32"),null
                ,skin.getDrawable("buttonPressed64x32"),font10);

        TextButton menu = new TextButton("MENU",menuButtonStyle);
        this.pause = new TextButton("PAUSE",pauseButtonStyle);
       // TextButton play = new TextButton("PLAY",pauseButtonStyle);

        menu.setPosition(1216,688);//задали позиции кнопок
       // play.setPosition(1150, 688);
        pause.setPosition(1150,688);


        menu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.MENU);
            }
        });

        pause.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pause();
            }
        });
        stage.addActor(menu);//добавим кнопку на сцену
        //stage.addActor(play);
        stage.addActor(pause);
        skin.dispose();
    }

    public void update(float dt){
        stage.act(dt);//обработать события сцены
    }
}