package com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FAQEntitie {

    @SerializedName("pregunta")
    @Expose
    private String pregunta;
    @SerializedName("respuesta")
    @Expose
    private String respuesta;
    @SerializedName("tipo_Prioridad")
    @Expose
    private String tipo_Prioridad;

    public FAQEntitie(String pregunta, String respuesta, String tipo_Prioridad) {
        this.pregunta = pregunta;
        this.respuesta = respuesta;
        this.tipo_Prioridad = tipo_Prioridad;
    }



    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public String getTipoPrioridad() {
        return tipo_Prioridad;
    }

    public void setTipoPrioridad(String tipoPrioridad) {
        this.tipo_Prioridad = tipoPrioridad;
    }


}
