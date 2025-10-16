/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EDD;

/**
 *
 * @author pedro
 */
public class Nodo<T> {
    private T tInfo;
    private Nodo<T> pNext;

    public Nodo(T elem) {
        this.tInfo = elem;
        this.pNext = null;
    }

    public T gettInfo() {
        return tInfo;
    }

    public void settInfo(T tInfo) {
        this.tInfo = tInfo;
    }

    public Nodo<T> getpNext() {
        return pNext;
    }

    public void setpNext(Nodo<T> pNext) {
        this.pNext = pNext;
    }
}