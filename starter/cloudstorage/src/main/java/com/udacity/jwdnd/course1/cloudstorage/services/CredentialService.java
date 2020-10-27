package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.CredentialMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CredentialService {

    private CredentialMapper credentialMapper;
    private EncryptionService encryptionService;

    public CredentialService(CredentialMapper credentialMapper, EncryptionService encryptionService) {
        this.credentialMapper = credentialMapper;
        this.encryptionService = encryptionService;
    }

    public void createCredential(Credential credential) {
        this.encryptPassword(credential);
        this.credentialMapper.insert(credential);
    }

    public void updateCredential(Credential credential) {
        this.encryptPassword(credential);
        this.credentialMapper.update(credential);
    }

    public void deleteCredential(Integer credentialId) {
        this.credentialMapper.delete(credentialId);
    }

    public List<Credential> getCredentials(Integer userId) {
        return this.credentialMapper.getCredentialsForUser(userId);
    }

    private void encryptPassword(Credential credential) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        String key = Base64.getEncoder().encodeToString(salt);
        credential.setKey(key);
        String encryptedPassword = encryptionService.encryptValue(credential.getPassword(), credential.getKey());
        credential.setPassword(encryptedPassword);
    }

    public String getDecryptedPassword(Credential credential) {
        return encryptionService.decryptValue(credential.getPassword(), credential.getKey());
    }

    public List getCredentialsWithPassword(Integer userId) {
        List<Credential> credentials = this.credentialMapper.getCredentialsForUser(userId);
        return credentials.stream().map(credential -> {
            return new Credential(
                    credential.getCredentialId(),
                    credential.getUrl(),
                    credential.getUsername(),
                    null,
                    getDecryptedPassword(credential),
                    credential.getUserId()
            );
        }).collect(Collectors.toList());
    }
}
