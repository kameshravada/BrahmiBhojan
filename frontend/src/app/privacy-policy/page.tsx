import { Metadata } from "next";
import { PrivacyPolicyContent } from "./privacy-content";

export const metadata: Metadata = {
  title: "Privacy Policy | Brahmi Bhojan",
  description:
    "Read the Brahmi Bhojan Privacy Policy to understand how we collect, use, protect and manage customer information when you use our website, create an account or place an order.",
  robots: {
    index: true,
    follow: true,
  },
};

export default function PrivacyPolicyPage() {
  return <PrivacyPolicyContent />;
}
