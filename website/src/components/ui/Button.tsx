import React from "react";

export interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: "primary" | "secondary" | "outline" | "ghost" | "danger";
  size?: "sm" | "md" | "lg";
  children: React.ReactNode;
}

export const Button: React.FC<ButtonProps> = ({
  variant = "primary",
  size = "md",
  className = "",
  children,
  ...props
}) => {
  const baseStyles =
    "inline-flex items-center justify-center font-semibold rounded-md transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand-primary focus-visible:ring-offset-2 disabled:opacity-50 disabled:pointer-events-none";

  const sizeStyles = {
    sm: "min-h-[36px] px-3 text-xs",
    md: "min-h-[48px] px-5 text-sm", // 48px touch target
    lg: "min-h-[52px] px-6 text-base",
  };

  const variantStyles = {
    primary:
      "bg-brand-primary text-white hover:bg-brand-primary-hover active:bg-blue-800",
    secondary:
      "bg-surface-card dark:bg-surface-dark-card text-content-primary dark:text-content-dark-primary border border-border-subtle dark:border-border-dark-subtle hover:bg-slate-100 dark:hover:bg-slate-800",
    outline:
      "border-2 border-brand-primary text-brand-primary hover:bg-brand-primary-light dark:hover:bg-blue-950",
    ghost:
      "text-brand-primary hover:bg-brand-primary-light dark:hover:bg-slate-850",
    danger:
      "bg-urgency-danger text-white hover:bg-red-600 active:bg-red-700",
  };

  return (
    <button
      className={`${baseStyles} ${sizeStyles[size]} ${variantStyles[variant]} ${className}`}
      {...props}
    >
      {children}
    </button>
  );
};
