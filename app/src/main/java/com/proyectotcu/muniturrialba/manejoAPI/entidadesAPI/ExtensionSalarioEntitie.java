package com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExtensionSalarioEntitie {
    @SerializedName("nombre")
    @Expose
    private String nombre;

    @SerializedName("apellido_1")
    @Expose
    private String apellido_1;

    @SerializedName("apellido_2")
    @Expose
    private String apellido_2;

    @SerializedName("edad")
    @Expose
    private Integer edad;

    @SerializedName("cedula")
    @Expose
    private String cedula;

    @SerializedName("departamento")
    @Expose
    private String departamento;

    @SerializedName("fecha_Entrega")
    @Expose
    private String fecha_Entrega;

    @SerializedName("salario")
    @Expose
    private Double salario;

    @SerializedName("descripcion")
    @Expose
    private String descripcion;


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

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
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

    public String getFechaEntrega() {
        return fecha_Entrega;
    }

    public void setFechaEntrega(String fechaEntrega) {
        this.fecha_Entrega = fechaEntrega;
    }

    public Double getSalario() {
        return salario;
    }

    public void setSalario(Double salario) {
        this.salario = salario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
