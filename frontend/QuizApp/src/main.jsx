  import React from "react";
  import ReactDOM from "react-dom/client";
  import App from "./App";
  import "./i18n";

  import { ThemeProvider, createTheme } from "@mui/material/styles";
  import CssBaseline from "@mui/material/CssBaseline";
  import { AuthProvider } from "./context/AuthContext";

  const theme = createTheme({
    palette: {
      mode: "light",
      primary: {
        main: "#2563eb"
      }
    }
  });

  ReactDOM.createRoot(document.getElementById("root")).render(
    <AuthProvider> 
            <ThemeProvider theme={theme}>
              <CssBaseline />
              <App />
            </ThemeProvider>
    </AuthProvider>

  );