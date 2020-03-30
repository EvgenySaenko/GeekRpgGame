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
    private TextureRegion iconHeroes;
    private TextureRegion iconHP;
    private TextureRegion iconExp;
    private StringBuilder strBuilder;
    private boolean activePointer;
    private int expForKilling;
    //private Sound sound;

    public Hero(GameController gc) {
        super(gc, 120.0f);
        this.textures = new TextureRegion(Assets.getInstance().getAtlas().findRegion("archerGOandFIRE")).split(64,64);
        this.iconHeroes = new TextureRegion(Assets.getInstance().getAtlas().findRegion("iconHero"));
        this.texturePointer = Assets.getInstance().getAtlas().findRegion("pointerGreen");
        this.iconHP = new TextureRegion(Assets.getInstance().getAtlas().findRegion("hpGreen"));
        this.iconExp = new TextureRegion(Assets.getInstance().getAtlas().findRegion("exp"));
        //this.sound = Gdx.audio.newSound(Gdx.files.internal("audio/swordStrike.mp3"));
        this.activePointer = false;
        this.changePosition(100.0f, 100.0f);
        this.dst.set(position);
        this.strBuilder = new StringBuilder();
        this.timePerFrame = 0.1f;
        this.weapon = gc.getWeaponController().getOneFromAnyPrototype();//даем герою оружие
    }

    //пока почему - то иногда при убийстве моба добавляется 0 опыта
    //а так все условия работают
    // Одинаковый уровень с противником  ==> 110%
    // Противник меньше вас на 2 и более уровней ==> 70% опыта
    //Если уровень противника выше вашего на 2 и более ==> 130% опыта
    public int increasedExperienceForKilling(int hp, int level){
        expForKilling = 0;
        if (this.level == level){//если уровни равны
            expForKilling += (hp *1.1f);
            System.out.println("check 1");
        }
        if (this.level > level){//если уровень героя больше уровня моба
            if (this.level - level >= 2){//если мой уровень на 2 уровня больше монстра
                expForKilling += (hp * 0.7f);
                System.out.println("check 2");
            }else {//в другом случает например всего лишь на 1 уровень герой больше моба
                expForKilling += (hp *1.1f);//то получаем обычный коэффициент
                System.out.println("check 1");
            }
        }
        if (this.level < level){
            if (level - this.level >= 2){//если уровень моба больше моего на 2
                expForKilling  += (hp *1.3f);
                System.out.println("check 3");
            }else {//в другом случает например всего лишь на 1 уровень монстр больше героя
                expForKilling += (hp *1.1f);//получаем обычный коэффициент
                System.out.println("check 1");
            }
        }
        experience += expForKilling;
        levelUp();
        System.out.println("Hero = > level " + this.level + " experience " + expForKilling);
        return expForKilling;
    }



    public void renderGUI(SpriteBatch batch, BitmapFont font) {
        strBuilder.setLength(0);
        strBuilder.append(hp).append(" / ").append(hpMax).append("\n");
        strBuilder.append(experience).append(" / ").append(Calculations.levelUp(this.level + 1)).append("\n");
        strBuilder.append("Power: ").append(power).append("\n");
//        strBuilder.append("Coins: ").append(coins).append("\n");
//        strBuilder.append("Damage: ").append(damage).append("\n");
//        strBuilder.append("Weapon: ").append(weapon.getTitle()).append(" [").append(weapon.getMinDamage()).append("-").append(weapon.getMaxDamage()).append("]\n");
        batch.draw(iconHeroes,10,620);

        batch.draw(iconHP,110,671,190 * ((float) hp / hpMax),14);
        batch.draw(iconExp,110,652,190 * ((float) experience / Calculations.levelUp(this.level + 1)),14);
        font20.draw(batch,String.valueOf(level),68,643);
        font.draw(batch, strBuilder, 180, 684);
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