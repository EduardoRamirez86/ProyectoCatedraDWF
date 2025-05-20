package sv.edu.udb.InvestigacionDwf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PagedResourcesAssembler;
import sv.edu.udb.InvestigacionDwf.model.entity.Pedido;

@Configuration
public class HateoasConfig {

    @Bean
    public PagedResourcesAssembler<Pedido> pagedResourcesAssembler() {
        return new PagedResourcesAssembler<>(null, null);
    }
}
