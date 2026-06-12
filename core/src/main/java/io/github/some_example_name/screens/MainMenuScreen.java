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
import com.badlogic.gdx.scenes.scene2d.ui.Image; // Import baru
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.some_example_name.Main;

public class MainMenuScreen implements Screen {
    private final Main game;
    private Stage stage;
    private Skin skin;
    private Texture backgroundTexture; // Variabel baru untuk menampung tekstur gambar

    public MainMenuScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        // 1. Inisialisasi Stage
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // --- Tambahkan Bagian Background Di Sini ---
        // 2. Muat Tekstur Background. PERHATIKAN: Nama file harus pas, termasuk spasinya.
        backgroundTexture = new Texture(Gdx.files.internal("background menu.png"));
        
        // Buat objek Image Scene2D dari tekstur tersebut
        Image backgroundImage = new Image(backgroundTexture);
        
        // Set agar gambar memenuhi seluruh layar Stage
        backgroundImage.setFillParent(true);
        
        // Tambahkan gambar ke Stage paling pertama (agar jadi layer paling belakang)
        stage.addActor(backgroundImage);
        // ------------------------------------------

        // 3. Buat Skin Programmatif (Tetap pertahankan ini agar tombol tidak crash)
        skin = new Skin();
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));
        pixmap.dispose();

        BitmapFont defaultFont = new BitmapFont();
        skin.add("default", defaultFont);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        // Gunakan warna semi-transparan untuk tombol agar background masih sedikit kelihatan
        textButtonStyle.up = skin.newDrawable("white", new Color(0.18f, 0.2f, 0.25f, 0.8f)); // Warna normal + 80% alpha
        textButtonStyle.over = skin.newDrawable("white", new Color(0.25f, 0.27f, 0.35f, 0.9f)); // Warna hover + 90% alpha
        textButtonStyle.down = skin.newDrawable("white", new Color(0.12f, 0.53f, 0.9f, 1f));   // Warna diklik (biru)
        textButtonStyle.font = skin.getFont("default");
        skin.add("default", textButtonStyle);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default");
        // Beri warna teks judul agar kontras dengan background baru (misal: Kuning Emas)
        labelStyle.fontColor = Color.valueOf("#ffd700"); 
        skin.add("default", labelStyle);

        // 4. Buat layouting Table untuk Tombol
        Table table = new Table();
        table.setFillParent(true); 
        stage.addActor(table); // Table akan digambar *di atas* backgroundImage

        // 5. Buat komponen UI
        Label titleLabel = new Label("TEBAK GAMBAR HEWAN", skin);
        titleLabel.setFontScale(2.5f); 
        
        TextButton playButton = new TextButton("PLAY GAME", skin);
        TextButton exitButton = new TextButton("EXIT", skin);

        // 6. Susun komponen ke dalam Table
        table.add(titleLabel).padBottom(60).row(); 
        table.add(playButton).fillX().uniformX().width(250).height(45).padBottom(15).row(); 
        table.add(exitButton).fillX().uniformX().width(250).height(45);

        // 7. Beri Logika Klik Listener
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
                dispose();
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void render(float delta) {
        // Clear screen warna latar belakang (sekarang tertutup background menu.png)
        Gdx.gl.glClearColor(0.11f, 0.12f, 0.16f, 1f); 
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Menggambar Stage (Gambar Background -> Table Tombol)
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Update viewport agar background dan tombol tetap proporsional saat window diubah
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        // Wajib bersihkan texture background untuk hemat memori
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        
        stage.dispose();
        skin.dispose();
    }
}