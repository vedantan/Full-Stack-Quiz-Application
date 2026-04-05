import React, { useContext, useMemo } from "react";
import {
  Container,
  Card,
  CardContent,
  Typography,
  Box,
  Stack,
  Divider,
  Button as MuiButton,
} from "@mui/material";
import PersonIcon from "@mui/icons-material/Person";
import LogoutIcon from "@mui/icons-material/Logout";
import ContentCopyIcon from "@mui/icons-material/ContentCopy";
import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";

import { AuthContext } from "../context/AuthContext";
import { notify } from "../utils/Toast";

function formatDateTime(epochSeconds) {
  if (!epochSeconds) return "-";
  const d = new Date(epochSeconds * 1000);
  return d.toLocaleString();
}

function Profile() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const { user, token, logout } = useContext(AuthContext);

  const expText = useMemo(() => formatDateTime(user?.exp), [user?.exp]);

  const handleLogout = () => {
    logout();
    notify.success("Logged out");
    navigate("/login");
  };

  const handleCopyToken = async () => {
    try {
      if (!token) return;
      await navigator.clipboard.writeText(token);
      notify.success("Token copied");
    } catch {
      notify.error("Could not copy token");
    }
  };

  return (
    <Container maxWidth="md" sx={{ mt: 5 }}>
      <Card
        elevation={6}
        sx={{
          borderRadius: 3,
          background:
            "linear-gradient(180deg, rgba(255,255,255,0.92) 0%, rgba(248,250,252,1) 100%)",
        }}
      >
        <CardContent>
          <Stack direction="row" spacing={2} alignItems="center">
            <Box
              sx={{
                width: 44,
                height: 44,
                borderRadius: "12px",
                display: "grid",
                placeItems: "center",
                bgcolor: "rgba(37, 99, 235, 0.12)",
                color: "#2563eb",
              }}
            >
              <PersonIcon />
            </Box>

            <Box sx={{ flex: 1 }}>
              <Typography variant="h5" fontWeight={700}>
                {t("navbar.profile")}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                View your account details and session information
              </Typography>
            </Box>

            <MuiButton
              variant="contained"
              color="error"
              startIcon={<LogoutIcon />}
              onClick={handleLogout}
            >
              Logout
            </MuiButton>
          </Stack>

          <Divider sx={{ my: 3 }} />

          <Box
            sx={{
              display: "grid",
              gridTemplateColumns: { xs: "1fr", sm: "1fr 1fr" },
              gap: 2,
            }}
          >
            <Card variant="outlined" sx={{ borderRadius: 2 }}>
              <CardContent>
                <Typography variant="overline" color="text.secondary">
                  Username
                </Typography>
                <Typography variant="h6" fontWeight={700} sx={{ mt: 0.5 }}>
                  {user?.sub || "-"}
                </Typography>
              </CardContent>
            </Card>

            <Card variant="outlined" sx={{ borderRadius: 2 }}>
              <CardContent>
                <Typography variant="overline" color="text.secondary">
                  Role
                </Typography>
                <Typography variant="h6" fontWeight={700} sx={{ mt: 0.5 }}>
                  {user?.role || "-"}
                </Typography>
              </CardContent>
            </Card>

            <Card variant="outlined" sx={{ borderRadius: 2 }}>
              <CardContent>
                <Typography variant="overline" color="text.secondary">
                  Token expires at
                </Typography>
                <Typography variant="h6" fontWeight={700} sx={{ mt: 0.5 }}>
                  {expText}
                </Typography>
              </CardContent>
            </Card>

            <Card variant="outlined" sx={{ borderRadius: 2 }}>
              <CardContent>
                <Typography variant="overline" color="text.secondary">
                  Session
                </Typography>
                <Typography variant="h6" fontWeight={700} sx={{ mt: 0.5 }}>
                  {token ? "Active" : "Not logged in"}
                </Typography>
                <Stack direction="row" spacing={1} sx={{ mt: 1.5 }}>
                  <MuiButton
                    variant="outlined"
                    size="small"
                    startIcon={<ContentCopyIcon />}
                    onClick={handleCopyToken}
                    disabled={!token}
                  >
                    Copy token
                  </MuiButton>
                  <MuiButton
                    variant="text"
                    size="small"
                    onClick={() => navigate("/")}
                  >
                    Go home
                  </MuiButton>
                </Stack>
              </CardContent>
            </Card>
          </Box>
        </CardContent>
      </Card>
    </Container>
  );
}

export default Profile;
