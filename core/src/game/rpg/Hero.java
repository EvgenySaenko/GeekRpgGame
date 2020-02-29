package game.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Hero {
    private Texture texture;
    private Vector2 position;
    private float speed;


    public Hero() {
        this.texture = new Texture("hero.png");
        this.position = new Vector2(100,100);
        this.speed = 150.0f;
    }

    public void render(SpriteBatch batch){
        batch.draw(texture,position.x - 50,position.y,50,50,100,100,1.0f,1.0f,
                0.0f,0,0,100,100,false,false);
    }

    public void update(float dt){
        if (Gdx.input.isKeyPressed(Input.Keys.A)){
            position.x -= speed * dt;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)){
            position.x += speed * dt;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)){
            position.y -= speed * dt;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)){
            position.y += speed * dt;
        }
    }
}
