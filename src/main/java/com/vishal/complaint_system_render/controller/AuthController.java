package com.vishal.complaint_system_render.controller;


import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vishal.complaint_system_render.entity.User;
import com.vishal.complaint_system_render.repository.UserRepository;
import com.vishal.complaint_system_render.service.EmailService;

import jakarta.servlet.http.HttpSession;

@RestController

@RequestMapping("/api/auth")

public class AuthController {
    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository repo;

    // REGISTER
    @PostMapping("/register")
    public String register(@RequestBody User user) {

        try {
            if (repo.findByEmail(user.getEmail()) != null) {
                return "Email already exists";
            }

            // Default role if not provided
            if (user.getRole() == null || user.getRole().isEmpty()) {
                user.setRole("USER");
            }

            repo.save(user);
            return "User registered successfully";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/send-register-otp")
    public String sendRegisterOtp(
            @RequestBody User user,
            HttpSession session) throws Exception {

        // CHECK EMAIL EXISTS
        if (repo.findByEmail(user.getEmail()) != null) {
            return "Email already registered";
        }

        // GENERATE OTP
        String otp = String.format(
                "%06d",
                new Random().nextInt(1_000_000)
        );

        // STORE OTP
        user.setOtp(otp);

        user.setOtpGeneratedTime(LocalDateTime.now());

        // STORE TEMP USER IN SESSION
        session.setAttribute("tempUser", user);

        // SEND EMAIL
        emailService.sendOtpEmail(
                user.getEmail(),
                otp
        );

        return "OTP sent successfully";
    }


    @PostMapping("/verify-register-otp")
    public String verifyRegisterOtp(
            @RequestBody User requestUser,
            HttpSession session) {

        // GET TEMP USER
        User tempUser =
                (User) session.getAttribute("tempUser");

        if (tempUser == null) {
            throw new RuntimeException(
                    "Session expired"
            );
        }

        // CHECK OTP EXPIRY
        if (tempUser.getOtpGeneratedTime()
                .plusMinutes(5)
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "OTP expired"
            );
        }

        // VERIFY OTP
        if (!tempUser.getOtp()
                .equals(requestUser.getOtp())) {

            throw new RuntimeException(
                    "Invalid OTP"
            );
        }

        // SAVE USER
        repo.save(tempUser);

        // CLEAR SESSION
        session.removeAttribute("tempUser");

        return "Registration successful";
    }

    // LOGIN


    @PostMapping("/login")
    public User login(@RequestBody User user, HttpSession session) {

        User existingUser = repo.findByEmail(user.getEmail());

        if (existingUser == null) {
            throw new RuntimeException("User not found");
        }

        if (!existingUser.getPassword().equals(user.getPassword())) {
                throw new RuntimeException("Invalid password");
        }

        session.setAttribute("user", existingUser);

        return existingUser;
    }
        @GetMapping("/me")
        public User getCurrentUser(HttpSession session) {

        User user = (User) session.getAttribute("user");

        System.out.println("=================================");
        System.out.println("SESSION USER = " + user);
        System.out.println("SESSION ID = " + session.getId());
        System.out.println("=================================");

        return user;
        }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "Logged out";
    }

    @PutMapping("/update-profile")
    public User updateProfile(
            @RequestBody User updatedUser,
            HttpSession session) {

        // ✅ Get logged-in user from session
        User sessionUser = (User) session.getAttribute("user");

        if (sessionUser == null) {
            throw new RuntimeException("User not logged in");
        }

        // ✅ Find user using ID
        User existingUser = repo.findById(sessionUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ Update fields
        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setAddress(updatedUser.getAddress());

        // ✅ Save
        User savedUser = repo.save(existingUser);

        // ✅ Update session
        session.setAttribute("user", savedUser);

        return savedUser;
    }

    @PostMapping("/send-otp")
    public String sendOtp(
            @RequestBody User requestUser)
            throws Exception {

        // FIND USER
        User user =
                repo.findByEmail(
                        requestUser.getEmail()
                );

        // USER NOT FOUND
        if (user == null) {

            return "User not found. Please register first.";
        }

        // GENERATE OTP
        String otp = String.format(
                "%06d",
                new Random().nextInt(999999)
        );

        // SAVE OTP
        user.setOtp(otp);

        user.setOtpGeneratedTime(
                LocalDateTime.now()
        );

        repo.save(user);

        // SEND EMAIL
        emailService.sendOtpEmail(
                user.getEmail(),
                otp
        );

        return "OTP sent successfully";
    }

    @PostMapping("/verify-otp")
    public User verifyOtp(
            @RequestBody User requestUser,
            HttpSession session) {

        User user =
                repo.findByEmail(
                        requestUser.getEmail()
                );

        if (user == null) {

            throw new RuntimeException(
                    "User not found"
            );
        }

        // DEBUG
        System.out.println(
                "Saved OTP: " + user.getOtp()
        );

        System.out.println(
                "Entered OTP: " + requestUser.getOtp()
        );

        // Remove spaces
        String savedOtp =
                user.getOtp().trim();

        String enteredOtp =
        requestUser.getOtp().trim();


        // ✅ CHECK OTP EXPIRY
        if (user.getOtpGeneratedTime()
                .plusMinutes(5)
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "OTP expired"
            );
        }


        // ✅ VERIFY OTP
        if (!savedOtp.equals(enteredOtp)) {

            throw new RuntimeException(
                    "Invalid OTP"
            );
        }

        // OPTIONAL:
        // clear OTP after login
        user.setOtp(null);

        repo.save(user);

        // LOGIN SUCCESS
        session.setAttribute("user", user);

        return user;
    }
}
