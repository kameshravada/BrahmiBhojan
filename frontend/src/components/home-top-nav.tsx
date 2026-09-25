/* eslint-disable @next/next/no-img-element */

"use client";

import { useMutation } from "@tanstack/react-query";
import { useRouter } from "next/navigation";
import { useMemo, useState, useEffect } from "react";

import { apiClient } from "@/lib/api/client";
import { useAuthStore } from "@/stores/auth-store";
import { useUiStore } from "@/stores/ui-store";

function getInitials(
  name: string | null | undefined,
  mobile: string | null | undefined,
) {
  if (name && name.trim().length > 0) {
    const parts = name.trim().split(/\s+/);
    if (parts.length === 1) {
      return parts[0].slice(0, 2).toUpperCase();
    }
    return `${parts[0][0] ?? ""}${parts[1][0] ?? ""}`.toUpperCase();
  }

  if (mobile && mobile.length >= 2) {
    return mobile.slice(-2).toUpperCase();
  }

  return "CU";
}

export function HomeTopNav() {
  const [isMounted, setIsMounted] = useState(false);

  // Use router only after hydration to prevent initialization errors
  const router = useRouter();
  const session = useAuthStore((state) => state.session);
  const clearSession = useAuthStore((state) => state.clearSession);
  const openLoginModal = useUiStore((state) => state.openLoginModal);
  const [isProfileMenuOpen, setIsProfileMenuOpen] = useState(false);

  useEffect(() => {
    setIsMounted(true);
  }, []);

  const isLoggedIn = Boolean(session?.accessToken);
  const profileInitials = useMemo(
    () => getInitials(session?.fullName, session?.mobile),
    [session?.fullName, session?.mobile],
  );
  const profileLabel = session?.fullName?.trim() || "Customer";

  const logoutMutation = useMutation({
    mutationFn: async (refreshToken: string) => {
      await apiClient.post("/api/v1/auth/logout", { refreshToken });
    },
  });

  const handleLogout = async () => {
    setIsProfileMenuOpen(false);
    const refreshToken =
      session?.refreshToken || localStorage.getItem("bb_refresh_token");

    try {
      if (refreshToken) {
        await logoutMutation.mutateAsync(refreshToken);
      }
    } catch {
      // Local cleanup should continue even if network logout fails.
    }

    localStorage.removeItem("bb_access_token");
    localStorage.removeItem("bb_refresh_token");
    clearSession();
  };

  // Safe click handlers that only work after hydration
  const handleOpenLoginModal = () => {
    if (isMounted) {
      openLoginModal();
    }
  };

  const handleToggleProfileMenu = () => {
    if (isMounted) {
      setIsProfileMenuOpen((current) => !current);
    }
  };

  return (
    <>
      <nav className="sticky top-0 z-50 hidden max-w-7xl mx-auto margin border-b border-[var(--outline-variant)]/20 bg-[rgba(248,250,246,0.8)] backdrop-blur-md md:block">
        <div className="mx-auto flex w-full  items-center justify-between px-5 py-3">
          <div className="flex items-center gap-3">
            <img
              src="/images/Brahmin Bhojan logo-trans.png"
              alt="BrahmiBhojan logo"
              className="h-12 w-auto object-contain"
            />
          </div>

          <div className="mx-16 flex-1">
            <div className="mx-auto flex max-w-xl items-center rounded-full bg-[var(--surface-container-highest)] px-4 py-2">
              <span className="material-symbols-outlined mr-2 text-[var(--on-surface-variant)]">
                search
              </span>
              <input
                className="input w-full border-none bg-transparent p-0 text-[var(--on-surface)] outline-none"
                placeholder="Search for Ghee, Honey, Millets..."
                type="text"
              />
              <button
                type="button"
                className="ml-2 flex items-center justify-center cursor-pointer"
              >
                <span className="material-symbols-outlined text-[var(--primary)]">
                  mic
                </span>
              </button>
            </div>
          </div>

          <div className="flex items-center gap-6">
            {!isLoggedIn && (
              <button
                type="button"
                onClick={handleOpenLoginModal}
                className="button rounded-lg bg-[var(--primary)] px-4 py-2 text-[var(--on-primary)] transition-colors hover:bg-[var(--primary-container)] cursor-pointer"
              >
                Login / Register
              </button>
            )}

            {!isLoggedIn && (
              <div className="h-6 w-px bg-[var(--outline-variant)]/40" />
            )}

            <button
              type="button"
              className="relative text-[var(--on-surface-variant)] hover:text-[var(--primary)] cursor-pointer"
            >
              <span className="material-symbols-outlined">favorite</span>
            </button>
            <button
              type="button"
              className="relative text-[var(--on-surface-variant)] hover:text-[var(--primary)] cursor-pointer"
            >
              <span className="material-symbols-outlined">shopping_cart</span>
              <span className="absolute -right-1 -top-1 flex h-4 w-4 items-center justify-center rounded-full bg-[var(--secondary)] text-[10px] font-bold text-[var(--on-secondary)]">
                2
              </span>
            </button>
            {isLoggedIn ? (
              <div className="relative">
                <button
                  type="button"
                  aria-label="Customer profile"
                  className="group flex items-center gap-2 rounded-full px-1 py-1 transition-colors hover:bg-[var(--surface-container-high)] cursor-pointer"
                  onClick={handleToggleProfileMenu}
                >
                  <span className="label max-w-[96px] truncate text-[var(--on-surface)]">
                    {profileLabel}
                  </span>
                  <span className="relative flex h-9 w-9 items-center justify-center rounded-full bg-[#e6d1df] text-sm font-semibold text-[#3c2f39] ">
                    {profileInitials}
                    <span className="absolute -bottom-0.5 -right-0.5 h-3 w-3 rounded-full border-2 border-[var(--surface)] bg-[#38b84c]" />
                  </span>
                </button>

                {isProfileMenuOpen ? (
                  <div className="absolute right-0 top-12 z-20 w-48 rounded-b-xl border border-[var(--outline-variant)] bg-[var(--surface-container-lowest)] p-2 shadow-[0_12px_30px_rgba(0,0,0,0.12)]">
                    <button
                      type="button"
                      className="label flex w-full items-center justify-between rounded-lg px-3 py-2 text-left text-[var(--on-surface)] hover:bg-[var(--surface-container-low)] cursor-pointer"
                    >
                      <span>Profile</span>
                      <span className="caption text-[var(--on-surface-variant)]">
                        Soon
                      </span>
                    </button>
                    <button
                      type="button"
                      className="label flex w-full items-center justify-between rounded-lg px-3 py-2 text-left text-[var(--on-surface)] hover:bg-[var(--surface-container-low)] cursor-pointer"
                    >
                      <span>Orders</span>
                      <span className="caption text-[var(--on-surface-variant)]">
                        Soon
                      </span>
                    </button>
                    <div className="my-1 h-px bg-[var(--outline-variant)]/70" />
                    <button
                      type="button"
                      onClick={handleLogout}
                      disabled={logoutMutation.isPending}
                      className="label w-full rounded-lg px-3 py-2 text-left text-[var(--error)] hover:bg-[var(--surface-container-low)] disabled:opacity-60 cursor-pointer"
                    >
                      {logoutMutation.isPending ? "Logging out..." : "Logout"}
                    </button>
                  </div>
                ) : null}
              </div>
            ) : (
              <button
                type="button"
                aria-label="Customer profile"
                className="opacity-50 cursor-not-allowed text-[var(--on-surface-variant)]"
                disabled
              >
                <span className="material-symbols-outlined">person</span>
              </button>
            )}
          </div>
        </div>
      </nav>

      <header className="sticky top-0 z-40 flex items-center justify-between bg-[rgba(248,250,246,0.8)] px-5 py-2 backdrop-blur-md md:hidden">
        <img
          src="/images/Brahmin Bhojan logo-trans.png"
          alt="BrahmiBhojan logo"
          className="h-10 w-auto object-contain"
        />
        <div className="flex items-center gap-2">
          <button
            type="button"
            className="p-2 text-[var(--on-surface-variant)] cursor-pointer"
          >
            <span className="material-symbols-outlined">search</span>
          </button>
          <button
            type="button"
            className="relative p-2 text-[var(--on-surface-variant)] cursor-pointer"
          >
            <span className="material-symbols-outlined">shopping_cart</span>
            <span className="absolute right-1 top-1 flex h-3.5 w-3.5 items-center justify-center rounded-full bg-[var(--secondary)] text-[10px] font-bold text-[var(--on-secondary)]">
              2
            </span>
          </button>
        </div>
      </header>
    </>
  );
}
