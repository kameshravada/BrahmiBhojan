import axios from "axios";

const FALLBACK_API_BASE_URL = "http://localhost:8080";

export const apiClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL ?? FALLBACK_API_BASE_URL,
  timeout: 12_000,
  headers: {
    "Content-Type": "application/json",
  },
});
