import { hostNameUrl } from "../config/api";
import axios from "axios";

// --- Helper ---
const isValidString = (val) => typeof val === "string" && val.trim() !== "";

// --- Safe Axios GET wrapper ---
const safeGet = async (url, fallback = []) => {
  try {
    const response = await axios.get(url);
    return response.data || fallback;
  } catch (error) {
    console.error(`GET ${url} failed:`, error?.response?.data || error.message);
    return fallback;
  }
};

// --- API Functions ---

export const findPets = async () => {
  return await safeGet(`${hostNameUrl}/trades/open`);
};

export const getMyBooks = async (userId) => {
  if (!isValidString(userId)) return [];
  return await safeGet(`${hostNameUrl}/books/user/${userId}`);
};

export const getMyBonds = async (userId, bookId) => {
  if (!isValidString(userId) || !isValidString(bookId)) return [];
  return await safeGet(`${hostNameUrl}/trades/user/${userId}/book/${bookId}`);
};

export const getNextFiveDayDueMatureBonds = async (userId) => {
  if (!isValidString(userId)) return [];
  return await safeGet(`${hostNameUrl}/trades/user/${userId}/nextfivedays`);
};

export const getLastFiveDayDueMatureBonds = async (userId) => {
  if (!isValidString(userId)) return [];
  return await safeGet(`${hostNameUrl}/trades/user/${userId}/pastfivedays`);
};

export const getAllBonds = async () => {
  return await safeGet(`${hostNameUrl}/trades/open`);
};

export const getAllTradesFormatted = async () => {
  const data = await safeGet(`${hostNameUrl}/trades/all`);

  if (!data || !Array.isArray(data.bonds_data)) return [];

  const transformed = data.bonds_data.map((item) => {
    const {
      tradeId,
      buySellIndicator,
      quantity,
      status,
      unitPrice,
      tradeDate,
      settleDate,
      book = {},
      security = {},
      counterparty = {}
    } = item;

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
      settlementDate: settleDate || "N/A",
      status: status || "N/A",
      assignedUser: 1, // hardcoded for now unless dynamic
      price: unitPrice || 0,
      currency: security.currency || "N/A",
      coupon: security.coupon || 0,
      buySellIndicator: buySellIndicator || "N/A"
    };
  });

  return transformed;
};
