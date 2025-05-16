package com.example.controller;

import com.example.model.Reminder;
import com.example.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderRepository reminderRepository;

    @PostMapping
    public Reminder create(@RequestBody Reminder reminder) {
        return reminderRepository.save(reminder);
    }

    @GetMapping
    public List<Reminder> getAll() {
        return reminderRepository.findAll();
    }

    @GetMapping("/{id}")
    public Reminder getById(@PathVariable String id) {
        return reminderRepository.findById(id).orElseThrow();
    }

    @PutMapping("/{id}")
    public Reminder update(@PathVariable String id, @RequestBody Reminder updated) {
        Reminder reminder = reminderRepository.findById(id).orElseThrow();
        reminder.setTitle(updated.getTitle());
        reminder.setDescription(updated.getDescription());
        reminder.setReminderTime(updated.getReminderTime());
        reminder.setPriority(updated.getPriority());
        return reminderRepository.save(reminder);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        reminderRepository.deleteById(id);
    }
}
