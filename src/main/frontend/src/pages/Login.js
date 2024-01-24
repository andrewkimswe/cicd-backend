import React, { useState } from 'react';
import OAuth2LoginButtons from '../components/OAuth2LoginButtons';
import './Login.css';
import { Link, useNavigate } from 'react-router-dom';

function Login() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();

        // 특정 닉네임(admin)과 비밀번호(0000)로 로그인을 시도합니다.
        if (username === 'admin' && password === '0000') {
            // 로그인 성공 시 '/admin' 페이지로 이동합니다.
            navigate('/posts');
        } else {
            // 로그인 실패 시 에러 처리 로직을 추가할 수 있습니다.
            console.error('Login failed');
        }
    };

    return (
        <div className="login-container">
            <header>
                <h2>Login to Your Account</h2>
            </header>
            <main>
                <form onSubmit={handleSubmit}>
                    <input
                        type="text"
                        placeholder="Username"
                        required
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                    />
                    <input
                        type="password"
                        placeholder="Password"
                        required
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                    <button type="submit">Login</button>
                </form>
                <p>
                    Don't have an account? <Link to="/signup">Sign Up</Link>
                </p>
                <OAuth2LoginButtons />
            </main>
        </div>
    );
}

export default Login;
