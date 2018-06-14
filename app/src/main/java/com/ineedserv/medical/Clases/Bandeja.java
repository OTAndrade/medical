package com.ineedserv.medical.Clases;

import java.io.Serializable;

/**
 * Created by andrade on 05-07-17.
 * esta clase maneja las solicitudes para el Medico
 */

public class Bandeja implements Serializable {
    String nombreDr;
    String nombrePcte;
    String distancia;
    String servicio;
    double latSolicitante;
    double lonSolicitante;
    String telefonoPcte;
    String idDr;
    String idPcte;
    String fechaSolicitud;
    String fechaAceptacion;
    String fechaConfirmacion;
    String horaCita;
    String estado;

    public Bandeja() {
    }

    public Bandeja(String nombreDr, String nombrePcte, String distancia, String servicio, double latSolicitante, double lonSolicitante, String telefonoPcte, String idDr, String idPcte, String fechaSolicitud, String fechaAceptacion, String fechaConfirmacion, String horaCita, String estado) {
        this.nombreDr = nombreDr;
        this.nombrePcte = nombrePcte;
        this.distancia = distancia;
        this.servicio = servicio;
        this.latSolicitante = latSolicitante;
        this.lonSolicitante = lonSolicitante;
        this.telefonoPcte = telefonoPcte;
        this.idDr = idDr;
        this.idPcte = idPcte;
        this.fechaSolicitud = fechaSolicitud;
        this.fechaAceptacion = fechaAceptacion;
        this.fechaConfirmacion = fechaConfirmacion;
        this.horaCita = horaCita;
        this.estado = estado;
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

    public double getLatSolicitante() {
        return latSolicitante;
    }

    public void setLatSolicitante(double latSolicitante) {
        this.latSolicitante = latSolicitante;
    }

    public double getLonSolicitante() {
        return lonSolicitante;
    }

    public void setLonSolicitante(double lonSolicitante) {
        this.lonSolicitante = lonSolicitante;
    }

    public String getTelefonoPcte() {
        return telefonoPcte;
    }

    public void setTelefonoPcte(String telefonoPcte) {
        this.telefonoPcte = telefonoPcte;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
