package com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI;

public class ReporteUsuarioEntitie {
    private String numeroDocumento;

    private String documentoPDF;

    public ReporteUsuarioEntitie(String numeroDocumento, String documentoPDF) {
        this.numeroDocumento = numeroDocumento;
        this.documentoPDF = documentoPDF;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getDocumentoPDF() {
        return documentoPDF;
    }

    public void setDocumentoPDF(String documentoPDF) {
        this.documentoPDF = documentoPDF;
    }
}