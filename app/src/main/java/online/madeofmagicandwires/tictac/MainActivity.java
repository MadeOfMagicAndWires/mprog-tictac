package online.madeofmagicandwires.tictac;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;

public class MainActivity extends AppCompatActivity {

    GameAndroid game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        game = new GameAndroid(3, this);
        game.drawBoard((GridLayout) findViewById(R.id.gameBoard));
        findViewById(R.id.resetBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.resetBoard((GridLayout) findViewById(R.id.gameBoard));
            }
        });

    }
}
