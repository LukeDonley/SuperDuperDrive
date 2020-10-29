package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.services.CredentialService;
import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    private FileService fileService;
    private NoteService noteService;
    private UserService userService;
    private CredentialService credentialService;

    public HomeController(FileService fileService, NoteService noteService, UserService userService, CredentialService credentialService) {
        this.fileService = fileService;
        this.noteService = noteService;
        this.userService = userService;
        this.credentialService = credentialService;
    }

    @RequestMapping("/home")
    public String getHomePage(Model model, Authentication authentication) {
        Integer userId = this.userService.getUserId(authentication.getName());
        if(userId == 0) {
            return "redirect:/login";
        }
        model.addAttribute("files", fileService.getFiles(userId));
        model.addAttribute("notes", noteService.getNotes(userId));
        model.addAttribute("credentials", credentialService.getCredentials(userId));
        model.addAttribute("credentialService", credentialService);
        return "home";
    }

}
