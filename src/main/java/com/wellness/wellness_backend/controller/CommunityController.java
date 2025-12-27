package com.wellness.wellness_backend.controller;

import com.wellness.wellness_backend.model.*;
import com.wellness.wellness_backend.service.*;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/community")
public class CommunityController {

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;
    private final PractitionerService practitionerService;

    public CommunityController(QuestionService questionService,
                               AnswerService answerService,
                               UserService userService,
                               PractitionerService practitionerService) {
        this.questionService = questionService;
        this.answerService = answerService;
        this.userService = userService;
        this.practitionerService = practitionerService;
    }

    // USER asks question
    @PostMapping("/questions")
    public Question askQuestion(@RequestParam String content,
                                Principal principal) {

        User user = userService.getByEmail(principal.getName());
        return questionService.askQuestion(user, content);
    }

    // anyone views questions
    @GetMapping("/questions")
    public List<Question> getQuestions() {
        return questionService.getAllQuestions();
    }

    // PRACTITIONER answers
    @PostMapping("/questions/{questionId}/answers")
    public Answer answerQuestion(@PathVariable Long questionId,
                                 @RequestParam String content,
                                 Principal principal) {

    	User user = userService.getByEmail(principal.getName());
    	Practitioner practitioner =
    	        practitionerService.getByUserId(user.getId());


        Question question = questionService.getById(questionId);

        return answerService.answerQuestion(
                question, practitioner, content);
    }

    // view answers
    @GetMapping("/questions/{questionId}/answers")
    public List<Answer> getAnswers(@PathVariable Long questionId) {
        return answerService.getAnswers(questionId);
    }
}
