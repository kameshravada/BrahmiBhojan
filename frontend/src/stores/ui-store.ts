import { create } from "zustand";

type UiStore = {
  isMobileNavOpen: boolean;
  toggleMobileNav: () => void;
  closeMobileNav: () => void;
};

export const useUiStore = create<UiStore>((set) => ({
  isMobileNavOpen: false,
  toggleMobileNav: () =>
    set((state) => ({ isMobileNavOpen: !state.isMobileNavOpen })),
  closeMobileNav: () => set({ isMobileNavOpen: false }),
}));
