/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.trabajodisenopatrones.patterns;

// PATRÓN DE COMPORTAMIENTO: OBSERVER (Interfaz del Observador)
// Define el método 'update' que debe ser implementado por cualquier clase
// que desee ser notificada de cambios en un 'Subject'. En nuestro caso, las UIs.
public interface Observer {
    void update();
}