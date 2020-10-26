package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class NoteController {

    private NoteService noteService;
    private UserService userService;

    public NoteController(NoteService noteService, UserService userService) {
        this.noteService = noteService;
        this.userService = userService;
    }

    @PostMapping("/note")
    public String createNote(Authentication authentication, Note note) {

        Integer userId = this.userService.getUserId(authentication.getName());
        note.setUserId(userId);
        if (note.getNoteId() == null) {
            noteService.addNote(note);
        } else {
            noteService.updateNote(note);
        }

        return "redirect:/result?success";
    }

    @GetMapping("/note/delete")
    public String deleteNote(Authentication authentication, @RequestParam("noteId") Integer noteId) {
        noteService.deleteNote(noteId);
        return "redirect:/result?success";
    }

}
