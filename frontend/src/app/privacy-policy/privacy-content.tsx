"use client";

import { useState } from "react";
import Link from "next/link";

interface PrivacySection {
  id: string;
  title: string;
  content: React.ReactNode;
}

const lastUpdatedDate = new Date("2026-09-25").toLocaleDateString("en-US", {
  year: "numeric",
  month: "long",
  day: "numeric",
});

const sections: PrivacySection[] = [
  {
    id: "about",
    title: "1. About this Privacy Policy",
    content: (
      <div className="space-y-4">
        <p className="body">Welcome to Brahmi Bhojan.</p>
        <p className="body">
          This Privacy Policy explains how Brahmi Bhojan ("Brahmi Bhojan", "we",
          "us", or "our") collects, uses, stores, and protects personal
          information when you visit our website, create an account, place an
          order, contact us, or otherwise use our services.
        </p>
        <p className="body">
          By using our website or services, you acknowledge that you have read
          and understood this Privacy Policy.
        </p>
        <p className="body">
          We aim to collect and process personal data responsibly and only for
          legitimate purposes connected with operating our business and
          providing our services.
        </p>
      </div>
    ),
  },
  {
    id: "information-we-collect",
    title: "2. Information We Collect",
    content: (
      <div className="space-y-4">
        <h4 className="h3 text-[var(--on-surface)]">
          Account & Authentication Information
        </h4>
        <ul className="space-y-2 list-disc list-inside text-[var(--on-surface-variant)]">
          <li>Mobile phone number</li>
          <li>OTP verification information</li>
          <li>Name, where provided</li>
          <li>Account-related identifiers</li>
          <li>Login and authentication records</li>
        </ul>
      </div>
    ),
  },
  {
    id: "order-delivery",
    title: "3. Order & Delivery Information",
    content: (
      <div className="space-y-4">
        <p className="body">
          When you place an order, we may collect information required to
          process and deliver your purchase, including:
        </p>
        <ul className="space-y-2 list-disc list-inside text-[var(--on-surface-variant)]">
          <li>Name</li>
          <li>Mobile number</li>
          <li>Delivery address</li>
          <li>Landmark</li>
          <li>Pincode</li>
          <li>Order details</li>
          <li>Billing information</li>
          <li>Delivery preferences</li>
          <li>Order history</li>
        </ul>
        <p className="body">
          This information helps us process your order, arrange delivery,
          provide customer support, and resolve order-related issues.
        </p>
      </div>
    ),
  },
  {
    id: "payment-information",
    title: "4. Payment Information",
    content: (
      <div className="space-y-4">
        <p className="body">
          Payments may be processed through third-party payment service
          providers.
        </p>
        <p className="body">
          Depending on the payment method you choose, payment providers may
          process information such as:
        </p>
        <ul className="space-y-2 list-disc list-inside text-[var(--on-surface-variant)]">
          <li>Payment transaction details</li>
          <li>Transaction ID</li>
          <li>Payment status</li>
          <li>Payment method</li>
          <li>Refund information</li>
        </ul>
        <p className="body text-[var(--on-surface-variant)] italic">
          Note: Brahmi Bhojan does not store complete card numbers, CVV, UTP
          PINs, or banking credentials. These are handled securely by payment
          providers only.
        </p>
      </div>
    ),
  },
  {
    id: "how-we-use",
    title: "5. How We Use Your Information",
    content: (
      <div className="space-y-4">
        <p className="body">We may use personal information to:</p>
        <ul className="space-y-2 list-disc list-inside text-[var(--on-surface-variant)]">
          <li>Create and manage your account</li>
          <li>Verify your mobile number</li>
          <li>Send OTPs</li>
          <li>Process and fulfill orders</li>
          <li>Arrange product delivery</li>
          <li>Provide customer support</li>
          <li>Process refunds and cancellations</li>
          <li>Send important service-related communications</li>
          <li>Maintain order records</li>
          <li>Prevent fraud and unauthorized activity</li>
          <li>Improve website performance and usability</li>
          <li>Understand how customers interact with our website</li>
          <li>Maintain security</li>
          <li>Comply with applicable legal requirements</li>
        </ul>
      </div>
    ),
  },
  {
    id: "otp-mobile",
    title: "6. OTP & Mobile Number",
    content: (
      <div className="space-y-4">
        <p className="body">
          The Brahmi Bhojan authentication system uses mobile-number
          authentication through OTP.
        </p>
        <p className="body">Your mobile number may be used to:</p>
        <ul className="space-y-2 list-disc list-inside text-[var(--on-surface-variant)]">
          <li>Send login OTPs</li>
          <li>Verify your identity</li>
          <li>Create or access your account</li>
          <li>Communicate important information about your orders</li>
          <li>Contact you regarding customer support or service issues</li>
        </ul>
      </div>
    ),
  },
  {
    id: "cookies",
    title: "7. Cookies & Similar Technologies",
    content: (
      <div className="space-y-4">
        <p className="body">
          Brahmi Bhojan may use cookies and similar technologies to provide a
          better and more reliable website experience.
        </p>
        <p className="body">These technologies may help us:</p>
        <ul className="space-y-2 list-disc list-inside text-[var(--on-surface-variant)]">
          <li>Keep you signed in</li>
          <li>Remember preferences</li>
          <li>Maintain shopping-cart functionality</li>
          <li>Understand website usage</li>
          <li>Improve performance</li>
          <li>Detect security issues</li>
          <li>Measure marketing effectiveness where applicable</li>
        </ul>
      </div>
    ),
  },
  {
    id: "analytics",
    title: "8. Analytics",
    content: (
      <div className="space-y-4">
        <p className="body">
          We may use analytics tools and services to understand how our website
          is used and to improve user experience. These services may collect
          information such as:
        </p>
        <ul className="space-y-2 list-disc list-inside text-[var(--on-surface-variant)]">
          <li>Pages visited</li>
          <li>Device information</li>
          <li>Browser information</li>
          <li>Referral source</li>
          <li>Website interactions</li>
          <li>General usage patterns</li>
        </ul>
        <p className="body text-[var(--on-surface-variant)] italic">
          Note: We use [ANALYTICS_PROVIDER] for analytics. Visit their privacy
          policy for more details.
        </p>
      </div>
    ),
  },
  {
    id: "information-sharing",
    title: "9. When We Share Information",
    content: (
      <div className="space-y-4">
        <p className="body">
          We do not sell personal information as a business practice.
        </p>
        <p className="body">
          We may share relevant information with trusted service providers where
          necessary to operate Brahmi Bhojan, including categories such as:
        </p>
        <ul className="space-y-2 list-disc list-inside text-[var(--on-surface-variant)]">
          <li>OTP/SMS providers</li>
          <li>Payment processors</li>
          <li>Delivery and logistics partners</li>
          <li>Hosting and cloud service providers</li>
          <li>Website analytics providers</li>
          <li>Customer-support tools</li>
          <li>Security and fraud-prevention providers</li>
        </ul>
        <p className="body">
          We may also disclose information where required by applicable law or
          legal process.
        </p>
      </div>
    ),
  },
  {
    id: "data-security",
    title: "10. Data Security",
    content: (
      <div className="space-y-4">
        <p className="body">
          We take reasonable technical and organizational measures to protect
          personal information against unauthorized access, misuse, alteration,
          disclosure, or destruction.
        </p>
        <p className="body">
          However, no internet-based service can guarantee absolute security.
        </p>
        <p className="body">
          Users should also take reasonable precautions to protect their mobile
          device, OTPs, and account credentials.
        </p>
        <div className="bg-[var(--surface-container-low)] border-l-4 border-[var(--error)] p-4 rounded">
          <p className="body-sm font-semibold text-[var(--error)]">
            Important Security Notice:
          </p>
          <p className="body-sm text-[var(--on-surface)]">
            Never share an OTP with another person, including someone claiming
            to represent Brahmi Bhojan.
          </p>
        </div>
      </div>
    ),
  },
  {
    id: "data-retention",
    title: "11. Data Retention",
    content: (
      <div className="space-y-4">
        <p className="body">
          We retain personal information only for as long as reasonably
          necessary for the purposes for which it was collected, including:
        </p>
        <ul className="space-y-2 list-disc list-inside text-[var(--on-surface-variant)]">
          <li>Providing our services</li>
          <li>Maintaining account and order records</li>
          <li>Resolving disputes</li>
          <li>Preventing fraud</li>
          <li>Meeting legal, accounting, tax, or regulatory requirements</li>
        </ul>
        <p className="body text-[var(--on-surface-variant)] italic">
          Note: Specific retention periods are determined based on business and
          legal requirements. Contact us for details.
        </p>
      </div>
    ),
  },
  {
    id: "privacy-rights",
    title: "12. Your Privacy Rights",
    content: (
      <div className="space-y-4">
        <p className="body">
          Subject to applicable law, users may have rights concerning their
          personal information, including:
        </p>
        <ul className="space-y-2 list-disc list-inside text-[var(--on-surface-variant)]">
          <li>Access to personal information</li>
          <li>Correction of inaccurate information</li>
          <li>Withdrawal of consent where applicable</li>
          <li>Requesting deletion where applicable</li>
          <li>Raising grievances</li>
          <li>Receiving information about processing of personal data</li>
        </ul>
        <p className="body">
          To exercise your privacy rights, contact us using the information
          provided in the "Contact Us" section below.
        </p>
      </div>
    ),
  },
  {
    id: "children-privacy",
    title: "13. Children's Privacy",
    content: (
      <div className="space-y-4">
        <p className="body">
          Our services are intended for general consumers. We do not knowingly
          collect personal information from children in circumstances where such
          collection would be restricted under applicable law.
        </p>
        <p className="body">
          If you believe we have collected information from a child in violation
          of applicable law, please contact us immediately.
        </p>
      </div>
    ),
  },
  {
    id: "third-party-services",
    title: "14. Third-Party Services",
    content: (
      <div className="space-y-4">
        <p className="body">
          Our website may contain integrations or links to third-party services,
          including payment providers, delivery providers, social-media
          platforms, analytics services, or other external services.
        </p>
        <p className="body">
          Those services operate under their own terms and privacy policies.
        </p>
        <p className="body">
          Brahmi Bhojan is not responsible for independent third-party privacy
          practices. We encourage you to review their privacy policies before
          sharing your information.
        </p>
      </div>
    ),
  },
  {
    id: "changes-to-policy",
    title: "15. Changes to This Privacy Policy",
    content: (
      <div className="space-y-4">
        <p className="body">
          We may update this Privacy Policy from time to time to reflect changes
          in:
        </p>
        <ul className="space-y-2 list-disc list-inside text-[var(--on-surface-variant)]">
          <li>Our services</li>
          <li>Technology</li>
          <li>Business operations</li>
          <li>Applicable laws</li>
          <li>Regulatory requirements</li>
        </ul>
        <p className="body">
          When significant changes are made, appropriate notice may be provided
          through the website or other available communication channels.
        </p>
        <p className="body">
          The "Last Updated" date at the top reflects the latest revision.
        </p>
      </div>
    ),
  },
  {
    id: "contact-us",
    title: "16. Contact & Privacy Support",
    content: (
      <div className="space-y-4">
        <p className="body">
          If you have questions about this Privacy Policy, your personal
          information, or a privacy-related concern, please contact:
        </p>
        <div className="bg-[var(--surface-container-low)] p-6 rounded-lg mt-4">
          <h4 className="label font-semibold text-[var(--on-surface)] mb-4">
            Brahmi Bhojan
          </h4>
          <div className="space-y-3">
            <div>
              <p className="label font-semibold text-[var(--on-surface)]">
                Email:
              </p>
              <p className="body text-[var(--primary)]">
                <a href="mailto:[SUPPORT_EMAIL]">[SUPPORT_EMAIL]</a>
              </p>
            </div>
            <div>
              <p className="label font-semibold text-[var(--on-surface)]">
                Phone:
              </p>
              <p className="body text-[var(--primary)]">
                <a href="tel:[SUPPORT_PHONE]">[SUPPORT_PHONE]</a>
              </p>
            </div>
            <div>
              <p className="label font-semibold text-[var(--on-surface)]">
                Address:
              </p>
              <p className="body text-[var(--on-surface-variant)]">
                [REGISTERED_BUSINESS_ADDRESS]
              </p>
            </div>
          </div>
          <p className="body-sm text-[var(--on-surface-variant)] mt-4 italic">
            For privacy or data-related requests, please mention "Privacy
            Request" in the subject line.
          </p>
        </div>
      </div>
    ),
  },
];

