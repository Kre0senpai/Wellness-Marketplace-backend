package com.wellness.wellness_backend.service;

import com.wellness.wellness_backend.dto.PractitionerDTO;
import com.wellness.wellness_backend.model.Practitioner;
import com.wellness.wellness_backend.model.User;
import com.wellness.wellness_backend.repo.PractitionerRepository;
import com.wellness.wellness_backend.repo.UserRepository;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service("practitionerService")
public class PractitionerService {

    private final PractitionerRepository practitionerRepository;
    private final UserRepository userRepository;

    public PractitionerService(
            PractitionerRepository practitionerRepository,
            UserRepository userRepository
    ) {
        this.practitionerRepository = practitionerRepository;
        this.userRepository = userRepository;
    }

    // =====================================================
    // 🔐 AUTHORIZATION HELPER (USED BY @PreAuthorize)
    // =====================================================
    public boolean isVerifiedPractitionerByEmail(String email) {
        if (email == null || email.isBlank()) return false;

        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) return false;

        Practitioner p = practitionerRepository.findByUserId(user.getId());
        return p != null && p.isVerified();
    }



    // ================================
    // CREATE PRACTITIONER PROFILE
    // ================================
    public Practitioner createPractitioner(Long userId, PractitionerDTO dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found with id: " + userId));

        if (practitionerRepository.existsByUserId(userId)) {
            throw new EntityExistsException(
                    "Practitioner profile already exists for this user");
        }

        Practitioner p = new Practitioner();
        p.setUserId(userId);
        p.setName(dto.getName());
        p.setSpecialization(dto.getSpecialization());
        p.setBio(dto.getBio());
        p.setEmail(dto.getEmail());
        p.setExperienceYears(
                dto.getExperienceYears() != null ? dto.getExperienceYears() : 0
        );
        p.setVerified(false);

        return practitionerRepository.save(p);
    }

    // ================================
    // UPLOAD CERTIFICATE
    // ================================
    public void uploadCertificate(Long userId, MultipartFile file) {

        Practitioner practitioner = practitionerRepository.findByUserId(userId);

        if (practitioner == null) {
            throw new RuntimeException("Practitioner profile not found");
        }

        if (file.isEmpty()) {
            throw new RuntimeException("Certificate file is empty");
        }

        try {
            String uploadDir = "uploads/certificates/";
            Files.createDirectories(Paths.get(uploadDir));

            String filename = "practitioner_" + practitioner.getId() + "_" +
                    file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + filename);

            Files.write(filePath, file.getBytes());

            practitioner.setCertificatePath(filePath.toString());
            practitionerRepository.save(practitioner);

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload certificate", e);
        }
    }

    // ================================
    // USER LOOKUP
    // ================================
    public Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found with email: " + email))
                .getId();
    }

    // ================================
    // GET VERIFIED PRACTITIONERS
    // ================================
    public List<Practitioner> getVerifiedPractitioners(String specialization) {

        if (specialization == null || specialization.isBlank()) {
            return practitionerRepository.findByVerifiedTrue();
        }

        return practitionerRepository
                .findByVerifiedTrueAndSpecializationIgnoreCase(specialization);
    }

    // ================================
    // DELETE PRACTITIONER
    // ================================
    public void deletePractitioner(Long id) {
        practitionerRepository.deleteById(id);
    }

    // ================================
    // UPDATE PRACTITIONER
    // ================================
    public Practitioner updatePractitioner(Practitioner practitioner) {
        return practitionerRepository.save(practitioner);
    }

    // ================================
    // GET ALL PRACTITIONERS
    // ================================
    public List<Practitioner> getAll() {
        return practitionerRepository.findAll();
    }

    // ================================
    // GET PRACTITIONER BY ID
    // ================================
    public Practitioner getById(Long id) {
        return practitionerRepository.findById(id).orElse(null);
    }

    // ================================
    // GET PRACTITIONER BY USER ID
    // ================================
    public Practitioner getByUserId(Long userId) {
        return practitionerRepository.findByUserId(userId);
    }
}
