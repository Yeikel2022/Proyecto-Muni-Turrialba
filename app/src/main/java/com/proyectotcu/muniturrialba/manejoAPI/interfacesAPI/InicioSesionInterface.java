package com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI;

import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.ExtensionInicioSesionEntitie;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface InicioSesionInterface {

    // |===========| Metodos de tipo: GET |===========|

    /* Metodo que sirve para traer a todos los usuarios -
     * del sistema. */
    @GET("api/iniciosSesion")
    Call<List<ExtensionInicioSesionEntitie>> obtenerUsuarios(@Query("tokenAcceso") String jwtToken);

}
