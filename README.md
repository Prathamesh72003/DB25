const processedBonds = bondsData2.map((bond) => {
  // Use optional chaining to safely access nested properties.
  // The nullish coalescing operator (??) provides a fallback if the property is null or undefined.
  const isin = bond.trade?.security?.isin ?? bond.security?.isin;
  const issuer = bond.trade?.security?.issuerName ?? bond.security?.issuerName;
  const maturity = bond.trade?.security?.maturityDate ?? bond.security?.maturityDate;
  const status = bond.trade?.security?.status ?? bond.security?.status;
  
  // For the `daystomaturity` field, you can check for the existence of `bond.trade` or `bond.book`
  const daystomaturity = bond.trade?.book ? "Assigned" : "N/A";
  
  return {
    isin,
    issuer,
    maturity,
    daystomaturity,
    status
  };
});
