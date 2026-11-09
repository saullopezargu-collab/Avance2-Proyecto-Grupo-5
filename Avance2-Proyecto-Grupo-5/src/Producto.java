import java.time.LocalDate;
import java.util.ArrayList;


public class Producto {
    //Atributos.
    private String nombre;
    private double precio;
    private String categoria;
    //Es null cuando el producto no vence.
    private LocalDate fechaVencimiento;
    private int cantidad;
    //Rutas de las imágenes, guardadas en la carpeta imagenes/ del proyecto.
    private final ArrayList<String> listaImagenes;
    //Referencia al siguiente nodo de la lista enlazada (carrito).
    private Producto siguiente;
    //Referencias a los hijos del nodo en el árbol binario de búsqueda (inventario).
    private Producto izquierdo;
    private Producto derecho;

    //Constructor: crea un producto sin imágenes y sin enlaces a otros nodos.
    public Producto(String nombre, double precio, String categoria,
                    LocalDate fechaVencimiento, int cantidad) {
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
        this.fechaVencimiento = fechaVencimiento;
        this.cantidad = cantidad;
        this.listaImagenes = new ArrayList<>();
        this.siguiente = null;
        this.izquierdo = null;
        this.derecho = null;
    }

    //Getters.
    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public String getCategoria() {
        return categoria;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public int getCantidad() {
        return cantidad;
    }

    public ArrayList<String> getListaImagenes() {
        return listaImagenes;
    }

    public Producto getSiguiente() {
        return siguiente;
    }

    public Producto getIzquierdo() {
        return izquierdo;
    }

    public Producto getDerecho() {
        return derecho;
    }

    //Setters.
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void setSiguiente(Producto siguiente) {
        this.siguiente = siguiente;
    }

    public void setIzquierdo(Producto izquierdo) {
        this.izquierdo = izquierdo;
    }

    public void setDerecho(Producto derecho) {
        this.derecho = derecho;
    }

    //Añade la ruta de una imagen a la lista de imágenes.
    public void agregarImagen(String ruta) {
        listaImagenes.add(ruta);
    }

    //Retorna el costo total del producto (precio por cantidad).
    public double calcularCostoTotal() {
        return precio * cantidad;
    }

    //Indica si el producto ya venció (los productos sin fecha nunca vencen).
    public boolean estaVencido() {
        return fechaVencimiento != null && fechaVencimiento.isBefore(LocalDate.now());
    }

    //Crea una copia del producto con la cantidad indicada y sin enlaces,
    //lista para insertarse en el carrito de un Cliente.
    public Producto copiarParaCarrito(int cantidadComprada) {
        Producto copia = new Producto(nombre, precio, categoria, fechaVencimiento, cantidadComprada);
        copia.listaImagenes.addAll(listaImagenes);
        return copia;
    }

    @Override
    //Retorna los datos del producto en una sola línea.
    public String toString() {
        String vencimiento = (fechaVencimiento == null) ? "No aplica" : fechaVencimiento.toString();
        String imagenes = listaImagenes.isEmpty() ? "Sin imágenes" : listaImagenes.toString();
        return "Producto: " + nombre
                + " | Precio: " + String.format("%.2f", precio)
                + " | Categoría: " + categoria
                + " | Vencimiento: " + vencimiento
                + " | Cantidad: " + cantidad
                + " | Imágenes: " + imagenes;
    }
}
