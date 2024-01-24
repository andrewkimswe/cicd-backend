import React, { useState } from 'react';
import OAuth2LoginButtons from '../components/OAuth2LoginButtons';
import './SignUp.css'; // Reuse the same CSS file for styling
import { Link, useNavigate } from 'react-router-dom';

function Signup() {
    const [email, setEmail] = useState('');
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate(); // Create an instance of useNavigate

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await fetch('http://localhost:8080/api/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, username, password })
            });
            if (response.ok) {
                console.log("회원가입 성공");
                navigate('/login');
            } else {
                console.log("회원가입 실패");
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
