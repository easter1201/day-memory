import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";
import { Badge } from "../Badge";

describe("Badge Component", () => {
  it("should render badge with text", () => {
    render(<Badge>Test Badge</Badge>);
    expect(screen.getByText("Test Badge")).toBeInTheDocument();
  });

  it("should render with default variant", () => {
    render(<Badge>Default</Badge>);
    const badge = screen.getByText("Default");
    expect(badge).toHaveClass("bg-primary", "text-primary-foreground");
  });

  it("should render with different variants", () => {
    const { rerender } = render(<Badge variant="secondary">Secondary</Badge>);
    expect(screen.getByText("Secondary")).toHaveClass("bg-secondary");

    rerender(<Badge variant="destructive">Destructive</Badge>);
    expect(screen.getByText("Destructive")).toHaveClass("bg-red-500");

    rerender(<Badge variant="outline">Outline</Badge>);
    expect(screen.getByText("Outline")).toHaveClass("border");
  });

  it("should render with event type variants", () => {
    const { rerender } = render(<Badge variant="birthday">Birthday</Badge>);
    expect(screen.getByText("Birthday")).toHaveClass("bg-pink-100", "text-pink-800");

    rerender(<Badge variant="anniversary">Anniversary</Badge>);
    expect(screen.getByText("Anniversary")).toHaveClass("bg-purple-100", "text-purple-800");

    rerender(<Badge variant="holiday">Holiday</Badge>);
    expect(screen.getByText("Holiday")).toHaveClass("bg-blue-100", "text-blue-800");
  });

  it("should render with gift category variants", () => {
    const { rerender } = render(<Badge variant="electronics">Electronics</Badge>);
    expect(screen.getByText("Electronics")).toHaveClass("bg-blue-100", "text-blue-800");

    rerender(<Badge variant="fashion">Fashion</Badge>);
    expect(screen.getByText("Fashion")).toHaveClass("bg-purple-100", "text-purple-800");

    rerender(<Badge variant="food">Food</Badge>);
    expect(screen.getByText("Food")).toHaveClass("bg-orange-100", "text-orange-800");
  });

  it("should apply custom className", () => {
    render(<Badge className="custom-class">Badge</Badge>);
    expect(screen.getByText("Badge")).toHaveClass("custom-class");
  });

  it("should render as a div element", () => {
    const { container } = render(<Badge>Badge</Badge>);
    expect(container.querySelector("div")).toBeInTheDocument();
  });
});
