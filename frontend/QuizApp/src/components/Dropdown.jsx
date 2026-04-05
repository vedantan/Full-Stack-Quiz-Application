import React from "react";
import { Box, FormControl, InputLabel, Select, MenuItem } from "@mui/material";

function Dropdown({
  options,
  value,
  onChange,
  caption,
  placeholder,
  width = 80,
  height = 40,
}) {
  return (
    <Box >

      <FormControl  size="small"  sx={{
          width: width,
          height: height,
        }}>

        {caption && <InputLabel>{caption}</InputLabel>}

        <Select
          value={value}
          label={caption}
          onChange={(e) => onChange(e.target.value)}
          sx={{
            backgroundColor: "white",
            borderRadius: "8px",
            height: height
          }}
        >

          {placeholder && (
            <MenuItem value="">
              <em>{placeholder}</em>
            </MenuItem>
          )}

          {options.map((opt, index) => (
            <MenuItem
              key={index}
              value={typeof opt === "object" ? opt.value : opt}
            >
              {typeof opt === "object" ? opt.label : opt}
            </MenuItem>
          ))}

        </Select>

      </FormControl>

    </Box>
  );
}

export default Dropdown;