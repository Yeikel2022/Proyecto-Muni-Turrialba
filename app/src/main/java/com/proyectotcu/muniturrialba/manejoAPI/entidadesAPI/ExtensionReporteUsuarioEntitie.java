package com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI;

import android.net.Uri;

import java.util.ArrayList;

public class ExtensionReporteUsuarioEntitie {
    private Uri documentoPDF;

    private ArrayList<ExtensionInicioSesionEntitie> listaDatos_DocumentoPDF;

    public ExtensionReporteUsuarioEntitie(Uri documento, ArrayList<ExtensionInicioSesionEntitie> listaDatos) {
        this.documentoPDF = documento;
        this.listaDatos_DocumentoPDF = listaDatos;
    }

    public Uri getDocumentoPDF() {
        return documentoPDF;
    }

    public void setDocumentoPDF(Uri documentoPDF) {
        this.documentoPDF = documentoPDF;
    }

    public ArrayList<ExtensionInicioSesionEntitie> getListaDatos_DocumentoPDF() {
        return listaDatos_DocumentoPDF;
    }

    public void setListaDatos_DocumentoPDF(ArrayList<ExtensionInicioSesionEntitie> listaDatos_DocumentoPDF) {
        this.listaDatos_DocumentoPDF = listaDatos_DocumentoPDF;
    }
}
