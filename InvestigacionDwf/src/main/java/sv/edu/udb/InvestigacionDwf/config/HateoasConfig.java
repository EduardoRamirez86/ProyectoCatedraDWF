package sv.edu.udb.InvestigacionDwf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PagedResourcesAssembler;
import sv.edu.udb.InvestigacionDwf.model.entity.Pedido;
import sv.edu.udb.InvestigacionDwf.model.entity.Producto;

@Configuration
public class HateoasConfig {

    @Bean
    public PagedResourcesAssembler<Pedido> pedidoPagedResourcesAssembler() {
        return new PagedResourcesAssembler<>(null, null);
    }

    @Bean
    public PagedResourcesAssembler<Producto> productoPagedResourcesAssembler() {
        return new PagedResourcesAssembler<>(null, null);
    }

}
