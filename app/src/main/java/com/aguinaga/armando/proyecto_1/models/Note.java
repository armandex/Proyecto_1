package com.aguinaga.armando.proyecto_1.models;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by arman on 13/12/2017.
 */

public class Note extends RealmObject {

    @PrimaryKey
    private int id;
    @Required
    private String descripcion;
    @Required
    private Date createDate;

    public Note (){

    }

    public Note (String descripcion){
        this.id = 0;
        this.descripcion = descripcion;
        this.createDate = new Date();
    }

    public int getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getCreateDate() {
        return createDate;
    }
}
