//Lista enlazada simple de productos; su único atributo es la referencia al primero.
//En este avance se usa como el carrito de compras de cada Cliente. El nombre identifica a cada producto.
public class ListaProductos {
    private Producto primero;

    //Constructor: crea una lista vacía.
    public ListaProductos() {
        this.primero = null;
    }

    //Retorna el primer nodo, para que otras clases puedan recorrer la lista.
    public Producto getPrimero() {
        return primero;
    }

    //Indica si la lista no tiene productos.
    public boolean estaVacia() {
        return primero == null;
    }

    //Inserta un producto al inicio de la lista.
    public void insertarProductoInicio(Producto nuevoProducto) {
        nuevoProducto.setSiguiente(primero);
        primero = nuevoProducto;
    }

    //Inserta un producto al final de la lista.
    public void insertarProductoFinal(Producto nuevoProducto) {
        nuevoProducto.setSiguiente(null);
        if (primero == null) {
            primero = nuevoProducto;
            return;
        }
        Producto productoTemp = primero;
        while (productoTemp.getSiguiente() != null) {
            productoTemp = productoTemp.getSiguiente();
        }
        productoTemp.setSiguiente(nuevoProducto);
    }

    //Busca un producto por nombre; retorna null si no existe.
    public Producto buscarProducto(String nombreBuscar) {
        Producto productoActual = primero;
        while (productoActual != null
                && !productoActual.getNombre().equalsIgnoreCase(nombreBuscar)) {
            productoActual = productoActual.getSiguiente();
        }
        return productoActual;
    }

    //Elimina un producto por nombre y lo retorna; retorna null si no existe.
    public Producto eliminarProducto(String nombreEliminar) {
        if (primero == null) {
            return null;
        }
        if (primero.getNombre().equalsIgnoreCase(nombreEliminar)) {
            Producto eliminado = primero;
            primero = primero.getSiguiente();
            eliminado.setSiguiente(null);
            return eliminado;
        }
        Producto anterior = primero;
        Producto actual = primero.getSiguiente();
        while (actual != null && !actual.getNombre().equalsIgnoreCase(nombreEliminar)) {
            anterior = actual;
            actual = actual.getSiguiente();
        }
        if (actual != null) {
            anterior.setSiguiente(actual.getSiguiente());
            actual.setSiguiente(null);
        }
        return actual;
    }

    //Retorna la cantidad de nodos (productos distintos) de la lista.
    public int contarProductos() {
        int contador = 0;
        Producto productoActual = primero;
        while (productoActual != null) {
            contador++;
            productoActual = productoActual.getSiguiente();
        }
        return contador;
    }

    //Recorre la lista y suma el costo total de cada producto.
    public double calcularCostoTotalAcumulado() {
        double costoAcumulado = 0;
        Producto productoActual = primero;
        while (productoActual != null) {
            costoAcumulado += productoActual.calcularCostoTotal();
            productoActual = productoActual.getSiguiente();
        }
        return costoAcumulado;
    }

    //Recorre la lista e imprime cada producto.
    public void mostrarLista() {
        if (primero == null) {
            System.out.println("La lista se encuentra vacía.");
            return;
        }
        Producto productoActual = primero;
        while (productoActual != null) {
            System.out.println(productoActual);
            productoActual = productoActual.getSiguiente();
        }
    }

    //Imprime una línea por producto con su cantidad, precio unitario y subtotal (detalle de factura).
    public void mostrarDetalleCostos() {
        if (primero == null) {
            System.out.println("  (sin productos)");
            return;
        }
        System.out.printf("  %-22s %8s %12s %14s%n", "Producto", "Cant.", "Precio", "Subtotal");
        Producto productoActual = primero;
        while (productoActual != null) {
            System.out.printf("  %-22s %8d %12.2f %14.2f%n",
                    recortar(productoActual.getNombre(), 22),
                    productoActual.getCantidad(),
                    productoActual.getPrecio(),
                    productoActual.calcularCostoTotal());
            productoActual = productoActual.getSiguiente();
        }
    }

    //Recorta un texto largo para que no desalinee las columnas.
    private static String recortar(String texto, int maximo) {
        return texto.length() <= maximo ? texto : texto.substring(0, maximo - 3) + "...";
    }
}
