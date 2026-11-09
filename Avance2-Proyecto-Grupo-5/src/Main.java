import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

//Clase principal: contiene el menú de consola y la rutina main().
public class Main {
    //Entrada y salida estándar.
    private static final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private static final PrintStream out = System.out;

    //Tienda que agrupa el inventario (ArbolProductos) y la cola de clientes (ColaClientes).
    private static final Tienda tienda = new Tienda("Tienda en Línea Grupo 5");
    private static final String DIRECTORIO_IMAGENES = "imagenes/";

    //Rutina principal: invoca el menú.
    public static void main(String[] args) throws IOException {
        out.println("Bienvenido al sistema de ventas de " + tienda.getNombre() + ".");
        menu();
    }

    //Menú de consola para interactuar con el inventario y con la cola de clientes.
    public static void menu() throws IOException {
        int opcion;
        do {
            out.println();
            out.println("========== " + tienda.getNombre().toUpperCase() + " ==========");
            out.println("Clientes en espera: " + tienda.getColaClientes().getTamano()
                    + " | Productos en inventario: " + tienda.getInventario().getCantidadProductos());
            out.println("--- Inventario ---");
            out.println(" 1. Insertar producto al inventario");
            out.println(" 2. Mostrar inventario");
            out.println(" 3. Buscar producto");
            out.println(" 4. Reabastecer producto");
            out.println(" 5. Agregar imagen a un producto");
            out.println(" 6. Eliminar producto del inventario");
            out.println("--- Clientes ---");
            out.println(" 7. Registrar cliente y llenar su carrito");
            out.println(" 8. Mostrar cola de clientes");
            out.println(" 9. Atender siguiente cliente (factura)");
            out.println("10. Resumen de ventas");
            out.println(" 0. Salir");
            opcion = leerEntero("Seleccione una opción: ");
            out.println();
            switch (opcion) {
                case 1:
                    insertarProducto();
                    break;
                case 2:
                    tienda.mostrarInventario();
                    break;
                case 3:
                    buscarProducto();
                    break;
                case 4:
                    reabastecerProducto();
                    break;
                case 5:
                    agregarImagen();
                    break;
                case 6:
                    eliminarProducto();
                    break;
                case 7:
                    registrarCliente();
                    break;
                case 8:
                    tienda.mostrarColaClientes();
                    break;
                case 9:
                    atenderCliente();
                    break;
                case 10:
                    mostrarResumenVentas();
                    break;
                case 0:
                    salir();
                    break;
                default:
                    out.println("Opción inválida. Intente de nuevo.");
            }
        } while (opcion != 0);
    }

    // ===================== Opciones del inventario =====================

    //Pide los datos de un producto y lo inserta en el árbol del inventario.
    private static void insertarProducto() throws IOException {
        String nombre = leerTextoNoVacio("Nombre: ");
        if (tienda.buscarProducto(nombre) != null) {
            out.println("Ya existe un producto con ese nombre. Use la opción 4 para reabastecerlo.");
            return;
        }
        double precio = leerDecimal("Precio: ");
        String categoria = leerTextoNoVacio("Categoría: ");
        LocalDate fecha = leerFecha("Fecha de vencimiento (AAAA-MM-DD, ENTER si no aplica): ");
        int cantidad = leerEntero("Cantidad en existencia: ");

        tienda.agregarProducto(new Producto(nombre, precio, categoria, fecha, cantidad));
        out.println("Producto insertado correctamente en el inventario.");
    }

    //Busca un producto por nombre e imprime sus datos.
    private static void buscarProducto() throws IOException {
        Producto producto = tienda.buscarProducto(leerTextoNoVacio("Nombre del producto: "));
        if (producto == null) {
            out.println("El producto no se encontró en el inventario.");
        } else {
            out.println(producto);
        }
    }

    //Suma unidades a la existencia de un producto.
    private static void reabastecerProducto() throws IOException {
        String nombre = leerTextoNoVacio("Nombre del producto: ");
        if (tienda.buscarProducto(nombre) == null) {
            out.println("El producto no se encontró en el inventario.");
            return;
        }
        int cantidad = leerEnteroEnRango("Unidades a agregar: ", 1, Integer.MAX_VALUE);
        tienda.reabastecerProducto(nombre, cantidad);
        out.println("Nueva existencia: " + tienda.buscarProducto(nombre).getCantidad() + " unidades.");
    }

