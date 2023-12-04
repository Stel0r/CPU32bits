package logica;

import interfaces.IRegistro;

public class Registro8Bit implements IRegistro {

    private int valor;

    public Registro8Bit() {
        this.valor = 0;
    }

    @Override
    public void setValor(int v) {
        this.valor = v;
    }

    @Override
    public int getValor() {
        
        return this.valor;
    }

    @Override
    public void clear() {
        this.valor = 0;
    }
}
