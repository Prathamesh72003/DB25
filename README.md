export const getAllTradesFormatted = async () => {
  const data = await safeGet(`${hostNameUrl}/trades/all`);
  
  // Handle non-array responses
  if (!Array.isArray(data)) {
    console.warn('API response is not an array:', data);
    return [];
  }
  
  const transformed = data.map((entry) => {
    // Destructure with proper fallbacks
    const {
      trade: {
        tradeId,
        book = {},
        security = {},
        counterparty = {},
        currency: tradeCurrency,
        status,
        quantity,
        unitPrice,
        buySellIndicator,
        tradeDate,
        settlementDate
      } = {},
      user = {}
    } = entry;

    return {
      id: Number(tradeId) || null,
      isin: security.isin || "N/A",
      cusip: security.cusip || "N/A",
      issuerName: security.issuerName || "N/A",
      counterparty: counterparty.name || "N/A",
      book: book.name || "N/A",
      quantity: Number(quantity) || 0,
      maturityDate: security.maturityDate || "N/A",
      tradeDate: tradeDate || "N/A",
      settlementDate: settlementDate || "N/A",
      status: status || "N/A",
      assignedUser: user.userId || null,
      price: Number(unitPrice) || 0,
      currency: security.currency || tradeCurrency || "N/A",
      coupon: Number(security.coupon) || 0,
      buySellIndicator: buySellIndicator || "N/A"
    };
  });

  return transformed;
};
