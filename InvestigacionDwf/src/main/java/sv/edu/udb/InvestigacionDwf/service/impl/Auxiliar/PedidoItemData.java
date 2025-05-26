package sv.edu.udb.InvestigacionDwf.service.impl.Auxiliar;

import sv.edu.udb.InvestigacionDwf.model.entity.Producto;

import java.math.BigDecimal;

// Clase auxiliar para almacenar datos temporalmente
public class PedidoItemData {
    private final Producto producto;
    private final int cantidad;
    private final BigDecimal precioUnitario;

    public PedidoItemData(Producto producto, int cantidad, BigDecimal precioUnitario) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
}
