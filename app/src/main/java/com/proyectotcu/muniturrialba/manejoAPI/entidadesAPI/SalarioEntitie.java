package com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SalarioEntitie {
    @SerializedName("fecha_Entrega")
    @Expose
    private String fecha_Entrega;
    @SerializedName("salario")
    @Expose
    private Double salario;
    @SerializedName("descripcion")
    @Expose
    private String descripcion;

    /* Este constructor parametrizado, nos ayudara -
     * a realizar aquellos procesos donde se necesita -
     * enviar, actualizar o eliminar información de los -
     * usuarios. Además de indicarle al sistema, donde -
     * debe guardar la información respectivamente. */
    public SalarioEntitie(String fechaEntrega, Double salario, String descripcion) {
        this.fecha_Entrega = fechaEntrega;
        this.salario = salario;
        this.descripcion = descripcion;
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
