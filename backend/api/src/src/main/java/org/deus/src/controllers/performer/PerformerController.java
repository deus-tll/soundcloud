package org.deus.src.controllers.performer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.deus.src.dtos.fromModels.PerformerDTO;
import org.deus.src.exceptions.StatusException;
import org.deus.src.requests.performer.PerformerCreateRequest;
import org.deus.src.requests.performer.PerformerUpdateRequest;
import org.deus.src.services.forModels.PerformerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/performers")
public class PerformerController {
    private final PerformerService performerService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PerformerDTO> createPerformer(@ModelAttribute @Valid PerformerCreateRequest request) {
        PerformerDTO performerDTO = this.performerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(performerDTO);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PerformerDTO> updatePerformer(@PathVariable Long id, @ModelAttribute @Valid PerformerUpdateRequest request) throws StatusException {
        PerformerDTO updatedPerformer = this.performerService.update(id, request);
        return ResponseEntity.ok(updatedPerformer);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerformerDTO> getPerformerById(@PathVariable Long id) throws StatusException {
        PerformerDTO performerDTO = this.performerService.getById(id);
        return ResponseEntity.ok(performerDTO);
    }

    @GetMapping
    public ResponseEntity<Page<PerformerDTO>> getAllPerformers(Pageable pageable) {
        Page<PerformerDTO> performers = this.performerService.getAll(pageable);
        return ResponseEntity.ok(performers);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PerformerDTO>> getAllPerformers() {
        List<PerformerDTO> performers = this.performerService.getAllPerformers();
        return ResponseEntity.ok(performers);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerformerById(@PathVariable Long id) throws StatusException {
        this.performerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}