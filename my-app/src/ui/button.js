export function Button({ children, onClick, className }) {
  return (
    <button
      className={`py-2 px-4 rounded-lg shadow-md hover:shadow-lg transition ${className}`}
      onClick={onClick}
    >
      {children}
    </button>
  );
}
