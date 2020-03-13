package game.rpg.logic.utils;

import java.util.ArrayList;
import java.util.List;

public abstract class ObjectPool<T extends Poolable> {
    protected List<T> activeList;//список активных объектов
    protected List<T> freeList;//список свободных объектов

    public List<T> getActiveList() {
        return activeList;
    }

    protected abstract T newObject();
    //из активного списка выкидываем(remove) remove => возвращает ссылку на объект => этот объект кладем(add) в freeList
    public void free(int index) {
        freeList.add(activeList.remove(index));
    }

    public ObjectPool() {//когда пул создаем => инициализируются пустые списки
        this.activeList = new ArrayList<T>();
        this.freeList = new ArrayList<T>();
    }
    //запрашиваем активный эелемент
    public T getActiveElement() {//возвращает нам рабочий элемент
        if (freeList.size() == 0) {//если при запросе свободный список => пуст
            freeList.add(newObject());//добавляем в него новый элемент
        }
        //удаляет с конца free листа последний элемент => записывает ссылку в temp
        T temp = freeList.remove(freeList.size() - 1);
        activeList.add(temp);//этот объект перекидываем в активный список
        return temp;//и возвращаем ссылку добавленного объекта для настройки
    }
    //пробегает с конца активного списка в начало => проверяя активный ли объект
    public void checkPool() {
        for (int i = activeList.size() - 1; i >= 0; i--) {
            if (!activeList.get(i).isActive()) {//если перестал быть активным
                free(i);//мы его освобождаем
            }
        }
    }
}
