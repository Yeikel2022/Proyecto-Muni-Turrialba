package com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI;

import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.ExtensionSalarioEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.SalarioEntitie;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface SalarioInterface {

    // |===========| Metodos de tipo: GET |===========|

    /* Metodo que sirve para traer a todos los usuarios -
     * del sistema. */
    @GET("api/salarios")
    Call<List<ExtensionSalarioEntitie>> obtenerSalarios(@Query("tokenAcceso") String jwtToken);


    // |===========| Metodos de tipo: POST |===========|

    /* Metodo que sirve para registrar a un nuevo usuario -
     * respectivamente. */
    @Headers("Content-Type: application/json")
    @POST("api/crearSalarios")
    Call<SalarioEntitie> crearSalarios(@Body SalarioEntitie salarioEntitie,
                                       @Query("cedulaUsuario") String cedula,
                                       @Query("tokenAcceso") String jwtToken);



    // |===========| Metodos de tipo: PUT |===========|
    /* Metodo que sirve para actualizar una pregunta y respuesta -
     * respectivamente. */
    @Headers("Content-Type: application/json")
    @PUT("api/actualizarSalarios")
    Call<Boolean> actualizarSalarios(@Body SalarioEntitie salarioEntitie,
                                     @Query("cedulaUsuario") String cedula,
                                     @Query("tokenAcceso") String jwtToken);



    // |===========| Metodos de tipo: DELETE |===========|
    /* Metodo que sirve para eliminar una pregunta y respuesta -
     * respectivamente. */
    @Headers("Content-Type: application/json")
    @DELETE("api/eliminarSalarios")
    Call<Boolean> eliminarSalarios(@Query("cedulaUsuario") String cedula,
                                   @Query("tokenAcceso") String jwtToken);

}
