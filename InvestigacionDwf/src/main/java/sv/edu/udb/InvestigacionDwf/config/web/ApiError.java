package sv.edu.udb.InvestigacionDwf.config.web;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ApiError {

    private Integer status;

    private String type;

    private String title;

    @Builder.Default
    private String source = "base";

    private String description;
}