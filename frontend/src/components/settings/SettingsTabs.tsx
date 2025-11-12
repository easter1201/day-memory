import { NavLink } from "react-router-dom";

const tabs = [
  { name: "프로필", path: "/settings/profile" },
  { name: "알림", path: "/settings/notifications" },
  { name: "계정", path: "/settings/account" },
];

export const SettingsTabs = () => {
  return (
    <div className="border-b">
      <nav className="flex space-x-8">
        {tabs.map((tab) => (
          <NavLink
            key={tab.path}
            to={tab.path}
            className={({ isActive }) =>
              `border-b-2 px-1 py-4 text-sm font-medium transition-colors ${
                isActive
                  ? "border-primary text-primary"
                  : "border-transparent text-muted-foreground hover:border-gray-300 hover:text-foreground"
              }`
            }
          >
            {tab.name}
          </NavLink>
        ))}
      </nav>
    </div>
  );
};
