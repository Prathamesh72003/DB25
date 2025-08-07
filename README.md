const maturingSoon = bonds.filter(bond => {
  const maturityDate = new Date(bond.maturityDate);
  const today = new Date();
  const daysDiff = Math.ceil((maturityDate - today) / (1000 * 60 * 60 * 24));
  return daysDiff <= 5 && daysDiff >= 0;
});
