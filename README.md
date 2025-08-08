const maturedBonds = bonds.filter(bond => {
  const maturityDate = new Date(bond.maturityDate);
  const today = new Date();
  return maturityDate < today;
});
