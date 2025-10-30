TXN123	{"requestId":"R1","taxRate":5.0}
TXN123	{"requestId":"R2","taxRate":10.0}
TXN123	{"requestId":"R3","taxRate":15.0}

transform("TXN123", "{\"requestId\":\"R1\",\"taxRate\":5.0\"}")
