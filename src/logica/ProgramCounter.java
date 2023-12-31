package logica;

import interfaces.IRegistro;

public class ProgramCounter implements IRegistro {
    
    // Utiliza un int para almacenar el valor del contador de programa de 4 bits. 
    // Solo se utilizarán los 4 bits menos significativos del int, ya que se solo 
    // se direccionarán 16 posiciones de memoria, pero el propio PC mantendrá 8 bits.
    private int valor;

    public ProgramCounter() {
        this.valor = 0;
    }

    @Override
    // Carga un valor en el contador de programa
    public void setValor(int v) {
        this.valor = v;
    }

    @Override
    public int getValor() {
        return this.valor;
    }

    // Incrementa el contador del programa
    public void activarConteo() {
        if (this.valor == 15) {
            // Si el PC ya está en 15, reinícielo ya que eso representa un desbordamiento de 4 bits
            this.valor = 0;
        } else {
            this.valor++;
        }
    }

    @Override
    // Reset del PC
    public void clear() {
        this.valor = 0;
    }
}
