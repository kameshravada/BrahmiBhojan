import { z } from "zod";

export const otpRequestSchema = z.object({
  mobile: z.string().regex(/^\+?[1-9]\d{9,14}$/, "Enter a valid mobile number"),
  deviceId: z.string().max(120).optional(),
});

export const otpVerifySchema = z.object({
  challengeId: z.string().min(1, "Missing challenge ID"),
  mobile: z.string().regex(/^\+?[1-9]\d{9,14}$/, "Enter a valid mobile number"),
  otp: z
    .string()
    .length(6, "OTP must be exactly 6 digits")
    .regex(/^[0-9]{6}$/, "OTP must contain 6 numeric digits"),
  deviceId: z.string().max(120).optional(),
});

export const loginPhoneFormSchema = z.object({
  mobileNumber: z
    .string()
    .min(10, "Mobile number must have at least 10 digits")
    .max(10, "Mobile number must be 10 digits")
    .regex(/^\d{10}$/, "Enter a valid 10-digit mobile number"),
});

export const loginOtpFormSchema = z.object({
  otp: z
    .string()
    .length(6, "OTP must be exactly 6 digits")
    .regex(/^\d{6}$/, "OTP must contain 6 numeric digits"),
});

export type OtpRequestInput = z.infer<typeof otpRequestSchema>;
export type OtpVerifyInput = z.infer<typeof otpVerifySchema>;
export type LoginPhoneFormInput = z.infer<typeof loginPhoneFormSchema>;
export type LoginOtpFormInput = z.infer<typeof loginOtpFormSchema>;
