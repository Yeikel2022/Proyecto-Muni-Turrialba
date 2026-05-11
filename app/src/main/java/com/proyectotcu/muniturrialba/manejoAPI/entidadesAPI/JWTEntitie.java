package com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JWTEntitie {
    /* Estas son las variables que nos ayudaran -
     * para la entidad JWT. Además de que ya -
     * están serializadas para el formato: JSON -
     * respectivamente. */
    @SerializedName("tokenAcceso")
    @Expose
    private String TokenAcceso;

    //Constructor vacio.
    public JWTEntitie() {
    }

    //Estos son los métodos GET y SET de las variables anteriores.
    public String getTokenAcceso() {
        return TokenAcceso;
    }

    public void setTokenAcceso(String Token) {
        this.TokenAcceso = Token;
    }

}
