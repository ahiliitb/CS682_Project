package com.SE.final_project.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ItemImageStorageService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp");

    private final Path uploadDir;

    public ItemImageStorageService(@Value("${app.item-upload.dir:uploads/item-images}") String dir) {
        this.uploadDir = Paths.get(dir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not create upload directory: " + this.uploadDir, e);
        }
    }

    /**
     * Saves an image and returns a web path such as {@code /uploads/item-images/uuid.jpg}, or empty if no file.
     */
    public Optional<String> store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Optional.empty();
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("Only JPEG, PNG, GIF, or WebP images are allowed.");
        }
        String ext = extensionFor(file.getOriginalFilename(), contentType);
        String filename = UUID.randomUUID() + ext;
        Path dest = uploadDir.resolve(filename);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not save image", e);
        }
        return Optional.of("/uploads/item-images/" + filename);
    }

    private static String extensionFor(String originalName, String contentType) {
        if (originalName != null) {
            int dot = originalName.lastIndexOf('.');
            if (dot >= 0 && dot < originalName.length() - 1) {
                String ext = originalName.substring(dot).toLowerCase(Locale.ROOT);
                if (Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp").contains(ext)) {
                    return ext.equals(".jpeg") ? ".jpg" : ext;
                }
            }
        }
        return switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
    }
}
