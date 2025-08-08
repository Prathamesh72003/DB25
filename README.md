const safeGet = async (url, fallback = []) => {
  try {
    const response = await axios.get(url);
    return Array.isArray(response.data) ? response.data : fallback;
  } catch (error) {
    console.error(`GET ${url} failed:`, error?.response?.data || error.message);
    return fallback; // Always return array
  }
};

export const getAllBonds = async () => {
  const [openTrades, closeTrades] = await Promise.all([
    safeGet(`${hostNameUrl}/trades/open`, []),
    safeGet(`${hostNameUrl}/trades/close`, [])
  ]);

  // Merge arrays — handles case where one or both are empty
  return [...openTrades, ...closeTrades];
};
