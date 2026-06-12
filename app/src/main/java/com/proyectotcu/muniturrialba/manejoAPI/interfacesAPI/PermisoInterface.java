package com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI;

import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.EmpleadoEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.PermisoEntitie;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PermisoInterface {

    // |===========| Metodos de tipo: GET |===========|

    /* Metodo que sirve para traer el usuario deseado, -
     * por medio del correo electronico. */
    @GET("api/permisos/{correo}")
    Call<PermisoEntitie> obtenerPermisos (@Path("correo") String correo,
                                         @Query("tokenAcceso") String jwtToken);


    // |===========| Metodos de tipo: POST |===========|
    /* Metodo que sirve para registrar a un nuevo usuario -
     * respectivamente. */
    @Headers("Content-Type: application/json")
    @POST("api/crearPermisos")
    Call<EmpleadoEntitie> crearPermisos(@Body PermisoEntitie permisoEntitie,
                                        @Query("correoUsuario") String correo,
                                        @Query("tokenAcceso") String jwtToken);



    // |===========| Metodos de tipo: PUT |===========|
    /* Metodo que sirve para actualizar una pregunta y respuesta -
     * respectivamente. */
    @Headers("Content-Type: application/json")
    @PUT("api/actualizarPermisos")
    Call<Boolean> actualizarPermisos(@Body PermisoEntitie permisoEntitie,
                                     @Query("correoUsuario") String correo,
                                     @Query("tokenAcceso") String jwtToken);

}
