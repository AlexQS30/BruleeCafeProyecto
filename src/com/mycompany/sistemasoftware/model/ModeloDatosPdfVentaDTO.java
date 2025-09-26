/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.sistemasoftware.model;

import java.util.List;

// DTO para transportar todos los datos necesarios para generar el PDF de una venta.
public class ModeloDatosPdfVentaDTO {
    private ModeloEmpresa datosEmpresa;
    private ModeloCliente datosCliente;
    private List<ModeloDetalleVenta> detallesVenta;
    private double totalVenta;
    private int idVenta;

    public ModeloDatosPdfVentaDTO(ModeloEmpresa datosEmpresa, ModeloCliente datosCliente, List<ModeloDetalleVenta> detallesVenta, double totalVenta, int idVenta) {
        this.datosEmpresa = datosEmpresa;
        this.datosCliente = datosCliente;
        this.detallesVenta = detallesVenta;
        this.totalVenta = totalVenta;
        this.idVenta = idVenta;
    }

    // Getters
    public ModeloEmpresa getDatosEmpresa() { return datosEmpresa; }
    public ModeloCliente getDatosCliente() { return datosCliente; }
    public List<ModeloDetalleVenta> getDetallesVenta() { return detallesVenta; }
    public double getTotalVenta() { return totalVenta; }
    public int getIdVenta() { return idVenta; }
}