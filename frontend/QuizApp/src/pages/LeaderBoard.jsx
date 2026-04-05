import React, { useEffect } from 'react'
import { toast } from "react-toastify";


function LeaderBoard() {
    useEffect(() => {toast.success("Leader Board fetched Successfully");}, [])
  return (
    <div>
      <h2>LeaderBoard</h2>

    </div>
  )
}

export default LeaderBoard

