package com.wellness.wellness_backend.service;

import com.wellness.wellness_backend.model.*;
import com.wellness.wellness_backend.repo.AnswerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    public AnswerService(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    public Answer answerQuestion(Question question,
                                 Practitioner practitioner,
                                 String content) {

        Answer a = new Answer();
        a.setQuestion(question);
        a.setPractitioner(practitioner);
        a.setContent(content);
        a.setCreatedAt(LocalDateTime.now());

        return answerRepository.save(a);
    }

    public List<Answer> getAnswers(Long questionId) {
        return answerRepository.findByQuestionId(questionId);
    }
}
