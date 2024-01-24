import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './BulletinBoard.css';
import { useNavigate } from 'react-router-dom';

function BulletinBoard() {
    const navigate = useNavigate();
    const [posts, setPosts] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [currentPage, setCurrentPage] = useState(1);
    const postsPerPage = 10; // 한 페이지에 표시할 게시물 수

    const indexOfLastPost = currentPage * postsPerPage;
    const indexOfFirstPost = indexOfLastPost - postsPerPage;
    const currentPosts = posts.slice(indexOfFirstPost, indexOfLastPost);

    const paginate = pageNumber => setCurrentPage(pageNumber);



    useEffect(() => {
        axios.get('http://localhost:8080/api/posts')
            .then(response => {
                if (Array.isArray(response.data)) {
                    setPosts(response.data);
                } else {
                    throw new Error('Data is not an array');
                }
            })
            .catch(err => {
                setError(`Error fetching posts: ${err.message}`);
            })
            .finally(() => {
                setLoading(false);
            });
    }, []);

    const handleLogout = () => {
        axios.post('http://localhost:8080/api/logout')
            .then(() => {
                navigate('/login');
            })
            .catch(error => {
                console.error('Logout failed:', error);
            });
    };


    const handleSearch = () => {
        setLoading(true);
        axios.get(`http://localhost:8080/api/posts?search=${searchTerm}`)
            .then(response => {
                setPosts(response.data);
                setCurrentPage(1);
            })
            .catch(err => {
                setError(`Error fetching posts: ${err.message}`);
            })
            .finally(() => {
                setLoading(false);
            });
    };



    const goToCreatePost = () => {
        navigate('/create-post'); // 글쓰기 페이지로 이동
    };


    return (
        <div className="board-container">
            <header className="board-header">
                <button className="logout-button" onClick={handleLogout}>Logout</button>
                <button className="create-post-button" onClick={goToCreatePost}>글쓰기</button>
            </header>
            <h1 className="board-title">Nefer</h1>
            {loading ? (
                <div>Loading...</div>
            ) : error ? (
                <p>{error}</p>
            ) : (
                currentPosts.map(post => (
                    <div key={post.id} className="post-item">
                        <h3 onClick={() => navigate(`/posts/${post.id}`)}>{post.title}</h3>
                        <div className="post-info">
                            <span>{post.nickname}</span>
                            <span>{new Date(post.createdAt).toLocaleDateString()}</span>
                            <span>{post.type}</span>
                            <span>Views: {post.views}</span> {/* Views displayed */}
                            <span>Likes: {post.likes}</span> {/* Likes displayed */}
                        </div>
                    </div>
                ))
            )}
            <div className="pagination">
                {[...Array(Math.ceil(posts.length / postsPerPage)).keys()].map(number => (
                    <button key={number} onClick={() => paginate(number + 1)}>
                        {number + 1}
                    </button>
                ))}
            </div>
            <div className="search-bar">
                <input
                    type="text"
                    placeholder="Search..."
                    value={searchTerm}
                    onChange={e => setSearchTerm(e.target.value)}
                />
                <button onClick={handleSearch}>Search</button>
            </div>
        </div>
    );
}

export default BulletinBoard;
