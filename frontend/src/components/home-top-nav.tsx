/* eslint-disable @next/next/no-img-element */

"use client";

import { useMutation } from "@tanstack/react-query";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useMemo, useState } from "react";

import { apiClient } from "@/lib/api/client";
import { useAuthStore } from "@/stores/auth-store";

function getInitials(name: string | null | undefined, mobile: string | null | undefined) {
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
  const router = useRouter();
  const session = useAuthStore((state) => state.session);
  const clearSession = useAuthStore((state) => state.clearSession);
  const [isProfileMenuOpen, setIsProfileMenuOpen] = useState(false);

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
    const refreshToken = session?.refreshToken || localStorage.getItem("bb_refresh_token");

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
    router.push("/login");
  };

  return (
    <>
      <nav className="sticky top-0 z-50 hidden w-full border-b border-[var(--outline-variant)]/20 bg-[rgba(248,250,246,0.8)] backdrop-blur-md md:block">
        <div className="mx-auto flex w-full  items-center justify-between px-5 py-3">
          <div className="flex items-center gap-3">
            <img
              src="https://lh3.googleusercontent.com/aida-public/AB6AXuBfA-dmWSV9GS_Mtqgxd4Wc8lrT0JuCDyXAcvDTL3yHupY-jQgHqWkBE77Is4pZtNeTj6bDz8qa_Fm2fy0i4vInsOl9R7A3J4_25AShHxMDt2vlFJflHR6gEVsBCnR9y2AB3rIIYIzU_w8RqzV47fnuS-1MdQYulZ98tNvROzwdo75x0cjNWXedPgQu0j0yj1vvhrREOEtaZ21DrdHLSkLUj-TyW5nfGtTbkzGwORyxD4ggzkm1GWXxmPPhO6peNBst6RIqGOWcYhhZ"
              alt="BrahmiBhojan logo"
              className="h-16 w-auto object-contain"
            />
          </div>

          <div className="mx-16 flex-1">
            <div className="mx-auto flex max-w-xl items-center rounded-full bg-[var(--surface-container-highest)] px-4 py-2">
              <span className="material-symbols-outlined mr-2 text-[var(--on-surface-variant)]">search</span>
              <input
                className="w-full border-none bg-transparent p-0 text-sm text-[var(--on-surface)] outline-none"
                placeholder="Search for Ghee, Honey, Millets..."
                type="text"
              />
              <button type="button" className="ml-2 flex items-center justify-center">
                <span className="material-symbols-outlined text-[var(--primary)]">mic</span>
              </button>
            </div>
          </div>

          <div className="flex items-center gap-6">
            <button type="button" className="group flex items-center gap-1 text-[var(--on-surface-variant)]">
              <span className="material-symbols-outlined transition-colors group-hover:text-[var(--primary)]">
                location_on
              </span>
              <span className="text-sm font-medium">Village Location</span>
            </button>
            <div className="h-6 w-px bg-[var(--outline-variant)]" />
            <button type="button" className="relative text-[var(--on-surface-variant)] hover:text-[var(--primary)]">
              <span className="material-symbols-outlined">favorite</span>
            </button>
            <button type="button" className="relative text-[var(--on-surface-variant)] hover:text-[var(--primary)]">
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
                  className="group flex items-center gap-2 rounded-full px-1 py-1 transition-colors hover:bg-[var(--surface-container-high)] hover:cursor-pointer"
                  onClick={() => setIsProfileMenuOpen((current) => !current)}
                >
                  <span className="max-w-[96px] truncate text-sm font-medium text-[var(--on-surface)]">
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
                      className="flex w-full items-center justify-between rounded-lg px-3 py-2 text-left text-sm text-[var(--on-surface)] hover:bg-[var(--surface-container-low)]"
                    >
                      <span>Profile</span>
                      <span className="text-xs text-[var(--on-surface-variant)]">Soon</span>
                    </button>
                    <button
                      type="button"
                      className="flex w-full items-center justify-between rounded-lg px-3 py-2 text-left text-sm text-[var(--on-surface)] hover:bg-[var(--surface-container-low)]"
                    >
                      <span>Orders</span>
                      <span className="text-xs text-[var(--on-surface-variant)]">Soon</span>
                    </button>
                    <div className="my-1 h-px bg-[var(--outline-variant)]/70" />
                    <button
                      type="button"
                      onClick={handleLogout}
                      disabled={logoutMutation.isPending}
                      className="w-full rounded-lg px-3 py-2 text-left text-sm font-medium text-[var(--error)] hover:bg-[var(--surface-container-low)] disabled:opacity-60"
                    >
                      {logoutMutation.isPending ? "Logging out..." : "Logout"}
                    </button>
                  </div>
                ) : null}
              </div>
            ) : (
              <Link
                href="/login"
                aria-label="Go to login"
                className="text-[var(--on-surface-variant)] transition-colors hover:text-[var(--primary)]"
              >
                <span className="material-symbols-outlined">person</span>
              </Link>
            )}
          </div>
        </div>
      </nav>

      <header className="sticky top-0 z-40 flex items-center justify-between bg-[rgba(248,250,246,0.8)] px-5 py-2 backdrop-blur-md md:hidden">
        <img
          src="https://lh3.googleusercontent.com/aida-public/AB6AXuBfA-dmWSV9GS_Mtqgxd4Wc8lrT0JuCDyXAcvDTL3yHupY-jQgHqWkBE77Is4pZtNeTj6bDz8qa_Fm2fy0i4vInsOl9R7A3J4_25AShHxMDt2vlFJflHR6gEVsBCnR9y2AB3rIIYIzU_w8RqzV47fnuS-1MdQYulZ98tNvROzwdo75x0cjNWXedPgQu0j0yj1vvhrREOEtaZ21DrdHLSkLUj-TyW5nfGtTbkzGwORyxD4ggzkm1GWXxmPPhO6peNBst6RIqGOWcYhhZ"
          alt="BrahmiBhojan logo"
          className="h-10 w-auto object-contain"
        />
        <div className="flex items-center gap-2">
          <button type="button" className="p-2 text-[var(--on-surface-variant)]">
            <span className="material-symbols-outlined">search</span>
          </button>
          <button type="button" className="relative p-2 text-[var(--on-surface-variant)]">
            <span className="material-symbols-outlined">shopping_cart</span>
            <span className="absolute right-1 top-1 flex h-3.5 w-3.5 items-center justify-center rounded-full bg-[var(--secondary)] text-[10px] font-bold text-[var(--on-secondary)]">
              2
            </span>
          </button>
          {isLoggedIn ? (
            <div className="">
                <button
              type="button"
              aria-label="Customer profile"
              className="relative flex h-8 w-8 items-center justify-center rounded-full bg-[#e6d1df] text-xs font-semibold text-[#3c2f39]"
              onClick={() => setIsProfileMenuOpen((current) => !current)}
            >
              {profileInitials}
              <span className="absolute -bottom-0.5 -right-0.5 h-2.5 w-2.5 rounded-full border border-[var(--surface)] bg-[#38b84c]" />
            </button>
            {isProfileMenuOpen ? (
                  <div className="absolute right-0 top-12 z-20 w-48 rounded-b-xl border border-[var(--outline-variant)] bg-[var(--surface-container-lowest)] p-2 shadow-[0_12px_30px_rgba(0,0,0,0.12)]">
                    <button
                      type="button"
                      className="flex w-full items-center justify-between rounded-lg px-3 py-2 text-left text-sm text-[var(--on-surface)] hover:bg-[var(--surface-container-low)]"
                    >
                      <span>Profile</span>
                      <span className="text-xs text-[var(--on-surface-variant)]">Soon</span>
                    </button>
                    <button
                      type="button"
                      className="flex w-full items-center justify-between rounded-lg px-3 py-2 text-left text-sm text-[var(--on-surface)] hover:bg-[var(--surface-container-low)]"
                    >
                      <span>Orders</span>
                      <span className="text-xs text-[var(--on-surface-variant)]">Soon</span>
                    </button>
                    <div className="my-1 h-px bg-[var(--outline-variant)]/70" />
                    <button
                      type="button"
                      onClick={handleLogout}
                      disabled={logoutMutation.isPending}
                      className="w-full rounded-lg px-3 py-2 text-left text-sm font-medium text-[var(--error)] hover:bg-[var(--surface-container-low)] disabled:opacity-60"
                    >
                      {logoutMutation.isPending ? "Logging out..." : "Logout"}
                    </button>
                  </div>
                ) : null}
            </div>
          ) : (
            <Link
              href="/login"
              aria-label="Go to login"
              className="p-2 text-[var(--on-surface-variant)] transition-colors hover:text-[var(--primary)]"
            >
              <span className="material-symbols-outlined">person</span>
            </Link>
          )}
        </div>
      </header>
    </>
  );
}
