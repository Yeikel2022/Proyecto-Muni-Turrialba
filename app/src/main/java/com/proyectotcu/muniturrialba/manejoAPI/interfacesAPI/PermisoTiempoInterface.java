package com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI;

import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.ExtensionPermisoTiempoEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.PermisoTiempoEntitie;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface PermisoTiempoInterface {

    // |===========| Metodos de tipo: GET |===========|

    /* Metodo que sirve para traer a todos los usuarios -
     * del sistema. */
    @GET("api/permisosTiempo")
    Call<List<ExtensionPermisoTiempoEntitie>> obtenerPermisosTiempo(@Query("tokenAcceso") String jwtToken);


    // |===========| Metodos de tipo: POST |===========|

    /* Metodo que sirve para registrar a un nuevo usuario -
     * respectivamente. */
    @Headers("Content-Type: application/json")
    @POST("api/crearPermisosTiempo")
    Call<PermisoTiempoEntitie> crearPermisosTiempo(@Body PermisoTiempoEntitie permisoTiempoEntitie,
                                                   @Query("cedulaUsuario") String cedula,
                                                   @Query("tokenAcceso") String jwtToken);



    // |===========| Metodos de tipo: PUT |===========|
    /* Metodo que sirve para actualizar una pregunta y respuesta -
     * respectivamente. */
    @Headers("Content-Type: application/json")
    @PUT("api/actualizarPermisosTiempo")
    Call<Boolean> actualizarPermisosTiempo(@Body PermisoTiempoEntitie permisoTiempoEntitie,
                                           @Query("cedulaUsuario") String cedula,
                                           @Query("tokenAcceso") String jwtToken);



    // |===========| Metodos de tipo: DELETE |===========|
    /* Metodo que sirve para eliminar una pregunta y respuesta -
     * respectivamente. */
    @Headers("Content-Type: application/json")
    @DELETE("api/eliminarPermisosTiempo")
    Call<Boolean> eliminarPermisosTiempo(@Query("cedulaUsuario") String cedula,
                                         @Query("tokenAcceso") String jwtToken);

}
