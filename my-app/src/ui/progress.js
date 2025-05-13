export function Progress({ value, className }) {
  return (
    <div className={`relative w-full h-4 bg-gray-200 rounded-full overflow-hidden ${className}`}>
      <div
        className="absolute top-0 left-0 h-full bg-blue-500"
        style={{ width: `${value}%` }}
      ></div>
    </div>
  );
}
