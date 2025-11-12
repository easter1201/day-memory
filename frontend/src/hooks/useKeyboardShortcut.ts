import { useEffect } from "react";

interface KeyboardShortcutOptions {
  key: string;
  ctrl?: boolean;
  shift?: boolean;
  alt?: boolean;
  callback: () => void;
  enabled?: boolean;
}

/**
 * Custom hook for keyboard shortcuts
 */
export function useKeyboardShortcut({
  key,
  ctrl = false,
  shift = false,
  alt = false,
  callback,
  enabled = true,
}: KeyboardShortcutOptions) {
  useEffect(() => {
    if (!enabled) return;

    const handleKeyDown = (event: KeyboardEvent) => {
      const isCtrlMatch = ctrl === event.ctrlKey || ctrl === event.metaKey;
      const isShiftMatch = shift === event.shiftKey;
      const isAltMatch = alt === event.altKey;
      const isKeyMatch = event.key.toLowerCase() === key.toLowerCase();

      if (isCtrlMatch && isShiftMatch && isAltMatch && isKeyMatch) {
        event.preventDefault();
        callback();
      }
    };

    window.addEventListener("keydown", handleKeyDown);

    return () => {
      window.removeEventListener("keydown", handleKeyDown);
    };
  }, [key, ctrl, shift, alt, callback, enabled]);
}
