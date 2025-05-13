export function Card({ children, className }) {
  return <div className={`rounded-lg shadow-md p-4 ${className}`}>{children}</div>;
}

export function CardContent({ children }) {
  return <div>{children}</div>;
}
