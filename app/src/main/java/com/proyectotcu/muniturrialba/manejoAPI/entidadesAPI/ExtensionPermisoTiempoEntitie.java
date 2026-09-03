package com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExtensionPermisoTiempoEntitie {
    @SerializedName("nombre")
    @Expose
    private String nombre;

    @SerializedName("apellido_1")
    @Expose
    private String apellido_1;

    @SerializedName("apellido_2")
    @Expose
    private String apellido_2;

    @SerializedName("cedula")
    @Expose
    private String cedula;

    @SerializedName("tipo_Permiso")
    @Expose
    private String tipo_Permiso;

    @SerializedName("departamento")
    @Expose
    private String departamento;

    @SerializedName("descripcion")
    @Expose
    private String descripcion;

    @SerializedName("fecha_Asignacion")
    @Expose
    private String fecha_Asignacion;

    @SerializedName("fecha_Finalizacion")
    @Expose
    private String fecha_Finalizacion;

    @SerializedName("estado_Permiso")
    @Expose
    private Boolean estado_Permiso;

    @SerializedName("id_Empleado")
    @Expose
    private Integer id_Empleado;


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public String getApellido_1() {
        return apellido_1;
    }

    public void setApellido_1(String apellido1) {
        this.apellido_1 = apellido1;
    }

    public String getApellido_2() {
        return apellido_2;
    }

    public void setApellido_2(String apellido2) {
        this.apellido_2 = apellido2;
    }


    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }


    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
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


    public Boolean getEstadoPermiso() {
        return estado_Permiso;
    }

    public void setEstadoPermiso(Boolean estadoPermiso) {
        this.estado_Permiso = estadoPermiso;
    }


    public Integer getIdEmpleado() {
        return id_Empleado;
    }

    public void setIdEmpleado(Integer idEmpleado) {
        this.id_Empleado = idEmpleado;
    }
}
