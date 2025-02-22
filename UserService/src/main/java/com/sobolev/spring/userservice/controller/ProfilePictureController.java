package com.sobolev.spring.userservice.controller;

import com.sobolev.spring.userservice.security.JwtTokenUtils;
import com.sobolev.spring.userservice.service.MinioService;
import com.sobolev.spring.userservice.service.UserDetailService;
import com.sobolev.spring.userservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile/picture")
public class ProfilePictureController {

    private final MinioService minioService;
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadProfilePicture(@RequestParam("file") MultipartFile file,
                                                       HttpServletRequest request) {

        String username = jwtTokenUtils.getUsernameFromToken(jwtTokenUtils.extractToken(request));
        String fileUrl = minioService.uploadProfilePicture(file, username);

        userService.updateProfilePicture(username, fileUrl);
        return ResponseEntity.ok("Your Image Uploaded Successfully");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteProfilePicture(HttpServletRequest request) {

        String username = jwtTokenUtils.getUsernameFromToken(jwtTokenUtils.extractToken(request));
        minioService.deleteProfilePicture(username);
        userService.updateProfilePicture(username, null);
        return ResponseEntity.ok("Profile picture deleted");
    }

    @GetMapping
    public ResponseEntity<InputStreamResource> getProfilePicture(HttpServletRequest request) {
        String username = jwtTokenUtils.getUsernameFromToken(jwtTokenUtils.extractToken(request));

        String fileUrl = userService.getProfilePictureUrl(username);
        System.out.println(fileUrl);

        try{
            InputStreamResource inputStreamResource = minioService.downloadProfilePicture(fileUrl);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(inputStreamResource);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
