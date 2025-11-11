import * as React from "react";
import { cva, type VariantProps } from "class-variance-authority";
import { cn } from "../../utils/cn";

const badgeVariants = cva(
  "inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-semibold transition-colors focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2",
  {
    variants: {
      variant: {
        default: "bg-primary text-primary-foreground hover:bg-primary/80",
        secondary: "bg-secondary text-secondary-foreground hover:bg-secondary/80",
        destructive: "bg-red-500 text-white hover:bg-red-600",
        outline: "border border-input bg-background hover:bg-accent hover:text-accent-foreground",
        // 이벤트 타입
        birthday: "bg-pink-100 text-pink-800",
        anniversary: "bg-purple-100 text-purple-800",
        holiday: "bg-blue-100 text-blue-800",
        other: "bg-gray-100 text-gray-800",
        // 선물 카테고리
        electronics: "bg-blue-100 text-blue-800",
        fashion: "bg-purple-100 text-purple-800",
        food: "bg-orange-100 text-orange-800",
        book: "bg-green-100 text-green-800",
        hobby: "bg-pink-100 text-pink-800",
        beauty: "bg-rose-100 text-rose-800",
        home: "bg-amber-100 text-amber-800",
      },
    },
    defaultVariants: {
      variant: "default",
    },
  }
);

export interface BadgeProps
  extends React.HTMLAttributes<HTMLDivElement>,
    VariantProps<typeof badgeVariants> {}

function Badge({ className, variant, ...props }: BadgeProps) {
  return <div className={cn(badgeVariants({ variant }), className)} {...props} />;
}

export { Badge, badgeVariants };
