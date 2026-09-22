package com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI;

import android.net.Uri;

import java.util.ArrayList;

public class ExtensionReportePermisoTiempoEntitie {
    private Uri documentoPDF;

    private ArrayList<ExtensionPermisoTiempoEntitie> listaDatos_DocumentoPDF;

    public ExtensionReportePermisoTiempoEntitie(Uri documento, ArrayList<ExtensionPermisoTiempoEntitie> listaDatos) {
        this.documentoPDF = documento;
        this.listaDatos_DocumentoPDF = listaDatos;
    }

    public Uri getDocumentoPDF() {
        return documentoPDF;
    }

    public void setDocumentoPDF(Uri documentoPDF) {
        this.documentoPDF = documentoPDF;
    }

    public ArrayList<ExtensionPermisoTiempoEntitie> getListaDatos_DocumentoPDF() {
        return listaDatos_DocumentoPDF;
    }

    public void setListaDatos_DocumentoPDF(ArrayList<ExtensionPermisoTiempoEntitie> listaDatos_DocumentoPDF) {
        this.listaDatos_DocumentoPDF = listaDatos_DocumentoPDF;
    }
}
