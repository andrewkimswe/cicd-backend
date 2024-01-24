import React, { useState } from 'react';
import OAuth2LoginButtons from '../components/OAuth2LoginButtons';
import './SignUp.css'; // Reuse the same CSS file for styling
import { Link, useNavigate } from 'react-router-dom';

function Signup() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate(); // Create an instance of useNavigate

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await fetch('http://localhost:8080/api/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password })
            });
            if (!response.ok) {
                const errorData = await response.json(); // 서버로부터의 응답 본문을 가져옵니다.
                console.log("회원가입 실패: ", errorData);
                // 에러 메시지를 사용자에게 표시하거나 로깅합니다.
                alert(`회원가입 실패: ${errorData.message}`); // 예를 들어 서버가 응답한 메시지를 표시합니다.
            } else {
                console.log("회원가입 성공");
                navigate('/login'); // 성공 시 로그인 페이지로 이동합니다.
            }
        } catch (error) {
            console.error("회원가입 중 오류 발생", error);
        }
    };



    return (
        <div className="login-container">
            <header>
                <h2>Sign Up</h2>
            </header>
            <main>
                <form onSubmit={handleSubmit}>
                    <input
                        type="email"
                        placeholder="Email"
                        required
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                    />
                    <input
                        type="password"
                        placeholder="Password"
                        required
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                    <button type="submit">Sign Up</button>
                </form>
                <p>
                    Already have an account? <Link to="/login">Login</Link>
                </p>
                <OAuth2LoginButtons /> {/* "Login With Google" 버튼을 렌더링합니다. */}
            </main>
        </div>
    );
}

export default Signup;
