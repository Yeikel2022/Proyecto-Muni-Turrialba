package com.proyectotcu.muniturrialba.manejoAPI;

import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.UsuarioInterface;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConexionAPI {

    //Variable estatica para indicar al retrofit que es nulo.
    private static Retrofit retrofit = null;

    /* Variable estatica para indicar el URL del API que se va a utilizar.
    * Ademas, se usa el 10.0.2.2 debido a que es el medio por el que -
    * Android Studio (su emulador) se comunica con el API.
    *
    * Nota: Se esta usando HTTP solo para el desarrollo, mas adelante -
    * se cambiara por el HTTPS, esto porque es el protocolo que se usa -
    * en produccion y porque da mas seguridad respectivamente. */
    private static String BASE_URL = "http://10.0.2.2:5114/";


    /* Este metodo sirve para realizar la conexion con -
     * el API respectivamente, esto por medio de Retrofit -
     * y GSON. */
    public static UsuarioInterface Conexion_API() {
        /* Si retrofit es igual a nulo, quiere decir -
         * que no se ha hecho todavia la conexión, por -
         * lo que se realiza el proceso. Caso contrario -
         * no se hace de nuevo la conexion. */
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(UsuarioInterface.class);
    }




}
