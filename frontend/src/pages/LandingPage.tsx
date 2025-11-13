import { useNavigate } from "react-router-dom";
import { useSelector, useDispatch } from "react-redux";
import { useEffect } from "react";
import { Button } from "../components/ui/Button";
import { HeroSection } from "../components/landing/HeroSection";
import { FeaturesSection } from "../components/landing/FeaturesSection";
import { Footer } from "../components/landing/Footer";
import type { RootState } from "../store";
import { logout } from "../store/slices/authSlice";
import toast from "react-hot-toast";

export const LandingPage = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { user, accessToken } = useSelector((state: RootState) => state.auth);
  const isLoggedIn = !!accessToken;

  // 로그인된 사용자는 대시보드로 리다이렉트
  useEffect(() => {
    if (isLoggedIn) {
      navigate("/dashboard");
    }
  }, [isLoggedIn, navigate]);

  const handleLogout = () => {
    dispatch(logout());
    toast.success("로그아웃되었습니다");
  };

  return (
    <div className="flex min-h-screen flex-col">
      {/* Header/Navigation */}
      <header className="sticky top-0 z-50 border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
        <div className="container mx-auto flex h-16 items-center justify-between px-4">
          <div className="flex items-center gap-2">
            <h1 className="text-xl font-bold text-primary">Day Memory</h1>
          </div>
          <nav className="flex items-center gap-4">
            {isLoggedIn ? (
              <>
                <span className="text-sm text-muted-foreground">
                  {user?.nickname}님 환영합니다
                </span>
                <Button onClick={() => navigate("/dashboard")}>
                  대시보드
                </Button>
                <Button variant="ghost" onClick={handleLogout}>
                  로그아웃
                </Button>
              </>
            ) : (
              <>
                <Button variant="ghost" onClick={() => navigate("/login")}>
                  로그인
                </Button>
                <Button onClick={() => navigate("/signup")}>회원가입</Button>
              </>
            )}
          </nav>
        </div>
      </header>

      {/* Main Content */}
      <main className="flex-1">
        <HeroSection />
        <FeaturesSection />
      </main>

      {/* Footer */}
      <Footer />
    </div>
  );
};
