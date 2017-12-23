package com.aguinaga.armando.proyecto_1.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import com.aguinaga.armando.proyecto_1.R;
/**
 * Created by arman on 21/12/2017.
 */

public class BoardActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        fab = (FloatingActionButton) findViewById(R.id.fabAddBoard2);
    }
}
