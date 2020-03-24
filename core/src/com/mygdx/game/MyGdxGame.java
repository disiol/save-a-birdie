package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class MyGdxGame implements Screen {
    public static final int VIEWPORT_WIDTH = 400;
    public static final int VIEWPORT_HEIGHT = 495;
    public static final int INT = 5;
    Texture hero, bombe, heroRip;

    SpriteBatch batch;
    OrthographicCamera camera;
    Rectangle bucket;
    Array<Rectangle> raindrops;
    long lastDropTime;
    Texture background;
    private boolean isFirstTouch;
    private int spead = 200;
    private long soruse;
    private BitmapFont font;
    private int lifes = INT;

    private float touchX;
    private float touchY;


    public MyGdxGame(Drop drop) {
        // загрузка изображений для капли и ведра, 64x64 пикселей каждый
        bombe = new Texture(Gdx.files.internal("Bombe.png"));
        hero = new Texture(Gdx.files.internal("BirdEnemyFlapSprite.png"));
        heroRip = new Texture(Gdx.files.internal("BirdEnemyDeathSprite.png"));

        font = new BitmapFont();

        background = new Texture(Gdx.files.internal("background.png"));

        // загрузка звукового эффекта падающей капли и фоновой "музыки" дождя

        // сразу же воспроизводиться музыка для фона


        // создается камера и SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        batch = new SpriteBatch();

        // создается Rectangle для представления ведра
        bucket = new Rectangle();
        // центрируем ведро по горизонтали
        bucket.x = 800 / 2 - 64 / 2;
        // размещаем на 20 пикселей выше нижней границы экрана.
        bucket.y = 20;
        bucket.width = 64;
        bucket.height = 64;

        // создает массив капель и возрождает первую
        raindrops = new Array<Rectangle>();
        spawnRaindrop();
    }

    private void spawnRaindrop() {
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0, 800 - 64);
        raindrop.y = 480;
        raindrop.width = 64;
        raindrop.height = 64;
        raindrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta) {
        // очищаем экран темно-синим цветом.
        // Аргументы для glClearColor красный, зеленый
        // синий и альфа компонент в диапазоне [0,1]
        // цвета используемого для очистки экрана.


        batch.begin();
        batch.draw(background, 0, 0);

        if (lifes > 0) {
            showSpedAndPointsLifes();
        } else {
            showGameOver();
            batch.draw(heroRip, bucket.x, bucket.y);

            batch.end();
        }


        // сообщает камере, что нужно обновить матрицы
        camera.update();

        // сообщаем SpriteBatch о системе координат
        // визуализации указанной для камеры.
        batch.setProjectionMatrix(camera.combined);

        if (lifes > 0) {
            // начинаем новую серию, рисуем ведро и
            // все капли
            //batch.begin();
            batch.draw(hero, bucket.x, bucket.y);
            for (Rectangle raindrop : raindrops) {
                batch.draw(bombe, raindrop.x, raindrop.y);
            }
            batch.end();


            Vector3 touchPos = new Vector3();
            Vector3 touchPosEnd = new Vector3();
            if (Gdx.input.isTouched()) {
                if (isFirstTouch) {
                    touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                    isFirstTouch = false;
                } else {
                    touchPosEnd.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                    camera.unproject(touchPos);
                    camera.unproject(touchPosEnd);
                    bucket.x = touchPosEnd.x - touchPos.x;

                    touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                }
            } else {
                isFirstTouch = true;
            }


            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.getDeltaTime();
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.getDeltaTime();

            // убедитесь что ведро остается в пределах экрана
            if (bucket.x < 0) bucket.x = 0;
            if (bucket.x > 800 - 64) bucket.x = 800 - 64;

            // проверка, нужно ли создавать новую каплю
            if (TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();

            // движение капли, удаляем все капли выходящие за границы экрана
            // или те, что попали в ведро. Воспроизведение звукового эффекта
            // при попадании.
            Iterator<Rectangle> iter = raindrops.iterator();
            while (iter.hasNext()) {
                Rectangle raindrop = iter.next();
                System.out.println(spead);
                raindrop.y -= spead * Gdx.graphics.getDeltaTime();
                if (raindrop.y + 64 < 0) {
                    spead ++;
                    soruse ++;

                    iter.remove();
                }
                if (raindrop.overlaps(bucket)) {
                    soruse --;
                    lifes --;


                    iter.remove();
                }
            }
        }else {

            if (Gdx.input.justTouched()) {

                    soruse = 0;
                    spead = 200;
                    lifes = INT;


            }
        }
    }

    @Override
    public void dispose() {
        // высвобождение всех нативных ресурсов
        heroRip.dispose();
        bombe.dispose();
        hero.dispose();
        batch.dispose();
    }

    @Override
    public void show() {

    }



    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    private void showSpedAndPointsLifes() {
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font.getData().setScale(2.0F);
        font.draw(batch, String.format("\t Lifes:%s \n Points: %s " + "\n" + "Speed: %s", lifes,soruse, spead), VIEWPORT_WIDTH / 4, VIEWPORT_HEIGHT / 2);
    }


    private void showGameOver() {
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font.getData().setScale(2.0F);
        font.draw(batch, String.format("The birdie died: \n Points: %s " + "\n" + "Speed: %s \n Touch the screen for the new game ", soruse, spead), VIEWPORT_WIDTH / 4, VIEWPORT_HEIGHT / 2);
    }

    private static boolean handleimage(Rectangle raindrop, float touchX, float touchY) {

        // Проверяем, находятся ли координаты касания экрана
        if ((touchX >= raindrop.x) && touchX <= (raindrop.x + raindrop.width) && (touchY >= raindrop.y) && touchY <= (raindrop.y + raindrop.height)) {


            return true;
        }
        return false;
    }
}