package game.rpg.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import game.rpg.logic.utils.ObjectPool;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WeaponController extends ObjectPool<Weapon> {
    private GameController gc;
    private List<Weapon> prototypes;//список прототипов

    @Override
    protected Weapon newObject() {
        return new Weapon(gc);
    }

    public WeaponController(GameController gc) {
        this.gc = gc;
        this.loadPrototypes();//когда создаем контроллер
    }
    //когда какое то оружие должно появится на экране
    public void setup(float x, float y) {
        Weapon out = getActiveElement();//мы достаем его из пула объектов
        out.copyFrom(prototypes.get(MathUtils.random(0, prototypes.size() - 1)));//копируем в него случайный вид из списка прототипов
        forge(out);//куем его
        out.setPosition(x, y); //кидаем в точку экрана
        out.activate();//активируем
    }

    public Weapon getOneFromAnyPrototype() {//чтобы задать персонажу первое оружие
        Weapon out = new Weapon(gc);
        out.copyFrom(prototypes.get(MathUtils.random(0, prototypes.size() - 1)));
        forge(out);
        return out;
    }

    public void forge(Weapon w) {//рандомно создает статы оружию (блок где генерится оружие с лучшими статами)
        for (int i = 0; i < 30; i++) {
            if (MathUtils.random(100) < 5) {
                w.setMinDamage(w.getMinDamage() + 1);
            }
        }
        for (int i = 0; i < 30; i++) {
            if (MathUtils.random(100) < 10) {
                w.setMaxDamage(w.getMaxDamage() + 1);
            }
        }
        if (w.getMinDamage() > w.getMaxDamage()) {
            w.setMinDamage(w.getMaxDamage());
        }
    }

    public void update(float dt) {
        checkPool();
    }

    public void loadPrototypes() {
        prototypes = new ArrayList<>();//создаем пустой лист
        BufferedReader reader = null;
        try {
            reader = Gdx.files.internal("data/weapons.csv").reader(8192);//обращаемся к модулю работы с файлами - запрашиваем файл
            reader.readLine();//вычитываем первую строчку - убираем хэдер из рассмотрения
            String line = null;
            while ((line = reader.readLine()) != null) {//читаем построчно
                prototypes.add(new Weapon(line));//каждую строку отдаем в конструктор Weapon а он ее уже сплитает
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();//файл закрыли и все что в файле было - стало в листе prototypes
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

