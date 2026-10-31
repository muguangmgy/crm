import {http} from './request'
import type {CaptchaVO, LoginDTO, LoginVO, PasswordChangeDTO, ProfileUpdateDTO, UserInfoVO} from '@/types/user'

const CLIENT_ID_KEY = 'platform_client_id'

function getClientId(): string {
    let clientId = localStorage.getItem(CLIENT_ID_KEY)
    if (!clientId) {
        clientId = `${Date.now()}-${Math.random().toString(36).substring(2, 9)}`
        localStorage.setItem(CLIENT_ID_KEY, clientId)
    }
    return clientId
}

export const authApi = {
    getCaptcha(): Promise<CaptchaVO> {
        return http.get<CaptchaVO>('/auth/captcha', {clientId: getClientId()})
    },

    login(data: LoginDTO): Promise<LoginVO> {
        return http.post<LoginVO>('/auth/login', data, {clientId: getClientId()})
    },

    refreshToken(): Promise<LoginVO> {
        return http.post<LoginVO>('/auth/refresh')
    },

    logout(): Promise<void> {
        return http.post<void>('/auth/logout')
    },

    getUserInfo(): Promise<UserInfoVO> {
        return http.get<UserInfoVO>('/auth/info')
    },

    updateProfile(data: ProfileUpdateDTO): Promise<void> {
        return http.put<void>('/auth/profile', data)
    },

    changePassword(data: PasswordChangeDTO): Promise<void> {
        return http.put<void>('/auth/password', data)
    }
}