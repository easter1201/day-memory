import { describe, it, expect, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { Input } from "../Input";

describe("Input Component", () => {
  it("should render input field", () => {
    render(<Input placeholder="Enter text" />);
    expect(screen.getByPlaceholderText("Enter text")).toBeInTheDocument();
  });

  it("should handle text input", async () => {
    const user = userEvent.setup();
    render(<Input placeholder="Enter text" />);

    const input = screen.getByPlaceholderText("Enter text");
    await user.type(input, "Hello World");

    expect(input).toHaveValue("Hello World");
  });

  it("should call onChange when value changes", async () => {
    const handleChange = vi.fn();
    const user = userEvent.setup();

    render(<Input onChange={handleChange} placeholder="Enter text" />);

    const input = screen.getByPlaceholderText("Enter text");
    await user.type(input, "Test");

    expect(handleChange).toHaveBeenCalled();
  });

  it("should be disabled when disabled prop is true", () => {
    render(<Input disabled placeholder="Enter text" />);
    expect(screen.getByPlaceholderText("Enter text")).toBeDisabled();
  });

  it("should render with different types", () => {
    const { rerender } = render(<Input type="text" placeholder="Text" />);
    expect(screen.getByPlaceholderText("Text")).toHaveAttribute("type", "text");

    rerender(<Input type="email" placeholder="Email" />);
    expect(screen.getByPlaceholderText("Email")).toHaveAttribute("type", "email");

    rerender(<Input type="password" placeholder="Password" />);
    expect(screen.getByPlaceholderText("Password")).toHaveAttribute("type", "password");

    rerender(<Input type="number" placeholder="Number" />);
    expect(screen.getByPlaceholderText("Number")).toHaveAttribute("type", "number");
  });

  it("should apply custom className", () => {
    render(<Input className="custom-class" placeholder="Input" />);
    expect(screen.getByPlaceholderText("Input")).toHaveClass("custom-class");
  });

  it("should forward ref correctly", () => {
    const ref = vi.fn();
    render(<Input ref={ref} placeholder="Input" />);
    expect(ref).toHaveBeenCalled();
  });
});
