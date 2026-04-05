import React, { useState } from "react";
import { Container, Card, CardContent, Typography, Box } from "@mui/material";
import { notify } from "../utils/Toast";
import Input from "../components/Input";
import Button from "../components/Button";
import { signupUser } from "../services/AuthApi";
import { useNavigate } from "react-router-dom";

function Signup() {

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const navigate = useNavigate();

    const handleSignup = async (e) => {
        e.preventDefault();

        try {
            const payload = { username, password };

            await signupUser(payload);

            notify.success("User Registered Successfully");

            // 👉 redirect to login after signup
            navigate("/login");

        } catch (error) {
            const message = error.response?.data;
            if (message) {
                notify.error(message);   // ✅ show backend message
            } else {
                notify.error("Signup failed");
            }
        }
    };

    return (
        <Container maxWidth="sm">

            <Card
                elevation={6}
                sx={{
                    mt: 10,
                    borderRadius: 3,
                    backgroundColor: "#f9fafb"
                }}
            >

                <CardContent>

                    <Typography
                        variant="h5"
                        fontWeight="bold"
                        textAlign="center"
                        sx={{ mb: 4 }}
                    >
                        Signup
                    </Typography>

                    <form onSubmit={handleSignup}>

                        <Box
                            sx={{
                                display: "flex",
                                flexDirection: "column",
                                alignItems: "center"
                            }}
                        >

                            <Input
                                caption="Username"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                required
                                width={300}
                                height={40}
                            />

                            <Input
                                type="password"
                                caption="Password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                            />

                        </Box>

                        <Box
                            sx={{
                                display: "flex",
                                justifyContent: "center",
                                mt: 2
                            }}
                        >

                            <Button
                                caption="Signup"
                                type="submit"
                                variant="contained"
                                width={150}
                                height={40}
                            />



                        </Box>

                    </form>

                    <Typography
                        variant="body2"
                        textAlign="center"
                        sx={{ mt: 2, cursor: "pointer", color: "blue" }}
                        onClick={() => navigate("/login")}
                    >
                        Already have an account? Login
                    </Typography>

                </CardContent>

            </Card>

        </Container>
    );
}

export default Signup;