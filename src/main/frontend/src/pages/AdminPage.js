import React, { useState, useEffect } from 'react';
import axios from 'axios';

function AdminPage() {
    const [users, setUsers] = useState([]);

    // 사용자 목록을 가져오는 함수
    const fetchUsers = async () => {
        try {
            const response = await axios.get('http://localhost:8080/api/users');
            setUsers(response.data);
        } catch (error) {
            console.error('Error fetching users:', error);
        }
    };

    useEffect(() => {
        fetchUsers();
    }, []);

    const handleChangeRole = async (userId, newRole) => {
        try {
            await axios.put(`http://localhost:8080/api/users/${userId}/role?role=${newRole}`);
            fetchUsers();
        } catch (error) {
            console.error('Error changing role:', error);
        }
    };

    return (
        <div>
            <h2>Admin Page</h2>
            <ul>
                {users.map(user => (
                    <li key={user.id}>
                        {user.email} - Role: {user.role}
                        <button onClick={() => handleChangeRole(user.id, 'ADMIN')}>Set as Admin</button>
                        <button onClick={() => handleChangeRole(user.id, 'USER')}>Set as User</button>
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default AdminPage;
