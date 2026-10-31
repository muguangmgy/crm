package com.platform.server.auth.service;

import com.platform.server.auth.dto.LoginDTO;
import com.platform.server.auth.dto.PasswordChangeDTO;
import com.platform.server.auth.dto.ProfileUpdateDTO;
import com.platform.server.auth.vo.CaptchaVO;
import com.platform.server.auth.vo.TokenVO;
import com.platform.server.auth.vo.UserInfoVO;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    CaptchaVO generateCaptcha(String clientId);

    TokenVO login(HttpServletRequest request, LoginDTO loginDTO, String clientId);

    TokenVO refreshToken(String oldAccessToken);

    void logout();

    UserInfoVO getCurrentUserInfo();

    void updateProfile(ProfileUpdateDTO dto);

    void changePassword(PasswordChangeDTO dto);
}