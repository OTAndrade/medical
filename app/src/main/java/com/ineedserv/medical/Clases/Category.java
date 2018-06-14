package com.ineedserv.medical.Clases;

/**
 * Created by andrade on 05-07-17.
 * se utiliza en la lista de solicitudes que se muestra
 */
import android.graphics.drawable.Drawable;


public class Category {

    private String title;
    private int categoryId;
    private String description;
    private Drawable imagen;

    public Category() {
        super();
    }

    public Category(int categoryId, String title, String description, Drawable imagen) {
        super();
        this.title = title;
        this.description = description;
        this.imagen = imagen;
        this.categoryId = categoryId;
    }


    public String getTitle() {
        return title;
    }

    public void setTittle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Drawable getImage() {
        return imagen;
    }

    public void setImagen(Drawable imagen) {
        this.imagen = imagen;
    }

    public int getCategoryId(){return categoryId;}

    public void setCategoryId(int categoryId){this.categoryId = categoryId;}

}
