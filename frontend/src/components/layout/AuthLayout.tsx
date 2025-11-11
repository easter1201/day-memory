import { Link } from "react-router-dom";

interface AuthLayoutProps {
  children: React.ReactNode;
  title?: string;
  subtitle?: string;
}

export const AuthLayout = ({ children, title, subtitle }: AuthLayoutProps) => {
  return (
    <div className="flex min-h-screen flex-col bg-background">
      {/* Simple Header */}
      <header className="border-b">
        <div className="container flex h-16 items-center">
          <Link to="/" className="flex items-center space-x-2">
            <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary text-primary-foreground">
              <span className="text-lg font-bold">D</span>
            </div>
            <span className="font-bold">Day Memory</span>
          </Link>
        </div>
      </header>

      {/* Auth Content */}
      <main className="flex flex-1 items-center justify-center p-4">
        <div className="w-full max-w-md space-y-6">
          {/* Title Section */}
          {(title || subtitle) && (
            <div className="space-y-2 text-center">
              {title && <h1 className="text-3xl font-bold tracking-tight">{title}</h1>}
              {subtitle && <p className="text-sm text-muted-foreground">{subtitle}</p>}
            </div>
          )}

          {/* Auth Form */}
          <div className="rounded-lg border bg-card p-6 shadow-sm">{children}</div>
        </div>
      </main>

      {/* Simple Footer */}
      <footer className="border-t py-6">
        <div className="container">
          <div className="flex flex-col items-center justify-between gap-4 text-sm text-muted-foreground md:flex-row">
            <p>© {new Date().getFullYear()} Day Memory. All rights reserved.</p>
            <div className="flex gap-4">
              <Link to="/privacy" className="hover:text-primary">
                개인정보처리방침
              </Link>
              <Link to="/terms" className="hover:text-primary">
                이용약관
              </Link>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
};
