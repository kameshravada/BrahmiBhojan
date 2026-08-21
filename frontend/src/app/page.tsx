/* eslint-disable @next/next/no-img-element */

"use client";

import { HomeTopNav } from "@/components/home-top-nav";

const categories = [
  {
    name: "Pure Ghee",
    image:
      "https://lh3.googleusercontent.com/aida-public/AB6AXuB7sE1znsWeHSr_GlMNrL_if-RU-LwcwXmgtv28SdOiaffGPZCqse2GZg_eQtMTQa9OCJCinOLO-BSBusmspoCG8VvYPTChkzayUeBZaWgIulnx3XrClMhxuI9QCJ5zgjsf8wgpUFYDlxIHJ-tW7gwjO4vYkgWEZN-NRvw00Fpg0KYR_fPfVwxjzQ3OrgE0YH0dTmgkSSG2wvekSShAaJ3LTEtLSFUpN23gZGgTbCMUmYYMXdb7rBLEX8uruhlklU9jxtQ2ti-lchks",
  },
  {
    name: "Cold Pressed",
    image:
      "https://lh3.googleusercontent.com/aida-public/AB6AXuBfoLG7-Mto8HogFs89N2kfvbTUWosLdMmwVD6Y0Ud6HwMlIl6xF8r1E1z7Npgt4YuplBWlTfAA6QZBPcWxJf2xL5SsIgg8Ct23XuKyljlspeSTcHePbvzI8wpLC9sGrgTMEd9ONbnHp_BGoMSPAXGx3wlbP0xAGQoyfAP-VylgFcZDpcfnGdn3t_irgitwNx40DTopMaoz3--moTr5QggpJPl3fX-wpKxy4-zw7uQuoB5cOMH8U8_emRhOFeYhuBjjUTVlKZ23XrB9",
  },
  {
    name: "Millets",
    image:
      "https://lh3.googleusercontent.com/aida-public/AB6AXuDknh1jQ2pa4llJFJYI-SMSIUcywIsef8_wEx9usO0fhFbOqiMbfpAO3NqpcxDQuwUxlVjDC-veh_2KQqeyAuZXFHtuQKH_w8B3_QaWlwQJdVFr5LL0JHyeI5zrqnor7Q0s3zkhGWC40x1uHtQqaPME_7YT8hgFj_FidS2MpQQiGUBQcqMTZaDM8T9ILfeakjW2N2DurmHHUEF_5jSg_CuDS0datOooiEB_7F68BtOshwM9Tb7OFvlYHk6o74MsbBT_RkfibMz7tG8S",
  },
  {
    name: "Wild Honey",
    image:
      "https://lh3.googleusercontent.com/aida-public/AB6AXuAACNBT1wOuralkP640HL5DxDLfOvBQOVva3r81ne_IWAmtTgtzQ_gdp-vHzxv_17yu_UYURKSUGcxCHfQ67hI00Dh2C7sXzTSCIPCUJBJlyoBua-HTK6yHtx-B7L-bfU5ThDOWzN3RfYm5eHI6xPiQalWH4qFE3YtWM2NGHIHzyzQBXAXv-FbWxd3zz6kFnpI8CHIgFf-k-gjgzk6ost4reqpAXmXfZoePukebWJs3bkNAxCmlxgNXIMUi2ZfasYgqV1VOd48Huofl",
  },
  {
    name: "Spices",
    image:
      "https://lh3.googleusercontent.com/aida-public/AB6AXuD6H7zHqEoZRahP-8RIUAg5EbJFhvYBp5Bycr8i-MXTEgfSRIgknKY_EgL-bZ_jU5VYBWS1xZHPZb3LeQ5v_k5S3740dc0Yi9f55Jr9p6K7vB0zUHMYrE5AxiGraWcr-bZrwq-9iXzWU7XtAGnHcHmdgYRaukx66YIUgtov-zuETIj3XztPPu3zifDXPHyE1tjKLa3ky74XAPTKQ8OAL8u54quh_5Yd2QC7C5XF0FLyM9fT8eAIdo0C9G1kG-z9ZMJ8JhHI7zAOassV",
  },
];

