package game.rpg;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class GeekRpgGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private BitmapFont font32;
    private TextureAtlas atlas;
    private TextureRegion textureGrass;
    private Hero hero;
    private Apple apple;
    static Projectile[] projectiles;
    private Vector2 dst;

    @Override
    public void create() {//создание объекта
        this.batch = new SpriteBatch();
        this.atlas = new TextureAtlas("game.pack");
        this.hero = new Hero(atlas);
        this.textureGrass = atlas.findRegion("grass");
        this.font32 = new BitmapFont(Gdx.files.internal("font32.fnt"));
        this.apple = new Apple(atlas);//создаем яблоки
        this.dst = new Vector2(0, 0);
        this.projectiles = new Projectile[100];//создаем стрелы
        for (int i = 0; i < projectiles.length; i++) {
            projectiles[i] = new Projectile(atlas);
        }
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        update(dt);
        Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 9; j++) {
                batch.draw(textureGrass, i * 80, j * 80);
            }
        }
        hero.render(batch);
        hero.renderGUI(batch, font32);

        for (int i = 0; i < projectiles.length; i++) {//рисуем стрелы
            projectiles[i].render(batch);
        }
        apple.render(batch);
        batch.end();
    }

    public void update(float dt) {
        hero.update(dt);
        apple.update();

        for (int i = 0; i < projectiles.length; i++) {
            if (projectiles[i].isActive()) {
                projectiles[i].update(dt);
                System.out.println(projectiles[i].getPosition());
                if (apple.getCircle().contains(projectiles[i].getPosition())) {
                    System.out.println("Good!!");
                    apple.deactivate();
                    apple.setup();
                    projectiles[i].deactivate();
                }
                //dst.set(apple.getPosition()).dst(projectiles[i].getPosition());

                System.out.println("dst - " + dst);

            }
        }
    }


    @Override
    public void dispose() {//освобождение ресурсов
        batch.dispose();
    }
}