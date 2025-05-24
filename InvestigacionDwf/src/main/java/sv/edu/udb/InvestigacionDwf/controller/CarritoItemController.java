package sv.edu.udb.InvestigacionDwf.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.InvestigacionDwf.dto.request.CarritoItemRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.CarritoItemResponse;
import sv.edu.udb.InvestigacionDwf.service.CarritoItemService;

import java.util.List;

@RestController
@RequestMapping(path = "/auth/carrito-item")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class CarritoItemController {

    private final CarritoItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Indica que se devuelve un 201 Created
    public CarritoItemResponse addItem(@RequestBody CarritoItemRequest req) {
        return itemService.addItem(req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Indica que no hay contenido para devolver (204 No Content)
    public void removeItem(@PathVariable Long id) {
        itemService.removeItem(id);
    }

    @GetMapping("/carrito/{idCarrito}")
    @ResponseStatus(HttpStatus.OK) // Indica que se devuelve un 200 OK
    public List<CarritoItemResponse> getItemsByCarrito(@PathVariable Long idCarrito) {
        return itemService.getItemsByCarritoId(idCarrito);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK) // Indica que se devuelve un 200 OK
    public List<CarritoItemResponse> getAllItems() {
        return itemService.getAllItems();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK) // Indica que se devuelve un 200 OK
    public CarritoItemResponse getItemById(@PathVariable Long id) {
        return itemService.getItemById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK) // Indica que se devuelve un 200 OK
    public CarritoItemResponse updateItem(@PathVariable Long id, @RequestBody CarritoItemRequest request) {
        return itemService.updateItem(id, request);
    }
}