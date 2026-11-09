
public class ColaClientes {
    private Cliente frente;
    private Cliente fin;
    private int tamano;

    //Constructor: crea una cola vacía.
    public ColaClientes() {
        this.frente = null;
        this.fin = null;
        this.tamano = 0;
    }

    //Indica si la cola no tiene clientes.
    public boolean estaVacia() {
        return frente == null;
    }

    //Retorna la cantidad de clientes en espera.
    public int getTamano() {
        return tamano;
    }

    //Inserta un cliente al final de la cola.
    public void encolar(Cliente nuevoCliente) {
        nuevoCliente.setSiguiente(null);
        if (frente == null) {
            frente = nuevoCliente;
        } else {
            fin.setSiguiente(nuevoCliente);
        }
        fin = nuevoCliente;
        tamano++;
    }

    //Retorna (sin sacarlo) el cliente que se atendería a continuación; null si la cola está vacía.
    //Se usa ">" estricto para que, en un empate, se conserve el que está más cerca del frente.
    public Cliente verSiguiente() {
        Cliente prioritario = frente;
        Cliente actual = (frente == null) ? null : frente.getSiguiente();
        while (actual != null) {
            if (actual.getPrioridad() > prioritario.getPrioridad()) {
                prioritario = actual;
            }
            actual = actual.getSiguiente();
        }
        return prioritario;
    }

    //Saca de la cola al cliente que corresponde atender y lo retorna; null si la cola está vacía.
    public Cliente atender() {
        Cliente atendido = verSiguiente();
        if (atendido == null) {
            return null;
        }
        if (atendido == frente) {
            frente = frente.getSiguiente();
            if (frente == null) {
                fin = null;
            }
        } else {
            Cliente anterior = frente;
            while (anterior.getSiguiente() != atendido) {
                anterior = anterior.getSiguiente();
            }
            anterior.setSiguiente(atendido.getSiguiente());
            if (atendido == fin) {
                fin = anterior;
            }
        }
        atendido.setSiguiente(null);
        tamano--;
        return atendido;
    }

    //Imprime los clientes en orden de llegada y señala quién será atendido a continuación.
    public void mostrarCola() {
        if (frente == null) {
            System.out.println("No hay clientes en espera.");
            return;
        }
        Cliente proximo = verSiguiente();
        Cliente actual = frente;
        int posicion = 1;
        System.out.println("Clientes en espera (del frente al final):");
        while (actual != null) {
            String marca = (actual == proximo) ? "  <-- siguiente en ser atendido" : "";
            System.out.println("  " + posicion + ". " + actual + marca);
            actual = actual.getSiguiente();
            posicion++;
        }
    }
}
