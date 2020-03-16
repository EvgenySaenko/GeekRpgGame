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
    private StringBuilder stringDamage;
    private boolean activePointer;

    public void addCoins(int amount) {
        coins += amount;
    }

    public Hero(GameController gc) {
        super(gc, 100, 100.0f);
        this.textures = new TextureRegion(Assets.getInstance().getAtlas().findRegion("archerGOandFIRE")).split(64,64);
        this.texturePointer = Assets.getInstance().getAtlas().findRegion("pointer32");
        this.activePointer = false;
        this.changePosition(100.0f, 100.0f);
        this.dst.set(position);
        this.strBuilder = new StringBuilder();
        this.stringDamage = new StringBuilder();
        this.timePerFrame = 0.1f;
        this.weapon = Weapon.createSimpleMeleeWeapon();//вначале герой с голыми руками

    }


    @Override
    public void render(SpriteBatch batch, BitmapFont font) {
        if (activePointer){
            batch.draw(texturePointer, dst.x - 16, dst.y - 16,
                    16, 16, 32, 32, 1, 1, lifetime * 120);
        }
        TextureRegion currentRegion = textures[0][getCurrentFrameIndex()];
        TextureRegion attackRegion = textures[1][getCurrentFrameIndex()];
        if (dst.x > position.x){
            if (currentRegion.isFlipX()) {
                currentRegion.flip(true, false);
                attackRegion.flip(true,false);
            }
        }else {
            if (!currentRegion.isFlipX()) {
                currentRegion.flip(true, false);
                attackRegion.flip(true,false);
            }
        }
        batch.draw(currentRegion, position.x - 32, position.y - 32, 32, 32, 64, 64, 1.5f, 1.5f, 0);

        if (state == State.ATTACK){
            batch.draw(attackRegion, position.x - 32, position.y - 32, 32, 32, 64, 64, 1.5f, 1.5f, 0);
        }
        if (hp < hpMax){
            batch.draw(textureHp, position.x - 32, position.y + 50, 60 * ((float) hp / hpMax), 10);
        }
    }

    public void renderGUI(SpriteBatch batch, BitmapFont font) {
        strBuilder.setLength(0);
        strBuilder.append("Class: ").append("Knight").append("\n");
        strBuilder.append("HP: ").append(hp).append(" / ").append(hpMax).append("\n");
        strBuilder.append("Coins: ").append(coins).append("\n");
        strBuilder.append("Weapon: ").append(weapon.getTitle()).append(" [").append(weapon.getMinDamage()).append("-").append(weapon.getMaxDamage()).append("]\n");
        font.draw(batch, strBuilder, 10, 710);
    }
    public void renderDamage(SpriteBatch batch, BitmapFont font){//заготовка к отлетающему хп
        if (hp < hpMax) {
            strBuilder.setLength(0);
            strBuilder.append(hp).append("\n");
            font.draw(batch, strBuilder, position.x - 16, position.y + 60);
        }
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
                //если расстояние от монстра к нашей мышки меньше 30 пикс => то мы считаем что тыкнули в монстра
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