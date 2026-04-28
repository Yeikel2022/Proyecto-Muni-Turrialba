package com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExtensionUsuarioEntitie {
    /* Estas son las variables que nos ayudaran -
     * para la entidad usuario. Además de que -
     * ya están serializadas para el formato: -
     * JSON respectivamente. */
    @SerializedName("correo_Electronico")
    @Expose
    private String correo_Electronico;

    @SerializedName("contraseña")
    @Expose
    private String contraseña;

    @SerializedName("respuesta")
    @Expose
    private Boolean respuesta;

    public void setCorreo_Electronico(String correoElectronico) {
        this.correo_Electronico = correoElectronico;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public Boolean getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(Boolean respuesta) {
        this.respuesta = respuesta;
    }

    /* Este constructor parametrizado, nos ayudara -
     * a realizar aquellos procesos donde se necesita -
     * enviar, actualizar o eliminar información de los -
     * usuarios. Además de indicarle al sistema, donde -
     * debe guardar la información respectivamente. */
    public ExtensionUsuarioEntitie(String correo_Electronico,
                          String contraseña) {
        this.correo_Electronico = correo_Electronico;
        this.contraseña = contraseña;
    }

}
