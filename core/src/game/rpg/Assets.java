package game.rpg;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;

public class Assets {
    private static final Assets ourInstance = new Assets();

    public static Assets getInstance() {
        return ourInstance;
    }

    private AssetManager assetManager;
    private TextureAtlas textureAtlas;

    public TextureAtlas getAtlas() {
        return textureAtlas;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    //инициализируется Assets менеджер
    private Assets() {
        assetManager = new AssetManager();
    }

    //загрузка ресурсов под тип конкретный тип экрана
    public void loadAssets(ScreenManager.ScreenType type) {
        switch (type) {
            case MENU:
                assetManager.load("images/game.pack", TextureAtlas.class);
                createStandardFont(24);
                createStandardFont(72);
                break;
            case GAME:
                assetManager.load("images/game.pack", TextureAtlas.class);
                createStandardFont(32);
                break;
        }
    }
    //создаем фонты
    private void createStandardFont(int size) {
        FileHandleResolver resolver = new InternalFileHandleResolver();
        assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
        FreetypeFontLoader.FreeTypeFontLoaderParameter fontParameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        fontParameter.fontFileName = "fonts/Roboto-Medium.ttf";//из фонта собираем все знаки
        fontParameter.fontParameters.size = size; // даем фонту возможность иметь размер
        fontParameter.fontParameters.color = Color.WHITE;//цвет
        fontParameter.fontParameters.borderWidth = 1;//обводка
        fontParameter.fontParameters.borderColor = Color.BLACK;//цвет обводки
        fontParameter.fontParameters.shadowOffsetX = 1;//тени
        fontParameter.fontParameters.shadowOffsetY = 1;
        fontParameter.fontParameters.shadowColor = Color.DARK_GRAY;//цвет тени
        //собираем фонт по всем параметрам => кладем в  BitmapFont
        assetManager.load("fonts/font" + size + ".ttf", BitmapFont.class, fontParameter);
    }
    //когда ресурсы загружены для экрана, метод запоминает ссылку на основной атлас
    public void makeLinks() {
        textureAtlas = assetManager.get("images/game.pack", TextureAtlas.class);
    }

    //освобождаем ресурсы(все что было загружено)
    public void clear() {
        assetManager.clear();
    }
}