/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */ 
package com.mycompany.sistemasoftware.patterns;

// PATRÓN DE COMPORTAMIENTO: OBSERVER (Interfaz del Sujeto)
// Define los métodos para que los 'Observers' se suscriban, desuscriban
// y para que el 'Subject' los notifique. En nuestro caso, los Gestores.
public interface Subject {
    void registerObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers();
}