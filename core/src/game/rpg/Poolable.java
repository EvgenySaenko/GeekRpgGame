package game.rpg;

//тот класс который помечен данным интерфесом мы его можем класть в пул объектов
public interface Poolable {
    boolean isActive(); //проверка активности-если не активен мы его должны освободить
}