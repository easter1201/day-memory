import * as React from "react";
import { Modal } from "./Modal";
import { Button } from "../ui/Button";

interface ConfirmDialogProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void | Promise<void>;
  title?: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
  variant?: "default" | "danger";
  isLoading?: boolean;
}

export const ConfirmDialog: React.FC<ConfirmDialogProps> = ({
  isOpen,
  onClose,
  onConfirm,
  title = "확인",
  message,
  confirmText = "확인",
  cancelText = "취소",
  variant = "default",
  isLoading = false,
}) => {
  const [loading, setLoading] = React.useState(false);

  const handleConfirm = async () => {
    try {
      setLoading(true);
      await onConfirm();
      onClose();
    } catch (error) {
      console.error("Confirm action failed:", error);
    } finally {
      setLoading(false);
    }
  };

  const isProcessing = isLoading || loading;

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={title}
      size="sm"
      closeOnOverlayClick={!isProcessing}
      footer={
        <div className="flex justify-end space-x-2">
          <Button variant="outline" onClick={onClose} disabled={isProcessing}>
            {cancelText}
          </Button>
          <Button
            variant={variant === "danger" ? "destructive" : "default"}
            onClick={handleConfirm}
            disabled={isProcessing}
            isLoading={isProcessing}
          >
            {confirmText}
          </Button>
        </div>
      }
    >
      <div className="space-y-4">
        {variant === "danger" && (
          <div className="flex items-center justify-center">
            <svg
              className="h-12 w-12 text-red-500"
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
              />
            </svg>
          </div>
        )}
        <p className="text-sm text-muted-foreground">{message}</p>
      </div>
    </Modal>
  );
};

// Hook for using ConfirmDialog
export const useConfirmDialog = () => {
  const [isOpen, setIsOpen] = React.useState(false);
  const [config, setConfig] = React.useState<Omit<ConfirmDialogProps, "isOpen" | "onClose">>({
    onConfirm: () => {},
    message: "",
  });

  const confirm = (newConfig: Omit<ConfirmDialogProps, "isOpen" | "onClose">) => {
    setConfig(newConfig);
    setIsOpen(true);
  };

  const close = () => {
    setIsOpen(false);
  };

  return {
    confirm,
    close,
    ConfirmDialogComponent: () => (
      <ConfirmDialog {...config} isOpen={isOpen} onClose={close} />
    ),
  };
};
