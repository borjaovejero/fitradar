package com.fitradar.backend.injury.controller;

import com.fitradar.backend.injury.dto.InjuryRecordRequest;
import com.fitradar.backend.injury.dto.InjuryRecordResponse;
import com.fitradar.backend.injury.model.InjuryRecord;
import com.fitradar.backend.injury.service.InjuryRecordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/injury-records")
@CrossOrigin
public class InjuryRecordController {

    private final InjuryRecordService injuryRecordService;

    public InjuryRecordController(InjuryRecordService injuryRecordService) {
        this.injuryRecordService = injuryRecordService;
    }

    // POST /api/injury-records
    @PostMapping
    public ResponseEntity<InjuryRecordResponse> create(
            @Valid @RequestBody InjuryRecordRequest request,
            Authentication authentication) {

        String username = authentication.getName();
        InjuryRecord record = injuryRecordService.createInjuryRecord(username, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(record));
    }

    // GET /api/injury-records
    @GetMapping
    public ResponseEntity<List<InjuryRecordResponse>> getMyRecords(Authentication authentication) {
        String username = authentication.getName();
        List<InjuryRecordResponse> responses = injuryRecordService.getByUsername(username)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // GET /api/injury-records/{id}
    @GetMapping("/{id}")
    public ResponseEntity<InjuryRecordResponse> getById(
            @PathVariable Long id,
            Authentication authentication) {

        InjuryRecord record = injuryRecordService.getById(id);
        if (!record.getUser().getUsername().equals(authentication.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(mapToResponse(record));
    }

    // PUT /api/injury-records/{id}
    @PutMapping("/{id}")
    public ResponseEntity<InjuryRecordResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody InjuryRecordRequest request,
            Authentication authentication) {

        String username = authentication.getName();
        InjuryRecord updated = injuryRecordService.updateInjuryRecord(username, id, request);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    // DELETE /api/injury-records/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Authentication authentication) {

        String username = authentication.getName();
        injuryRecordService.deleteInjuryRecord(username, id);
        return ResponseEntity.noContent().build();
    }

    private InjuryRecordResponse mapToResponse(InjuryRecord r) {
        InjuryRecordResponse res = new InjuryRecordResponse();
        res.setId(r.getId());
        res.setUsername(r.getUser().getUsername());
        res.setInjuryType(r.getInjuryType());
        res.setBodyZone(r.getBodyZone());
        res.setInjurySeverity(r.getInjurySeverity());
        res.setStartDate(r.getStartDate());
        res.setEndDate(r.getEndDate());
        res.setDaysOff(r.getDaysOff());
        res.setRecurrence(r.isRecurrence());
        res.setFullyRecovered(r.isFullyRecovered());
        res.setReportedToModel(r.isReportedToModel());
        res.setDescription(r.getDescription());
        return res;
    }
}