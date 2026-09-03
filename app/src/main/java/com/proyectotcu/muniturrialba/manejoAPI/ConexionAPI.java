package com.proyectotcu.muniturrialba.manejoAPI;

import android.content.Context;
import android.content.SharedPreferences;

import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.EmpleadoInterface;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.FAQInterface;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.InicioSesionInterface;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.PermisoInterface;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.PermisoTiempoInterface;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.SalarioInterface;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.UsuarioInterface;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConexionAPI {
    /* NOTA IMPORTANTE:
     * Corregir lo de la conexión para que solo sea uno. */

    //Variable estatica para indicar al retrofit que es nulo.
    private static Retrofit retrofit = null;

    /* Variable estatica para indicar el URL del API que se va a utilizar.
     * Ademas, se usa el 10.0.2.2 debido a que es el medio por el que -
     * Android Studio (su emulador) se comunica con el API.
     *
     * NOTA: Se esta usando HTTP solo para el desarrollo, más adelante -
     * se cambiara por el HTTPS, esto porque es el protocolo que se usa -
     * en produccion y porque da más seguridad respectivamente. */
    private static String BASE_URL = "http://10.0.2.2:5114/";


    /* Este metodo sirve para realizar la conexion con -
     * el API respectivamente, esto por medio de Retrofit -
     * y GSON.
     *
     * NOTA: El Context, es una clase abstracta que permite -
     * acceder a recursos del sistema. En este caso, nos permite -
     * acceder al SharedPreferences que se habia definido previamente, -
     * osea el: "Archivo_Autenticacion". */
    public static UsuarioInterface Conexion_API(Context context) {
        /* Lo que hace este comando es crear una variable de tipo: SharedPreferences, -
         * donde dicha variable estaria accediendo a un archivo XML que tiene como nombre -
         * "Archivo_Autenticacion", y dicho archivo solo podra ser usado por la aplicación -
         * móvil (de ahi el MODE_PRIVATE).
         *
         * NOTA: La razón de esto es porque ese archivo XML nos servira para poder almacenar -
         * el token de acceso que nos brindaria el API, de modo que así, se pueda mantener la -
         * sesión y además, poder acceder a las funciones del API.
         *
         * También hay que recalcar que si no existe dicho archivo, el SharedPreferences lo -
         * creara automaticamente. */
        SharedPreferences archivoXML = context.getSharedPreferences(
                "Archivo_Autenticacion", Context.MODE_PRIVATE);

        /* Lo que hace este comando es crear una variable de tipo OkHttpClient, -
         * que nos ayudara a construir una nueva solicitud (o petición) HTTP del -
         * cliente, en donde esta llevaria consigo el token de acceso que nos dio -
         * el API para que nos podamos autenticar y se pueda realizar las operaciones -
         * de la misma.
         *
         * NOTA: Esto se logra gracias al addInterceptor, el cual es un comando que se -
         * ejecuta antes de la solicitud que vamos a construir, y ya luego este mismo -
         * recibiria la respuesta de lo que pasamos a ese interceptor. Que en este caso, -
         * es una instancia hacia la clase "AutenticacionAPI", donde se le esta pasando -
         * el archivo SharedPreferences que se habia definido antes. El cual contiene -
         * el token de acceso para poder autenticarnos respectivamente. */
        OkHttpClient solicitudCliente = new OkHttpClient.Builder()
                .addInterceptor(new AutenticacionAPI(archivoXML))
                .build();

        /* Si retrofit es igual a nulo, quiere decir que no se ha hecho todavia la conexión, -
         * por lo que se realiza el proceso. Caso contrario no se hace de nuevo la conexion.
         *
         * NOTA: El .client(solicitudCliente) es para indicar que utilice la solicitud HTTP -
         * que uno creo, en este caso: solicitudCliente. */
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(solicitudCliente)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(UsuarioInterface.class);
    }

    public static FAQInterface Conexion_API_FAQ(Context context) {
        /* Lo que hace este comando es crear una variable de tipo: SharedPreferences, -
         * donde dicha variable estaria accediendo a un archivo XML que tiene como nombre -
         * "Archivo_Autenticacion", y dicho archivo solo podra ser usado por la aplicación -
         * móvil (de ahi el MODE_PRIVATE).
         *
         * NOTA: La razón de esto es porque ese archivo XML nos servira para poder almacenar -
         * el token de acceso que nos brindaria el API, de modo que así, se pueda mantener la -
         * sesión y además, poder acceder a las funciones del API.
         *
         * También hay que recalcar que si no existe dicho archivo, el SharedPreferences lo -
         * creara automaticamente. */
        SharedPreferences archivoXML = context.getSharedPreferences(
                "Archivo_Autenticacion", Context.MODE_PRIVATE);

        /* Lo que hace este comando es crear una variable de tipo OkHttpClient, -
         * que nos ayudara a construir una nueva solicitud (o petición) HTTP del -
         * cliente, en donde esta llevaria consigo el token de acceso que nos dio -
         * el API para que nos podamos autenticar y se pueda realizar las operaciones -
         * de la misma.
         *
         * NOTA: Esto se logra gracias al addInterceptor, el cual es un comando que se -
         * ejecuta antes de la solicitud que vamos a construir, y ya luego este mismo -
         * recibiria la respuesta de lo que pasamos a ese interceptor. Que en este caso, -
         * es una instancia hacia la clase "AutenticacionAPI", donde se le esta pasando -
         * el archivo SharedPreferences que se habia definido antes. El cual contiene -
         * el token de acceso para poder autenticarnos respectivamente. */
        OkHttpClient solicitudCliente = new OkHttpClient.Builder()
                .addInterceptor(new AutenticacionAPI(archivoXML))
                .build();

        /* Si retrofit es igual a nulo, quiere decir que no se ha hecho todavia la conexión, -
         * por lo que se realiza el proceso. Caso contrario no se hace de nuevo la conexion.
         *
         * NOTA: El .client(solicitudCliente) es para indicar que utilice la solicitud HTTP -
         * que uno creo, en este caso: solicitudCliente. */
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(solicitudCliente)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(FAQInterface.class);
    }

    public static EmpleadoInterface Conexion_API_Empleado(Context context) {
        /* Lo que hace este comando es crear una variable de tipo: SharedPreferences, -
         * donde dicha variable estaria accediendo a un archivo XML que tiene como nombre -
         * "Archivo_Autenticacion", y dicho archivo solo podra ser usado por la aplicación -
         * móvil (de ahi el MODE_PRIVATE).
         *
         * NOTA: La razón de esto es porque ese archivo XML nos servira para poder almacenar -
         * el token de acceso que nos brindaria el API, de modo que así, se pueda mantener la -
         * sesión y además, poder acceder a las funciones del API.
         *
         * También hay que recalcar que si no existe dicho archivo, el SharedPreferences lo -
         * creara automaticamente. */
        SharedPreferences archivoXML = context.getSharedPreferences(
                "Archivo_Autenticacion", Context.MODE_PRIVATE);

        /* Lo que hace este comando es crear una variable de tipo OkHttpClient, -
         * que nos ayudara a construir una nueva solicitud (o petición) HTTP del -
         * cliente, en donde esta llevaria consigo el token de acceso que nos dio -
         * el API para que nos podamos autenticar y se pueda realizar las operaciones -
         * de la misma.
         *
         * NOTA: Esto se logra gracias al addInterceptor, el cual es un comando que se -
         * ejecuta antes de la solicitud que vamos a construir, y ya luego este mismo -
         * recibiria la respuesta de lo que pasamos a ese interceptor. Que en este caso, -
         * es una instancia hacia la clase "AutenticacionAPI", donde se le esta pasando -
         * el archivo SharedPreferences que se habia definido antes. El cual contiene -
         * el token de acceso para poder autenticarnos respectivamente. */
        OkHttpClient solicitudCliente = new OkHttpClient.Builder()
                .addInterceptor(new AutenticacionAPI(archivoXML))
                .build();

        /* Si retrofit es igual a nulo, quiere decir que no se ha hecho todavia la conexión, -
         * por lo que se realiza el proceso. Caso contrario no se hace de nuevo la conexion.
         *
         * NOTA: El .client(solicitudCliente) es para indicar que utilice la solicitud HTTP -
         * que uno creo, en este caso: solicitudCliente. */
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(solicitudCliente)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(EmpleadoInterface.class);
    }

    public static PermisoInterface Conexion_API_Permiso_UsuarioEmpleado(Context context) {
        /* Lo que hace este comando es crear una variable de tipo: SharedPreferences, -
         * donde dicha variable estaria accediendo a un archivo XML que tiene como nombre -
         * "Archivo_Autenticacion", y dicho archivo solo podra ser usado por la aplicación -
         * móvil (de ahi el MODE_PRIVATE).
         *
         * NOTA: La razón de esto es porque ese archivo XML nos servira para poder almacenar -
         * el token de acceso que nos brindaria el API, de modo que así, se pueda mantener la -
         * sesión y además, poder acceder a las funciones del API.
         *
         * También hay que recalcar que si no existe dicho archivo, el SharedPreferences lo -
         * creara automaticamente. */
        SharedPreferences archivoXML = context.getSharedPreferences(
                "Archivo_Autenticacion", Context.MODE_PRIVATE);

        /* Lo que hace este comando es crear una variable de tipo OkHttpClient, -
         * que nos ayudara a construir una nueva solicitud (o petición) HTTP del -
         * cliente, en donde esta llevaria consigo el token de acceso que nos dio -
         * el API para que nos podamos autenticar y se pueda realizar las operaciones -
         * de la misma.
         *
         * NOTA: Esto se logra gracias al addInterceptor, el cual es un comando que se -
         * ejecuta antes de la solicitud que vamos a construir, y ya luego este mismo -
         * recibiria la respuesta de lo que pasamos a ese interceptor. Que en este caso, -
         * es una instancia hacia la clase "AutenticacionAPI", donde se le esta pasando -
         * el archivo SharedPreferences que se habia definido antes. El cual contiene -
         * el token de acceso para poder autenticarnos respectivamente. */
        OkHttpClient solicitudCliente = new OkHttpClient.Builder()
                .addInterceptor(new AutenticacionAPI(archivoXML))
                .build();

        /* Si retrofit es igual a nulo, quiere decir que no se ha hecho todavia la conexión, -
         * por lo que se realiza el proceso. Caso contrario no se hace de nuevo la conexion.
         *
         * NOTA: El .client(solicitudCliente) es para indicar que utilice la solicitud HTTP -
         * que uno creo, en este caso: solicitudCliente. */
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(solicitudCliente)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(PermisoInterface.class);
    }

    public static PermisoTiempoInterface Conexion_API_Permiso_Tiempo(Context context) {
        /* Lo que hace este comando es crear una variable de tipo: SharedPreferences, -
         * donde dicha variable estaria accediendo a un archivo XML que tiene como nombre -
         * "Archivo_Autenticacion", y dicho archivo solo podra ser usado por la aplicación -
         * móvil (de ahi el MODE_PRIVATE).
         *
         * NOTA: La razón de esto es porque ese archivo XML nos servira para poder almacenar -
         * el token de acceso que nos brindaria el API, de modo que así, se pueda mantener la -
         * sesión y además, poder acceder a las funciones del API.
         *
         * También hay que recalcar que si no existe dicho archivo, el SharedPreferences lo -
         * creara automaticamente. */
        SharedPreferences archivoXML = context.getSharedPreferences(
                "Archivo_Autenticacion", Context.MODE_PRIVATE);

        /* Lo que hace este comando es crear una variable de tipo OkHttpClient, -
         * que nos ayudara a construir una nueva solicitud (o petición) HTTP del -
         * cliente, en donde esta llevaria consigo el token de acceso que nos dio -
         * el API para que nos podamos autenticar y se pueda realizar las operaciones -
         * de la misma.
         *
         * NOTA: Esto se logra gracias al addInterceptor, el cual es un comando que se -
         * ejecuta antes de la solicitud que vamos a construir, y ya luego este mismo -
         * recibiria la respuesta de lo que pasamos a ese interceptor. Que en este caso, -
         * es una instancia hacia la clase "AutenticacionAPI", donde se le esta pasando -
         * el archivo SharedPreferences que se habia definido antes. El cual contiene -
         * el token de acceso para poder autenticarnos respectivamente. */
        OkHttpClient solicitudCliente = new OkHttpClient.Builder()
                .addInterceptor(new AutenticacionAPI(archivoXML))
                .build();

        /* Si retrofit es igual a nulo, quiere decir que no se ha hecho todavia la conexión, -
         * por lo que se realiza el proceso. Caso contrario no se hace de nuevo la conexion.
         *
         * NOTA: El .client(solicitudCliente) es para indicar que utilice la solicitud HTTP -
         * que uno creo, en este caso: solicitudCliente. */
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(solicitudCliente)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(PermisoTiempoInterface.class);
    }

    public static SalarioInterface Conexion_API_Salario(Context context) {
        /* Lo que hace este comando es crear una variable de tipo: SharedPreferences, -
         * donde dicha variable estaria accediendo a un archivo XML que tiene como nombre -
         * "Archivo_Autenticacion", y dicho archivo solo podra ser usado por la aplicación -
         * móvil (de ahi el MODE_PRIVATE).
         *
         * NOTA: La razón de esto es porque ese archivo XML nos servira para poder almacenar -
         * el token de acceso que nos brindaria el API, de modo que así, se pueda mantener la -
         * sesión y además, poder acceder a las funciones del API.
         *
         * También hay que recalcar que si no existe dicho archivo, el SharedPreferences lo -
         * creara automaticamente. */
        SharedPreferences archivoXML = context.getSharedPreferences(
                "Archivo_Autenticacion", Context.MODE_PRIVATE);

        /* Lo que hace este comando es crear una variable de tipo OkHttpClient, -
         * que nos ayudara a construir una nueva solicitud (o petición) HTTP del -
         * cliente, en donde esta llevaria consigo el token de acceso que nos dio -
         * el API para que nos podamos autenticar y se pueda realizar las operaciones -
         * de la misma.
         *
         * NOTA: Esto se logra gracias al addInterceptor, el cual es un comando que se -
         * ejecuta antes de la solicitud que vamos a construir, y ya luego este mismo -
         * recibiria la respuesta de lo que pasamos a ese interceptor. Que en este caso, -
         * es una instancia hacia la clase "AutenticacionAPI", donde se le esta pasando -
         * el archivo SharedPreferences que se habia definido antes. El cual contiene -
         * el token de acceso para poder autenticarnos respectivamente. */
        OkHttpClient solicitudCliente = new OkHttpClient.Builder()
                .addInterceptor(new AutenticacionAPI(archivoXML))
                .build();

        /* Si retrofit es igual a nulo, quiere decir que no se ha hecho todavia la conexión, -
         * por lo que se realiza el proceso. Caso contrario no se hace de nuevo la conexion.
         *
         * NOTA: El .client(solicitudCliente) es para indicar que utilice la solicitud HTTP -
         * que uno creo, en este caso: solicitudCliente. */
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(solicitudCliente)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(SalarioInterface.class);
    }

    public static InicioSesionInterface Conexion_API_Inicio_Sesion(Context context) {
        /* Lo que hace este comando es crear una variable de tipo: SharedPreferences, -
         * donde dicha variable estaria accediendo a un archivo XML que tiene como nombre -
         * "Archivo_Autenticacion", y dicho archivo solo podra ser usado por la aplicación -
         * móvil (de ahi el MODE_PRIVATE).
         *
         * NOTA: La razón de esto es porque ese archivo XML nos servira para poder almacenar -
         * el token de acceso que nos brindaria el API, de modo que así, se pueda mantener la -
         * sesión y además, poder acceder a las funciones del API.
         *
         * También hay que recalcar que si no existe dicho archivo, el SharedPreferences lo -
         * creara automaticamente. */
        SharedPreferences archivoXML = context.getSharedPreferences(
                "Archivo_Autenticacion", Context.MODE_PRIVATE);

        /* Lo que hace este comando es crear una variable de tipo OkHttpClient, -
         * que nos ayudara a construir una nueva solicitud (o petición) HTTP del -
         * cliente, en donde esta llevaria consigo el token de acceso que nos dio -
         * el API para que nos podamos autenticar y se pueda realizar las operaciones -
         * de la misma.
         *
         * NOTA: Esto se logra gracias al addInterceptor, el cual es un comando que se -
         * ejecuta antes de la solicitud que vamos a construir, y ya luego este mismo -
         * recibiria la respuesta de lo que pasamos a ese interceptor. Que en este caso, -
         * es una instancia hacia la clase "AutenticacionAPI", donde se le esta pasando -
         * el archivo SharedPreferences que se habia definido antes. El cual contiene -
         * el token de acceso para poder autenticarnos respectivamente. */
        OkHttpClient solicitudCliente = new OkHttpClient.Builder()
                .addInterceptor(new AutenticacionAPI(archivoXML))
                .build();

        /* Si retrofit es igual a nulo, quiere decir que no se ha hecho todavia la conexión, -
         * por lo que se realiza el proceso. Caso contrario no se hace de nuevo la conexion.
         *
         * NOTA: El .client(solicitudCliente) es para indicar que utilice la solicitud HTTP -
         * que uno creo, en este caso: solicitudCliente. */
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(solicitudCliente)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(InicioSesionInterface.class);
    }
}
