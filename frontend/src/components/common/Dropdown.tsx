import * as React from "react";
import { cn } from "../../utils/cn";

interface DropdownItem {
  label: string;
  onClick: () => void;
  icon?: React.ReactNode;
  variant?: "default" | "danger";
  disabled?: boolean;
}

interface DropdownProps {
  trigger: React.ReactNode;
  items: DropdownItem[];
  align?: "left" | "right";
  className?: string;
}

export const Dropdown: React.FC<DropdownProps> = ({
  trigger,
  items,
  align = "right",
  className,
}) => {
  const [isOpen, setIsOpen] = React.useState(false);
  const dropdownRef = React.useRef<HTMLDivElement>(null);

  React.useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    };

    const handleEscape = (event: KeyboardEvent) => {
      if (event.key === "Escape") {
        setIsOpen(false);
      }
    };

    if (isOpen) {
      document.addEventListener("mousedown", handleClickOutside);
      document.addEventListener("keydown", handleEscape);
    }

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
      document.removeEventListener("keydown", handleEscape);
    };
  }, [isOpen]);

  const handleItemClick = (item: DropdownItem) => {
    if (!item.disabled) {
      item.onClick();
      setIsOpen(false);
    }
  };

  return (
    <div ref={dropdownRef} className={cn("relative", className)}>
      <div onClick={() => setIsOpen(!isOpen)} className="cursor-pointer">
        {trigger}
      </div>

      {isOpen && (
        <div
          className={cn(
            "absolute top-full z-50 mt-2 w-56 rounded-md border bg-popover p-1 shadow-md",
            align === "right" ? "right-0" : "left-0"
          )}
        >
          {items.map((item, index) => (
            <button
              key={index}
              onClick={() => handleItemClick(item)}
              disabled={item.disabled}
              className={cn(
                "flex w-full items-center space-x-2 rounded-sm px-3 py-2 text-sm transition-colors",
                item.variant === "danger"
                  ? "text-red-600 hover:bg-red-50 focus:bg-red-50"
                  : "hover:bg-accent focus:bg-accent",
                item.disabled && "cursor-not-allowed opacity-50"
              )}
            >
              {item.icon && <span className="h-4 w-4">{item.icon}</span>}
              <span>{item.label}</span>
            </button>
          ))}
        </div>
      )}
    </div>
  );
};

// Profile Dropdown 전용 컴포넌트
interface ProfileDropdownProps {
  user: {
    name: string;
    email?: string;
    avatar?: string;
  };
  items: DropdownItem[];
  className?: string;
}

export const ProfileDropdown: React.FC<ProfileDropdownProps> = ({
  user,
  items,
  className,
}) => {
  const trigger = (
    <div className="flex items-center space-x-3 rounded-lg p-2 hover:bg-accent">
      {user.avatar ? (
        <img
          src={user.avatar}
          alt={user.name}
          className="h-8 w-8 rounded-full"
        />
      ) : (
        <div className="flex h-8 w-8 items-center justify-center rounded-full bg-primary text-primary-foreground">
          <span className="text-sm font-medium">
            {user.name.charAt(0).toUpperCase()}
          </span>
        </div>
      )}
      <div className="hidden md:block">
        <p className="text-sm font-medium">{user.name}</p>
        {user.email && (
          <p className="text-xs text-muted-foreground">{user.email}</p>
        )}
      </div>
      <svg
        className="h-4 w-4 text-muted-foreground"
        xmlns="http://www.w3.org/2000/svg"
        fill="none"
        viewBox="0 0 24 24"
        stroke="currentColor"
      >
        <path
          strokeLinecap="round"
          strokeLinejoin="round"
          strokeWidth={2}
          d="M19 9l-7 7-7-7"
        />
      </svg>
    </div>
  );

  return <Dropdown trigger={trigger} items={items} align="right" className={className} />;
};
