import React from "react";
import { Box, TextField } from "@mui/material";

function Input({
  name,
  value,
  onChange,
  caption, // for legend tag
  placeholder,
  error,
  width = 300,
  height = 40,
  required = false,
  type="text"
}) {

  const handleInvalid = (e) => {
    if (required) {
      console.log("field is required....")
      const fieldName = caption || placeholder || name;
      e.target.setCustomValidity(`${fieldName} is required*`);
    }
  };

  const handleInput = (e) => {
    e.target.setCustomValidity("");
  };

  return (
    <Box sx={{ mb: 2 }}>
      <TextField
        name={name}
        value={value} 
        label={caption}    // for legend tag
        placeholder={placeholder}
        onChange={onChange}
        required={required}
        type={type}
        error={Boolean(error)}
        helperText={error}
        size="small"
        fullWidth
        onInvalid={handleInvalid}
        onInput={handleInput}
        sx={{
          width: width,

          "& .MuiOutlinedInput-root": {
            height: height,
            borderRadius: "8px",
            backgroundColor: "white"
          },

          "& .MuiFormHelperText-root": {
            color: "red"
          },

          "& .MuiFormLabel-root.Mui-error": {
            color: "red"
          }
        }}
      />
    </Box>
  );
}

export default Input;