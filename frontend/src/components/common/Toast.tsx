import toast from "react-hot-toast";

export type ToastType = "success" | "error" | "info" | "warning";

interface ToastOptions {
  duration?: number;
  position?:
    | "top-left"
    | "top-center"
    | "top-right"
    | "bottom-left"
    | "bottom-center"
    | "bottom-right";
}

const defaultOptions: ToastOptions = {
  duration: 3000,
  position: "top-right",
};

// Toast 유틸리티 함수들
export const Toast = {
  success: (message: string, options?: ToastOptions) => {
    toast.success(message, {
      ...defaultOptions,
      ...options,
    });
  },

  error: (message: string, options?: ToastOptions) => {
    toast.error(message, {
      ...defaultOptions,
      ...options,
    });
  },

  info: (message: string, options?: ToastOptions) => {
    toast(message, {
      ...defaultOptions,
      ...options,
      icon: "ℹ️",
    });
  },

  warning: (message: string, options?: ToastOptions) => {
    toast(message, {
      ...defaultOptions,
      ...options,
      icon: "⚠️",
      style: {
        background: "hsl(var(--background))",
        color: "hsl(var(--foreground))",
        border: "1px solid #f59e0b",
      },
    });
  },

  promise: <T,>(
    promise: Promise<T>,
    messages: {
      loading: string;
      success: string;
      error: string;
    },
    options?: ToastOptions
  ) => {
    return toast.promise(
      promise,
      {
        loading: messages.loading,
        success: messages.success,
        error: messages.error,
      },
      {
        ...defaultOptions,
        ...options,
      }
    );
  },

  custom: (message: string, options?: ToastOptions & { icon?: string }) => {
    toast(message, {
      ...defaultOptions,
      ...options,
    });
  },

  dismiss: (toastId?: string) => {
    toast.dismiss(toastId);
  },

  remove: (toastId?: string) => {
    toast.remove(toastId);
  },
};

export default Toast;
