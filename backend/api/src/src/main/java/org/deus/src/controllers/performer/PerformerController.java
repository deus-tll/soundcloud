package org.deus.src.controllers.performer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.deus.src.dtos.fromModels.PerformerDTO;
import org.deus.src.exceptions.StatusException;
import org.deus.src.requests.performer.PerformerCreateRequest;
import org.deus.src.requests.performer.PerformerUpdateRequest;
import org.deus.src.services.PerformerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/performers")
public class PerformerController {
    private final PerformerService performerService;

    @PostMapping
    public ResponseEntity<PerformerDTO> createPerformer(@RequestBody @Valid PerformerCreateRequest request) {
        PerformerDTO performerDTO = this.performerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(performerDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PerformerDTO> updatePerformer(@PathVariable Long id, @RequestBody @Valid PerformerUpdateRequest request) throws StatusException {
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerformerById(@PathVariable Long id) throws StatusException {
        this.performerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}