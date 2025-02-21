package com.finance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.finance.model.Learning;
import com.finance.service.LearningService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/learning")
public class LearningController {

    @Autowired
    private LearningService learningService;

    // POST /api/learning-modules - Add a new learning module
    @PostMapping("{userId}")
    public ResponseEntity<Learning> addLearningModule(@RequestBody Learning learningModule, @PathVariable String userId ) {
        Learning newLearningModule = learningService.addLearningModule(learningModule, userId);
        return ResponseEntity.ok(newLearningModule);
    }

    // GET /api/learning-modules/{id} - Get a learning module by ID
    @GetMapping("/{id}")
    public ResponseEntity<Learning> getLearningModuleById(@PathVariable Long id) {
        Optional<Learning> learningModule = learningService.getLearningModuleById(id);
        return learningModule.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/user/{userid}")
    public ResponseEntity<List<Learning>> getLearningModuleByUserId(@PathVariable String userid) {
        System.out.println("Fetching learnings for user ID: " + userid);
        List<Learning> learningModule = learningService.getLearningModuleByUserId(userid);

        if (learningModule.isEmpty()) {
            System.out.println("No learnings found for user ID: " + userid);
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(learningModule);
        }
    }

    // GET /api/learning-modules - Get all learning modules
    @GetMapping
    public List<Learning> getAllLearningModules() {
        return learningService.getAllLearningModules();
    }

    // PUT /api/learning-modules/{id} - Update learning module details
    @PutMapping("/{id}")
    public ResponseEntity<Learning> updateLearningModule(@PathVariable Long id, @RequestBody Learning updatedLearningModule) {
        Learning learningModule = learningService.updateLearningModule(id, updatedLearningModule);
        return learningModule != null ? ResponseEntity.ok(learningModule) : ResponseEntity.notFound().build();
    }

    // DELETE /api/learning-modules/{id} - Delete learning module by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLearningModule(@PathVariable Long id) {
        learningService.deleteLearningModule(id);
        return ResponseEntity.noContent().build();
    }
}
