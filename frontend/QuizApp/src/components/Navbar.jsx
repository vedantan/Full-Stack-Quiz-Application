import React, { useContext } from "react";
import { Link as RouterLink, useNavigate, useLocation } from "react-router-dom";
import { AppBar, Toolbar, Typography, Button as MuiButton, Box, Chip } from "@mui/material";
import LeaderboardIcon from "@mui/icons-material/Leaderboard";
import PersonIcon from "@mui/icons-material/Person";
import QuizIcon from "@mui/icons-material/Quiz";
import QuestionAnswerIcon from "@mui/icons-material/QuestionAnswer";
import LanguageIcon from "@mui/icons-material/Language";
import HouseIcon from "@mui/icons-material/House";
import { useTranslation } from "react-i18next";

import Dropdown from "./Dropdown";
import Button from "./Button"; 

import { AuthContext } from "../context/AuthContext";

function Navbar() {

  const { t, i18n } = useTranslation();
  const navigate = useNavigate();
  const location = useLocation();
  const {user, token, logout } = useContext(AuthContext);

  const navItems = [
    { label: t("navbar.home"), path: "/", icon: <HouseIcon /> },
    { label: t("navbar.questions"), path: "/questions", icon: <QuestionAnswerIcon /> },
    { label: t("navbar.addQuestion"), path: "/add-question", icon: <QuestionAnswerIcon /> },
    { label: t("navbar.quiz"), path: "/quiz", icon: <QuizIcon /> },
    { label: t("navbar.leaderboard"), path: "/leaderboard", icon: <LeaderboardIcon /> },
  ];

  const languageOptions = [
    { value: "en", label: "English" },
    { value: "hi", label: "हिन्दी" },
    { value: "mr", label: "मराठी" }
  ];

  const handleLogout = () => {
    logout();   // ✅ use context
    navigate("/login");
  };

  return (
    <Box sx={{ display: "flex", justifyContent: "center", mt: 2 }}>

      <AppBar
        position="static"
        elevation={0}
        sx={{
          width: "90%",
          borderRadius: "14px",
          background: "rgba(15, 23, 42, 0.9)",
          backdropFilter: "blur(12px)",
          border: "1px solid rgba(255,255,255,0.08)"
        }}
      >
        <Toolbar>

          {/* Logo */}
          <Typography
            variant="h6"
            component={RouterLink}
            to="/"
            sx={{
              flexGrow: 1,
              textDecoration: "none",
              color: "white",
              fontWeight: 600
            }}
          >
            Quiz App
          </Typography>

          {/* Nav Links */}
          <Box sx={{ display: "flex", alignItems: "center" }}>

            {navItems.map((item) => (
              <MuiButton
                key={item.path}
                startIcon={item.icon}
                component={RouterLink}
                to={item.path}
                sx={{
                  color: "#e2e8f0",
                  textTransform: "none",
                  fontSize: "14px",
                  ml: 1,
                  "&:hover": {
                    color: "white",
                    background: "rgba(255,255,255,0.08)"
                  }
                }}
              >
                {item.label}
              </MuiButton>
            ))}

            {/* Profile */}
            {token && (
              <MuiButton
                startIcon={<PersonIcon />}
                component={RouterLink}
                to="/profile"
                sx={{
                  color: "#e2e8f0",
                  textTransform: "none",
                  fontSize: "14px",
                  ml: 1,
                  "&:hover": {
                    color: "white",
                    background: "rgba(255,255,255,0.08)",
                  },
                }}
              >
               {user?.sub ? user.sub : t("navbar.profile")}
              </MuiButton>
            )}

            {/* Auth Buttons */}
            <Box sx={{ display: "flex", gap: 1, ml: 2 }}>

              {token ? (
                <Button
                  caption="Logout"
                  onClick={handleLogout}
                  width={90}
                  height={36}
                  color="error"
                />
              ) : (
                <>
                  {location.pathname !== "/login" && (
                    <Button
                      caption="Login"
                      onClick={() => navigate("/login")}
                      width={90}
                      height={36}
                      variant="outlined"
                    />
                  )}

                  {location.pathname !== "/signup" && (
                    <Button
                      caption="Signup"
                      onClick={() => navigate("/signup")}
                      width={90}
                      height={36}
                      variant="outlined"
                    />
                  )}
                </>
              )}

            </Box>

            {/* Language */}
            <Box sx={{ display: "flex", alignItems: "center", ml: 2 }}>
              <LanguageIcon sx={{ color: "white", mr: 1 }} />

              <Dropdown
                options={languageOptions}
                value={i18n.language.substring(0, 2)}
                onChange={(lang) => i18n.changeLanguage(lang)}
                width={100}
              />
            </Box>



          </Box>

        </Toolbar>
      </AppBar>

    </Box>
  );
}

export default Navbar;