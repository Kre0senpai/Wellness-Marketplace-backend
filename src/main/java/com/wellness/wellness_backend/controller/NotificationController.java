package com.wellness.wellness_backend.controller;

import com.wellness.wellness_backend.model.Notification;
import com.wellness.wellness_backend.model.User;
import com.wellness.wellness_backend.service.NotificationService;
import com.wellness.wellness_backend.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    public NotificationController(NotificationService notificationService,
                                  UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping
    public List<Notification> getMyNotifications(Principal principal) {

        User user = userService.getByEmail(principal.getName());
        return notificationService.getUserNotifications(user.getId());
    }
}
