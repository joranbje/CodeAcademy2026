package no.soprasteria.controller;

import no.soprasteria.db.DataRepository;
import no.soprasteria.domain.IdemDataDTO;
import no.soprasteria.rabbit.FanOutMessageService;
import no.soprasteria.rabbit.RabbitMQConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final DataRepository dataRepository;
    private final FanOutMessageService fanOutMessageService;

    public MessageController(DataRepository dataRepository, FanOutMessageService fanOutMessageService) {
        this.dataRepository = dataRepository;
        this.fanOutMessageService = fanOutMessageService;
    }

    @GetMapping("latest")
    public ResponseEntity<List<IdemDataDTO>> get() {
        return ResponseEntity.ok(dataRepository.findLatest()
                .stream()
                .map(message -> new IdemDataDTO(message.getId().toString(), message.getAuthor(), message.getMessage(), message.getCreatedAt()))
                .toList());
    }

    @PutMapping("post-new-message")
    public ResponseEntity<?> postMessage(@RequestBody Message message) {
        fanOutMessageService.publishMessageToQueue(new IdemDataDTO(UUID.randomUUID().toString(), message.author(), message.message(), LocalDateTime.now()), RabbitMQConfiguration.EXCHANGE_NAME_FANOUT, "");
        return ResponseEntity.accepted().build();
    }
}