const tableOfContents = sections.map((section) => ({
  id: section.id,
  title: section.title.replace(/^\d+\.\s/, ""),
}));

export function PrivacyPolicyContent() {
  const [expandedToc, setExpandedToc] = useState(false);
  const [activeSection, setActiveSection] = useState<string | null>(null);

  const scrollToSection = (id: string) => {
    const element = document.getElementById(id);
    if (element) {
      element.scrollIntoView({ behavior: "smooth" });
      setActiveSection(id);
    }
  };

  return (
    <div className="bg-[var(--surface)] min-h-screen">
      {/* Hero Section */}
      <section className="mx-auto max-w-7xl px-5 py-12 md:py-16">
        <div className="max-w-2xl">
          <h1 className="h1 mb-4 text-[var(--on-surface)]">Privacy Policy</h1>
          <p className="body-lg text-[var(--on-surface-variant)] mb-6">
            Your privacy matters to us. Here's how Brahmi Bhojan collects, uses
            and protects your information.
          </p>
          <p className="body-sm text-[var(--on-surface-variant)]">
            <span className="font-semibold">Last updated:</span>{" "}
            {lastUpdatedDate}
          </p>
        </div>
      </section>

      {/* Summary Card */}
      <section className="mx-auto max-w-7xl px-5 pb-12">
        <div className="rounded-xl bg-[#f0ebe8] border border-[var(--secondary)]/20 p-6 md:p-8">
          <div className="flex gap-4">
            <span className="material-symbols-outlined text-[var(--secondary)] text-3xl flex-shrink-0 mt-1">
              shield
            </span>
            <div>
              <h2 className="h3 mb-2 text-[var(--on-surface)]">
                Your privacy matters
              </h2>
              <p className="body text-[var(--on-surface-variant)]">
                We collect personal information only when it is reasonably
                needed to provide our products and services, process orders,
                authenticate your account, communicate with you, and improve
                your experience.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Main Content */}
      <div className="mx-auto max-w-7xl px-5 pb-16">
        {/* Breadcrumbs */}
        <nav className="mb-8 flex items-center gap-2 text-sm text-[var(--on-surface-variant)]">
          <Link href="/" className="hover:text-[var(--primary)]">
            Home
          </Link>
          <span className="material-symbols-outlined text-sm">
            chevron_right
          </span>
          <span>Privacy Policy</span>
        </nav>

        <div className="grid gap-8 md:grid-cols-4 lg:gap-12">
          {/* Sidebar - Desktop */}
          <aside className="hidden md:block">
            <div className="sticky top-20 space-y-2">
              <h3 className="label font-semibold text-[var(--on-surface)] mb-4 uppercase">
                On this page
              </h3>
              <nav className="space-y-1">
                {tableOfContents.map((item) => (
                  <button
                    key={item.id}
                    onClick={() => scrollToSection(item.id)}
                    type="button"
                    className={`block w-full text-left px-3 py-2 rounded-lg text-sm transition-all duration-200 ${
                      activeSection === item.id
                        ? "bg-[var(--primary)] text-[var(--on-primary)]"
                        : "text-[var(--on-surface-variant)] hover:bg-[var(--surface-container-low)] hover:text-[var(--on-surface)]"
                    }`}
                  >
                    {item.title}
                  </button>
                ))}
              </nav>
            </div>
          </aside>

          {/* Mobile TOC - Accordion */}
          <div className="md:hidden col-span-1 mb-8">
            <button
              onClick={() => setExpandedToc(!expandedToc)}
              type="button"
              className="label flex w-full items-center justify-between bg-[var(--surface-container-low)] rounded-lg px-4 py-3 font-semibold text-[var(--on-surface)] hover:bg-[var(--surface-container-high)] transition-colors"
            >
              On this page
              <span className="material-symbols-outlined text-lg">
                {expandedToc ? "expand_less" : "expand_more"}
              </span>
            </button>
            {expandedToc && (
              <nav className="mt-2 space-y-1 bg-[var(--surface-container-low)] rounded-lg p-2">
                {tableOfContents.map((item) => (
                  <button
                    key={item.id}
                    onClick={() => {
                      scrollToSection(item.id);
                      setExpandedToc(false);
                    }}
                    type="button"
                    className="block w-full text-left px-3 py-2 rounded text-sm text-[var(--on-surface-variant)] hover:bg-[var(--surface-container-high)] hover:text-[var(--on-surface)] transition-colors"
                  >
                    {item.title}
                  </button>
                ))}
              </nav>
            )}
          </div>

          {/* Main Content */}
          <main className="md:col-span-3 lg:col-span-3">
            <article className="prose prose-lg max-w-none space-y-10">
              {sections.map((section) => (
                <section
                  key={section.id}
                  id={section.id}
                  className="scroll-mt-20"
                >
                  <h2 className="h2 mb-4 text-[var(--on-surface)]">
                    {section.title}
                  </h2>
                  <div className="text-[var(--on-surface)]">
                    {section.content}
                  </div>
                </section>
              ))}
            </article>
          </main>
        </div>

        {/* Back to Top Button */}
        <button
          onClick={() => window.scrollTo({ top: 0, behavior: "smooth" })}
          type="button"
          className="fixed bottom-6 right-6 flex items-center justify-center w-12 h-12 bg-[var(--primary)] text-[var(--on-primary)] rounded-full shadow-lg hover:bg-[var(--primary-container)] hover:text-[var(--primary)] transition-all duration-200 md:bottom-8 md:right-8"
          aria-label="Back to top"
        >
          <span className="material-symbols-outlined">expand_less</span>
        </button>
      </div>
    </div>
  );
}
