import React from 'react';
import './OAuth2LoginButtons.css';

function OAuth2LoginButtons() {
    // OAuth2 로그인 플로우로 리다이렉션을 처리하는 함수입니다.
    const handleOAuthLogin = (providerUrl) => {
        window.location.href = providerUrl; // 클릭된 버튼에 해당하는 OAuth2 공급자의 URL로 리다이렉션합니다.
    };

    // 각 OAuth2 공급자에 대한 로그인 플로우를 시작하는 URL입니다.
    const OAUTH_URLS = {
        google: "http://localhost:8080/oauth2/authorization/google"
    };

    return (
        <div className="oauth-buttons">
            <button className="oauth-button google" onClick={() => handleOAuthLogin(OAUTH_URLS.google)}>
                Continue with Google
            </button>
        </div>
    );
}

export default OAuth2LoginButtons;
