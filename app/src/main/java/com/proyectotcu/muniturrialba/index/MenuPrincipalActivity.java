package com.proyectotcu.muniturrialba.index;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.proyectotcu.muniturrialba.MainActivity;
import com.proyectotcu.muniturrialba.R;
import com.proyectotcu.muniturrialba.databinding.ActivityMenuPrincipalBinding;
import com.proyectotcu.muniturrialba.manejoAPI.ConexionAPI;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.UsuarioEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.UsuarioInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MenuPrincipalActivity extends AppCompatActivity {

    //Variable para usar el ViewBinding de esta clase.
    private ActivityMenuPrincipalBinding activityMenuPrincipalBinding;

    TextView textView;
    UsuarioInterface usuarioInterface;

    /* Este metodo sirve para poder crear y enlazar la clase hacia la vista respectiva.
     * También se sustituyo aspectos como "find by id (y similares)" por el uso del ViewBinding,-
     * el cual es una nueva forma para llamar los elementos de la vista de una forma más optimizada.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activityMenuPrincipalBinding =
                ActivityMenuPrincipalBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(activityMenuPrincipalBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_MenuPrincipal), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        PruebaDeAPI();
        LlamarAPI();
    }

    private void LlamarAPI() {
        Call<List<UsuarioEntitie>> usuarios =
                usuarioInterface.obtenerUsuarios();

        usuarios.enqueue(new Callback<List<UsuarioEntitie>>() {
            @Override
            public void onResponse(Call<List<UsuarioEntitie>> call,
             Response<List<UsuarioEntitie>> response) {
                if (response.isSuccessful()){
                    for (int i = 0; i < response.body().size() ; i++) {
                        UsuarioEntitie usuarioEntitie = response.body().get(i);
                        textView.append(usuarioEntitie.getNombre() + "\n"
                        + usuarioEntitie.getApellido_1() + " " + usuarioEntitie.getApellido_2()
                        + "\n" + usuarioEntitie.getEdad()
                        + "\n" + usuarioEntitie.getCedula()
                        + "\n" + usuarioEntitie.getTelefono()
                        + "\n" + usuarioEntitie.getCorreo_Electronico()
                        + "\n" + usuarioEntitie.getContraseña()
                        + "\n" + usuarioEntitie.getFecha_Creacion()
                        + "\n" + usuarioEntitie.getImagen_Perfil()
                        + "\n" + usuarioEntitie.getId_Rol());
                    }
                }
                else {
                    Toast.makeText(MenuPrincipalActivity.this,
                            response.message(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UsuarioEntitie>> call,
            Throwable t) {
                Toast.makeText(MenuPrincipalActivity.this,
                        t.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void PruebaDeAPI() {
        textView = activityMenuPrincipalBinding.txtPrueba;
        usuarioInterface = ConexionAPI.Conexion_API();

    }
}