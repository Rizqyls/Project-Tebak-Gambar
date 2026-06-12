package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import io.github.some_example_name.Main;

public class GameOverScreen implements Screen {
    private final Main game;
    private Stage stage;
    private Skin skin;

    public GameOverScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ExtendViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);

        // Init Skin Sederhana
        skin = new Skin();
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));
        skin.add("default", new BitmapFont());

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.up = skin.newDrawable("white", Color.valueOf("#1e88e5"));
        btnStyle.font = skin.getFont("default");
        skin.add("default", btnStyle);

        Label.LabelStyle lblStyle = new Label.LabelStyle();
        lblStyle.font = skin.getFont("default");
        skin.add("default", lblStyle);

        // Layout
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label winLabel = new Label("SELAMAT! KAMU MENANG!", skin);
        winLabel.setFontScale(3f);
        winLabel.setColor(Color.GOLD);

        Label scoreLabel = new Label("Semua hewan berhasil ditebak! (120 Poin)", skin);
        scoreLabel.setFontScale(1.5f);

        TextButton backButton = new TextButton("KEMBALI KE MENU UTAMA", skin);

        table.add(winLabel).padBottom(20).row();
        table.add(scoreLabel).padBottom(50).row();
        table.add(backButton).width(300).height(50);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.12f, 0.16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); skin.dispose(); }
}