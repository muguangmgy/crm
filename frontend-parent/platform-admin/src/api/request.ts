import axios, {
    type AxiosInstance,
    type AxiosRequestConfig,
    type AxiosResponse,
    type InternalAxiosRequestConfig
} from 'axios'
import {ElMessage} from 'element-plus'
import type {ApiResponse} from '@/types/api'
import {useUserStore} from '@/stores/user'
import router from '@/router'

const BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api'

const instance: AxiosInstance = axios.create({
    baseURL: BASE_URL,
    timeout: 20000,
    headers: {
        'Content-Type': 'application/json'
    }
})

let isRefreshing = false
let hasRedirectedToLogin = false
let refreshSubscribers: ((token: string) => void)[] = []

function subscribeTokenRefresh(cb: (token: string) => void) {
    refreshSubscribers.push(cb)
}

function onRefreshed(token: string) {
    refreshSubscribers.forEach(cb => cb(token))
    refreshSubscribers = []
}

function onRefreshFailed() {
    refreshSubscribers = []
}

instance.interceptors.request.use(
    (config) => {
        const userStore = useUserStore()
        const token = userStore.token
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    (error) => Promise.reject(error)
)

instance.interceptors.response.use(
    async (response: AxiosResponse<ApiResponse>) => {
        const {code, message, data} = response.data
        const config = response.config as InternalAxiosRequestConfig

        // 正常响应
        if (code === 200) {
            return data as AxiosResponse
        }

        //1006 只允许单点登录时跳转登录页
        if (code === 1006) {
            handleLogout(message);
            return Promise.reject(new Error(message))
        }

        // 401：Token过期或无效，尝试刷新
        if (code === 401) {
            return handle401(config, message)
        }

        // 403：权限不足
        if (code === 403) {
            ElMessage.error(message || '无访问权限')
            return Promise.reject(new Error(message || '无访问权限'))
        }

        // 其他业务错误
        ElMessage.error(message || '请求失败')
        return Promise.reject(new Error(message || '请求失败'))
    },
    (error) => {
        // 网络错误等非HTTP 200情况
        if (!error.response) {
            ElMessage.error('网络连接失败')
        } else {
            ElMessage.error('服务器错误')
        }
        return Promise.reject(error)
    }
)

async function handle401(config: InternalAxiosRequestConfig, message: string): Promise<any> {
    // 如果已经决定跳转登录页，不再处理
    if (hasRedirectedToLogin) {
        return Promise.reject(new Error(message))
    }

    // 判断是否为refresh接口本身，避免死循环
    const isRefreshRequest = config.url?.includes('/auth/refresh')
    if (isRefreshRequest) {
        handleLogout()
        return Promise.reject(new Error(message))
    }

    // 检查本地是否有token，无则直接登出
    const userStore = useUserStore()
    const token = userStore.token
    if (!token) {
        handleLogout()
        return Promise.reject(new Error(message))
    }

    if (!isRefreshing) {
        isRefreshing = true
        try {
            const res = await axios.post<ApiResponse<{ accessToken: string; expireTime: number }>>(
                `${BASE_URL}/auth/refresh`,
                {},
                {headers: {Authorization: `Bearer ${token}`}}
            )
            const refreshCode = res.data.code
            if (refreshCode === 200) {
                const newToken = res.data.data!.accessToken
                userStore.setToken(newToken)
                onRefreshed(newToken)
                config.headers.Authorization = `Bearer ${newToken}`
                return instance(config)
            } else {
                // 刷新失败（业务错误码401等）
                handleLogout()
                return Promise.reject(new Error(res.data.message || '刷新失败'))
            }
        } catch (err) {
            // 刷新请求本身失败（网络错误）
            handleLogout()
            return Promise.reject(err)
        } finally {
            isRefreshing = false
        }
    } else {
        // 已有刷新请求在进行中，等待刷新结果
        return new Promise((resolve, reject) => {
            subscribeTokenRefresh((newToken: string) => {
                config.headers.Authorization = `Bearer ${newToken}`
                resolve(instance(config))
            })
            setTimeout(() => {
                reject(new Error('Token refresh timeout'))
            }, 10000)
        })
    }
}

function handleLogout(errorMsg?: string) {
    if (hasRedirectedToLogin) return
    hasRedirectedToLogin = true
    onRefreshFailed()
    const userStore = useUserStore()
    userStore.clearToken()
    ElMessage.error(errorMsg ?? '登录已过期，请重新登录')
    router.push('/login')
    setTimeout(() => {
        hasRedirectedToLogin = false
    }, 2000)
}

async function request<T>(config: AxiosRequestConfig): Promise<T> {
    return instance.request<any, T>(config)
}

export const http = {
    get<T>(url: string, params?: object): Promise<T> {
        return request<T>({method: 'GET', url, params})
    },
    post<T>(url: string, data?: object, params?: object): Promise<T> {
        return request<T>({method: 'POST', url, data, params})
    },
    put<T>(url: string, data?: object): Promise<T> {
        return request<T>({method: 'PUT', url, data})
    },
    delete<T>(url: string): Promise<T> {
        return request<T>({method: 'DELETE', url})
    }
}