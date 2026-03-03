package weather.api.app.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import weather.api.app.DTO.DataDTO;
import weather.api.app.Model.Data;
import weather.api.app.Service.DataService;

@RestController
public class MeasurementController {

    private final DataService dataService;
    private final SimpMessagingTemplate messagingTemplate;

    public MeasurementController(DataService dataService, SimpMessagingTemplate messagingTemplate) {
        this.dataService = dataService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/measurements")
    public ResponseEntity<?> addMeasurement(@RequestBody DataDTO dto) {

        // Guardar en BD
        Data saved = dataService.saveFromDTO(dto);

        // Enviar por WebSocket
        messagingTemplate.convertAndSend("/topic/live", saved);

        return ResponseEntity.ok().build();
    }
}
