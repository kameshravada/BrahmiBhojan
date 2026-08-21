import { create } from "zustand";
import { persist } from "zustand/middleware";

type AuthSession = {
  userId: string;
  mobile: string;
  email: string | null;
  fullName: string | null;
  role: string;
  accessToken: string;
  refreshToken: string;
  expiresInSeconds: number;
};

type AuthStore = {
  session: AuthSession | null;
  setSession: (session: AuthSession) => void;
  clearSession: () => void;
};

export const useAuthStore = create<AuthStore>()(
  persist(
    (set) => ({
      session: null,
      setSession: (session) => set({ session }),
      clearSession: () => set({ session: null }),
    }),
    {
      name: "bb-auth-session",
      partialize: (state) => ({ session: state.session }),
    },
  ),
);
