"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useMutation } from "@tanstack/react-query";
import { AxiosError } from "axios";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { useForm, useWatch } from "react-hook-form";

import { apiClient } from "@/lib/api/client";
import {
  type LoginOtpFormInput,
  type LoginPhoneFormInput,
  loginOtpFormSchema,
  loginPhoneFormSchema,
} from "@/schemas/auth";
import { useAuthStore } from "@/stores/auth-store";

type OtpRequestResponse = {
  challengeId: string;
  expiresInSeconds: number;
  otpPreview?: string | null;
};

type AuthResponse = {
  userId: string;
  mobile: string;
  email: string | null;
  fullName: string | null;
  role: string;
  accessToken: string;
  refreshToken: string;
  expiresInSeconds: number;
};

function getDeviceId() {
  const key = "bb_device_id";
  const existing = localStorage.getItem(key);
  if (existing) {
    return existing;
  }
  const created = crypto.randomUUID();
  localStorage.setItem(key, created);
  return created;
}

function toE164Indian(mobileNumber: string) {
  return `+91${mobileNumber}`;
}

export default function LoginPage() {
  const router = useRouter();
  const [step, setStep] = useState<"mobile" | "otp">("mobile");
  const [challengeId, setChallengeId] = useState("");
  const [e164Mobile, setE164Mobile] = useState("");
  const [otpError, setOtpError] = useState("");
  const [otpExpiresAtEpochMs, setOtpExpiresAtEpochMs] = useState<number | null>(
    null,
  );
  const [secondsLeft, setSecondsLeft] = useState<number | null>(null);

  const setSession = useAuthStore((state) => state.setSession);

  const phoneForm = useForm<LoginPhoneFormInput>({
    resolver: zodResolver(loginPhoneFormSchema),
    defaultValues: {
      mobileNumber: "",
    },
  });

  const otpForm = useForm<LoginOtpFormInput>({
    resolver: zodResolver(loginOtpFormSchema),
    defaultValues: {
      otp: "",
    },
  });

  const otpValue = useWatch({
    control: otpForm.control,
    name: "otp",
    defaultValue: "",
  });
  const otpDigits = Array.from(
    { length: 6 },
    (_, index) => otpValue[index] ?? "",
  );

  const requestOtpMutation = useMutation({
    mutationFn: async (payload: { mobile: string; deviceId: string }) => {
      const response = await apiClient.post<OtpRequestResponse>(
        "/api/v1/auth/otp/request",
        payload,
      );
      return response.data;
    },
  });

  const verifyOtpMutation = useMutation({
    mutationFn: async (payload: {
      challengeId: string;
      mobile: string;
      otp: string;
      deviceId: string;
    }) => {
      const response = await apiClient.post<AuthResponse>(
        "/api/v1/auth/otp/verify",
        payload,
      );
      return response.data;
    },
  });

  useEffect(() => {
    if (!otpExpiresAtEpochMs || step !== "otp") {
      setSecondsLeft(null);
      return;
    }

    const updateCountdown = () => {
      const remaining = Math.max(
        0,
        Math.ceil((otpExpiresAtEpochMs - Date.now()) / 1000),
      );
      setSecondsLeft(remaining);
      if (remaining === 0) {
        setOtpError("OTP expired. Please request a new OTP.");
      }
    };

    updateCountdown();
    const intervalId = window.setInterval(updateCountdown, 1000);

    return () => {
      window.clearInterval(intervalId);
    };
  }, [otpExpiresAtEpochMs, step]);

  const handleOtpDigitChange = (index: number, value: string) => {
    const numericValue = value.replace(/\D/g, "").slice(-1);
    const nextDigits = [...otpDigits];
    nextDigits[index] = numericValue;
    const merged = nextDigits.join("");
    otpForm.setValue("otp", merged, { shouldValidate: true });
    if (numericValue && index < 5) {
      const nextInput = document.getElementById(
        `otp-${index + 1}`,
      ) as HTMLInputElement | null;
      nextInput?.focus();
    }
  };

  const handleOtpKeyDown = (index: number, key: string) => {
    if (key === "Backspace" && !otpDigits[index] && index > 0) {
      const previousInput = document.getElementById(
        `otp-${index - 1}`,
      ) as HTMLInputElement | null;
      previousInput?.focus();
    }
  };

  const onSubmitPhone = phoneForm.handleSubmit(async (values) => {
    const mobile = toE164Indian(values.mobileNumber);
    const deviceId = getDeviceId();

    try {
      const data = await requestOtpMutation.mutateAsync({ mobile, deviceId });
      setChallengeId(data.challengeId);
      setE164Mobile(mobile);
      setOtpExpiresAtEpochMs(Date.now() + data.expiresInSeconds * 1000);
      setOtpError("");
      setStep("otp");
      otpForm.setValue("otp", "");
      requestAnimationFrame(() => {
        const firstOtpInput = document.getElementById(
          "otp-0",
        ) as HTMLInputElement | null;
        firstOtpInput?.focus();
      });
    } catch (error) {
      const message =
        (error as { response?: { data?: { message?: string } } }).response?.data
          ?.message ?? "Unable to request OTP right now. Please try again.";
      phoneForm.setError("mobileNumber", { message });
    }
  });

  const onSubmitOtp = otpForm.handleSubmit(async (values) => {
    if (!challengeId || !e164Mobile) {
      setOtpError("Missing OTP challenge context. Please request OTP again.");
      setStep("mobile");
      return;
    }

    const deviceId = getDeviceId();

    if (otpExpiresAtEpochMs && Date.now() > otpExpiresAtEpochMs) {
      setOtpError("OTP expired. Please request a new OTP.");
      otpForm.setValue("otp", "");
      return;
    }

    try {
      const data = await verifyOtpMutation.mutateAsync({
        challengeId,
        mobile: e164Mobile,
        otp: values.otp,
        deviceId,
      });

      setSession({
        userId: data.userId,
        mobile: data.mobile,
        email: data.email,
        fullName: data.fullName,
        role: data.role,
        accessToken: data.accessToken,
        refreshToken: data.refreshToken,
        expiresInSeconds: data.expiresInSeconds,
      });

      localStorage.setItem("bb_access_token", data.accessToken);
      localStorage.setItem("bb_refresh_token", data.refreshToken);
      router.replace("/");
    } catch (error) {
      const axiosError = error as AxiosError<{ message?: string } | string>;
      const backendMessage =
        typeof axiosError.response?.data === "string"
          ? axiosError.response.data
          : axiosError.response?.data?.message;
      const message =
        axiosError.response?.status === 401
          ? "OTP is invalid or expired. Use the latest OTP or request a new one."
          : (backendMessage ?? "Invalid OTP. Please try again.");
      setOtpError(message);
    }
  });

  const handleBack = () => {
    setStep("mobile");
    setOtpError("");
    setOtpExpiresAtEpochMs(null);
    otpForm.setValue("otp", "");
  };

  const handleResendOtp = async () => {
    if (!e164Mobile) {
      return;
    }

    try {
      const data = await requestOtpMutation.mutateAsync({
        mobile: e164Mobile,
        deviceId: getDeviceId(),
      });
      setChallengeId(data.challengeId);
      setOtpExpiresAtEpochMs(Date.now() + data.expiresInSeconds * 1000);
      setOtpError("");
      otpForm.setValue("otp", "");
    } catch (error) {
      const message =
        (error as { response?: { data?: { message?: string } } }).response?.data
          ?.message ?? "Unable to resend OTP now. Please wait and try again.";
      setOtpError(message);
    }
  };

  return (
    <div className="relative flex min-h-screen items-center justify-center overflow-hidden bg-[var(--surface-container-low)] p-5">
      <main className="relative z-10 w-full max-w-md rounded-[24px] bg-[var(--surface-container-lowest)] p-10 shadow-[0_12px_48px_rgba(0,0,0,0.12)]">
        <header className="mb-8 text-center">
          <h1 className="mb-2 text-[44px] font-bold leading-[52px] tracking-[-0.02em] text-[var(--primary)]">
            BrahmiBhojan
          </h1>
          <p className="text-base text-[var(--on-surface-variant)]">
            The essence of New Organic
          </p>
        </header>

        {step === "mobile" ? (
          <section>
            <h2 className="mb-2 text-[36px] font-semibold leading-[44px] text-[var(--on-surface)]">
              Welcome
            </h2>
            <p className="mb-6 text-base text-[var(--on-surface-variant)]">
              Enter your mobile number to continue.
            </p>

            <form className="flex flex-col gap-6" onSubmit={onSubmitPhone}>
              <div>
                <div className="relative flex items-center overflow-hidden rounded-xl bg-[var(--surface-container)] focus-within:ring-2 focus-within:ring-[var(--primary)]">
                  <div className="flex items-center gap-1 border-r border-[var(--outline-variant)] bg-[var(--surface-container-high)] px-3 py-3">
                    <span className="material-symbols-outlined text-[var(--on-surface-variant)] fill-icon">
                      public
                    </span>
                    <span className="text-sm font-medium text-[var(--on-surface)]">
                      +91
                    </span>
                  </div>
                  <input
                    autoComplete="tel-national"
                    className="w-full border-none bg-transparent px-3 py-3 text-lg text-[var(--on-surface)] outline-none placeholder:text-[var(--on-surface-variant)]/65"
                    placeholder="Mobile Number"
                    type="tel"
                    inputMode="numeric"
                    maxLength={10}
                    onInput={(e) => {
                      e.currentTarget.value = e.currentTarget.value.replace(
                        /\D/g,
                        "",
                      );
                    }}
                    {...phoneForm.register("mobileNumber", {
                      required: "Mobile number is required",
                      pattern: {
                        value: /^\d{10}$/,
                        message: "Enter a valid 10-digit mobile number",
                      },
                    })}
                  />
                </div>
                {phoneForm.formState.errors.mobileNumber?.message ? (
                  <p className="mt-2 text-sm text-[var(--error)]">
                    {phoneForm.formState.errors.mobileNumber.message}
                  </p>
                ) : null}
              </div>

              <button
                className="mt-1 flex h-14 w-full items-center justify-center rounded-xl bg-[var(--primary)] text-sm font-semibold text-[var(--on-primary)] transition-colors hover:bg-[var(--primary-container)] disabled:opacity-60"
                type="submit"
                disabled={requestOtpMutation.isPending}
              >
                {requestOtpMutation.isPending ? "Sending OTP..." : "Get OTP"}
              </button>
            </form>
          </section>
        ) : (
          <section>
            <button
              className="mb-6 flex items-center gap-1 text-[var(--on-surface-variant)] transition-colors hover:text-[var(--primary)]"
              type="button"
              onClick={handleBack}
            >
              <span className="material-symbols-outlined text-[20px]">
                arrow_back
              </span>
              <span className="text-xs font-semibold">Back</span>
            </button>

            <h2 className="mb-2 text-[36px] font-semibold leading-[44px] text-[var(--on-surface)]">
              Verify
            </h2>
            <p className="mb-6 text-base text-[var(--on-surface-variant)]">
              Enter the 6-digit code sent to
              <br />
              <span className="font-medium text-[var(--on-surface)]">
                {e164Mobile}
              </span>
            </p>

            {secondsLeft !== null ? (
              <p className="mb-4 text-sm text-[var(--on-surface-variant)]">
                OTP expires in {Math.floor(secondsLeft / 60)}:
                {(secondsLeft % 60).toString().padStart(2, "0")}
              </p>
            ) : null}

            <form className="flex flex-col gap-8" onSubmit={onSubmitOtp}>
              <div>
                <div className="flex justify-between gap-1">
                  {otpDigits.map((digit, index) => (
                    <input
                      key={`otp-${index}`}
                      id={`otp-${index}`}
                      className="h-14 w-12 rounded-xl border-2 border bg-[var(--surface-container)] text-center text-2xl font-semibold text-[var(--on-surface)] outline-none transition-colors focus:border-[var(--primary)]"
                      inputMode="numeric"
                      maxLength={1}
                      type="text"
                      value={digit}
                      onChange={(event) =>
                        handleOtpDigitChange(index, event.target.value)
                      }
                      onKeyDown={(event) => handleOtpKeyDown(index, event.key)}
                    />
                  ))}
                </div>

                {(otpError || otpForm.formState.errors.otp?.message) && (
                  <p className="mt-2 text-sm text-[var(--error)]">
                    {otpError || otpForm.formState.errors.otp?.message}
                  </p>
                )}
              </div>

              <button
                className="flex h-14 w-full items-center justify-center rounded-xl bg-[var(--primary)] text-sm font-semibold text-[var(--on-primary)] transition-colors hover:bg-[var(--primary-container)] disabled:opacity-60"
                type="submit"
                disabled={verifyOtpMutation.isPending}
              >
                {verifyOtpMutation.isPending
                  ? "Verifying..."
                  : "Verify & Proceed"}
              </button>

              <div className="text-center">
                <button
                  type="button"
                  className="bg-transparent text-sm font-semibold text-[var(--primary)] hover:underline"
                  onClick={handleResendOtp}
                >
                  Resend OTP
                </button>
              </div>
            </form>
          </section>
        )}
      </main>

      <div className="pointer-events-none fixed left-0 top-0 -z-10 h-full w-full overflow-hidden opacity-50">
        <div className="absolute -left-[10%] -top-[20%] h-[60%] w-[60%] rounded-full bg-[var(--secondary-container)] blur-[100px]" />
        <div className="absolute -right-[10%] top-[60%] h-[50%] w-[50%] rounded-full bg-[var(--surface-tint)] opacity-20 blur-[120px]" />
      </div>
    </div>
  );
}
