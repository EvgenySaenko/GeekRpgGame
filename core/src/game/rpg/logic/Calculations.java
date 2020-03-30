package game.rpg.logic;

public class Calculations {

    public static int levelUp(int level){
        return (level * (level - 1) / 2 * 100);
    }

    public static int amountOfDamage(float power,int weaponDamage){
        return (int) (10 * (power * weaponDamage));//0,5 * 12 = 6 урона => готовый урон персонажа 5 уровня
    }

    public static int amountHp(int level){
        return (80 + (100 * (level - 1)));
    }


}
