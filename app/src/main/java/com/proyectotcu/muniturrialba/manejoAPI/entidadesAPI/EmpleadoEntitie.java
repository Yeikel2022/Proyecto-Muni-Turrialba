package com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EmpleadoEntitie {
    @SerializedName("activo")
    @Expose
    private Boolean activo;
    @SerializedName("departamento")
    @Expose
    private String departamento;


    /* Este constructor parametrizado, nos ayudara -
     * a realizar aquellos procesos donde se necesita -
     * enviar, actualizar o eliminar información de los -
     * usuarios. Además de indicarle al sistema, donde -
     * debe guardar la información respectivamente. */
    public EmpleadoEntitie(boolean Activo, String Departamento) {
        this.activo = Activo;
        this.departamento = Departamento;
    }


    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

}