    //Agrega la ruta de una imagen a un producto.
    private static void agregarImagen() throws IOException {
        Producto producto = tienda.buscarProducto(leerTextoNoVacio("Nombre del producto: "));
        if (producto == null) {
            out.println("El producto no se encontró en el inventario.");
            return;
        }
        String archivo = leerTextoNoVacio("Archivo de imagen (ej. leche.jpg, en '" + DIRECTORIO_IMAGENES + "'): ");
        String ruta = DIRECTORIO_IMAGENES + archivo;
        if (!new File(ruta).exists()) {
            out.println("Aviso: el archivo '" + ruta + "' no existe en la carpeta del proyecto.");
        }
        producto.agregarImagen(ruta);
        out.println("Imagen agregada correctamente.");
    }

    //Elimina un producto del inventario por nombre.
    private static void eliminarProducto() throws IOException {
        Producto eliminado = tienda.eliminarProducto(leerTextoNoVacio("Nombre del producto a eliminar: "));
        if (eliminado == null) {
            out.println("El producto no se encontró en el inventario.");
        } else {
            out.println("Se eliminó el producto '" + eliminado.getNombre() + "' del inventario.");
        }
    }

    // ===================== Opciones de clientes =====================

    //Pide los datos del cliente, le permite llenar su carrito y lo agrega a la cola.
    private static void registrarCliente() throws IOException {
        if (tienda.getInventario().estaVacio()) {
            out.println("El inventario está vacío. Inserte productos antes de registrar clientes.");
            return;
        }
        String nombre = leerTextoNoVacio("Nombre del cliente: ");
        String identificacion = leerTextoNoVacio("Identificación: ");
        out.println("Tipo de cliente: " + Cliente.PRIORIDAD_BASICO + ". Básico | "
                + Cliente.PRIORIDAD_AFILIADO + ". Afiliado | " + Cliente.PRIORIDAD_PREMIUM + ". Premium");
        int prioridad = leerEnteroEnRango("Seleccione el tipo: ", Cliente.PRIORIDAD_BASICO, Cliente.PRIORIDAD_PREMIUM);
        Cliente cliente = new Cliente(nombre, identificacion, prioridad);

        if (!llenarCarrito(cliente)) {
            out.println("Registro cancelado. Los productos se devolvieron al inventario.");
            return;
        }
        if (tienda.registrarCliente(cliente)) {
            out.println("Cliente " + cliente.getNombre() + " agregado a la cola. Total a pagar: "
                    + String.format("%.2f", cliente.calcularTotalCarrito())
                    + ". Clientes en espera: " + tienda.getColaClientes().getTamano() + ".");
        } else {
            out.println("El carrito quedó vacío; el cliente no se agregó a la cola.");
        }
    }

    //Submenú para llenar el carrito a partir del inventario.
    //Retorna true si el cliente confirma su carrito y false si lo cancela.
    private static boolean llenarCarrito(Cliente cliente) throws IOException {
        while (true) {
            out.println();
            out.println("--- Carrito de " + cliente.getNombre() + " ("
                    + cliente.getCarrito().contarProductos() + " productos, total "
                    + String.format("%.2f", cliente.calcularTotalCarrito()) + ") ---");
            out.println("1. Ver inventario disponible");
            out.println("2. Agregar producto al carrito");
            out.println("3. Quitar producto del carrito");
            out.println("4. Ver carrito");
            out.println("5. Confirmar carrito y pasar a la cola");
            out.println("0. Cancelar registro");
            int opcion = leerEntero("Seleccione una opción: ");
            switch (opcion) {
                case 1:
                    tienda.mostrarInventario();
                    break;
                case 2:
                    agregarAlCarrito(cliente);
                    break;
                case 3:
                    quitarDelCarrito(cliente);
                    break;
                case 4:
                    cliente.getCarrito().mostrarDetalleCostos();
                    break;
                case 5:
                    if (cliente.getCarrito().estaVacia()) {
                        out.println("Agregue al menos un producto antes de confirmar.");
                    } else {
                        return true;
                    }
                    break;
                case 0:
                    tienda.cancelarCarrito(cliente);
                    return false;
                default:
                    out.println("Opción inválida. Intente de nuevo.");
            }
        }
    }

    //Pide un producto y una cantidad y los pasa del inventario al carrito.
    private static void agregarAlCarrito(Cliente cliente) throws IOException {
        String nombreProducto = leerTextoNoVacio("Nombre del producto: ");
        Producto producto = tienda.buscarProducto(nombreProducto);
        if (producto == null) {
            out.println("El producto no se encontró en el inventario.");
            return;
        }
        out.println(producto.getNombre() + " | Precio: " + String.format("%.2f", producto.getPrecio())
                + " | Disponible: " + producto.getCantidad());
        int cantidad = leerEnteroEnRango("Cantidad a comprar: ", 1, Integer.MAX_VALUE);
        try {
            tienda.agregarAlCarrito(cliente, nombreProducto, cantidad);
            out.println("Se agregaron " + cantidad + " unidad(es) de '" + producto.getNombre() + "' al carrito.");
        } catch (IllegalArgumentException e) {
            out.println("No se pudo agregar: " + e.getMessage());
        }
    }

