package com.aguinaga.armando.proyecto_1;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.aguinaga.armando.proyecto_1.models.Board;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    private Realm realm;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //DB REALM

        Realm.init(getApplicationContext());
        realm = Realm.getDefaultInstance();

        fab = findViewById(R.id.fabAddBoard);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showAlertForeCreatingBoard("title", "message");
                showAlertForeCreatingBoard("Agregar nueva pizarra", "Escribe un nombre para tu nueva pizarra");
            }
        });
    }
    //CRUD
    private void createNewBoard(String boardName){
        realm.beginTransaction();
        Board board = new Board(boardName);
        realm.copyToRealm(board);
        realm.commitTransaction();
    }
    //DIALOGS
    private void showAlertForeCreatingBoard(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_create_board,null);
        builder.setView(viewInflated);

        final EditText input = (EditText) viewInflated.findViewById(R.id.editTextNewBoard);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String boardName = input.getText().toString().trim();
                if (boardName.length() > 0){
                    createNewBoard(boardName);
                }else{
                    Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.create().show();

    }


}
