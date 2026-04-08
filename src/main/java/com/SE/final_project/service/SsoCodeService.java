package com.SE.final_project.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.SE.final_project.model.NotificationType;
import com.SE.final_project.model.SsoAccessCode;
import com.SE.final_project.model.User;
import com.SE.final_project.repository.SsoAccessCodeRepository;
import com.SE.final_project.repository.UserRepository;

@Service
public class SsoCodeService {

    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private final SsoAccessCodeRepository ssoAccessCodeRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final SecureRandom random = new SecureRandom();

    @Value("${app.sso.code-ttl-minutes:15}")
    private int codeTtlMinutes;

    public SsoCodeService(SsoAccessCodeRepository ssoAccessCodeRepository,
                          UserRepository userRepository,
                          NotificationService notificationService) {
        this.ssoAccessCodeRepository = ssoAccessCodeRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public List<SsoAccessCode> getActiveCodes() {
        return ssoAccessCodeRepository.findTop50ByUsedAtIsNullAndExpiresAtAfterOrderByCreatedAtDesc(LocalDateTime.now());
    }

    public List<SsoAccessCode> getRecentCodes() {
        return ssoAccessCodeRepository.findTop100ByOrderByCreatedAtDesc();
    }

    @Transactional
    public SsoAccessCode issueCode(String adminUsername) {
        User admin = requireUser(adminUsername);
        String code = generateCode();
        SsoAccessCode accessCode = new SsoAccessCode(code, LocalDateTime.now().plusMinutes(Math.max(1, codeTtlMinutes)), admin);
        return ssoAccessCodeRepository.save(accessCode);
    }

    @Transactional
    public void consumeCode(String code, String email) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("SSO code is required.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }

        SsoAccessCode accessCode = ssoAccessCodeRepository.findByCode(code.trim());
        if (accessCode == null) {
            throw new IllegalArgumentException("Invalid SSO code.");
        }
        if (!accessCode.isUsable(LocalDateTime.now())) {
            throw new IllegalArgumentException("SSO code expired or already used.");
        }

        accessCode.markUsed(email.trim());
        ssoAccessCodeRepository.save(accessCode);

        User user = userRepository.findByEmail(email.trim());
        if (user != null) {
            notificationService.notifyUser(user.getUsername(),
                    "Secure login completed",
                    "Your account was accessed through campus SSO.",
                    NotificationType.SECURITY,
                    true);
        }
    }

    private User requireUser(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + username);
        }
        return user;
    }

    private String generateCode() {
        String code;
        do {
            StringBuilder builder = new StringBuilder("IITB-");
            for (int i = 0; i < 8; i++) {
                builder.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
            }
            code = builder.toString();
        } while (ssoAccessCodeRepository.findByCode(code) != null);
        return code;
    }
}
