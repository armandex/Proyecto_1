package com.aguinaga.armando.proyecto_1.models;

import com.aguinaga.armando.proyecto_1.app.MyApplication;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by arman on 13/12/2017.
 */

public class Board extends RealmObject{

    @PrimaryKey
    private int id;
    @Required
    private String title;
    @Required
    private Date createDate;

    private RealmList<Note> notes;

    public Board (){

    }

    public Board(String title){
        this.id = MyApplication.BoardID.incrementAndGet();
        this.title = title;
        this.createDate = new Date();
        this.notes = new RealmList<Note>();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public RealmList<Note> getNotes() {
        return notes;
    }
}
