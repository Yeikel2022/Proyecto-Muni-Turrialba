package com.proyectotcu.muniturrialba.manejoAPI;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AutenticacionAPI implements Interceptor {

    //Una variable privada de tipo SharedPreferences.
    private SharedPreferences archivoXML_Compartido;


    /* Este constructor nos sirve para que cuando se -
     * inicie una instancia de esta clase, se pase -
     * por parametro el archivo XML del token.
     *
     * Además porque nos permitira también acceder más facilmente -
     * a la variable: archivoXML_Compartido, desde otros métodos -
     * dentro de esta misma clase respectivamente. */
    public AutenticacionAPI(SharedPreferences archivoXML_Parametrizado) {
        this.archivoXML_Compartido = archivoXML_Parametrizado;
    }


    /* Es un metodo (que ya lo contiene la interfaz: Interceptor) que consiste -
     * en capturar la solicitud HTTP que se le esta haciendo al servidor (o en -
     * este caso el API), y luego, si existe un token de acceso, lo que pasaria -
     * es que creara un header que contendra los aspectos necesarios para poder -
     * autenticarse al servidor. Esto gracias a la variable: "solicitudModificada", -
     * que servira para construir y colocar ese header con el token respectivamente. */
    @NonNull
    @Override
    public Response intercept (@NonNull Chain chain) throws IOException {

        /* Es una variable de texto que servira para poder obtener el token de acceso -
         * que habiamos guardado en el archivo XML llamado: Archivo_Autenticacion, y si -
         * no, entonces la variable guardaria un nulo por defecto. */
        String resultadoToken = archivoXML_Compartido.getString("JWT_token", null);

        /* Esta variable consiste en guardar la solicitud original que se -
         * esta realizando, pero se le da la opción de añadir o modificar -
         * algo a dicha solicitud.
         *
         * NOTA: Esto es gracias al comando: "newBuilder()", ya que la -
         * solicitud original no se puede cambiar de forma directa, de -
         * ahí que se esta haciendo una copia del mismo y se le añade -
         * dicho comando. */
        Request.Builder solicitudModificada = chain.request().newBuilder();


        /* Si en la variable: resultadoToken, resulta ser distinto de nulo -
         * entonces se creara un header con el nombre: Authorization donde -
         * se incluira el Bearer (el cual es necesario) y el token almacenado -
         * para poder autenticarse con el API. */
        if(resultadoToken != null) {
            solicitudModificada.addHeader("Authorization", "Bearer " + resultadoToken);
        }

        //Devuelve la solicitud HTTP:
        return chain.proceed(solicitudModificada.build());
    }

}
