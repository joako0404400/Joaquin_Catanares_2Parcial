package com.productos.model;

public class Proveedor {

    private int id;
    private String razonSocial;
    private String telefono;
    private String email;
    private String ciudad;

    public Proveedor(int id, String razonSocial, String telefono, String email, String ciudad) {
        this.id = id;
        this.razonSocial = razonSocial;
        this.telefono = telefono;
        this.email = email;
        this.ciudad = ciudad;
    }

    public int getId()              { return id; }
    public String getRazonSocial()  { return razonSocial; }
    public String getTelefono()     { return telefono; }
    public String getEmail()        { return email; }
    public String getCiudad()       { return ciudad; }

    @Override
    public String toString() {
        return razonSocial + " (" + ciudad + ")";
    }
}