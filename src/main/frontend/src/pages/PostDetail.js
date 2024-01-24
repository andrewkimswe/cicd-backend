import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router-dom';
import './PostDetail.css';

function PostDetail() {
    const [post, setPost] = useState(null);
    const [comments, setComments] = useState([]);
    const { postId } = useParams();
    const navigate = useNavigate();
    const [newComment, setNewComment] = useState('');

    useEffect(() => {
        const fetchData = async () => {
            const hasViewed = sessionStorage.getItem(`viewed-${postId}`);
            if (!hasViewed) {
                sessionStorage.setItem(`viewed-${postId}`, 'true');
                const headers = {
                    'Increment-View': 'true'
                };

                try {
                    const postResponse = await axios.get(`http://localhost:8080/api/posts/${postId}`, { headers });
                    setPost(postResponse.data);

                    const commentsResponse = await axios.get(`http://localhost:8080/api/comments/posts/${postId}`);
                    setComments(commentsResponse.data);
                } catch (error) {
                    console.error("Error fetching data:", error);
                }
            } else {
                // 이미 조회된 글이므로 추가적인 조회수 증가를 방지
                console.log("Already viewed");
            }
        };

        fetchData();
    }, [postId]);




    const incrementViewCount = async () => {
        try {
            await axios.put(`http://localhost:8080/api/posts/${postId}/increment-views`);
            // Optionally, you can fetch the post again to update the view count in the UI
        } catch (error) {
            console.error("Error incrementing views:", error);
        }
    };

    const fetchPostAndComments = async () => {
        try {
            const headers = {};
            if (!sessionStorage.getItem(`viewed-${postId}`)) {
                sessionStorage.setItem(`viewed-${postId}`, 'true');
                headers['Increment-View'] = 'true';
            }
            const postResponse = await axios.get(`http://localhost:8080/api/posts/${postId}`, { headers });
            setPost(postResponse.data);

            const commentsResponse = await axios.get(`http://localhost:8080/api/comments/posts/${postId}`);
            setComments(commentsResponse.data);
        } catch (error) {
            console.error("Error fetching data:", error);
        }
    };

    const handleEdit = () => {
        navigate(`/edit-post/${postId}`);
    };

    const navigateToBulletinBoard = () => {
        navigate('/posts'); // Navigate to the bulletin board
    };

    const handleDelete = async () => {
        const password = prompt("Please enter your password to delete this post:");
        if (password) {
            try {
                await axios.delete(`http://localhost:8080/api/posts/${postId}`, {
                    data: { password: password }
                });
                navigate('/posts');
            } catch (error) {
                console.error("Error deleting post:", error);
            }
        }
    };


    const handleNewCommentSubmit = async (e) => {
        e.preventDefault();
        const password = prompt('Enter your password:');
        if (!newComment.trim() || !password) return;
        try {
            await axios.post(`http://localhost:8080/api/comments`, {
                postId: postId,
                content: newComment,
                password: password
            });
            fetchComments(); // Fetch comments again to refresh the list
            setNewComment(''); // Clear the new comment input field
        } catch (error) {
            console.error("Error submitting comment:", error);
        }
    };

    const fetchComments = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/api/comments/posts/${postId}`);
            setComments(response.data);
        } catch (error) {
            console.error("Error fetching comments:", error);
        }
    };

    const handleEditComment = async (commentId) => {
        const newCommentText = prompt('Edit your comment:');
        const password = prompt('Enter your password:');
        if (newCommentText && password) {
            try {
                const response = await axios.put(`http://localhost:8080/api/comments/${commentId}`, {
                    content: newCommentText
                }, {
                    params: {
                        password: password
                    }
                });
                fetchComments(); // Refresh comments after editing
                // Update the state with the new comment list
            } catch (error) {
                console.error('Error updating comment:', error);
            }
        }
    };


    const handleDeleteComment = async (commentId) => {
        const password = prompt('Enter your password:');
        if (window.confirm('Are you sure you want to delete this comment?') && password) {
            try {
                await axios.delete(`http://localhost:8080/api/comments/${commentId}`, {
                    params: { password: password } // Send password as a query parameter
                });
                fetchComments(); // Refresh comments after deletion
            } catch (error) {
                console.error('Error deleting comment:', error);
            }
        }
    };

    const handleLike = async () => {
        try {
            await axios.put(`http://localhost:8080/api/posts/${postId}/like`);
            const updatedPost = await axios.get(`http://localhost:8080/api/posts/${postId}`);
            setPost(updatedPost.data);
        } catch (error) {
            console.error("Error liking post:", error);
        }
    };

    return (
        <div className="post-detail-container">
            <div className="button-container">
                <button onClick={navigateToBulletinBoard}>Back to Bulletin Board</button>
                <div>
                    <button onClick={handleEdit}>수정</button>
                    <button onClick={handleDelete}>삭제</button>
                </div>
            </div>
            <h1 className="post-title">{post?.title}</h1>
            <p className="post-content">{post?.content}</p>
            {post?.imageUrl && (
                <img src={post.imageUrl} alt="Post attachment" style={{ maxWidth: '100%' }} />
            )}
            <button onClick={handleLike} className="like-button">Like</button>
            <div className="comments-container">
                {comments.map(comment => (
                    <div key={comment.id} className="comment-item">
                        <div className="comment-content">
                            <p>{comment.content}</p>
                        </div>
                        <div className="comment-buttons">
                            <button onClick={() => handleEditComment(comment.id)} className="edit">수정</button>
                            <button onClick={() => handleDeleteComment(comment.id)} className="delete">삭제</button>
                        </div>
                    </div>
                ))}
            </div>

            <form onSubmit={handleNewCommentSubmit} className="new-comment-container">
            <textarea
                className="new-comment-textarea"
                value={newComment}
                onChange={(e) => setNewComment(e.target.value)}
                placeholder="Write a comment..."
            />
                <button type="submit" className="new-comment-submit">Submit Comment</button>
            </form>
        </div>
    );


}

export default PostDetail;
