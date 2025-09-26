
package com.mycompany.sistemasoftware.model;

public class ModeloEmpresa {
    private int id;
    private String ruc;
    private String representanteLegal;
    private int telefono;
    private String direccion;
    private String razonSocial;
    
    public ModeloEmpresa(){
        
    }

    public ModeloEmpresa(int id, String ruc, String nombre, int telefono, String direccion, String razon) {
        this.id = id;
        this.ruc = ruc;
        this.representanteLegal = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
        this.razonSocial = razon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getRepresentanteLegal() {
        return representanteLegal;
    }

    public void setRepresentanteLegal(String representanteLegal) {
        this.representanteLegal = representanteLegal;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }
    
    
}
