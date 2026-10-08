import React from "react";

export interface HeadingProps extends React.HTMLAttributes<HTMLHeadingElement> {
  level?: 1 | 2 | 3 | 4;
  children: React.ReactNode;
}

export const Heading: React.FC<HeadingProps> = ({
  level = 1,
  className = "",
  children,
  ...props
}) => {
  const styles: Record<number, string> = {
    1: "text-3xl sm:text-4xl font-extrabold tracking-tight text-content-primary dark:text-content-dark-primary",
    2: "text-2xl sm:text-3xl font-bold tracking-tight text-content-primary dark:text-content-dark-primary",
    3: "text-xl sm:text-2xl font-semibold tracking-tight text-content-primary dark:text-content-dark-primary",
    4: "text-lg font-semibold text-content-primary dark:text-content-dark-primary",
  };

  const combinedClass = `${styles[level]} ${className}`;

  switch (level) {
    case 1:
      return <h1 className={combinedClass} {...props}>{children}</h1>;
    case 2:
      return <h2 className={combinedClass} {...props}>{children}</h2>;
    case 3:
      return <h3 className={combinedClass} {...props}>{children}</h3>;
    case 4:
      return <h4 className={combinedClass} {...props}>{children}</h4>;
    default:
      return <h1 className={combinedClass} {...props}>{children}</h1>;
  }
};
