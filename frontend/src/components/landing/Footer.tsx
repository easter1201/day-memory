import { Link } from "react-router-dom";

export const Footer = () => {
  const currentYear = new Date().getFullYear();

  return (
    <footer className="border-t bg-muted/50">
      <div className="container mx-auto px-4 py-12">
        <div className="grid gap-8 md:grid-cols-4">
          {/* Brand */}
          <div className="md:col-span-2">
            <h3 className="mb-3 text-xl font-bold text-primary">Day Memory</h3>
            <p className="mb-4 text-sm text-muted-foreground">
              소중한 사람들의 특별한 날을 기억하고
              <br />
              완벽한 선물로 마음을 전하세요
            </p>
            <p className="text-xs text-muted-foreground">
              © {currentYear} Day Memory. All rights reserved.
            </p>
          </div>

          {/* Product Links */}
          <div>
            <h4 className="mb-3 font-semibold">서비스</h4>
            <ul className="space-y-2 text-sm">
              <li>
                <Link
                  to="/login"
                  className="text-muted-foreground transition-colors hover:text-foreground"
                >
                  로그인
                </Link>
              </li>
              <li>
                <Link
                  to="/signup"
                  className="text-muted-foreground transition-colors hover:text-foreground"
                >
                  회원가입
                </Link>
              </li>
            </ul>
          </div>

          {/* Contact */}
          <div>
            <h4 className="mb-3 font-semibold">문의</h4>
            <ul className="space-y-2 text-sm text-muted-foreground">
              <li>support@daymemory.com</li>
              <li>서울시 강남구</li>
            </ul>
          </div>
        </div>

        {/* Bottom Section */}
        <div className="mt-8 border-t pt-8">
          <div className="flex flex-col items-center justify-between gap-4 text-xs text-muted-foreground md:flex-row">
            <div className="flex gap-4">
              <a href="#" className="transition-colors hover:text-foreground">
                이용약관
              </a>
              <a href="#" className="transition-colors hover:text-foreground">
                개인정보처리방침
              </a>
            </div>
            <div className="flex gap-4">
              <a
                href="https://github.com"
                target="_blank"
                rel="noopener noreferrer"
                className="transition-colors hover:text-foreground"
              >
                GitHub
              </a>
            </div>
          </div>
        </div>
      </div>
    </footer>
  );
};
