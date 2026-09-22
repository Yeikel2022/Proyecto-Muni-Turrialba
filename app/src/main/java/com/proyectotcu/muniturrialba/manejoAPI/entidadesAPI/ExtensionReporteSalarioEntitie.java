package com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI;

import android.net.Uri;

import java.util.ArrayList;

public class ExtensionReporteSalarioEntitie {
    private Uri documentoPDF;

    private ArrayList<ExtensionSalarioEntitie> listaDatos_DocumentoPDF;

    public ExtensionReporteSalarioEntitie(Uri documento, ArrayList<ExtensionSalarioEntitie> listaDatos) {
        this.documentoPDF = documento;
        this.listaDatos_DocumentoPDF = listaDatos;
    }

    public Uri getDocumentoPDF() {
        return documentoPDF;
    }

    public void setDocumentoPDF(Uri documentoPDF) {
        this.documentoPDF = documentoPDF;
    }

    public ArrayList<ExtensionSalarioEntitie> getListaDatos_DocumentoPDF() {
        return listaDatos_DocumentoPDF;
    }

    public void setListaDatos_DocumentoPDF(ArrayList<ExtensionSalarioEntitie> listaDatos_DocumentoPDF) {
        this.listaDatos_DocumentoPDF = listaDatos_DocumentoPDF;
    }
}
