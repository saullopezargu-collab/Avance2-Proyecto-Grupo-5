import java.time.LocalDate;

public class Tienda {
    //Atributos.
    private String nombre;
    private final ArbolProductos inventario;
    private final ColaClientes colaClientes;
    private int consecutivoFacturas;
    private double totalVendido;

    //Constructor: crea una tienda con el inventario y la cola vacíos.
    public Tienda(String nombre) {
        this.nombre = nombre;
        this.inventario = new ArbolProductos();
        this.colaClientes = new ColaClientes();
        this.consecutivoFacturas = 0;
        this.totalVendido = 0;
    }

    //Getters y setters.
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ArbolProductos getInventario() {
        return inventario;
    }

    public ColaClientes getColaClientes() {
        return colaClientes;
    }

    public int getFacturasEmitidas() {
        return consecutivoFacturas;
    }

    public double getTotalVendido() {
        return totalVendido;
    }

    // ===================== Inventario =====================

    //Inserta un producto en el inventario; retorna false si ya existe uno con ese nombre.
    public boolean agregarProducto(Producto producto) {
        return inventario.insertar(producto);
    }

    //Busca un producto del inventario por nombre; retorna null si no existe.
    public Producto buscarProducto(String nombreProducto) {
        return inventario.buscar(nombreProducto);
    }

    //Elimina un producto del inventario y lo retorna; retorna null si no existe.
    //Los carritos guardan copias, por lo que no se ven afectados.
    public Producto eliminarProducto(String nombreProducto) {
        return inventario.eliminar(nombreProducto);
    }

    //Suma unidades a la existencia de un producto; retorna false si el producto no existe.
    public boolean reabastecerProducto(String nombreProducto, int cantidad) {
        Producto producto = inventario.buscar(nombreProducto);
        if (producto == null) {
            return false;
        }
        producto.setCantidad(producto.getCantidad() + cantidad);
        return true;
    }

    //Imprime el inventario en orden alfabético.
    public void mostrarInventario() {
        inventario.mostrarInOrden();
    }

    // ===================== Carrito =====================

    //Pasa unidades de un producto del inventario al carrito del cliente y descuenta la existencia.
    //Lanza IllegalArgumentException con el motivo si la compra no es posible.
    public void agregarAlCarrito(Cliente cliente, String nombreProducto, int cantidad) {
        Producto producto = inventario.buscar(nombreProducto);
        if (producto == null) {
            throw new IllegalArgumentException("El producto '" + nombreProducto + "' no existe en el inventario.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        if (producto.estaVencido()) {
            throw new IllegalArgumentException("El producto '" + producto.getNombre() + "' está vencido y no se puede vender.");
        }
        if (cantidad > producto.getCantidad()) {
            throw new IllegalArgumentException("Existencia insuficiente de '" + producto.getNombre()
                    + "'. Disponible: " + producto.getCantidad() + ".");
        }
        producto.setCantidad(producto.getCantidad() - cantidad);
        cliente.agregarAlCarrito(producto, cantidad);
    }

    //Quita un producto del carrito y devuelve sus unidades al inventario (si el producto aún existe).
    //Retorna el producto quitado o null si no estaba en el carrito.
    public Producto quitarDelCarrito(Cliente cliente, String nombreProducto) {
        Producto quitado = cliente.quitarDelCarrito(nombreProducto);
        if (quitado != null) {
            devolverAlInventario(quitado);
        }
        return quitado;
    }

    //Vacía el carrito del cliente y devuelve todas las unidades al inventario.
    public void cancelarCarrito(Cliente cliente) {
        ListaProductos carrito = cliente.getCarrito();
        while (!carrito.estaVacia()) {
            Producto quitado = carrito.eliminarProducto(carrito.getPrimero().getNombre());
            devolverAlInventario(quitado);
        }
    }

    //Suma al inventario las unidades de un producto que sale de un carrito.
    private void devolverAlInventario(Producto productoCarrito) {
        Producto enInventario = inventario.buscar(productoCarrito.getNombre());
        if (enInventario != null) {
            enInventario.setCantidad(enInventario.getCantidad() + productoCarrito.getCantidad());
        }
    }

    // ===================== Cola de clientes =====================

    //Agrega el cliente a la cola de atención; retorna false si su carrito está vacío.
    public boolean registrarCliente(Cliente cliente) {
        if (cliente.getCarrito().estaVacia()) {
            return false;
        }
        colaClientes.encolar(cliente);
        return true;
    }

    //Indica si hay clientes esperando.
    public boolean hayClientesEnEspera() {
        return !colaClientes.estaVacia();
    }

    //Imprime la cola de clientes.
    public void mostrarColaClientes() {
        colaClientes.mostrarCola();
    }

    //Atiende al cliente de mayor prioridad, imprime su factura y lo retorna; null si no hay clientes.
    public Cliente atenderSiguienteCliente() {
        Cliente atendido = colaClientes.atender();
        if (atendido == null) {
            return null;
        }
        consecutivoFacturas++;
        totalVendido += atendido.calcularTotalCarrito();
        imprimirFactura(atendido, consecutivoFacturas);
        return atendido;
    }

    //Imprime la factura del cliente: encabezado, detalle del carrito y costo total acumulado.
    private void imprimirFactura(Cliente cliente, int numeroFactura) {
        String linea = "  ==============================================================";
        System.out.println(linea);
        System.out.println("  " + nombre.toUpperCase() + " - FACTURA No. " + String.format("%05d", numeroFactura));
        System.out.println("  Fecha: " + LocalDate.now());
        System.out.println("  Cliente: " + cliente.getNombre() + " | ID: " + cliente.getIdentificacion());
        System.out.println("  Tipo de cliente: " + cliente.getTipoCliente() + " (prioridad " + cliente.getPrioridad() + ")");
        System.out.println(linea);
        cliente.getCarrito().mostrarDetalleCostos();
        System.out.println(linea);
        System.out.printf("  %-44s %14.2f%n", "COSTO TOTAL ACUMULADO:", cliente.calcularTotalCarrito());
        System.out.println(linea);
        System.out.println("  ¡Gracias por su compra!");
    }
}
