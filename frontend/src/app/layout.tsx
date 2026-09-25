import type { Metadata } from "next";
import type { ReactNode } from "react";
import { Playfair_Display, Manrope } from "next/font/google";
import "./globals.css";
import { AppProviders } from "@/components/providers/app-providers";
import { Footer } from "@/components/footer";

const playfairDisplay = Playfair_Display({
  subsets: ["latin"],
  weight: ["500", "600", "700"],
  variable: "--font-display",
  display: "swap",
});

const manrope = Manrope({
  subsets: ["latin"],
  weight: ["400", "500", "600", "700"],
  variable: "--font-body",
  display: "swap",
});

export const metadata: Metadata = {
  title: {
    default: "BrahmiBhojan | Traditional Nutrition, Modern Convenience",
    template: "%s | BrahmiBhojan",
  },
  description:
    "BrahmiBhojan is a healthy grocery and traditional foods storefront focused on fresh essentials, trusted sourcing, and quick checkout.",
  metadataBase: new URL("https://www.brahmibhojan.com"),
  openGraph: {
    title: "BrahmiBhojan",
    description:
      "Healthy groceries and traditional foods with quality-first sourcing.",
    type: "website",
    siteName: "BrahmiBhojan",
  },
  robots: {
    index: true,
    follow: true,
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: ReactNode;
}>) {
  return (
    <html
      lang="en"
      className={`${playfairDisplay.variable} ${manrope.variable} h-full antialiased`}
    >
      <head>
        {/* eslint-disable-next-line @next/next/no-page-custom-font, @next/next/google-font-display */}
        <link
          rel="stylesheet"
          href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=block"
        />
      </head>
      <body className="min-h-full flex flex-col">
        <AppProviders>{children}</AppProviders>
        <Footer />
      </body>
    </html>
  );
}
