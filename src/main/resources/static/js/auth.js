const Auth = {
    init: function() {
        this.setupLogin();
        this.setupLogout();
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
            logoutButton.addEventListener('click', this.logout.bind(this));
        }
    },

    login: async function(event) {
        event.preventDefault(); // 기본 폼 제출 동작 방지

        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        console.log("로그인 요청 호출됨");
        try {
            const response = await fetch('/crispy/api/auth/login/v1', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    username: username,
                    password: password
                }),
                credentials: 'include' // 쿠키를 포함하도록 설정
            });

            if (!response.ok) {
                console.log('응답 상태 코드:', response.status); // 응답 상태 코드 출력
                const text = await response.text();
                console.error('로그인 실패 응답:', text);
                throw new Error('로그인 실패');
            }

            const contentType = response.headers.get('content-type');
            if (contentType && contentType.indexOf('application/json') !== -1) {
                const data = await response.json();
                console.log('로그인 성공:', data);
                // 메인 페이지로 리다이렉트
                window.location.href = '/crispy/main';
            } else {
                const text = await response.text();
                console.error('예상치 못한 응답:', text);
                throw new Error('Unexpected response format');
            }
        } catch (error) {
            console.error('로그인 실패:', error);
        }
    },

    logout: async function() {
        console.log("로그아웃 요청 호출됨");
        try {
            const response = await fetch('/crispy/api/auth/logout', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'include' // 쿠키를 포함하도록 설정
            });

            if (response.ok) {
                console.log('로그아웃 성공');
                // 로그인 페이지로 리다이렉트
                window.location.href = '/crispy/login';
            } else {
                console.error('로그아웃 실패');
            }
        } catch (error) {
            console.error('로그아웃 요청 중 오류 발생:', error);
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

    authenticatedFetch: async function(url, options = {}) {
        const accessToken = this.getAccessToken();
        console.log("Fetch: ", accessToken);

        if (!options.headers) {
            options.headers = {};
        }

        options.headers['Authorization'] = 'Bearer ' + accessToken;
        console.log("Headers: ", options.headers);

        const response = await fetch(url, options);
        console.log("Response: ", response);

        if (response.status === 401) {
            const newAccessToken = await this.refreshAccessToken();
            if (newAccessToken) {
                options.headers['Authorization'] = 'Bearer ' + newAccessToken;
                return fetch(url, options);
            }
        }
        console.log(response);
        return response;
    },

    refreshAccessToken: async function() {
        const refreshToken = this.getRefreshToken();
        const response = await fetch('/crispy/api/auth/refresh', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + refreshToken
            },
            credentials: 'include' // 쿠키를 포함하도록 설정
        });

        if (response.ok) {
            const data = await response.json();
            this.saveTokens(data.accessToken, data.refreshToken);
            return data.accessToken;
        } else {
            console.error('Failed to refresh access token');
            window.location.href = '/crispy/login';
            return null;
        }
    },

    checkAuthentication: function() {
        const accessToken = this.getAccessToken();
        if (!accessToken) {
            window.location.href = '/crispy/login';
        } else {
            this.fetchUserInfo();
        }
    },

    fetchUserInfo: function() {
        this.authenticatedFetch('/crispy/api/auth/me')
            .then(response => {
                console.log(response);
                if (!response.ok) {
                    throw new Error('Failed to fetch user info');
                }
                return response.json();
            })
            .then(data => {
                console.log('User data:', data);
                console.log("작동중?");
                // 사용자 데이터를 사용하여 페이지를 설정합니다.
                document.getElementById('empId').innerText = data.empId;
            })
            .catch(error => {
                console.error('Error:', error);
                // 로그인 페이지로 리다이렉트 또는 에러 처리
                window.location.href = '/crispy/login';
            });
    }
};

// Initialize Auth and setup login and authenticated fetch
document.addEventListener('DOMContentLoaded', () => {
    Auth.init();
});
