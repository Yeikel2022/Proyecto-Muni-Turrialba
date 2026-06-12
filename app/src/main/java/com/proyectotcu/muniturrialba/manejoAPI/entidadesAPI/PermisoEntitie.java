package com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PermisoEntitie {

    @SerializedName("leer")
    @Expose
    private Boolean leer;
    @SerializedName("crear")
    @Expose
    private Boolean crear;
    @SerializedName("actualizar")
    @Expose
    private Boolean actualizar;
    @SerializedName("eliminar")
    @Expose
    private Boolean eliminar;


    /* Este constructor parametrizado, nos ayudara -
     * a realizar aquellos procesos donde se necesita -
     * enviar, actualizar o eliminar información de los -
     * usuarios. Además de indicarle al sistema, donde -
     * debe guardar la información respectivamente. */
    public PermisoEntitie(boolean Leer, boolean Crear, boolean Actualizar, boolean Eliminar) {
        this.leer = Leer;
        this.crear = Crear;
        this.actualizar = Actualizar;
        this.eliminar = Eliminar;
    }



    public Boolean getLeer() {
        return leer;
    }

    public void setLeer(Boolean leer) {
        this.leer = leer;
    }

    public Boolean getCrear() {
        return crear;
    }

    public void setCrear(Boolean crear) {
        this.crear = crear;
    }

    public Boolean getActualizar() {
        return actualizar;
    }

    public void setActualizar(Boolean actualizar) {
        this.actualizar = actualizar;
    }

    public Boolean getEliminar() {
        return eliminar;
    }

    public void setEliminar(Boolean eliminar) {
        this.eliminar = eliminar;
    }

}
