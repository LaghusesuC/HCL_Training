package com.hcl.training.payroll.service;

import com.hcl.training.payroll.domain.entity.AppUser;
import com.hcl.training.payroll.dto.AuthRequest;
import com.hcl.training.payroll.dto.AuthResponse;
import com.hcl.training.payroll.exception.BadRequestException;
import com.hcl.training.payroll.repository.AppUserRepository;
import com.hcl.training.payroll.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final AppUserRepository appUserRepository;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider,
                       AppUserRepository appUserRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.appUserRepository = appUserRepository;
    }

    public AuthResponse authenticateUser(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        AppUser appUser = appUserRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadRequestException("User not found: " + request.username()));

        Long empId = appUser.getEmployee() != null ? appUser.getEmployee().getEmployeeId() : null;
        String fullName = appUser.getEmployee() != null ? appUser.getEmployee().getFullName() : appUser.getUsername();

        return new AuthResponse(jwt, appUser.getUsername(), appUser.getRole().name(), empId, fullName);
    }

    public AuthResponse getCurrentUserProfile(String username) {
        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found: " + username));

        Long empId = appUser.getEmployee() != null ? appUser.getEmployee().getEmployeeId() : null;
        String fullName = appUser.getEmployee() != null ? appUser.getEmployee().getFullName() : appUser.getUsername();

        String token = tokenProvider.generateTokenFromUsername(username, appUser.getRole().name());
        return new AuthResponse(token, appUser.getUsername(), appUser.getRole().name(), empId, fullName);
    }
}
