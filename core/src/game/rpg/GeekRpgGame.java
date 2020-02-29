package game.rpg;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class GeekRpgGame extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture textureGrass;
	private Texture texturePointer;
	private Vector2 pointerPosition;
	private float rt;
	private Hero hero;

	@Override
	public void create() {//создание объекта
		batch = new SpriteBatch();
		hero = new Hero();
		textureGrass = new Texture("grass.png");
		texturePointer = new Texture("pointer32.png");
		pointerPosition = new Vector2(0,0);

	}

	@Override
	public void render() {
		float dt = Gdx.graphics.getDeltaTime();
		update(dt);
		Gdx.gl.glClearColor(1, 0.35f, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 9; j++) {
				batch.draw(textureGrass,i * 80,j * 80);
			}
		}
		batch.draw(texturePointer,pointerPosition.x - 16,pointerPosition.y - 16,
				16,16,32,32,1,1,rt,0,0,32,32,false,false);
		hero.render(batch);

		batch.end();
	}

	public void update(float dt) {
		rt -= dt * 160;
		hero.update(dt);
		if (Gdx.input.justTouched()){
			pointerPosition.set(Gdx.input.getX(),720.0f - Gdx.input.getY());
		}
	}


	@Override
	public void dispose() {//освобождение ресурсов
		batch.dispose();
	}
}