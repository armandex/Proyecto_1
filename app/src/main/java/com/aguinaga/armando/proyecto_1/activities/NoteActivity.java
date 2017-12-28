package com.aguinaga.armando.proyecto_1.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
        registerForContextMenu(listView);
    }
    //CRUD
    private void createNewNote(String note){
        realm.beginTransaction();
        Note _note = new Note(note);
        realm.copyToRealm(_note);
        board.getNotes().add(_note);
        realm.commitTransaction();
    }
    private void editNote(String newName, Note note){
        realm.beginTransaction();
        note.setDescripcion(newName);
        realm.copyToRealmOrUpdate(note);
        realm.commitTransaction();
    }
    private void deleteNote(Note note){
        realm.beginTransaction();
        note.deleteFromRealm();
        realm.commitTransaction();
    }
    private void deleteAllNotes(){
        realm.beginTransaction();
        board.getNotes().deleteAllFromRealm();
        realm.commitTransaction();
    }

    // ** DIALOGS **
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
    private void showAlertForeEditingNote(String title, String message, final Note note){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_create_note,null);
        builder.setView(viewInflated);

        final EditText input = (EditText) viewInflated.findViewById(R.id.editTextNewNote);
        input.setText(note.getDescripcion());
        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String noteName = input.getText().toString().trim();
                if (noteName.length() == 0){
                    Toast.makeText(NoteActivity.this, "Es requerido el nombre para editar", Toast.LENGTH_SHORT).show();
                }else if(noteName.equals(note.getDescripcion())){
                    Toast.makeText(NoteActivity.this, "El nombre es igual que es anterior", Toast.LENGTH_LONG).show();
                }else{
                    editNote(noteName, note);
                }
            }
        });
        builder.create().show();
        note.addChangeListener(new RealmChangeListener<RealmModel>() {
            @Override
            public void onChange(RealmModel realmModel) {
                adapter.notifyDataSetChanged();
            }
        });

    }
    // ** EVENTOS **

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_notes_all:
                deleteAllNotes();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        //menu.setHeaderTitle(notes.get(info.position).getDescripcion());
        getMenuInflater().inflate(R.menu.context_menu_note_activity, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()){
            case R.id.delete_note_board:
                deleteNote(notes.get(info.position));
                return true;
            case R.id.edit_note_board:
                showAlertForeEditingNote("Editar nota", "Cambiar la nota", notes.get(info.position));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    @Override
    public void onChange(RealmResults<Board> boards) {
        adapter.notifyDataSetChanged();
    }
}
