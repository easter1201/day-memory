import { Link } from "react-router-dom";
import { useState } from "react";
import { cn } from "../../utils/cn";

export const Header = () => {
  const [isProfileOpen, setIsProfileOpen] = useState(false);
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  // TODO: Replace with actual user data from Redux store
  const user = {
    name: "사용자",
    email: "user@example.com",
  };

  const handleLogout = () => {
    // TODO: Implement logout logic
    console.log("Logout");
  };

  return (
    <header className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
      <div className="container flex h-16 items-center">
        {/* Logo */}
        <Link to="/dashboard" className="mr-6 flex items-center space-x-2">
          <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary text-primary-foreground">
            <span className="text-lg font-bold">D</span>
          </div>
          <span className="hidden font-bold sm:inline-block">Day Memory</span>
        </Link>

        {/* Desktop Navigation */}
        <nav className="hidden md:flex md:flex-1 md:items-center md:space-x-6">
          <Link
            to="/dashboard"
            className="text-sm font-medium transition-colors hover:text-primary"
          >
            대시보드
          </Link>
          <Link
            to="/events"
            className="text-sm font-medium text-muted-foreground transition-colors hover:text-primary"
          >
            이벤트
          </Link>
          <Link
            to="/gifts"
            className="text-sm font-medium text-muted-foreground transition-colors hover:text-primary"
          >
            선물
          </Link>
          <Link
            to="/recommendations"
            className="text-sm font-medium text-muted-foreground transition-colors hover:text-primary"
          >
            AI 추천
          </Link>
        </nav>

        {/* Right side */}
        <div className="flex flex-1 items-center justify-end space-x-4">
          {/* Profile Dropdown */}
          <div className="relative">
            <button
              onClick={() => setIsProfileOpen(!isProfileOpen)}
              className="flex items-center space-x-2 rounded-md p-2 hover:bg-accent"
            >
              <div className="flex h-8 w-8 items-center justify-center rounded-full bg-primary text-primary-foreground">
                <span className="text-sm font-medium">{user.name[0]}</span>
              </div>
              <span className="hidden text-sm font-medium md:inline-block">
                {user.name}
              </span>
            </button>

            {/* Dropdown Menu */}
            {isProfileOpen && (
              <>
                <div
                  className="fixed inset-0 z-40"
                  onClick={() => setIsProfileOpen(false)}
                />
                <div className="absolute right-0 z-50 mt-2 w-56 rounded-md border bg-popover p-1 shadow-md">
                  <div className="px-2 py-1.5">
                    <p className="text-sm font-medium">{user.name}</p>
                    <p className="text-xs text-muted-foreground">{user.email}</p>
                  </div>
                  <div className="my-1 h-px bg-border" />
                  <Link
                    to="/settings"
                    className="block rounded-sm px-2 py-1.5 text-sm hover:bg-accent"
                    onClick={() => setIsProfileOpen(false)}
                  >
                    설정
                  </Link>
                  <button
                    onClick={handleLogout}
                    className="w-full rounded-sm px-2 py-1.5 text-left text-sm hover:bg-accent"
                  >
                    로그아웃
                  </button>
                </div>
              </>
            )}
          </div>

          {/* Mobile Menu Button */}
          <button
            onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
            className="inline-flex h-10 w-10 items-center justify-center rounded-md md:hidden"
          >
            <svg
              className="h-6 w-6"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              {isMobileMenuOpen ? (
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M6 18L18 6M6 6l12 12"
                />
              ) : (
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M4 6h16M4 12h16M4 18h16"
                />
              )}
            </svg>
          </button>
        </div>
      </div>

      {/* Mobile Menu */}
      {isMobileMenuOpen && (
        <div className="border-t md:hidden">
          <nav className="container space-y-1 py-4">
            <Link
              to="/dashboard"
              className="block rounded-md px-3 py-2 text-sm font-medium hover:bg-accent"
              onClick={() => setIsMobileMenuOpen(false)}
            >
              대시보드
            </Link>
            <Link
              to="/events"
              className="block rounded-md px-3 py-2 text-sm font-medium text-muted-foreground hover:bg-accent"
              onClick={() => setIsMobileMenuOpen(false)}
            >
              이벤트
            </Link>
            <Link
              to="/gifts"
              className="block rounded-md px-3 py-2 text-sm font-medium text-muted-foreground hover:bg-accent"
              onClick={() => setIsMobileMenuOpen(false)}
            >
              선물
            </Link>
            <Link
              to="/recommendations"
              className="block rounded-md px-3 py-2 text-sm font-medium text-muted-foreground hover:bg-accent"
              onClick={() => setIsMobileMenuOpen(false)}
            >
              AI 추천
            </Link>
          </nav>
        </div>
      )}
    </header>
  );
};
