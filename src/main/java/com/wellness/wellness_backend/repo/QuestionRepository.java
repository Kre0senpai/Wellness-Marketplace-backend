package com.wellness.wellness_backend.repo;

import com.wellness.wellness_backend.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
