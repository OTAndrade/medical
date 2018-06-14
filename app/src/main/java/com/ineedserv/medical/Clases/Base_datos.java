package com.ineedserv.medical.Clases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by andrade on 05-07-17.
 * Esta clase maneja los métodos necesarios para manejar una base de datos local (en el dispositivo)
 */
public class Base_datos extends SQLiteOpenHelper {
    public static final String NOMBREBD = "ineedserv.sqlite";
    public static final String direccion ="http://www.ineedserv.com/app/";

    //Versión de la base de datos
    public Base_datos(Context context, int VERSION) {
        super(context, NOMBREBD, null, VERSION);
    }

    //Método utilizado cuando se crea la base de datos.
    public void onCreate(SQLiteDatabase db) {

        /* se crea la tabla que tiene registrado al usuario que se instalo la aplicacion*/
        db.execSQL("create table relacion (id integer," +
                   "urlDr varchar, " +
                   "urlPct varchar );");

        Log.d("Todos los tablas: ", "Se crearon las tablas");
    }

    //Método utilizado cuando se actualiza la base de datos
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists relacion");
        /* se crea la tabla que tiene registrado al usuario que se instalo la aplicacion*/
        db.execSQL("create table relacion (id integer primary key autoincrement not null," +
                "urlDr varchar, " +
                "urlPct varchar);");

        Log.d("Todos los tablas: ", "Se modificaron las tablas");
    }

}