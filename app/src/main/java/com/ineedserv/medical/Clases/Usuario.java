package com.ineedserv.medical.Clases;

import java.io.Serializable;

/**
 * Created by andrade on 05-07-17.
 * clase que maneja los datos del usuario conectado
 */

public class Usuario implements Serializable {
    String IdOfertante;
    String Pais;
    String Instancia;
    String Correo;
    String TipoUsuario;
    String Estado;
    String Nombre;
    String Contrasenia;
    String FbUid;

    public Usuario() {
    }

    public Usuario(String idOfertante,String pais, String instancia, String correo, String tipoUsuario, String estado, String nombre, String contrasenia, String fbUid) {
        IdOfertante = idOfertante;
        Pais = pais;
        Instancia = instancia;
        Correo = correo;
        TipoUsuario = tipoUsuario;
        Estado = estado;
        Nombre = nombre;
        Contrasenia = contrasenia;
        FbUid = fbUid;
    }

    public String getIdOfertante() {return IdOfertante;}

    public void setIdOfertante(String idOfertante) {IdOfertante = idOfertante;}

    public String getPais() {
        return Pais;
    }

    public void setPais(String pais) {
        Pais = pais;
    }

    public String getInstancia() {
        return Instancia;
    }

    public void setInstancia(String instancia) {
        Instancia = instancia;
    }

    public String getCorreo() {
        return Correo;
    }

    public void setCorreo(String correo) {
        Correo = correo;
    }

    public String getTipoUsuario() {
        return TipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        TipoUsuario = tipoUsuario;
    }

    public String getEstado() {
        return Estado;
    }

    public void setEstado(String estado) {
        Estado = estado;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getContrasenia() {
        return Contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        Contrasenia = contrasenia;
    }

    public String getFbUid() {
        return FbUid;
    }

    public void setFbUid(String fbUid) {
        FbUid = fbUid;
    }
}
