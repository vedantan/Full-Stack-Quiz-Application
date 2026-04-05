# Frontend Dependencies Documentation

This file documents the main libraries and tools used in this React project for future reference.

---

# 1. Vite

### Installation

```
npm create vite@latest
```

### Purpose

* Used as the build tool and development server.
* Provides extremely fast startup and Hot Module Replacement (HMR).
* Much faster than traditional bundlers like Webpack.

---

# 2. React Router DOM

### Installation

```
npm install react-router-dom
```

### Purpose

* Enables client-side routing in the React application.
* Allows navigation between pages without refreshing the browser.

### Example

```javascript
import { BrowserRouter, Routes, Route } from "react-router-dom";
```

---

# 3. Axios

### Installation

```
npm install axios
```

### Purpose

* Used to make HTTP requests to backend APIs.
* In this project it is used to communicate with Spring Boot microservices.

### Example

```javascript
axios.get("/api/questions");
```

---

# 4. Material UI (MUI)

### Installation

```
npm install @mui/material @emotion/react @emotion/styled
```

### Purpose

* Provides modern, responsive UI components.
* Used for building consistent UI elements like cards, buttons, grids, typography etc.

### Example Components

* Container
* Typography
* Card
* Grid
* Button
* Dialog

---

# 5. React Toastify

### Installation

```
npm install react-toastify
```

### Purpose

* Used for displaying toast notifications.
* Helps show success, error, and warning messages to users.

### Example

```javascript
import { toast } from "react-toastify";

toast.success("Operation successful");
```

---

# 6. Internationalization (i18n)

This project supports multiple languages using i18next.

## 6.1 i18next

### Installation

```
npm install i18next
```

### Purpose

* Core internationalization framework.
* Handles translation resources and language switching.

---

## 6.2 react-i18next

### Installation

```
npm install react-i18next
```

### Purpose

* React integration for i18next.
* Provides hooks like `useTranslation()` to easily translate text inside React components.

### Example

```javascript
const { t } = useTranslation();

<h1>{t("welcome_message")}</h1>
```

---

## 6.3 i18next-browser-languagedetector

### Installation

```
npm install i18next-browser-languagedetector
```

### Purpose

* Automatically detects the user's browser language.
* Sets the application language accordingly.

---

# Summary

| Dependency                       | Purpose                                |
| -------------------------------- | -------------------------------------- |
| Vite                             | Fast build tool and development server |
| React Router DOM                 | Client-side routing                    |
| Axios                            | API communication with backend         |
| Material UI                      | UI component library                   |
| React Toastify                   | Toast notifications                    |
| i18next                          | Internationalization framework         |
| react-i18next                    | React integration for translations     |
| i18next-browser-languagedetector | Detects browser language               |

---



npm install jwt-decode

