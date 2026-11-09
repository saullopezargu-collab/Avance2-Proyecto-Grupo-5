
public class ArbolProductos {
    private Producto raiz;
    private int cantidadProductos;

    //Constructor: crea un árbol vacío.
    public ArbolProductos() {
        this.raiz = null;
        this.cantidadProductos = 0;
    }

    //Indica si el árbol no tiene productos.
    public boolean estaVacio() {
        return raiz == null;
    }

    //Retorna la cantidad de nodos del árbol.
    public int getCantidadProductos() {
        return cantidadProductos;
    }

    //Compara dos nombres (llaves del árbol) sin distinguir mayúsculas y minúsculas.
    private static int compararLlaves(String nombre1, String nombre2) {
        return nombre1.compareToIgnoreCase(nombre2);
    }

    //Inserta un producto en su posición; retorna false si ya existe uno con ese nombre.
    public boolean insertar(Producto nuevoProducto) {
        if (buscar(nuevoProducto.getNombre()) != null) {
            return false;
        }
        nuevoProducto.setIzquierdo(null);
        nuevoProducto.setDerecho(null);
        raiz = insertarRecursivo(raiz, nuevoProducto);
        cantidadProductos++;
        return true;
    }

    //Desciende por el árbol hasta encontrar el espacio vacío donde va el nuevo nodo.
    private Producto insertarRecursivo(Producto actual, Producto nuevoProducto) {
        if (actual == null) {
            return nuevoProducto;
        }
        if (compararLlaves(nuevoProducto.getNombre(), actual.getNombre()) < 0) {
            actual.setIzquierdo(insertarRecursivo(actual.getIzquierdo(), nuevoProducto));
        } else {
            actual.setDerecho(insertarRecursivo(actual.getDerecho(), nuevoProducto));
        }
        return actual;
    }

    //Busca un producto por nombre de forma iterativa; retorna null si no existe.
    public Producto buscar(String nombreBuscar) {
        Producto actual = raiz;
        while (actual != null) {
            int comparacion = compararLlaves(nombreBuscar, actual.getNombre());
            if (comparacion == 0) {
                return actual;
            }
            actual = (comparacion < 0) ? actual.getIzquierdo() : actual.getDerecho();
        }
        return null;
    }

    //Elimina un producto por nombre y lo retorna; retorna null si no existe.
    public Producto eliminar(String nombreEliminar) {
        Producto encontrado = buscar(nombreEliminar);
        if (encontrado == null) {
            return null;
        }
        raiz = eliminarRecursivo(raiz, nombreEliminar);
        encontrado.setIzquierdo(null);
        encontrado.setDerecho(null);
        cantidadProductos--;
        return encontrado;
    }

    //Retorna la nueva raíz del subárbol después de quitar el nodo con la llave indicada.
    //Casos: nodo hoja o con un hijo (se reemplaza por ese hijo) y nodo con dos hijos
    //(se reemplaza por su sucesor inorden, el menor del subárbol derecho).
    private Producto eliminarRecursivo(Producto actual, String nombreEliminar) {
        if (actual == null) {
            return null;
        }
        int comparacion = compararLlaves(nombreEliminar, actual.getNombre());
        if (comparacion < 0) {
            actual.setIzquierdo(eliminarRecursivo(actual.getIzquierdo(), nombreEliminar));
            return actual;
        }
        if (comparacion > 0) {
            actual.setDerecho(eliminarRecursivo(actual.getDerecho(), nombreEliminar));
            return actual;
        }
        if (actual.getIzquierdo() == null) {
            return actual.getDerecho();
        }
        if (actual.getDerecho() == null) {
            return actual.getIzquierdo();
        }
        //Se reubica el nodo sucesor (no se copian datos) para conservar la identidad de cada objeto.
        Producto sucesor = buscarMinimo(actual.getDerecho());
        sucesor.setDerecho(eliminarMinimo(actual.getDerecho()));
        sucesor.setIzquierdo(actual.getIzquierdo());
        return sucesor;
    }

    //Retorna el nodo con la menor llave del subárbol (el que está más a la izquierda).
    private Producto buscarMinimo(Producto actual) {
        while (actual.getIzquierdo() != null) {
            actual = actual.getIzquierdo();
        }
        return actual;
    }

    //Quita el nodo mínimo del subárbol y retorna la nueva raíz de ese subárbol.
    private Producto eliminarMinimo(Producto actual) {
        if (actual.getIzquierdo() == null) {
            return actual.getDerecho();
        }
        actual.setIzquierdo(eliminarMinimo(actual.getIzquierdo()));
        return actual;
    }

    //Retorna la altura del árbol (un árbol vacío tiene altura 0).
    public int calcularAltura() {
        return calcularAlturaRecursivo(raiz);
    }

    private int calcularAlturaRecursivo(Producto actual) {
        if (actual == null) {
            return 0;
        }
        return 1 + Math.max(calcularAlturaRecursivo(actual.getIzquierdo()),
                calcularAlturaRecursivo(actual.getDerecho()));
    }

    //Imprime el inventario con un recorrido inorden, que lo muestra en orden alfabético.
    public void mostrarInOrden() {
        if (raiz == null) {
            System.out.println("El inventario se encuentra vacío.");
            return;
        }
        System.out.printf("  %-22s %-14s %12s %10s  %s%n", "Producto", "Categoría", "Precio", "Existencia", "Estado");
        mostrarInOrdenRecursivo(raiz);
        System.out.println("  Productos registrados: " + cantidadProductos + " | Altura del árbol: " + calcularAltura());
    }

    //Recorrido inorden: subárbol izquierdo, nodo actual y subárbol derecho.
    private void mostrarInOrdenRecursivo(Producto actual) {
        if (actual == null) {
            return;
        }
        mostrarInOrdenRecursivo(actual.getIzquierdo());
        System.out.printf("  %-22s %-14s %12.2f %10d  %s%n",
                recortar(actual.getNombre(), 22),
                recortar(actual.getCategoria(), 14),
                actual.getPrecio(),
                actual.getCantidad(),
                describirEstado(actual));
        mostrarInOrdenRecursivo(actual.getDerecho());
    }

    //Indica si un producto se puede vender, está agotado o vencido.
    private static String describirEstado(Producto producto) {
        if (producto.estaVencido()) {
            return "Vencido";
        }
        return (producto.getCantidad() == 0) ? "Agotado" : "Disponible";
    }

    //Recorta un texto largo para que no desalinee las columnas.
    private static String recortar(String texto, int maximo) {
        return texto.length() <= maximo ? texto : texto.substring(0, maximo - 3) + "...";
    }
}
