// src/main/java/sv/edu/udb/InvestigacionDwf/service/impl/CarritoItemServiceImpl.java
package sv.edu.udb.InvestigacionDwf.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.InvestigacionDwf.dto.request.CarritoItemRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.CarritoItemResponse;
import sv.edu.udb.InvestigacionDwf.model.CarritoItem;
import sv.edu.udb.InvestigacionDwf.repository.CarritoItemRepository;
import sv.edu.udb.InvestigacionDwf.service.CarritoItemService;
import sv.edu.udb.InvestigacionDwf.service.mapper.CarritoItemMapper;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarritoItemServiceImpl implements CarritoItemService {

    private final CarritoItemRepository itemRepository;
    private final CarritoItemMapper itemMapper;

    @Override
    @Transactional
    public CarritoItemResponse addItem(CarritoItemRequest req) {
        CarritoItem item = itemMapper.toEntity(req);
        CarritoItem saved = itemRepository.save(item);
        return itemMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void removeItem(Long idCarritoItem) {
        itemRepository.deleteById(idCarritoItem);
    }
}
