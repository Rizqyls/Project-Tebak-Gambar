package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import io.github.some_example_name.Main;

public class GameScreen implements Screen {
    private final Main game;
    private Stage stage;
    private Skin skin;
    private int totalPoin = 0;
    private Label poinLabel;
    private Array<SoalHewan> daftarSoal;

    public GameScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        // 1. Inisialisasi Kamera dan Viewport (Auto-scaling 1280x720)
        OrthographicCamera camera = new OrthographicCamera();
        ExtendViewport viewport = new ExtendViewport(1280, 720, camera);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        initSkin();
        initDataSoal();

        // 2. Table Utama
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        // 3. Grid Table (2 Baris x 6 Kolom)
        Table gridTable = new Table();
        
        int kolomCount = 0;
        for (final SoalHewan soal : daftarSoal) {
            Table kotakSoal = new Table();
            kotakSoal.setBackground(skin.newDrawable("white", Color.valueOf("#2d323f")));
            kotakSoal.pad(15); 

            Texture imgTexture = new Texture(Gdx.files.internal(soal.namaFileGambar)); 
            Image img = new Image(imgTexture);
            
            // Tombol masuk ke mode tebak popup
            final TextButton btnMasuk = new TextButton("masuk", skin);

            // Susun elemen grid
            kotakSoal.add(img).width(140).height(140).padBottom(15).row();
            kotakSoal.add(btnMasuk).width(110).height(35);

            // Listener untuk memicu Pop-up Dialog
            btnMasuk.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (!soal.isTerjawab) {
                        tampilkanPopUpTebak(soal, btnMasuk);
                    }
                }
            });

            gridTable.add(kotakSoal).pad(15);
            kolomCount++;
            
            if (kolomCount == 6) {
                gridTable.row();
            }
        }

        // 4. Label Poin Bawah
        poinLabel = new Label("Setiap satu gambar dijawab diberi 10 poin. Total Poin: 0", skin);
        poinLabel.setFontScale(2.2f); 

        // 5. Susun ke Table Utama
        mainTable.add(gridTable).expand().fill().pad(30).row();
        mainTable.add(poinLabel).padBottom(40);
    }

    /**
     * Fungsi Pop-up Dialog tebakan (Sudah dibersihkan dari typo spasi)
     */
    private void tampilkanPopUpTebak(final SoalHewan soal, final TextButton btnGridAsal) {
        final Dialog popUpDialog = new Dialog("", skin) {
            @Override
            protected void result(Object object) {
                // Sengaja kosong agar tidak auto-close saat klik sembarang
            }
        };

        popUpDialog.setBackground(skin.newDrawable("white", Color.valueOf("#1a1c22")));
        popUpDialog.pad(25);

        // Komponen di dalam pop-up
        Image imgBesar = new Image(new Texture(Gdx.files.internal(soal.namaFileGambar)));
        Label labelPetunjuk = new Label("Hewan apakah ini?", skin);
        labelPetunjuk.setFontScale(1.8f);

        final TextField inputTebakan = new TextField("", skin);
        inputTebakan.setMessageText("Ketik jawaban di sini...");

        TextButton btnCek = new TextButton("Cek", skin);
        TextButton btnBatal = new TextButton("Batal", skin);

        // Susun komponen konten Dialog (Vertikal)
        popUpDialog.getContentTable().add(imgBesar).width(280).height(280).padBottom(15).row();
        popUpDialog.getContentTable().add(labelPetunjuk).padBottom(10).row();
        popUpDialog.getContentTable().add(inputTebakan).width(260).height(40).padBottom(15).row();

        // Susun tombol aksi (Horizontal)
        Table tombolTable = new Table();
        tombolTable.add(btnCek).width(120).height(40).padRight(10);
        tombolTable.add(btnBatal).width(100).height(40);
        popUpDialog.getButtonTable().add(tombolTable);

        // Logika evaluasi jawaban
        btnCek.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String jawaban = inputTebakan.getText().trim();
                
                if (jawaban.equalsIgnoreCase(soal.kunciJawaban)) {
                    soal.isTerjawab = true;
                    totalPoin += 10;
                    poinLabel.setText("Setiap satu gambar dijawab diberi 10 poin. Total Poin: " + totalPoin);
                    
                    // Kunci tombol di grid asal
                    btnGridAsal.setText("Ok!");
                    btnGridAsal.setDisabled(true);
                    
                    popUpDialog.hide(); // Tutup Pop-up

                    // Jika menang (120 poin), pindah halaman selesai
                    if (totalPoin == 120) {
                        game.setScreen(new GameOverScreen(game));
                        dispose();
                    }
                } else {
                    inputTebakan.setText("");
                    inputTebakan.setMessageText("Salah! Coba lagi...");
                }
            }
        });

        // Logika menutup pop-up via tombol Batal
        btnBatal.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                popUpDialog.hide();
            }
        });

        popUpDialog.show(stage);
    }

    private void initDataSoal() {
        daftarSoal = new Array<>();
        daftarSoal.add(new SoalHewan(1, "pinguin.png", "pinguin"));
        daftarSoal.add(new SoalHewan(2, "beruang.png", "beruang"));
        daftarSoal.add(new SoalHewan(3, "monyet.png", "monyet"));
        daftarSoal.add(new SoalHewan(4, "gajah.png", "gajah"));
        daftarSoal.add(new SoalHewan(5, "singa.png", "singa"));
        daftarSoal.add(new SoalHewan(6, "harimau.png", "harimau"));
        daftarSoal.add(new SoalHewan(7, "jerapah.png", "jerapah"));
        daftarSoal.add(new SoalHewan(8, "kucing.png", "kucing")); 
        daftarSoal.add(new SoalHewan(9, "anjing.png", "anjing"));
        daftarSoal.add(new SoalHewan(10, "ayam.png", "ayam"));
        daftarSoal.add(new SoalHewan(11, "bebek.png", "bebek"));
        daftarSoal.add(new SoalHewan(12, "burung.png", "burung"));
    }

    private void initSkin() {
        skin = new Skin();
        
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));
        pixmap.dispose();

        BitmapFont font = new BitmapFont();
        skin.add("default", font);

        // Style Tombol Cek
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = skin.newDrawable("white", Color.valueOf("#1e88e5"));
        buttonStyle.font = skin.getFont("default");
        skin.add("default", buttonStyle);

        // Style Kolom Input Ketik (TextField)
        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.background = skin.newDrawable("white", Color.LIGHT_GRAY);
        textFieldStyle.font = skin.getFont("default");
        textFieldStyle.fontColor = Color.BLACK;
        skin.add("default", textFieldStyle);

        // Style Teks Label Poin
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default");
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        // --- TAMBAHKAN BAGIAN INI UNTUK MENYEMBUHKAN ERROR DIALOG ---
        // Membuat WindowStyle dasar agar Dialog (Pop-up) bisa digambar oleh LibGDX
        com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle windowStyle = new com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle();
        // Menggunakan warna background abu-abu sangat gelap untuk bingkai pop-up
        windowStyle.background = skin.newDrawable("white", Color.valueOf("#1a1c22")); 
        windowStyle.titleFont = skin.getFont("default");
        windowStyle.titleFontColor = Color.WHITE;
        skin.add("default", windowStyle); // Daftarkan dengan nama "default"
        // ------------------------------------------------------------
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.11f, 0.12f, 0.16f, 1f); 
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}