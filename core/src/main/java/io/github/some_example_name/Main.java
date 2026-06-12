package io.github.some_example_name;

import com.badlogic.gdx.Game;
import io.github.some_example_name.screens.MainMenuScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    
    @Override
    public void create() {
        // Mengatur screen pertama yang akan muncul saat game dibuka
        // 'this' dikirimkan agar class Screen lain bisa mengakses method setScreen() untuk pindah halaman
        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        // SANGAT PENTING: super.render() wajib dipanggil.
        // Fungsinya untuk mendelegasikan proses render ke Screen yang sedang aktif (misal: MainMenuScreen).
        super.render(); 
    }
    
    @Override
    public void dispose() {
        super.dispose();
    }
}
