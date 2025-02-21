package com.finance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finance.exception.InvalidEntityException;
import com.finance.model.Learning;
import com.finance.model.UserInfo;
import com.finance.repository.LearningRepository;
import com.finance.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class LearningService {

    @Autowired
    private LearningRepository learningRepository;
    
    @Autowired
    private UserRepository userRepository;

    // Create a new learning module
    public Learning addLearningModule(Learning learning,String userId) throws InvalidEntityException{
    	UserInfo user = userRepository.findById(userId).orElseThrow(() -> new InvalidEntityException("User not found"));
    	learning.setUser(user);
        return learningRepository.save(learning);
    }

    // Get a learning module by ID
    public Optional<Learning> getLearningModuleById(Long id) {
        return learningRepository.findById(id);
    }
    
 // Get a learning module by ID
    public List<Learning> getLearningModuleByUserId(String userid) {
        return learningRepository.findByUser_UserId(userid);
    }

    // Get all learning modules
    public List<Learning> getAllLearningModules() {
        return learningRepository.findAll();
    }

    // Update a learning module
    public Learning updateLearningModule(Long id, Learning updatedLearningModule) {
        Optional<Learning> existingLearningModule = learningRepository.findById(id);
        if (existingLearningModule.isPresent()) {
            Learning learning = existingLearningModule.get();
            learning.setTitle(updatedLearningModule.getTitle());
            learning.setDescription(updatedLearningModule.getDescription());
            learning.setContent(updatedLearningModule.getContent());
            return learningRepository.save(learning);
        } else {
            return null; // Or throw custom exception if not found
        }
    }

    // Delete a learning module by ID
    public void deleteLearningModule(Long id) {
        learningRepository.deleteById(id);
    }
}
