=TEXT(COUNTA(INDIRECT("MainSheet!J7:J" & MATCH("Day 1", MainSheet!I7:I100, 0) + 6)) / 55, "0.00%")