const products = [
  {
    name: "A2 Gir Cow Cultured Ghee",
    seller: "Farmer Ravi • Gujarat",
    price: "Rs 850",
    unit: "/500ml",
    badge: "Organic",
    badgeBg: "bg-[var(--tertiary-fixed-dim)]",
    badgeText: "text-[var(--on-tertiary-fixed)]",
    image:
      "https://lh3.googleusercontent.com/aida-public/AB6AXuA66gvMHmW4k1tjp1dZ-WY15heuIFyoku8NTYcvfUrLu-zzf2MGgwR5sBqBXzmwswgVlfqEuNyn9czfBMivZO8CmKY1ohVbNl3xeyuf1zLTQmdVn68mGiauj8xh6Uyvme8B-b9WD8z8XanWuZxudbTnc-YtpDdAu4l-XTpU3kZtrejGq0mEeLcgnNyiU_MDLHugsMlPS4SQaEeNXNvrCknhLV9D3y-Pp6HJTMrTfoHso3AlIbOcf7e_8GoO76brkyl9SDJSOUvsHncI",
    avatar:
      "https://lh3.googleusercontent.com/aida-public/AB6AXuC4R9p_sjxnsdI5wmGYPwMmHe1NnEkpqz4MfbhlVTnQ-n9Va5iL7UDIJexxpHsb1-7oV5ALu4AVRc_lflVYCkvwcAltaZFImGhq1-NUHTgVITLDLbXAfRP4Zl4Q2q26cDB-1r1zUEEZjKr6bkp14lQHtWyWobJfxsdjXUpDCH86j3Hmfk2Fse9DYFH1-7mTWQP2pQfbkMp-RCjPUxuxmauuPKydk4cYUwDPFz3Qa3tVz9nz_tsdE_jbBXBXbZwvw0vmkmWw8wbWec33",
  },
  {
    name: "Raw Forest Honey",
    seller: "Tribal Co-op • MP",
    price: "Rs 450",
    unit: "/250g",
    badge: "Wild Gathered",
    badgeBg: "bg-[var(--secondary-fixed)]",
    badgeText: "text-[var(--on-secondary-fixed)]",
    image:
      "https://lh3.googleusercontent.com/aida-public/AB6AXuB0OGvi7_tgE7Z0pj73VY_wgwBp7OraHaZ__mxE0OCRnRE-QVYlf1JsmziTzxiT2HnucHXlTMw4rVk02q1NYkV7j7RHGVmxYLz16pZrodAP0lAdauUw-_uNkidprpM6OkbdjP4HXI3rlg32iqTDKYgJEzFfEwZ8cl1xle2Q9u_9oPr0ezJ5lJ7_RlmIFrDhWxve_BQRHDbC-PHrJkhKleCE3bWDjPESutk1EldDZT3-I_AaEu8KCgD_GwE1L9l5JhG9hyRWVaphm8of",
    avatar:
      "https://lh3.googleusercontent.com/aida-public/AB6AXuAMj3nCvMaZbmaM-_odNtP3GzJeytfsG2qBhq5u8Rlb43z_FCdO5Tmrt_LXi9VQNzD3apKKEVWao_NTnSK59aYXQlWKzQ0n8O9t1pAhZ8htNUtc7JGaopJ4ZdaLlwQkVzA5AkxA_QgnDgSv1O1QaLgxbull8lIPIopVvT9BTTmXvaPZHRucNvI1Jf6bH-dXIizWlXMIzi4quz18QsprQmj_Bd8WPFKcJBVT8TmR3jj7ANoOLlCTJaagQC5cbWi7FlYE8xNR40xwW5Bh",
  },
];

