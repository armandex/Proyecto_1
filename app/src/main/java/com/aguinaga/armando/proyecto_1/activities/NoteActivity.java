package com.aguinaga.armando.proyecto_1.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.aguinaga.armando.proyecto_1.R;
import com.aguinaga.armando.proyecto_1.adapters.NoteAdapter;
import com.aguinaga.armando.proyecto_1.models.Board;
import com.aguinaga.armando.proyecto_1.models.Note;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmResults;

/**
 * Created by arman on 27/12/2017.
 */

public class NoteActivity  extends AppCompatActivity implements RealmChangeListener<RealmResults<Board>> {


    private Realm realm;
    private FloatingActionButton fab;
    private ListView listView;
    private NoteAdapter adapter;
    private RealmList<Note> notes;

    private int boardId;
    private Board board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        //DB REALM
        Realm.init(getApplicationContext());
        realm = Realm.getDefaultInstance();

        if (getIntent().getExtras() != null){
            boardId = getIntent().getExtras().getInt("id");

            board = realm.where(Board.class).equalTo("id", boardId).findFirst();
            board.addChangeListener(new RealmChangeListener<RealmModel>() {
                @Override
                public void onChange(RealmModel realmModel) {
                    adapter.notifyDataSetChanged();
                }
            });
            notes = board.getNotes();

            this.setTitle(board.getTitle());

            fab = findViewById(R.id.fabAddNote);
            listView = findViewById(R.id.listViewNote);
            adapter = new NoteAdapter(this, notes, R.layout.list_view_note_item);
            listView.setAdapter(adapter);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAlertForeCreatingNote("Ingresar nueva nota:", "Escribe una nota para "+board.getTitle()+ " .");
                }
            });
        }


    }
    //CRUD
    private void createNewNote(String note){
        realm.beginTransaction();
        Note _note = new Note(note);
        realm.copyToRealm(_note);
        board.getNotes().add(_note);
        realm.commitTransaction();
    }

    //DIALOGS
    private void showAlertForeCreatingNote(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_create_note,null);
        builder.setView(viewInflated);

        final EditText input = (EditText) viewInflated.findViewById(R.id.editTextNewNote);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String note = input.getText().toString().trim();
                if (note.length() > 0){
                    createNewNote(note);
                }else{
                    Toast.makeText(NoteActivity.this, "La nota no puede estar en blanco", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.create().show();
    }

    @Override
    public void onChange(RealmResults<Board> boards) {
        adapter.notifyDataSetChanged();
    }
}
