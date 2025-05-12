package sv.edu.udb.InvestigacionDwf.model.enums;

public enum EstadoPedido {
    CARRITO,        // Pedido en carrito (no confirmado)
    PENDIENTE,      // Confirmado, esperando pago
    PAGADO,         // Pago exitoso
    EN_PROCESO,     // Preparando envío
    ENVIADO,        // En camino
    ENTREGADO,      // Recibido por el cliente
    CANCELADO       // Pedido cancelado
}
