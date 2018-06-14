package com.ineedserv.medical.Clases;

import java.io.Serializable;

/**
 * Created by andrade on 05-07-17.
 * clase que maneja las solictudes elaboradas por el usuario
 */

public class Solicitudes implements Serializable {
    String nombreDr;
    String nombrePcte;
    String distancia;
    String servicio;
    double latOfertante;
    double lonOfertante;
    String telefonoDr;
    String idDr;
    String idPcte;
    String fechaSolicitud;
    String fechaAceptacion;
    String fechaConfirmacion;
    String horaCita;
    String direccion;
    String estado;
    String costo;
    String experiencia;

    public Solicitudes() {
    }

    public Solicitudes(String nombreDr, String nombrePcte, String distancia, String servicio, double latOfertante, double lonOfertante, String telefonoDr, String idDr, String idPcte, String fechaSolicitud, String fechaAceptacion, String fechaConfirmacion, String horaCita, String direccion, String estado, String costo, String experiencia) {
        this.nombreDr = nombreDr;
        this.nombrePcte = nombrePcte;
        this.distancia = distancia;
        this.servicio = servicio;
        this.latOfertante = latOfertante;
        this.lonOfertante = lonOfertante;
        this.telefonoDr = telefonoDr;
        this.idDr = idDr;
        this.idPcte = idPcte;
        this.fechaSolicitud = fechaSolicitud;
        this.fechaAceptacion = fechaAceptacion;
        this.fechaConfirmacion = fechaConfirmacion;
        this.horaCita = horaCita;
        this.direccion = direccion;
        this.estado = estado;
        this.costo = costo;
        this.experiencia = experiencia;

    }

    public String getNombreDr() {
        return nombreDr;
    }

    public void setNombreDr(String nombreDr) {
        this.nombreDr = nombreDr;
    }

    public String getNombrePcte() {
        return nombrePcte;
    }

    public void setNombrePcte(String nombrePcte) {
        this.nombrePcte = nombrePcte;
    }

    public String getDistancia() {
        return distancia;
    }

    public void setDistancia(String distancia) {
        this.distancia = distancia;
    }

    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }

    public double getLatOfertante() {
        return latOfertante;
    }

    public void setLatOfertante(double latOfertante) {
        this.latOfertante = latOfertante;
    }

    public double getLonOfertante() {
        return lonOfertante;
    }

    public void setLonOfertante(double lonOfertante) {
        this.lonOfertante = lonOfertante;
    }

    public String getTelefonoDr() {
        return telefonoDr;
    }

    public void setTelefonoDr(String telefonoDr) {
        this.telefonoDr = telefonoDr;
    }

    public String getIdDr() {
        return idDr;
    }

    public void setIdDr(String idDr) {
        this.idDr = idDr;
    }

    public String getIdPcte() {
        return idPcte;
    }

    public void setIdPcte(String idPcte) {
        this.idPcte = idPcte;
    }

    public String getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(String fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public String getFechaAceptacion() {
        return fechaAceptacion;
    }

    public void setFechaAceptacion(String fechaAceptacion) {
        this.fechaAceptacion = fechaAceptacion;
    }

    public String getFechaConfirmacion() {
        return fechaConfirmacion;
    }

    public void setFechaConfirmacion(String fechaConfirmacion) {
        this.fechaConfirmacion = fechaConfirmacion;
    }

    public String getHoraCita() {
        return horaCita;
    }

    public void setHoraCita(String horaCita) {
        this.horaCita = horaCita;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCosto() {return costo;}

    public void setCosto(String costo) {this.costo = costo;}

    public String getExperiencia() {return experiencia;}

    public void setExperiencia(String experiencia) {this.experiencia = experiencia;}
}