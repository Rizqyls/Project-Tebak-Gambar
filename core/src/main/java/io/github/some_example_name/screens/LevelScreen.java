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

public class LevelScreen implements Screen {
    private final Main game;
    private Stage stage;
    private Skin skin;

    public LevelScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ExtendViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);

        // Init Skin
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

        // Layout Utama
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Tombol Back/Exit di pojok kiri atas
        TextButton backBtn = new TextButton("EXIT", skin);
        table.add(backBtn).expand().top().left().pad(20).width(100).height(40).row();

        // Judul Level
        Label title = new Label("PILIH LEVEL", skin);
        title.setFontScale(2.5f);
        table.add(title).expandX().padBottom(50).row();

        // Container untuk tombol level (Horizontal)
        Table levelTable = new Table();
        for (int i = 1; i <= 3; i++) {
            final int levelNum = i;
            TextButton lvlBtn = new TextButton("Level " + i, skin);

            lvlBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.setScreen(new GameScreen(game, levelNum));
                    dispose();
                }
            });
            
            levelTable.add(lvlBtn).width(150).height(200).pad(15);
        }
        
        table.add(levelTable).center().expandY();

        // Listener Back ke Menu Utama
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.11f, 0.12f, 0.16f, 1f);
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
