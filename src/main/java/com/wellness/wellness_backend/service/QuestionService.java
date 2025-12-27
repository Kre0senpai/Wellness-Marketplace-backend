package com.wellness.wellness_backend.service;

import com.wellness.wellness_backend.model.Question;
import com.wellness.wellness_backend.model.User;
import com.wellness.wellness_backend.repo.QuestionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Question askQuestion(User user, String content) {
        Question q = new Question();
        q.setUser(user);
        q.setContent(content);
        q.setCreatedAt(LocalDateTime.now());
        return questionRepository.save(q);
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public Question getById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
    }
}
