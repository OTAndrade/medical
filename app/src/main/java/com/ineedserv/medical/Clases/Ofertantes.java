package com.ineedserv.medical.Clases;

import java.io.Serializable;

/**
 * Created by andrade on 05-07-17.
 * Clase para recuperar todos los ofertantes que se encuentren en la region del usuario
 */

public class Ofertantes implements Serializable {

    String Clave;
    String Correo;
    String Costo;
    String DatoServicio;
    String Direccion;
    String Especialidad;
    String Estado;
    String Experiencia;
    String Instancia;
    String Latitud;
    String Longitud;
    String Nombre;
    String NumeroRegistro;
    String Pais;
    String Usuario;

    public Ofertantes() {
    }

    public Ofertantes(String Clave, String Correo, String Costo, String DatoServicio, String Direccion, String Especialidad, String Estado, String Experiencia, String Instancia, String Latitud, String Longitud, String Nombre, String NumeroRegistro, String Pais, String Usuario) {
        Clave = Clave;
        Correo = Correo;
        Costo = Costo;
        DatoServicio = DatoServicio;
        Direccion = Direccion;
        Especialidad = Especialidad;
        Estado = Estado;
        Experiencia = Experiencia;
        Instancia = Instancia;
        Latitud = Latitud;
        Longitud = Longitud;
        Nombre = Nombre;
        NumeroRegistro = NumeroRegistro;
        Pais = Pais;
        Usuario = Usuario;
    }

    public void setClave(String Clave) {
        Clave = Clave;
    }

    public void setCorreo(String Correo) {
        Correo = Correo;
    }

    public void setCosto(String Costo) {
        Costo = Costo;
    }

    public void setDatoservicio(String DatoServicio) {
        DatoServicio = DatoServicio;
    }

    public void setDireccion(String Direccion) {
        Direccion = Direccion;
    }

    public void setEspecialidad(String Especialidad) {
        Especialidad = Especialidad;
    }

    public void setEstado(String Estado) { Estado = Estado;  }

    public void setExperiencia(String Experiencia) {
        Experiencia = Experiencia;
    }

    public void setInstancia(String Instancia) {
        Instancia = Instancia;
    }

    public void setLatitud(String  Latitud) {
        Latitud = Latitud;
    }

    public void setLongitud(String Longitud) {
        Longitud = Longitud;
    }

    public void setNombre(String Nombre) {
        Nombre = Nombre;
    }

    public void setNumeroregistro(String NumeroRegistro) {
        NumeroRegistro = NumeroRegistro;
    }

    public void setPais(String pais) {
        Pais = Pais;
    }

    public void setUsuario(String Usuario) {
        Usuario = Usuario;
    }

    public String getClave() {
        return Clave;
    }

    public String getCorreo() {
        return Correo;
    }

    public String getCosto() {
        return Costo;
    }

    public String getDatoservicio() {
        return DatoServicio;
    }

    public String getDireccion() {
        return Direccion;
    }

    public String getEspecialidad() {
        return Especialidad;
    }

    public String getEstado() {
        return Estado;
    }

    public String getExperiencia() {
        return Experiencia;
    }

    public String getInstancia() {
        return Instancia;
    }

    public String getLatitud() {
        return Latitud;
    }

    public String getLongitud() {
        return Longitud;
    }

    public String getNombre() {
        return Nombre;
    }

    public String getNumeroregistro() {
        return NumeroRegistro;
    }

    public String getPais() {
        return Pais;
    }

    public String getUsuario() {
        return Usuario;
    }
}
