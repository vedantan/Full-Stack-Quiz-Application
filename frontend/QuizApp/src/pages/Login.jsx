import React, { useState , useContext } from "react";
import { Container, Card, CardContent, Typography, Box } from "@mui/material";
import { notify } from "../utils/Toast";
import Input from "../components/Input";
import Button from "../components/Button";
import { generateToken } from "../services/AuthApi";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";


function Login() {

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const navigate = useNavigate();
    const { login } = useContext(AuthContext);


    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const payload = { username, password };
            const data = await generateToken(payload);
           // localStorage.setItem("token", data);
            login(data);
            navigate("/");
            notify.success("User Logged in Successfully");
        } catch (error) {
            if (error.response?.status === 404) {
                notify.error("User not found");
            }
            else if (error.response?.status === 401) {
                notify.error("Invalid password");
            }
            else {
                notify.error("Login failed");
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
                        Login
                    </Typography>

                    <form onSubmit={handleLogin}>

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
                                caption="Login"
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
                        onClick={() => navigate("/signup")}
                    >
                        Dodn't have account? Signup
                    </Typography>

                </CardContent>

            </Card>

        </Container>
    );
}

export default Login;