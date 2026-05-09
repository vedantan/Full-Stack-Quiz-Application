import React, { useEffect, useState } from 'react'
import { toast } from "react-toastify";
import { getLeaderBoard } from '../services/QuizApi';

function LeaderBoard() {

    const [leaderboard, setLeaderboard] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {

        const fetchLeaderboard = async () => {
            try {
                const response = await getLeaderBoard();
                setLeaderboard(response.data);
                toast.success("Leaderboard fetched successfully 🚀");
            } catch (error) {
                console.error(error);
                toast.error("Failed to fetch leaderboard ❌");
            } finally {
                setLoading(false);
            }
        };

        fetchLeaderboard();

    }, []);

    return (
        <div style={{ padding: "20px" }}>
            <h2>🏆 LeaderBoard</h2>

            {loading ? (
                <p>Loading...</p>
            ) : leaderboard.length === 0 ? (
                <p>No users found</p>
            ) : (
                <div style={{ marginTop: "20px", display: "flex", flexDirection: "column", gap: "12px" }}>
                    
                    {leaderboard.map((user, index) => (
                        <div
                            key={index}
                            style={{
                                display: "flex",
                                justifyContent: "space-between",
                                alignItems: "center",
                                padding: "15px 20px",
                                borderRadius: "10px",
                                boxShadow: "0 2px 8px rgba(0,0,0,0.1)",
                                background: index < 3 ? "#e6f7ff" : "#fff"
                            }}
                        >
                            {/* Rank */}
                            <div style={{ fontWeight: "bold", fontSize: "18px" }}>
                                #{user.rank}
                            </div>

                            {/* Username */}
                            <div style={{ flex: 1, textAlign: "center", fontSize: "16px" }}>
                                {user.username}
                            </div>

                            {/* Score */}
                            <div style={{ fontWeight: "bold" }}>
                                {user.score} pts
                            </div>
                        </div>
                    ))}

                </div>
            )}
        </div>
    )
}

export default LeaderBoard;