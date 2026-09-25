/* eslint-disable @next/next/no-img-element */

"use client";

import Link from "next/link";
import { useState } from "react";

export function Footer() {
  const [expandedSection, setExpandedSection] = useState<string | null>(null);

  const toggleSection = (section: string) => {
    setExpandedSection(expandedSection === section ? null : section);
  };

  const FooterSection = ({
    title,
    links,
  }: {
    title: string;
    links: { label: string; href: string }[];
  }) => {
    const isExpanded = expandedSection === title;

    return (
      <div>
        {/* Desktop: Regular section */}
        <div className="hidden md:block">
          <div className="flex items-center gap-2 mb-4">
            <h3 className="label text-[var(--surface)] font-semibold uppercase tracking-wide">
              {title}
            </h3>
            <div className="w-8 h-0.5 bg-[var(--secondary)]" />
          </div>
          <ul className="space-y-3">
            {links.map((link, index) => (
              <li key={`${link.label}-${index}`}>
                <Link
                  href={link.href}
                  className="body-sm text-[var(--surface)] transition-colors duration-200 hover:text-[var(--secondary)]"
                >
                  {link.label}
                </Link>
              </li>
            ))}
          </ul>
        </div>

        {/* Mobile: Accordion */}
        <div className="md:hidden border-b border-[var(--primary)]">
          <button
            onClick={() => toggleSection(title)}
            type="button"
            className="label flex w-full items-center justify-between py-3 text-[var(--surface)] font-semibold uppercase tracking-wide hover:text-[var(--secondary)] transition-colors"
          >
            {title}
            <span className="material-symbols-outlined text-xl">
              {isExpanded ? "expand_less" : "expand_more"}
            </span>
          </button>
          {isExpanded && (
            <ul className="space-y-2 pb-4">
              {links.map((link, index) => (
                <li key={`${link.label}-${index}`}>
                  <Link
                    href={link.href}
                    className="body-sm block text-[var(--surface)] transition-colors duration-200 hover:text-[var(--secondary)]"
                  >
                    {link.label}
                  </Link>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>
    );
  };

  const shopLinks = [
    { label: "All Products", href: "/" },
    { label: "Sunnundalu", href: "/" },
    { label: "Laddus", href: "/" },
    { label: "Hot Snacks", href: "/" },
    { label: "Ghee", href: "/" },
    { label: "Spices", href: "/" },
    { label: "Millets", href: "/" },
    { label: "New Arrivals", href: "/" },
  ];

  const customerCareLinks = [
    { label: "Contact Us", href: "/" },
    { label: "FAQs", href: "/" },
    { label: "Shipping & Delivery", href: "/" },
    { label: "Returns & Refunds", href: "/" },
    { label: "Track Order", href: "/" },
    { label: "Help & Support", href: "/" },
  ];

  const informationLinks = [
    { label: "About Brahmi Bhojan", href: "/" },
    { label: "Privacy Policy", href: "/privacy-policy" },
    { label: "Terms & Conditions", href: "/" },
    { label: "Cancellation & Refund Policy", href: "/" },
    { label: "Shipping Policy", href: "/" },
  ];

  const socialLinks = [
    { icon: "📷", label: "Instagram", href: "https://instagram.com" },
    { icon: "▶️", label: "YouTube", href: "https://youtube.com" },
    { icon: "💬", label: "WhatsApp", href: "https://whatsapp.com" },
  ];

  return (
    <footer className="border-t border-[var(--primary)]/20 bg-[#2d1f1f] relative overflow-hidden">
      {/* Decorative leaf elements - top left */}
      <div className="absolute top-0 left-0 opacity-70 pointer-events-none hidden md:block">
        <img
          src="/images/footer-leaf-top-left.png"
          alt=""
          className="w-40 h-40 object-contain"
        />
      </div>

      {/* Decorative spices/turmeric - bottom left */}
      <div className="absolute bottom-0 left-0 opacity-80 pointer-events-none hidden md:block">
        <img
          src="/images/footer-spices-left.png"
          alt=""
          className="w-48 h-48 object-contain"
        />
      </div>

      {/* Decorative leaf elements - top right */}
      <div className="absolute top-0 right-0 opacity-70 pointer-events-none hidden md:block">
        <img
          src="/images/footer-leaf-top-right.png"
          alt=""
          className="w-40 h-40 object-contain"
        />
      </div>

      {/* Decorative bowl with food - bottom right */}
      <div className="absolute bottom-0 right-0 opacity-85 pointer-events-none hidden md:block">
        <img
          src="/images/footer-bowl-right.png"
          alt=""
          className="w-56 h-56 object-contain"
        />
      </div>

      {/* Main Footer Content */}
      <div className="mx-auto w-full max-w-7xl px-5 py-12 md:py-16 relative z-10">
        <div className="grid grid-cols-1 md:grid-cols-5 gap-8 mb-12">
          {/* Brand Section */}
          <div className="md:col-span-1">
            <div className="flex items-center gap-3 mb-4">
              <img
                src="/images/Brahmin Bhojan logo-trans.png"
                alt="Brahmi Bhojan"
                className="h-16 w-auto object-contain"
              />
            </div>
            <p className="label mb-3 text-[var(--secondary)] uppercase tracking-widest">
              ENDLESS FLAVOUR • PURE VEG
            </p>
            <p className="body-sm mb-6 text-[var(--surface)] leading-relaxed">
              Traditional vegetarian goodness, thoughtfully prepared and
              delivered to your home.
            </p>

            {/* Brand Highlights - Desktop only */}
            <div className="hidden md:flex flex-col gap-3">
              <div className="flex items-center gap-2 text-[var(--surface)]">
                <span className="material-symbols-outlined text-[var(--secondary)] text-xl">
                  check_circle
                </span>
                <span className="body-sm">Pure Vegetarian</span>
              </div>
              <div className="flex items-center gap-2 text-[var(--surface)]">
                <span className="material-symbols-outlined text-[var(--secondary)] text-xl">
                  check_circle
                </span>
                <span className="body-sm">No Preservatives</span>
              </div>
              <div className="flex items-center gap-2 text-[var(--surface)]">
                <span className="material-symbols-outlined text-[var(--secondary)] text-xl">
                  check_circle
                </span>
                <span className="body-sm">Traditionally Handcrafted</span>
              </div>
            </div>
          </div>

          {/* SHOP Column */}
          <div className="hidden md:block">
            <FooterSection title="SHOP" links={shopLinks} />
          </div>

          {/* CUSTOMER CARE Column */}
          <div className="hidden md:block">
            <FooterSection title="CUSTOMER CARE" links={customerCareLinks} />
          </div>

          {/* INFORMATION Column */}
          <div className="hidden md:block">
            <FooterSection title="INFORMATION" links={informationLinks} />
          </div>

          {/* CONNECT WITH US + NEED HELP Section */}
          <div className="md:col-span-1">
            {/* Connect With Us */}
            <div className="mb-10 md:mb-6">
              <div className="flex items-center gap-2 mb-4">
                <h3 className="label text-[var(--surface)] font-semibold uppercase tracking-wide">
                  Connect With Us
                </h3>
                <div className="flex-1 h-0.5 bg-[var(--secondary)]" />
              </div>
              <p className="body-sm mb-4 text-[var(--surface)]">
                Follow Brahmi Bhojan for updates, new products and traditional
                recipes.
              </p>
              <div className="flex gap-4">
                {socialLinks.map((social) => (
                  <a
                    key={social.label}
                    href={social.href}
                    target="_blank"
                    rel="noopener noreferrer"
                    aria-label={social.label}
                    className="inline-flex items-center justify-center w-12 h-12 rounded-full border-2 border-[var(--secondary)] text-[var(--surface)] transition-all duration-200 hover:bg-[var(--secondary)] hover:text-[var(--on-secondary)] hover:scale-110 text-xl"
                  >
                    <span>{social.icon}</span>
                  </a>
                ))}
              </div>
            </div>

            {/* NEED HELP? Section - Desktop only */}
            <div className="hidden md:block">
              <div className="flex items-center gap-2 mb-4">
                <h3 className="label text-[var(--secondary)] font-semibold uppercase tracking-wide">
                  NEED HELP?
                </h3>
                <div className="flex-1 h-0.5 bg-[var(--secondary)]" />
              </div>
              <div className="space-y-4">
                <div className="flex items-start gap-3">
                  <span className="material-symbols-outlined text-[var(--secondary)] text-lg flex-shrink-0 mt-0.5">
                    phone
                  </span>
                  <div>
                    <p className="body-sm text-[var(--surface)]">Phone</p>
                    <p className="body text-[var(--surface)]">
                      +91 98765 43210
                    </p>
                  </div>
                </div>
                <div className="flex items-start gap-3">
                  <span className="material-symbols-outlined text-[var(--secondary)] text-lg flex-shrink-0 mt-0.5">
                    mail
                  </span>
                  <div>
                    <p className="body-sm text-[var(--surface)]">Email</p>
                    <p className="body text-[var(--surface)] break-words">
                      support@brahmibhojan.com
                    </p>
                  </div>
                </div>
                <div className="flex items-start gap-3">
                  <span className="material-symbols-outlined text-[var(--secondary)] text-lg flex-shrink-0 mt-0.5">
                    location_on
                  </span>
                  <div>
                    <p className="body-sm text-[var(--surface)]">Address</p>
                    <p className="body text-[var(--surface)]">
                      Hyderabad, Telangana, India
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Mobile Footer Sections */}
        <div className="md:hidden space-y-0 mb-8">
          <FooterSection title="SHOP" links={shopLinks} />
          <FooterSection title="CUSTOMER CARE" links={customerCareLinks} />
          <FooterSection title="INFORMATION" links={informationLinks} />

          {/* NEED HELP? Mobile Section */}
          <div className="border-b border-[var(--primary)] py-4">
            <h3 className="label text-[var(--secondary)] font-semibold uppercase tracking-wide mb-4">
              NEED HELP?
            </h3>
            <div className="space-y-4">
              <div className="flex items-start gap-3">
                <span className="material-symbols-outlined text-[var(--secondary)] text-lg flex-shrink-0 mt-0.5">
                  phone
                </span>
                <div>
                  <p className="body-sm text-[var(--surface)]">Phone</p>
                  <p className="body text-[var(--surface)]">+91 98765 43210</p>
                </div>
              </div>
              <div className="flex items-start gap-3">
                <span className="material-symbols-outlined text-[var(--secondary)] text-lg flex-shrink-0 mt-0.5">
                  mail
                </span>
                <div>
                  <p className="body-sm text-[var(--surface)]">Email</p>
                  <p className="body text-[var(--surface)] break-words">
                    support@brahmibhojan.com
                  </p>
                </div>
              </div>
              <div className="flex items-start gap-3">
                <span className="material-symbols-outlined text-[var(--secondary)] text-lg flex-shrink-0 mt-0.5">
                  location_on
                </span>
                <div>
                  <p className="body-sm text-[var(--surface)]">Address</p>
                  <p className="body text-[var(--surface)]">
                    Hyderabad, Telangana, India
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Divider */}
      <div className="h-px bg-gradient-to-r from-transparent via-[var(--secondary)]/30 to-transparent relative z-10" />

      {/* Bottom Bar */}
      <div className="mx-auto w-full max-w-7xl px-5 py-6 md:py-8 relative z-10">
        <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4 text-center md:text-left">
          <p className="body-sm text-[var(--surface)]">
            © 2026 Brahmi Bhojan. All rights reserved.
          </p>
          <div className="flex flex-wrap justify-center md:justify-end gap-4">
            <Link
              href="/privacy-policy"
              className="body-sm text-[var(--surface)] hover:text-[var(--secondary)] transition-colors"
            >
              Privacy Policy
            </Link>
            <span className="body-sm text-[var(--surface)]">•</span>
            <Link
              href="/"
              className="body-sm text-[var(--surface)] hover:text-[var(--secondary)] transition-colors"
            >
              Terms & Conditions
            </Link>
            <span className="body-sm text-[var(--surface)]">•</span>
            <Link
              href="/"
              className="body-sm text-[var(--surface)] hover:text-[var(--secondary)] transition-colors"
            >
              Shipping Policy
            </Link>
            <span className="body-sm text-[var(--surface)]">•</span>
            <Link
              href="/"
              className="body-sm text-[var(--surface)] hover:text-[var(--secondary)] transition-colors"
            >
              Cancellation & Refund Policy
            </Link>
          </div>
          <div className="flex items-center justify-center gap-2">
            <span className="material-symbols-outlined text-[var(--secondary)] text-lg">
              favorite
            </span>
            <p className="body-sm text-[var(--secondary)]">
              Made with care for every home.
            </p>
          </div>
        </div>
      </div>
    </footer>
  );
}
