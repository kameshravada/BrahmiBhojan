import { create } from "zustand";

type UiStore = {
  isMobileNavOpen: boolean;
  toggleMobileNav: () => void;
  closeMobileNav: () => void;
  isLoginModalOpen: boolean;
  openLoginModal: () => void;
  closeLoginModal: () => void;
};

export const useUiStore = create<UiStore>((set) => ({
  isMobileNavOpen: false,
  toggleMobileNav: () =>
    set((state) => ({ isMobileNavOpen: !state.isMobileNavOpen })),
  closeMobileNav: () => set({ isMobileNavOpen: false }),
  isLoginModalOpen: false,
  openLoginModal: () => {
    set({ isLoginModalOpen: true });
  },
  closeLoginModal: () => set({ isLoginModalOpen: false }),
}));
