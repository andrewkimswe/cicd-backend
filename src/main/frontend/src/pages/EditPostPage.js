import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router-dom';
import './EditPostPage.css';

function EditPostPage() {
    const [post, setPost] = useState({ title: '', content: '', nickname: '' });
    const [errors, setErrors] = useState({ title: '', content: '' }); // 추가: 에러 상태
    const { postId } = useParams();
    const navigate = useNavigate();

    useEffect(() => {
        fetchPostData();
    }, [postId]);

    const fetchPostData = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/api/posts/${postId}`);
            const postData = response.data;
            setPost({
                title: postData.title,
                content: postData.content,
                nickname: postData.nickname
            });
        } catch (error) {
            console.error('Error fetching post:', error);
        }
    };

    const navigateToBulletinBoard = () => {
        navigate('/posts');
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setPost((prevPost) => ({ ...prevPost, [name]: value }));

        if (name === 'title') {
            setErrors((prevErrors) => ({ ...prevErrors, title: value ? '' : 'Title is required' }));
        } else if (name === 'content') {
            setErrors((prevErrors) => ({ ...prevErrors, content: value ? '' : 'Content is required' }));
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        // Prompt for password
        const enteredPassword = prompt("Please enter your password to edit this post:");
        if (!enteredPassword) {
            alert("Password is required");
            return;
        }

        if (!post.title || !post.content) {
            // Handle case where title or content is missing
            alert("Title and content are required");
            return;
        }

        try {
            const updateData = { ...post, password: enteredPassword };
            await axios.put(`http://localhost:8080/api/posts/${postId}`, updateData);
            navigate(`/posts/${postId}`);
        } catch (error) {
            console.error('Error updating post:', error);
            if (error.response && error.response.status === 403) {
                alert("Incorrect password");
            }
        }
    };


    return (
        <div className="edit-post-container">
            <div className="button-container">
                <button onClick={navigateToBulletinBoard}>Back to Bulletin Board</button>
            </div>
            <h1>Edit Post</h1>
            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label htmlFor="title">Title</label>
                    <input
                        type="text"
                        id="title"
                        name="title"
                        value={post.title}
                        onChange={handleInputChange}
                        aria-label="Title"
                    />
                    {errors.title && <span className="error">{errors.title}</span>}
                </div>
                <div className="form-group">
                    <label htmlFor="content">Content</label>
                    <textarea
                        id="content"
                        name="content"
                        value={post.content}
                        onChange={handleInputChange}
                        aria-label="Content"
                    />
                    {errors.content && <span className="error">{errors.content}</span>}
                </div>
                <button type="submit">Update Post</button>
            </form>
        </div>
    );
}

export default EditPostPage;
