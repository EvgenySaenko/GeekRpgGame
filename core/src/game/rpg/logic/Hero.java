package game.rpg.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import game.rpg.screens.utils.Assets;


public class Hero extends GameCharacter {
    private TextureRegion texturePointer;
    private int coins;
    private StringBuilder strBuilder;

    public void addCoins(int amount) {
        coins += amount;
    }

    public Hero(GameController gc) {
        super(gc, 10, 300.0f);
        this.texture = Assets.getInstance().getAtlas().findRegion("archer");
        this.texturePointer = Assets.getInstance().getAtlas().findRegion("pointer32");
        this.changePosition(100.0f, 100.0f);
        this.dst.set(position);
        this.strBuilder = new StringBuilder();
    }

    @Override
    public void render(SpriteBatch batch, BitmapFont font) {
        batch.draw(texturePointer, dst.x - 16, dst.y - 16, 16, 16, 32, 32, 1.0f, 1.0f, lifetime * 120.0f);
        batch.draw(texture, position.x - 32, position.y - 32, 32, 32, 64, 64, 1.2f, 1.2f,
                0.0f);
        batch.draw(textureHp, position.x - 30, position.y + 40, 60 * ((float) hp / hpMax), 8);
    }

    public void renderGUI(SpriteBatch batch, BitmapFont font) {
        strBuilder.setLength(0);
        strBuilder.append("Class: ").append("Knight").append("\n");
        strBuilder.append("HP: ").append(hp).append(" / ").append(hpMax).append("\n");
        strBuilder.append("Coins: ").append(coins).append("\n");
        font.draw(batch, strBuilder, 10, 710);
    }

    @Override
    public void isDeath() {
        coins = 0;
        hp = hpMax;
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            dst.set(Gdx.input.getX(), 720.0f - Gdx.input.getY());
        }
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            gc.getProjectilesController().setup(position.x, position.y, Gdx.input.getX(), 720.0f - Gdx.input.getY());
        }
    }

    @Override
    public boolean isActive() {
        return false;
    }
}