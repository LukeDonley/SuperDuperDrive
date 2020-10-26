package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.CredentialMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CredentialService {

    private CredentialMapper credentialMapper;

    public CredentialService(CredentialMapper credentialMapper) {
        this.credentialMapper = credentialMapper;
    }

    public void createCredential(Credential credential) {
        this.credentialMapper.insert(credential);
    }

    public void updateCredential(Credential credential) {
        this.credentialMapper.update(credential);
    }

    public void deleteCredential(Integer credentialId) {
        this.credentialMapper.delete(credentialId);
    }

    public List<Credential> getCredentials(Integer userId) {
        return this.credentialMapper.getCredentialsForUser(userId);
    }
}