export default function Home() {
  return (
    <div className="bg-[var(--surface)] pb-[100px] text-[var(--on-surface)] antialiased md:pb-0">
      <HomeTopNav />

      <main className="mx-auto w-full max-w-7xl px-5 md:mt-6">
        <section className="relative mb-16 h-[300px] w-full overflow-hidden rounded-[24px] shadow-[0_4px_20px_rgba(24,77,54,0.04)] md:h-[480px]">
          <img
            className="absolute inset-0 h-full w-full object-cover"
            alt="Village farm and family food preparation"
            src="https://lh3.googleusercontent.com/aida-public/AB6AXuAKzC2iCeTzh067oqtySH3QOxt3iOFmZCy5rf2oG-T0CVbkLK8fAiSkKn_FKyyvC_NR0Mq2YTVT5TwpQF-ybqkkJi4fxPSCOI_c0lMhsBuDUBpF0A0KevkFTW7JT_Uod4--lup3taYl1f9bD7flGixm24Pc6KBbcQrd9wX24TJKT2efCUl2kTfnbjQ-7yIWjR8t3IPOGcU0eKfRqSJCbQc_-eCONvLBvbNPiBeqnAatSD2-CBYwt5EFaLS6qZzQ3f0XbFN0boYgEjOI"
          />
          <div className="absolute inset-0 flex flex-col justify-end bg-gradient-to-t from-[rgba(184,239,207,0.8)] to-transparent p-6 md:p-16">
            <h1 className="hero-title-shadow mb-3 max-w-2xl text-3xl font-semibold leading-tight text-[var(--on-surface)] md:text-5xl">
              Authentic Village Flavors, Delivered.
            </h1>
            <p className="mb-6 max-w-lg text-base text-[var(--on-surface)]/90 md:text-lg">
              Experience the true taste of heritage with organically grown, traditionally crafted food straight
              from the hands of our farmers.
            </p>
            <button
              type="button"
              className="h-14 w-max rounded-full bg-[var(--primary)] px-8 text-sm font-semibold text-[var(--on-primary)] transition-colors hover:opacity-90"
            >
              Explore Harvest
            </button>
          </div>
        </section>

        <section className="mb-16">
          <h2 className="mb-6 text-2xl font-semibold text-[var(--on-surface)]">Categories</h2>
          <div className="hide-scrollbar -mx-5 flex gap-3 overflow-x-auto px-5 pb-1 md:mx-0 md:px-0">
            {categories.map((category) => (
              <button
                key={category.name}
                type="button"
                className="group flex min-w-[90px] flex-col items-center md:min-w-[110px]"
              >
                <div className="relative mb-1 h-16 w-16 overflow-hidden rounded-full bg-[var(--surface-container-high)] shadow-[0_4px_20px_rgba(24,77,54,0.04)] md:h-20 md:w-20">
                  <img
                    className="absolute inset-0 h-full w-full object-cover transition-transform duration-500 group-hover:scale-110"
                    alt={category.name}
                    src={category.image}
                  />
                </div>
                <span className="text-xs font-semibold text-[var(--on-surface-variant)] md:text-sm">
                  {category.name}
                </span>
              </button>
            ))}
          </div>
        </section>

        <section className="mb-16">
          <div className="mb-6 flex items-end justify-between">
            <h2 className="text-2xl font-semibold text-[var(--on-surface)]">Trending Today</h2>
            <button
              type="button"
              className="flex items-center gap-1 text-sm font-semibold text-[var(--primary)] transition-opacity hover:opacity-80"
            >
              View All
              <span className="material-symbols-outlined text-base">arrow_forward</span>
            </button>
          </div>

          <div className="grid grid-cols-2 gap-3 md:grid-cols-4 md:gap-6">
            {products.map((product) => (
              <article
                key={product.name}
                className="flex h-full flex-col overflow-hidden rounded-2xl border border-[var(--outline-variant)]/30 bg-[var(--surface-container-lowest)] shadow-[0_4px_20px_rgba(24,77,54,0.04)]"
              >
                <div className="relative aspect-square w-full overflow-hidden bg-[var(--surface-container-low)]">
                  <img
                    className="h-full w-full object-cover transition-transform duration-700 hover:scale-105"
                    alt={product.name}
                    src={product.image}
                  />
                  <span
                    className={`${product.badgeBg} ${product.badgeText} absolute left-3 top-3 rounded-full px-2 py-1 text-[10px] font-bold uppercase tracking-wide`}
                  >
                    {product.badge}
                  </span>
                </div>

                <div className="flex flex-grow flex-col justify-between p-3">
                  <div>
                    <h3 className="mb-1 line-clamp-2 text-sm font-semibold text-[var(--on-surface)]">{product.name}</h3>
                    <div className="mb-3 flex items-center gap-1">
                      <div className="h-5 w-5 overflow-hidden rounded-full bg-[var(--surface-container-highest)]">
                        <img className="h-full w-full object-cover" alt={product.seller} src={product.avatar} />
                      </div>
                      <span className="text-xs text-[var(--on-surface-variant)]">{product.seller}</span>
                    </div>
                  </div>

                  <div className="mt-auto flex items-center justify-between border-t border-[var(--outline-variant)]/20 pt-3">
                    <p className="text-xl font-semibold text-[var(--primary)]">
                      {product.price}
                      <span className="text-xs font-normal text-[var(--on-surface-variant)]">{product.unit}</span>
                    </p>
                    <button
                      type="button"
                      className="flex h-10 w-10 items-center justify-center rounded-full bg-[var(--secondary-container)] text-[var(--on-secondary-container)] transition-colors hover:bg-[var(--secondary)] hover:text-[var(--on-secondary)]"
                    >
                      <span className="material-symbols-outlined">add</span>
                    </button>
                  </div>
                </div>
              </article>
            ))}
          </div>
        </section>
      </main>

      <nav className="pointer-events-none fixed bottom-0 left-0 z-50 flex w-full justify-center px-5 pb-6 pt-2 md:hidden">
        <div className="pointer-events-auto flex w-[calc(100%-40px)] items-center justify-between rounded-full bg-[var(--surface)] px-2 py-2 shadow-[0_8px_30px_rgba(0,0,0,0.08)]">
          {[
            { icon: "home", label: "Home", active: true },
            { icon: "grid_view", label: "Categories", active: false },
            { icon: "receipt_long", label: "Orders", active: false },
            { icon: "favorite", label: "Wishlist", active: false },
            { icon: "person", label: "Profile", active: false },
          ].map((item) => (
            <a
              key={item.label}
              className={`flex min-w-[64px] flex-col items-center justify-center rounded-full px-2 py-2 transition-colors duration-200 ${
                item.active
                  ? "bg-[var(--secondary-container)] text-[var(--on-secondary-container)]"
                  : "text-[var(--on-surface-variant)] hover:bg-[var(--surface-container-low)]"
              }`}
              href="#"
            >
              <span className={`material-symbols-outlined mb-1 ${item.active ? "fill-icon" : ""}`}>{item.icon}</span>
              <span className="text-[10px] font-semibold">{item.label}</span>
            </a>
          ))}
        </div>
      </nav>
    </div>
  );
}
