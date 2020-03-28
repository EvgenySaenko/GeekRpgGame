package game.rpg.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import game.rpg.screens.utils.Assets;

public class Hero extends GameCharacter {
    private TextureRegion texturePointer;
    private StringBuilder strBuilder;
    private boolean activePointer;
    //private Sound sound;

    public Hero(GameController gc) {
        super(gc, 200, 120.0f);
        this.textures = new TextureRegion(Assets.getInstance().getAtlas().findRegion("archerGOandFIRE")).split(64,64);
        this.texturePointer = Assets.getInstance().getAtlas().findRegion("pointerGreen");
        //this.sound = Gdx.audio.newSound(Gdx.files.internal("audio/swordStrike.mp3"));
        this.activePointer = false;
        this.changePosition(100.0f, 100.0f);
        this.dst.set(position);
        this.strBuilder = new StringBuilder();
        this.timePerFrame = 0.1f;
        this.weapon = gc.getWeaponController().getOneFromAnyPrototype();//даем герою оружие
    }



    public void renderGUI(SpriteBatch batch, BitmapFont font) {
        strBuilder.setLength(0);
        strBuilder.append("Class: ").append("Knight").append("\n");
        strBuilder.append("HP: ").append(hp).append(" / ").append(hpMax).append("\n");
        strBuilder.append("Coins: ").append(coins).append("\n");
        strBuilder.append("Weapon: ").append(weapon.getTitle()).append(" [").append(weapon.getMinDamage()).append("-").append(weapon.getMaxDamage()).append("]\n");
        font.draw(batch, strBuilder, 10, 710);
    }


    @Override
    public void onDeath() {
        super.onDeath();
        coins = 0;
        hp = hpMax;
    }

    @Override
    public boolean takeDamage(GameCharacter attacker, int damage) {
        gc.getInfoController().setupAnyAmount(position.x,position.y, Color.RED,"-",damage);//отрисовка урона по герою
        //sound.play();
        return super.takeDamage(attacker, damage);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
//        if (state == State.IDLE || state == State.ATTACK){
//            activePointer = false;
//        }else {
//            activePointer = true;
//        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {//если нажали левую кнопку
            for (int i = 0; i < gc.getMonstersController().getActiveList().size(); i++) {//перебираем всех актив монстров
                Monster m = gc.getMonstersController().getActiveList().get(i);//берем монстра
                //если расстояние от монстра к нашей мышки меньше 30 пикс => то мы считаем что тыкнули в монстра
                if (m.getPosition().dst(gc.getMouse()) < 30.0f) {
                    state = State.ATTACK;//переходим в состояние атаки
                    target = m;//мишенью является монстр
                    return;
                }
            }
            //если мы мышкой не попали ни в кого, то просто идем туда куда тыкнули мышкой
            dst.set(gc.getMouse());//подставили координаты мыши
            state = State.MOVE;//и состояние меняем на двигаться
            target = null;//мишень в ноль
        }

        if (hp < hpMax * 0.2){
            speed = 150 ;
        }
    }
}