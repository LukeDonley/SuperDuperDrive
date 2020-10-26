package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CredentialController {

    private CredentialService credentialService;
    private UserService userService;

    public CredentialController(CredentialService credentialService, UserService userService) {
        this.credentialService = credentialService;
        this.userService = userService;
    }

    @PostMapping("/credential")
    public String createCredential(Authentication authentication, Credential credential) {
        Integer userId = this.userService.getUserId(authentication.getName());
        credential.setUserId(userId);

        if(credential.getCredentialId() == null) {
            credentialService.createCredential(credential);
        }
        else {
            credentialService.updateCredential(credential);
        }

        return "redirect:/result?success";
    }

    @GetMapping("/credential/delete")
    public String updateCredential(Authentication authentication, @RequestParam("credentialId") Integer credentialId) {
        this.credentialService.deleteCredential(credentialId);

        return "redirect:/result?success";
    }
}
