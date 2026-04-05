import React from "react";
import { Button as MuiButton } from "@mui/material";

function Button({
  caption,
  onClick,
  type = "button",
  variant = "contained",
  color = "primary",
 // size = "medium",
  width,
  height,
 // fullWidth = false,
  sx = {}
}) {
  return (
    <MuiButton
      type={type}
      variant={variant}
      color={color}
      //size={size}
      //fullWidth={fullWidth}
      onClick={onClick}
      sx={{
        width: width,
        height: height,
        borderRadius: 2,
        fontWeight: 600,
        textTransform: "none",
        ...sx
      }}
    >
      {caption}
    </MuiButton>
  );
}

export default Button;