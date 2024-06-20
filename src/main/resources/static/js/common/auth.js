const Auth = {
    init: function() {
        this.setupLogin();
        this.setupLogout();
        this.autoLogin();
    },

    setupLogin: function() {
        const loginForm = document.getElementById('loginForm');
        if (loginForm) {
            loginForm.addEventListener('submit', this.login.bind(this));
        }
    },

    setupLogout: function() {
        const logoutButton = document.getElementById('logout-button');
        if (logoutButton) {
            logoutButton?.addEventListener('click', this.logout.bind(this));
        }
    },

    login: async function(event) {
        event.preventDefault(); // 기본 폼 제출 동작 방지

        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        const rememberMe = document.getElementById('remember-me').checked;

        this.clearErrorMessages();

        try {
            const response = await fetch('/api/auth/login/v1', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    username: username,
                    password: password,
                    rememberMe: rememberMe
                }),
                credentials: 'include' // 쿠키를 포함하도록 설정
            });

            if (!response.ok) {
                const errorData = await response.json();
                const contentType = response.headers.get('content-type');
                if (contentType && contentType.indexOf('application/json') !== -1) {
                    this.displayErrorMessages(errorData);
                } else if (errorData.error) {
                    console.log(errorData.error);
                    alert(errorData.error)
                }
            }

            const contentType = response.headers.get('content-type');
            if (contentType && contentType.indexOf('application/json') !== -1) {
                const data = await response.json();
                console.log('로그인 성공:', data);
                // 자동 로그인 설정 저장
                if (rememberMe) {
                    localStorage.setItem('rememberMe', 'true');
                } else {
                    localStorage.removeItem('rememberMe');
                }
                // 메인 페이지로 리다이렉트
                window.location.href = '/crispy/main';
            } else {
                throw new Error('Unexpected response format');
            }
        } catch (error) {
        }
    },
    displayErrorMessages: function(errors) {
        if (errors.username) {
            const usernameError = document.getElementById('username-error');
            usernameError.textContent = errors.username;
            usernameError.style.display = 'block';
            document.getElementById("username").focus();
            return;
        }
        if (errors.password) {
            const passwordError = document.getElementById('password-error');
            passwordError.textContent = errors.password;
            passwordError.style.display = 'block';
            document.getElementById("password").focus();
            return;
        }
        if (errors.error) {
            Swal.fire({
                icon: "warning",
                text: errors.error,
                width: "365px",
                timer: 1500,
            })
        }
    },

    clearErrorMessages: function() {
        const errorMessages = document.querySelectorAll('.error-message');
        errorMessages.forEach(error => {
            error.style.display = 'none';
            error.textContent = '';
        });
    },

    logout: async function() {
        try {
            const response = await fetch('/api/auth/logout', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'include' // 쿠키를 포함하도록 설정
            });

            if (response.ok) {
                localStorage.removeItem('rememberMe'); // 자동 로그인 설정 제거
                // 로그인 페이지로 리다이렉트
                window.location.href = '/crispy/login';
            } else {
            }
        } catch (error) {
        }
    },

    saveTokens: function(accessToken, refreshToken) {
        document.cookie = `accessToken=${accessToken}; Path=/;`;
        document.cookie = `refreshToken=${refreshToken}; Path=/;`;
    },

    getCookie: function(name) {
        const value = `; ${document.cookie}`;
        const parts = value.split(`; ${name}=`);
        if (parts.length === 2) return parts.pop().split(';').shift();
    },

    getAccessToken: function() {
        return this.getCookie('accessToken');
    },

    getRefreshToken: function() {
        return this.getCookie('refreshToken');
    },

    autoLogin: async function() {
        const rememberMe = localStorage.getItem('rememberMe') === 'true';
        if (rememberMe) {
            await this.refreshAccessToken();
        } else {
            await this.checkAccessToken();
        }
    },

    checkAccessToken: async function() {
        const accessToken = this.getAccessToken();
        if (!accessToken) {
            await this.logout();
        }
    },

    authenticatedFetch: async function(url, options = {}) {
        const accessToken = this.getAccessToken();
        console.log("Fetch: ", accessToken);

        if (!options.headers) {
            options.headers = {};
        }

        options.headers['Authorization'] = 'Bearer ' + accessToken;
        console.log("Headers: ", options.headers);

        let response = await fetch(url, options);
        console.log("Response: ", response);

        if (response.status === 401) {
            console.log('액세스 토큰 만료됨. 리프레시 토큰으로 재발급 시도.');
            const newAccessToken = await this.refreshAccessToken();
            if (newAccessToken) {
                options.headers['Authorization'] = 'Bearer ' + newAccessToken;
                response = await fetch(url, options); // 다시 요청 보내기
            } else {
                localStorage.removeItem('rememberMe'); // 자동 로그인 설정 제거
                window.location.href = '/crispy/login';
            }
        }
        return response;
    },

    refreshAccessToken: async function() {
        const refreshToken = this.getRefreshToken();
        if (!refreshToken) {
            window.location.href = '/crispy/login';
            return null;
        }

        const response = await fetch('/api/auth/refresh', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include' // 쿠키를 포함하도록 설정
        });

        if (response.ok) {
            const data = await response.json();
            this.saveTokens(data.accessToken, data.refreshToken);
            return data.accessToken;
        } else {
            console.error('리프레시 토큰을 통한 액세스 토큰 재발급 실패');
            localStorage.removeItem('rememberMe'); // 자동 로그인 설정 제거
            window.location.href = '/crispy/login';
            return null;
        }
    }
};

// Initialize Auth and setup login and authenticated fetch
document.addEventListener('DOMContentLoaded', () => {
    Auth.init();
});