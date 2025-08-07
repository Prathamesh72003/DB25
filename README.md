export const getAllTradesFormatted = async () => {
  const data = await safeGet(`${hostNameUrl}/trades/all`);

  if (!Array.isArray(data)) return [];

  const transformed = data.map((entry) => {
    const {
      trade: {
        tradeId,
        buySellIndicator,
        quantity,
        status,
        unitPrice,
        tradeDate,
        settlementDate,
        book = {},
        security = {},
        counterparty = {}
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
      quantity: quantity || 0,
      maturityDate: security.maturityDate || "N/A",
      tradeDate: tradeDate || "N/A",
      settlementDate: settlementDate || "N/A",
      status: status || "N/A",
      assignedUser: user.userId || null,
      price: unitPrice || 0,
      currency: security.currency || "N/A",
      coupon: security.coupon || 0,
      buySellIndicator: buySellIndicator || "N/A"
    };
  });

  return transformed;
};