    //Quita un producto del carrito y devuelve las unidades al inventario.
    private static void quitarDelCarrito(Cliente cliente) throws IOException {
        if (cliente.getCarrito().estaVacia()) {
            out.println("El carrito está vacío.");
            return;
        }
        Producto quitado = tienda.quitarDelCarrito(cliente, leerTextoNoVacio("Nombre del producto a quitar: "));
        if (quitado == null) {
            out.println("Ese producto no está en el carrito.");
        } else {
            out.println("Se quitó '" + quitado.getNombre() + "' del carrito.");
        }
    }

    //Atiende al cliente de mayor prioridad e imprime su factura.
    private static void atenderCliente() {
        if (!tienda.hayClientesEnEspera()) {
            out.println("No hay clientes en espera.");
            return;
        }
        Cliente atendido = tienda.atenderSiguienteCliente();
        out.println("Cliente " + atendido.getNombre() + " atendido. Clientes restantes: "
                + tienda.getColaClientes().getTamano() + ".");
    }

    //Imprime la cantidad de facturas emitidas y el monto total vendido.
    private static void mostrarResumenVentas() {
        out.println("Facturas emitidas: " + tienda.getFacturasEmitidas());
        out.println("Total vendido: " + String.format("%.2f", tienda.getTotalVendido()));
        out.println("Clientes en espera: " + tienda.getColaClientes().getTamano());
    }

    //Avisa si quedan clientes sin atender antes de cerrar el programa.
    private static void salir() {
        if (tienda.hayClientesEnEspera()) {
            out.println("Aviso: quedan " + tienda.getColaClientes().getTamano() + " cliente(s) sin atender.");
        }
        out.println("¡Hasta pronto!");
    }

    // ===================== Lectura de datos =====================

    //Lee una línea; si la entrada se cierra, termina el programa.
    private static String leerLinea() throws IOException {
        String linea = in.readLine();
        if (linea == null) {
            out.println("Entrada finalizada. Cerrando el programa.");
            System.exit(0);
        }
        return linea;
    }

    //Lee un entero no negativo.
    private static int leerEntero(String mensaje) throws IOException {
        return leerEnteroEnRango(mensaje, 0, Integer.MAX_VALUE);
    }

    //Lee un entero que esté dentro del rango [minimo, maximo].
    private static int leerEnteroEnRango(String mensaje, int minimo, int maximo) throws IOException {
        while (true) {
            out.print(mensaje);
            String linea = leerLinea();
            try {
                int valor = Integer.parseInt(linea.trim());
                if (valor >= minimo && valor <= maximo) {
                    return valor;
                }
                if (maximo == Integer.MAX_VALUE) {
                    out.println("El valor debe ser mayor o igual que " + minimo + ".");
                } else {
                    out.println("El valor debe estar entre " + minimo + " y " + maximo + ".");
                }
            } catch (NumberFormatException e) {
                out.println("Entrada inválida. Ingrese un número entero.");
            }
        }
    }

    //Lee un número decimal no negativo (acepta coma o punto).
    private static double leerDecimal(String mensaje) throws IOException {
        while (true) {
            out.print(mensaje);
            String linea = leerLinea();
            try {
                double valor = Double.parseDouble(linea.trim().replace(',', '.'));
                if (valor >= 0) {
                    return valor;
                }
                out.println("El valor no puede ser negativo.");
            } catch (NumberFormatException e) {
                out.println("Entrada inválida. Ingrese un número (ej. 1500.50).");
            }
        }
    }

    //Lee un texto que no esté vacío.
    private static String leerTextoNoVacio(String mensaje) throws IOException {
        while (true) {
            out.print(mensaje);
            String linea = leerLinea().trim();
            if (!linea.isEmpty()) {
                return linea;
            }
            out.println("Este campo no puede estar vacío.");
        }
    }

    //Si el campo se deja vacío retorna null (el producto no vence).
    private static LocalDate leerFecha(String mensaje) throws IOException {
        while (true) {
            out.print(mensaje);
            String linea = leerLinea().trim();
            if (linea.isEmpty()) {
                return null;
            }
            try {
                return LocalDate.parse(linea);
            } catch (DateTimeParseException e) {
                out.println("Fecha inválida. Use el formato AAAA-MM-DD.");
            }
        }
    }
}
