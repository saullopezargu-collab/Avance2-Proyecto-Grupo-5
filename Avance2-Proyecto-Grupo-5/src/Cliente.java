//Clase nodo de la ColaClientes: cada Cliente guarda sus datos, su carrito de compras
//y la referencia al siguiente Cliente de la cola.
public class Cliente {
    //Niveles de prioridad válidos: un número mayor significa que se atiende antes.
    public static final int PRIORIDAD_BASICO = 1;
    public static final int PRIORIDAD_AFILIADO = 2;
    public static final int PRIORIDAD_PREMIUM = 3;

    //Atributos.
    private String nombre;
    private String identificacion;
    private int prioridad;
    //Carrito personal: lista enlazada con copias de los productos elegidos del inventario.
    private final ListaProductos carrito;
    private Cliente siguiente;

    //Constructor: crea un cliente con el carrito vacío y sin siguiente.
    public Cliente(String nombre, String identificacion, int prioridad) {
        validarPrioridad(prioridad);
        this.nombre = nombre;
        this.identificacion = identificacion;
        this.prioridad = prioridad;
        this.carrito = new ListaProductos();
        this.siguiente = null;
    }

    //Lanza una excepción si la prioridad no está entre 1 y 3.
    private static void validarPrioridad(int prioridad) {
        if (prioridad < PRIORIDAD_BASICO || prioridad > PRIORIDAD_PREMIUM) {
            throw new IllegalArgumentException("La prioridad debe estar entre "
                    + PRIORIDAD_BASICO + " y " + PRIORIDAD_PREMIUM + ".");
        }
    }

    //Getters.
    public String getNombre() {
        return nombre;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public ListaProductos getCarrito() {
        return carrito;
    }

    public Cliente getSiguiente() {
        return siguiente;
    }

    //Setters.
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public void setPrioridad(int prioridad) {
        validarPrioridad(prioridad);
        this.prioridad = prioridad;
    }

    public void setSiguiente(Cliente siguiente) {
        this.siguiente = siguiente;
    }

    //Retorna el nombre del tipo de cliente según su prioridad.
    public String getTipoCliente() {
        switch (prioridad) {
            case PRIORIDAD_PREMIUM:
                return "Premium";
            case PRIORIDAD_AFILIADO:
                return "Afiliado";
            default:
                return "Básico";
        }
    }

    //Agrega al carrito una copia del producto del inventario con la cantidad indicada.
    //Si el producto ya estaba en el carrito, solo se suma la cantidad.
    public void agregarAlCarrito(Producto productoInventario, int cantidad) {
        Producto enCarrito = carrito.buscarProducto(productoInventario.getNombre());
        if (enCarrito != null) {
            enCarrito.setCantidad(enCarrito.getCantidad() + cantidad);
        } else {
            carrito.insertarProductoFinal(productoInventario.copiarParaCarrito(cantidad));
        }
    }

    //Quita un producto del carrito y lo retorna; retorna null si no estaba.
    public Producto quitarDelCarrito(String nombreProducto) {
        return carrito.eliminarProducto(nombreProducto);
    }

    //Retorna el costo total acumulado del carrito.
    public double calcularTotalCarrito() {
        return carrito.calcularCostoTotalAcumulado();
    }

    @Override
    //Retorna los datos del cliente en una sola línea.
    public String toString() {
        return nombre + " (ID: " + identificacion + ")"
                + " | Tipo: " + getTipoCliente() + " [" + prioridad + "]"
                + " | Productos en carrito: " + carrito.contarProductos()
                + " | Total: " + String.format("%.2f", calcularTotalCarrito());
    }
}
