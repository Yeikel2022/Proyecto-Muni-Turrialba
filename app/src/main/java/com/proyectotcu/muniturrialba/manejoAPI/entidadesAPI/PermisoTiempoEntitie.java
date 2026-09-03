package com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PermisoTiempoEntitie {
    @SerializedName("tipo_Permiso")
    @Expose
    private String tipo_Permiso;

    @SerializedName("descripcion")
    @Expose
    private String descripcion;

    @SerializedName("fecha_Asignacion")
    @Expose
    private String fecha_Asignacion;

    @SerializedName("fecha_Finalizacion")
    @Expose
    private String fecha_Finalizacion;


    public PermisoTiempoEntitie(String tipo_Permiso, String descripcion, String fecha_Asignacion, String fecha_Finalizacion) {
        this.tipo_Permiso = tipo_Permiso;
        this.descripcion = descripcion;
        this.fecha_Asignacion = fecha_Asignacion;
        this.fecha_Finalizacion = fecha_Finalizacion;
    }


    public String getTipoPermiso() {
        return tipo_Permiso;
    }

    public void setTipoPermiso(String tipoPermiso) {
        this.tipo_Permiso = tipoPermiso;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFechaAsignacion() {
        return fecha_Asignacion;
    }

    public void setFechaAsignacion(String fechaAsignacion) {
        this.fecha_Asignacion = fechaAsignacion;
    }

    public String getFechaFinalizacion() {
        return fecha_Finalizacion;
    }

    public void setFechaFinalizacion(String fechaFinalizacion) {
        this.fecha_Finalizacion = fechaFinalizacion;
    }

}
