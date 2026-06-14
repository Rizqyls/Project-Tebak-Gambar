package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
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
    private final int levelAktif;
    private Stage stage;
    private Skin skin;
    private int totalPoin;
    private Label poinLabel;
    private Array<SoalHewan> semuaSoal;
    private Array<SoalHewan> daftarSoal;

    public GameScreen(Main game) {
        this(game, 1, 0);
    }

    public GameScreen(Main game, int levelAktif) {
        this(game, levelAktif, 0);
    }

    public GameScreen(Main game, int levelAktif, int totalPoinAwal) {
        this.game = game;
        this.levelAktif = levelAktif;
        this.totalPoin = totalPoinAwal;
    }

    @Override
    public void show() {
        OrthographicCamera camera = new OrthographicCamera();
        ExtendViewport viewport = new ExtendViewport(1280, 720, camera);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        initSkin();
        initDataSoal();
        daftarSoal = ambilSoalUntukLevel(levelAktif);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        Table gridTable = new Table();
        
        int kolomCount = 0;
        for (final SoalHewan soal : daftarSoal) {
            Table kotakSoal = new Table();
            kotakSoal.setBackground(skin.newDrawable("white", Color.valueOf("#2d323f")));
            kotakSoal.pad(15); 

            Texture imgTexture = new Texture(Gdx.files.internal(soal.namaFileGambar)); 
            Image img = new Image(imgTexture);
            
            final TextButton btnMasuk = new TextButton("masuk", skin);

            kotakSoal.add(img).width(140).height(140).padBottom(15).row();
            kotakSoal.add(btnMasuk).width(110).height(35);

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

        poinLabel = new Label("Level " + levelAktif + " | Setiap satu gambar dijawab diberi 10 poin. Total Poin: " + totalPoin, skin);
        poinLabel.setFontScale(2.2f); 

        mainTable.add(gridTable).expand().fill().pad(30).row();
        mainTable.add(poinLabel).padBottom(40);
    }

    private void tampilkanPopUpTebak(final SoalHewan soal, final TextButton btnGridAsal) {
        final Dialog popUpDialog = new Dialog("", skin) {
            @Override
            protected void result(Object object) {
            }
        };

        popUpDialog.setBackground(skin.newDrawable("white", Color.valueOf("#1a1c22")));
        popUpDialog.pad(25);

        Image imgBesar = new Image(new Texture(Gdx.files.internal(soal.namaFileGambar)));
        Label labelPetunjuk = new Label("Hewan apakah ini?", skin);
        labelPetunjuk.setFontScale(1.8f);

        final TextField inputTebakan = new TextField("", skin);
        inputTebakan.setMessageText("Ketik jawaban di sini...");

        final Label labelSalah = new Label("", skin);
        labelSalah.setColor(Color.SCARLET);

        TextButton btnCek = new TextButton("Cek", skin);
        TextButton btnBatal = new TextButton("Batal", skin);

        popUpDialog.getContentTable().add(imgBesar).width(280).height(280).padBottom(15).row();
        popUpDialog.getContentTable().add(labelPetunjuk).padBottom(10).row();
        popUpDialog.getContentTable().add(inputTebakan).width(260).height(40).padBottom(15).row();
        popUpDialog.getContentTable().add(labelSalah).padBottom(10).row();

        Table tombolTable = new Table();
        tombolTable.add(btnCek).width(120).height(40).padRight(10);
        tombolTable.add(btnBatal).width(100).height(40);
        popUpDialog.getButtonTable().add(tombolTable);

        final Runnable cekJawaban = new Runnable() {
            @Override
            public void run() {
                String jawaban = inputTebakan.getText().trim();

                if (jawaban.equalsIgnoreCase(soal.kunciJawaban)) {
                    soal.isTerjawab = true;
                    totalPoin += 10;
                    poinLabel.setText("Level " + levelAktif + " | Setiap satu gambar dijawab diberi 10 poin. Total Poin: " + totalPoin);

                    btnGridAsal.setText("Ok!");
                    btnGridAsal.setDisabled(true);

                    popUpDialog.hide();

                    if (levelSelesai()) {
                        if (levelAktif < 3) {
                            game.setScreen(new GameScreen(game, levelAktif + 1, totalPoin));
                        } else {
                            game.setScreen(new GameOverScreen(game));
                        }

                        dispose();
                    }
                } else {
                    inputTebakan.setText("");
                    labelSalah.setText("Jawaban salah, coba lagi.");
                    inputTebakan.setMessageText("Ketik jawaban di sini...");
                    stage.setKeyboardFocus(inputTebakan);
                }
            }
        };

        btnCek.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                cekJawaban.run();
            }
        });

        inputTebakan.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER || keycode == Input.Keys.NUMPAD_ENTER) {
                    cekJawaban.run();
                    return true;
                }
                return false;
            }
        });

        btnBatal.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                popUpDialog.hide();
            }
        });

        popUpDialog.show(stage);
        stage.setKeyboardFocus(inputTebakan);
    }

    private void initDataSoal() {
        semuaSoal = new Array<>();
        semuaSoal.add(new SoalHewan(1, "pinguin.png", "penguin"));
        semuaSoal.add(new SoalHewan(2, "beruang.png", "bear"));
        semuaSoal.add(new SoalHewan(3, "monyet.png", "monkey"));
        semuaSoal.add(new SoalHewan(4, "gajah.png", "elephant"));
        semuaSoal.add(new SoalHewan(5, "singa.png", "lion"));
        semuaSoal.add(new SoalHewan(6, "harimau.png", "tiger"));
        semuaSoal.add(new SoalHewan(7, "jerapah.png", "giraffe"));
        semuaSoal.add(new SoalHewan(8, "kucing.png", "cat")); 
        semuaSoal.add(new SoalHewan(9, "anjing.png", "dog"));
        semuaSoal.add(new SoalHewan(10, "ayam.png", "chicken"));
        semuaSoal.add(new SoalHewan(11, "bebek.png", "duck"));
        semuaSoal.add(new SoalHewan(12, "burung.png", "bird"));
    }

    private Array<SoalHewan> ambilSoalUntukLevel(int level) {
        int soalPerLevel = level + 2;
        int startIndex = 0;

        for (int i = 1; i < level; i++) {
            startIndex += i + 2;
        }

        Array<SoalHewan> soalLevel = new Array<>();
        for (int i = startIndex; i < startIndex + soalPerLevel && i < semuaSoal.size; i++) {
            soalLevel.add(semuaSoal.get(i));
        }

        return soalLevel;
    }

    private boolean levelSelesai() {
        for (SoalHewan soal : daftarSoal) {
            if (!soal.isTerjawab) {
                return false;
            }
        }

        return true;
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

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = skin.newDrawable("white", Color.valueOf("#1e88e5"));
        buttonStyle.font = skin.getFont("default");
        skin.add("default", buttonStyle);

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.background = skin.newDrawable("white", Color.LIGHT_GRAY);
        textFieldStyle.font = skin.getFont("default");
        textFieldStyle.fontColor = Color.BLACK;
        skin.add("default", textFieldStyle);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default");
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        // --- TAMBAHKAN BAGIAN INI UNTUK MENYEMBUHKAN ERROR DIALOG ---
        com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle windowStyle = new com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle();
        windowStyle.background = skin.newDrawable("white", Color.valueOf("#1a1c22")); 
        windowStyle.titleFont = skin.getFont("default");
        windowStyle.titleFontColor = Color.WHITE;
        skin.add("default", windowStyle);
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