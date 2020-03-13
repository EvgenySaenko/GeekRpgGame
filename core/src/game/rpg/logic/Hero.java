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
    private boolean activePointer;

    public void addCoins(int amount) {
        coins += amount;
    }

    public Hero(GameController gc) {
        super(gc, 50, 100.0f);
        this.texture = Assets.getInstance().getAtlas().findRegion("knight");
        this.texturePointer = Assets.getInstance().getAtlas().findRegion("pointer32");
        this.activePointer = false;
        this.changePosition(100.0f, 100.0f);
        this.dst.set(position);
        this.strBuilder = new StringBuilder();
    }

    @Override
    public void render(SpriteBatch batch, BitmapFont font) {
        if (activePointer){
            batch.draw(texturePointer, dst.x - 16, dst.y - 16,
                    16, 16, 32, 32, 1, 1, lifetime * 120);
        }
        batch.draw(texture, position.x - 30, position.y - 30, 30, 30, 60, 60, 1, 1, 0);
        batch.draw(textureHp, position.x - 30, position.y + 30, 60 * ((float) hp / hpMax), 12);
    }

    public void renderGUI(SpriteBatch batch, BitmapFont font) {
        strBuilder.setLength(0);
        strBuilder.append("Class: ").append("Knight").append("\n");
        strBuilder.append("HP: ").append(hp).append(" / ").append(hpMax).append("\n");
        strBuilder.append("Coins: ").append(coins).append("\n");
        font.draw(batch, strBuilder, 10, 710);
    }

    @Override
    public void onDeath() {
        super.onDeath();
        coins = 0;
        hp = hpMax;
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if (state == State.IDLE || state == State.ATTACK){
            activePointer = false;
        }else {
            activePointer = true;
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {//если нажали левую кнопку
            for (int i = 0; i < gc.getMonstersController().getActiveList().size(); i++) {//перебираем всех актив монстров
                Monster m = gc.getMonstersController().getActiveList().get(i);//берем монстра
                //если расстояние от монстра к нашей мишки меньше 30 пикс => то мы считаем что тыкнули в монстра
                if (m.getPosition().dst(Gdx.input.getX(), 720.0f - Gdx.input.getY()) < 30.0f) {
                    state = State.ATTACK;//переходим в состояние атаки
                    target = m;//мишенью является монстр
                    return;
                }
            }
            //если мы мышкой не попали ни в кого, то просто идем туда куда тыкнули мышкой
            dst.set(Gdx.input.getX(), 720.0f - Gdx.input.getY());
            state = State.MOVE;//и состояние меняем на двигаться
            target = null;//мишень в ноль
        }

        if (hp < hpMax * 0.2){
            speed = 150 ;
        }
    }
}