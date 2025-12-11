package com.wellness.wellness_backend.service;

import org.springframework.stereotype.Service;
import com.wellness.wellness_backend.repo.PractitionerRepository;
import com.wellness.wellness_backend.model.Practitioner;

import java.util.List;

@Service
public class PractitionerService {

    private final PractitionerRepository repo;

    public PractitionerService(PractitionerRepository repo) {
        this.repo = repo;
    }

    public Practitioner create(Practitioner p) {
        return repo.save(p);
    }

    public List<Practitioner> getAll() {
        return repo.findAll();
    }

    public Practitioner getById(Long id) {
        return repo.findById(id).orElse(null);
    }
}
