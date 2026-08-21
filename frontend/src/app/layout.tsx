import type { Metadata } from "next";
import type { ReactNode } from "react";
import { Inter } from "next/font/google";
import "./globals.css";
import { AppProviders } from "@/components/providers/app-providers";

const inter = Inter({
  variable: "--font-inter",
  subsets: ["latin"],
  weight: ["400", "500", "600", "700"],
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
    <html lang="en" className={`${inter.variable} h-full antialiased`}>
      <head>
        {/* eslint-disable-next-line @next/next/no-page-custom-font, @next/next/google-font-display */}
        <link
          rel="stylesheet"
          href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=block"
        />
      </head>
      <body className="min-h-full">
        <AppProviders>{children}</AppProviders>
      </body>
    </html>
  );
}
