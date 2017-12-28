package com.aguinaga.armando.proyecto_1.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.aguinaga.armando.proyecto_1.adapters.BoardAdapter;
import com.aguinaga.armando.proyecto_1.models.Board;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements RealmChangeListener<RealmResults<Board>>, AdapterView.OnItemClickListener {

    private Realm realm;
    private FloatingActionButton fab;
    private ListView listView;
    private BoardAdapter adapter;
    private RealmResults<Board> boards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //DB REALM
        Realm.init(getApplicationContext());
        realm = Realm.getDefaultInstance();
        boards = realm.where(Board.class).findAll();
        boards.addChangeListener(this);

        adapter = new BoardAdapter(this, boards, R.layout.list_view_board_item);
        listView = findViewById(R.id.listViewBoard);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        fab = findViewById(R.id.fabAddBoard);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertForeCreatingBoard("Agregar nueva pizarra", "Escribe un nombre para tu nueva pizarra");
            }
        });

        //borrando la BD cuando inicia
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();

        registerForContextMenu(listView);
    }
    //CRUD Actions
    private void createNewBoard(String boardName){
        realm.beginTransaction();
        Board board = new Board(boardName);
        realm.copyToRealm(board);
        realm.commitTransaction();
    }

    private void editBoard(String newName, Board board){
        realm.beginTransaction();
        board.setTitle(newName);
        realm.copyToRealmOrUpdate(board);
        realm.commitTransaction();
    }

    private void deleteBoard(Board board){
        realm.beginTransaction();
        board.deleteFromRealm();
        realm.commitTransaction();
    }
    private void deleteAllBoard (){
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
    }
    // ** DIALOGS **
    private void showAlertForeCreatingBoard(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_create_board,null);
        builder.setView(viewInflated);

        final EditText input = viewInflated.findViewById(R.id.editTextNewBoard);

        builder.setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
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
    private void showAlertForeEditingBoard(String title, String message, final Board board){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_create_board,null);
        builder.setView(viewInflated);

        final EditText input = (EditText) viewInflated.findViewById(R.id.editTextNewBoard);
        input.setText(board.getTitle());
        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String boardName = input.getText().toString().trim();
                if (boardName.length() == 0){
                    Toast.makeText(MainActivity.this, "Es requerido el nombre para editar.", Toast.LENGTH_LONG).show();
                }else if(boardName.equals(board.getTitle())){
                    Toast.makeText(MainActivity.this, "El nombre es igual que el anterior.", Toast.LENGTH_LONG).show();
                }else{
                    editBoard(boardName,board);
                }
            }
        });
        builder.create().show();

    }

    // ** EVENTOS**
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_board_activity,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_all:
                deleteAllBoard();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(boards.get(info.position).getTitle());
        getMenuInflater().inflate(R.menu.context_menu_board_activity, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()){
            case R.id.delete_board:
                deleteBoard(boards.get(info.position));
                return true;
            case R.id.edit_board:
                showAlertForeEditingBoard("Editar pizarra", "Cambiar el título", boards.get(info.position));
                return true;
            default:
                return super.onContextItemSelected(item);

        }
    }

    @Override
    public void onChange(RealmResults<Board> boards) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent(MainActivity.this, NoteActivity.class);
        intent.putExtra("id", boards.get(position).getId());
        startActivity(intent);
    }
}
